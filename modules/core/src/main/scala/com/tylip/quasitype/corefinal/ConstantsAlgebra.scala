package com.tylip.quasitype.corefinal

trait ConstantsAlgebra extends LogicAlgebra {
  def intSet: Set
  def predSafeIntSet: Set
  def stringSet: Set
  def stringMProp: UU => Prop
  def stringVProp: PFormula
  def intMProp: UU => Prop
  def intMPropF: UFormula => PFormula
  def intVProp: PFormula
  def intLtVProp(x: Int): PFormula
  def unitMProp: UU => Prop
  def unitVProp: PFormula
}
