package com.tylip.quasitype.coreinitial

sealed trait Morphism { def name: String }
sealed trait Prop
sealed trait Set
sealed trait UFormula
sealed trait PFormula

final case class Id(dom: Set) extends Morphism {
  def name: String = s"id($dom)"
}

final case class Comp(f: Morphism, g: Morphism) extends Morphism {
  def name: String = s"comp($f, $g)"
}

final case class CompF(f: UFormula, g: UFormula) extends UFormula {
  def name: String = s"compF($f, $g)"
}

final case class Paired(f: Morphism, g: Morphism) extends Morphism {
  def name: String = s"paired($f, $g)"
}

final case class PairedF(f: UFormula, g: UFormula) extends UFormula {
  def name: String = s"pairedF($f, $g)"
}

case object Pi1 extends Morphism { val name = "pi1" }
case object Pi2 extends Morphism { val name = "pi2" }

final case class Spec(
  name: String,
  prop: Prop,
  mustFail: Boolean = false,
)

final case class Forall(dom: Set, j: PFormula) extends PFormula

final case class Product(a: Set, b: Set) extends Set

final case class Hom(dom: Set, cod: Set) extends Set

final case class Quote(m: Morphism) extends UFormula
final case class Ground(predicate: PFormula, env: List[Any]) extends Prop
final case class Pop(n: Int) extends UFormula

final case class Eql(s: Set, x: Morphism, y: Morphism) extends Prop
final case class EqlF(s: Set, x: UFormula, y: UFormula) extends PFormula

final case class Elem(x: Set, y: Morphism) extends Prop

final case class ElemF(x: Set, y: UFormula) extends PFormula

case object UnitSet extends Set
case object IntSet extends Set
case object StringSet extends Set
case object PredSafeIntSet extends Set
case class IntLtProp(x: Int) extends PFormula
case class StringMProp(morphism: Morphism) extends Prop
case object StringVProp extends PFormula
case class IntMProp(morphism: Morphism) extends Prop
case object IntVProp extends PFormula
case class UnitMProp(morphism: Morphism) extends Prop
case object UnitVProp extends PFormula

case object GetInt extends Morphism {
  def dom: Set = UnitSet
  def cod: Set = IntSet
  override def name: String = "getInt"
}

case object SetInt extends Morphism {
  def dom: Set = UnitSet
  def cod: Set = IntSet
  override def name: String = "getInt"
}
