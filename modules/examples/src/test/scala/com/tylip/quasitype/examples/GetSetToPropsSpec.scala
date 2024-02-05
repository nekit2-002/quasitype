package com.tylip.quasitype.examples

import com.tylip.quasitype.coreinitial
import com.tylip.quasitype.coreinitial._
import org.specs2.Specification
import sourcecode.{Args, Name}

object GetSetToPropsSpec
    extends Specification
    with SpecSetInitialImpl
    with CategGetSetAlgebraInitialImpl
    with CategAlgebraInitialImpl
    with ConstantsAlgebraInitialImpl {

  def is =
    s2"""
      Scala eval interpretation checks:
        $transformed"""

  private def transformed = {
    val props = GetSetToProps(allSpecs)
    props.must(
      contain(
        beLike[Spec] { case Spec("IntSpec", _, _) => ok },
      ),
    )
  }

}

//noinspection ScalaFileName
object GetSetToPropsAutoSpec
    extends Specification
    with UnitGetSetScalaAutoAlgebra
    with SpecSetInitialImpl
    with CategGetSetAlgebraInitialImpl
    with CategAlgebraInitialImpl
    with ConstantsAlgebraInitialImpl {

  def is =
    s2"""
      Scala eval interpretation checks:
        $transformed"""

  private def transformed = {
    val props = GetSetToProps(allSpecs)
    props.must(
      contain(
        beLike[Spec] { case Spec("IntAS", _, _) => ok },
      ),
    )
  }

  override def mkMorphismFun(
    f: Any => Any,
  )(implicit name: Name, args: Args): Morphism =
    coreinitial.GetInt

}
