package com.tylip.quasitype.coreinitial

import com.tylip.quasitype.corefinal._
import com.tylip.quasitype.coreinitial

trait LogicAlgebraInitialImpl extends LogicAlgebra {

  type Set = coreinitial.Set
  type Prop = coreinitial.Prop
  type PFormula = coreinitial.PFormula
  type UU <: InitialUU
  type UFormula = coreinitial.UFormula

  def forall(dom: Set, j: PFormula): PFormula =
    Forall(dom, j)

  def eql(s: Set)(x: UU, y: UU): Prop =
    Eql(s, x, y)

  def eqlF(s: Set)(x: UFormula, y: UFormula): PFormula =
    EqlF(s, x, y)

  def elem(s: Set)(x: Morphism): Prop =
    Elem(s, x)

  def elemF(s: Set)(x: UFormula): PFormula =
    ElemF(s, x)

}

//noinspection ScalaFileName
trait SpecSetInitialImpl extends LogicAlgebraInitialImpl with SpecSet {
  type Spec = coreinitial.Spec
  def spec(
    prop: Prop,
    name: String,
    mustFail: Boolean = false,
  ): Spec =
    Spec(name, prop, mustFail)
}

trait CategAlgebraInitialImpl
    extends LogicAlgebraInitialImpl
    with CategAlgebra {

  type Morphism = coreinitial.Morphism

  def hom(a: Obj, b: Obj): Set = Hom(a, b)

  def quote(m: Morphism): UFormula = Quote(m)

  def ground(predicate: PFormula, env: List[Any]): Prop =
    Ground(predicate, env)

  def pop(n: Int): UFormula = Pop(n)

  def unit: Obj = UnitSet

  def comp(x: Morphism, y: Morphism): Morphism = Comp(x, y)

  def id(x: Obj): Morphism = Id(x)

  def product(a: Set, b: Set): Set =
    Product(a, b)

  def compF(x: UFormula, y: UFormula): UFormula = CompF(x, y)

  def paired(f: InitialUU, g: InitialUU): InitialUU = Paired(f, g)

  def pairedF(f: UFormula, g: UFormula): UFormula = PairedF(f, g)

  def pi1: InitialUU = Pi1

  def pi2: InitialUU = Pi2

}
