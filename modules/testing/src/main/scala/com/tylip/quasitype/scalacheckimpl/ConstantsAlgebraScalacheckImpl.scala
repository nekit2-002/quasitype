package com.tylip.quasitype.scalacheckimpl

import com.tylip.quasitype.corefinal.ConstantsAlgebra
import org.scalacheck.effect.PropF
import org.scalacheck.{Arbitrary, Cogen, Gen}

trait ConstantsAlgebraScalacheckImpl
    extends CategAlgebraScalacheckImpl
    with ConstantsAlgebra {
  def intSet: Set =
    mkSet(
      Arbitrary.arbInt.arbitrary,
      Cogen.cogenInt.contramap(_.asInstanceOf[Int]),
      intVProp,
    )
  def predSafeIntSet: Set =
    mkSet(
      Gen.chooseNum(Int.MinValue + 1, Int.MaxValue),
      Cogen.cogenInt.contramap(_.asInstanceOf[Int]),
      x => PropF.boolean(x.asInstanceOf[Int] > Int.MinValue),
    )
  def stringSet: Set =
    mkSet(
      Arbitrary.arbString.arbitrary,
      Cogen.cogenString.contramap(_.asInstanceOf[String]),
      stringVProp,
    )
  def stringVProp: Any => Prop =
    x => PropF.boolean(x.isInstanceOf[String])
  def stringMProp: Morphism => Prop =
    f => stringVProp(f.f.asInstanceOf[Unit => Any](()))
  def intVProp: Any => Prop =
    value => PropF.boolean(value.isInstanceOf[Int])
  def intMProp: Morphism => Prop =
    f => intVProp(f.f.asInstanceOf[Unit => Any](()))
  def intMPropF: UFormula => PFormula =
    f => e => intMProp(f(e))
  def intLtVProp(x: Int): Any => Prop =
    y => PropF.boolean(y.asInstanceOf[Int] < x)
  def unitMProp: Morphism => Prop =
    f => unitVProp(f.f.asInstanceOf[Unit => Any](()))
  def unitVProp: Any => Prop =
    x => unit.elem(x :: Nil)
}
