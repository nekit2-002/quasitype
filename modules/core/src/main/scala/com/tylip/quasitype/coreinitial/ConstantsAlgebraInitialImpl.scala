package com.tylip.quasitype.coreinitial

import com.tylip.quasitype.corefinal.ConstantsAlgebra

trait ConstantsAlgebraInitialImpl
    extends ConstantsAlgebra
    with LogicAlgebraInitialImpl {
  def intSet: Set = IntSet
  def stringSet: Set = StringSet
  def predSafeIntSet: Set = PredSafeIntSet
  def intLtVProp(x: Int): PFormula = IntLtProp(x)
  def stringMProp: UU => Prop = StringMProp.apply
  def stringVProp: PFormula = StringVProp
  def intMProp: UU => Prop = IntMProp.apply
  def intMPropF: UFormula => PFormula = ???
  def intVProp: PFormula = IntVProp
  def unitMProp: UU => Prop = UnitMProp.apply
  def unitVProp: PFormula = UnitVProp
}
