package com.tylip.quasitype.examples

import cats.syntax.option._
import com.tylip.quasitype.coreinitial._
import com.tylip.quasitype.examples

//noinspection ScalaFileName
final case class GetSetTransformState(
  result: List[Spec],
  unprocessed: List[Spec],
)

final case class PropertySpec(
  name: String,
  get: Morphism,
  set: Morphism,
) { outer =>
  def spec: Spec =
    Spec(
      name = outer.name,
      prop = Eql(Hom(IntSet, IntSet), Comp(get, set), Id(IntSet)),
    )
}

object GetSetToProps {

  private val getterRegex = "^get(\\w+)$".r
  private val setterRegex = "^set(\\w+)$".r

  def propForSetter(
    setter: Spec,
    others: List[Spec],
  ): Option[PropertySpec] =
    for {
      mtch <- setterRegex.findFirstMatchIn(setter.name)
      name = mtch.group(1)
      (valueSet, setterF) <- setter.prop.some.collect {
        case Elem(Hom(valueSet, UnitSet), setterF) =>
          (valueSet, setterF)
      }
      getterF <- others.collectFirst {
        case Spec(
              methodName,
              Elem(Hom(UnitSet, `valueSet`), getterF),
              false,
            ) if methodName == s"get$name" =>
          getterF
      }
      result = examples.PropertySpec(name = name, get = getterF, set = setterF)
    } yield result

  def propForGetter(
    getter: Spec,
    others: List[Spec],
  ): Option[PropertySpec] =
    for {
      mtch <- getterRegex.findFirstMatchIn(getter.name)
      name = mtch.group(1)
      (valueSet, getterF) <- getter.prop.some.collect {
        case Elem(Hom(UnitSet, valueSet), getterF) =>
          (valueSet, getterF)
      }
      setterF <- others.collectFirst {
        case Spec(
              methodName,
              Elem(Hom(`valueSet`, UnitSet), setterF),
              false,
            ) if methodName == s"set$name" =>
          setterF
      }
      result = examples.PropertySpec(name = name, get = getterF, set = setterF)
    } yield result

  def apply(terms: Seq[Spec]): Seq[Spec] =
    Iterator
      .iterate(GetSetTransformState(List(), terms.toList))(state =>
        state.unprocessed match {
          case head :: tail =>
            propForSetter(head, tail)
              .map(prop =>
                GetSetTransformState(
                  state.result :+ prop.spec,
                  tail.filter(_.name != prop.get.name),
                ),
              )
              .orElse(
                propForGetter(head, tail).map(prop =>
                  GetSetTransformState(
                    state.result :+ prop.spec,
                    tail.filter(_.name != prop.set.name),
                  ),
                ),
              ).getOrElse(GetSetTransformState(state.result :+ head, tail))
          case Nil => state
        },
      ).dropWhile(_.unprocessed.nonEmpty).next().result

}
