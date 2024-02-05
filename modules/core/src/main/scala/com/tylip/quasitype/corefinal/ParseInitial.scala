package com.tylip.quasitype.corefinal

import com.tylip.quasitype.coreinitial

trait ParseInitial
    extends CategAlgebra
    with ConstantsAlgebra
    with CategGetSetAlgebra {

  def parse(set: coreinitial.Set): Set =
    set match {
      case coreinitial.UnitSet =>
        unit
      case coreinitial.IntSet =>
        intSet
      case coreinitial.StringSet =>
        stringSet
      case coreinitial.PredSafeIntSet =>
        predSafeIntSet
      case coreinitial.Product(a, b) =>
        product(parse(a), parse(b))
      case coreinitial.Hom(dom, cod) =>
        hom(parse(dom), parse(cod))
    }

  def parse(prop: coreinitial.Prop): Prop =
    prop match {
      case coreinitial.Ground(predicate, env) =>
        ground(
          parse(predicate),
          env.map {
            case m: coreinitial.Morphism => parse(m)
            case other                   => other
          },
        )
      case coreinitial.Eql(s, x, y) =>
        eql(parse(s))(parse(x), parse(y))
      case coreinitial.Elem(x, y) =>
        elem(parse(x))(parse(y))
      case coreinitial.StringMProp(m) =>
        stringMProp(parse(m))
      case coreinitial.IntMProp(m) =>
        intMProp(parse(m))
      case coreinitial.UnitMProp(m) =>
        unitMProp(parse(m))
    }

  def parse(morphism: coreinitial.Morphism): Morphism =
    morphism match {
      case coreinitial.Id(dom) =>
        id(parse(dom))
      case coreinitial.Comp(f, g) =>
        comp(parse(f), parse(g))
      case coreinitial.Paired(f, g) =>
        paired(parse(f), parse(g))
      case coreinitial.Pi1 =>
        pi1
      case coreinitial.Pi2 =>
        pi2
      case coreinitial.GetInt =>
        getInt
      case coreinitial.SetInt =>
        setInt
    }

  def parse(depMorphism: coreinitial.UFormula): UFormula =
    depMorphism match {
      case coreinitial.Quote(m) =>
        quote(parse(m))
      case coreinitial.Pop(n) =>
        pop(n)
      case coreinitial.CompF(f, g) =>
        compF(parse(f), parse(g))
      case coreinitial.PairedF(f, g) =>
        pairedF(parse(f), parse(g))
    }

  def parse(predicate: coreinitial.PFormula): PFormula =
    predicate match {
      case coreinitial.ElemF(x, y) =>
        elemF(parse(x))(parse(y))
      case coreinitial.EqlF(s, x, y) =>
        eqlF(parse(s))(parse(x), parse(y))
      case coreinitial.Forall(dom, j) =>
        forall(parse(dom), parse(j))
      case coreinitial.IntLtProp(x) =>
        intLtVProp(x)
      case coreinitial.StringVProp =>
        stringVProp
      case coreinitial.IntVProp =>
        intVProp
      case coreinitial.UnitVProp =>
        unitVProp
    }

}
