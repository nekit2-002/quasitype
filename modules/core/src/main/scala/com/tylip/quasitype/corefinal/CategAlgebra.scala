package com.tylip.quasitype.corefinal

import cats.syntax.apply._
import com.tylip.quasitype.ext._

trait BindingAlgebra {
  type UU
  type UFormula
  def quote(m: UU): UFormula
  def pop(n: Int): UFormula
}

trait LogicAlgebra extends BindingAlgebra {

  type Set
  type Prop
  type PFormula

  def ground(predicate: PFormula, env: List[Any] = Nil): Prop

  def elem(s: Set)(x: UU): Prop
  def elemF(s: Set)(x: UFormula): PFormula
  def eql(s: Set)(x: UU, y: UU): Prop
  def eqlF(s: Set)(x: UFormula, y: UFormula): PFormula

  def forall(dom: Set, j: PFormula): PFormula

  def eqReflSpec(s: Set): Prop =
    ground(forall(s, eqlF(s)(pop(0), pop(0))))

}

trait SpecSet extends LogicAlgebra {
  type Spec
  final case class NamedSet(name: String, set: Set) {
    override def toString: String = name
  }
  def specSC(
    prop: Prop,
    mustFail: Boolean = false,
  )(implicit name: sourcecode.Name, args: sourcecode.Args): Spec =
    spec(prop, nameArgs, mustFail)
  def spec(
    prop: Prop,
    name: String,
    mustFail: Boolean = false,
  ): Spec
  def allSetsNamed: List[NamedSet] = Nil
  def allSets: List[Set] = allSetsNamed.map(_.set)
  def allSpecs: Seq[Spec]
}

trait CategAlgebra extends LogicAlgebra with SpecSet {

  type Obj = Set

  type Morphism

  type UU = Morphism

  def hom(a: Obj, b: Obj): Set

  def comp(x: Morphism, y: Morphism): Morphism
  def compF(x: UFormula, y: UFormula): UFormula

  def id(x: Obj): Morphism

  def compAssSpec(a: Obj, b: Obj, c: Obj, d: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(a, b),
          forall(
            hom(b, c),
            forall(
              hom(c, d),
              eqlF(hom(a, d))(
                compF(pop(0), compF(pop(1), pop(2))),
                compF(compF(pop(0), pop(1)), pop(2)),
              ),
            ),
          ),
        ),
      ),
    )

  def idLSpec(a: Obj, b: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(a, b),
          eqlF(hom(a, b))(compF(quote(id(b)), pop(0)), pop(0)),
        ),
      ),
    )

  def idRSpec(a: Obj, b: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(a, b),
          eqlF(hom(a, b))(compF(quote(id(b)), pop(0)), pop(0)),
        ),
      ),
    )

  def unit: Obj

  def unitSpec(o: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(o, unit),
          forall(hom(o, unit), eqlF(hom(o, unit))(pop(1), pop(0))),
        ),
      ),
    )

  def product(a: Set, b: Set): Set

  def paired(f: Morphism, g: Morphism): Morphism
  def pairedF(f: UFormula, g: UFormula): UFormula

  def pi1: Morphism

  def pi1Spec(x: Set, a: Set, b: Set): Spec =
    specSC(
      ground(
        forall(
          hom(x, a),
          forall(
            hom(x, b),
            eqlF(hom(x, a))(compF(quote(pi1), pairedF(pop(1), pop(0))), pop(1)),
          ),
        ),
      ),
    )

  def pi2: Morphism

  def pi2Spec(x: Set, a: Set, b: Set): Spec =
    specSC(
      ground(
        forall(
          hom(x, a),
          forall(
            hom(x, b),
            eqlF(hom(x, a))(compF(quote(pi2), pairedF(pop(1), pop(0))), pop(0)),
          ),
        ),
      ),
    )

  override def allSetsNamed: List[NamedSet] =
    super.allSetsNamed :+ NamedSet("unit", unit)

  def allSpecs: Seq[Spec] =
    (allSets, allSets, allSets, allSets).mapN(compAssSpec) ++
      allSets.map(unitSpec) ++
      (allSets, allSets).mapN(idLSpec) ++
      (allSets, allSets).mapN(idRSpec) ++
      (allSets, allSets, allSets).mapN(pi1Spec) ++
      (allSets, allSets, allSets).mapN(pi2Spec)

}

trait IOAlgebra extends CategAlgebra {

  def ioO(x: Obj): Obj

  def ioM(m: Morphism): Morphism

  def ioMF(m: UFormula): UFormula

  def etaIo(x: Obj): Morphism

  def muIo(x: Obj): Morphism

  def functorCompIoSpec(a: Obj, b: Obj, c: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(b, c),
          forall(
            hom(a, b),
            eqlF(hom(ioO(a), ioO(c)))(
              ioMF(compF(pop(1), pop(0))),
              compF(ioMF(pop(1)), ioMF(pop(0))),
            ),
          ),
        ),
      ),
    )

  def ntInEtaIo(a: Obj): Spec =
    specSC(elem(hom(a, ioO(a)))(etaIo(a)))

  def ntCompEtaIo(a: Obj, b: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(a, b),
          eqlF(hom(a, ioO(b)))(
            compF(quote(etaIo(b)), pop(0)),
            compF(ioMF(pop(0)), quote(etaIo(a))),
          ),
        ),
      ),
    )

  def ntInMuIo(a: Obj): Spec =
    specSC(elem(hom(ioO(ioO(a)), ioO(a)))(muIo(a)))

  def ntCompMuIo(a: Obj, b: Obj): Spec =
    specSC(
      ground(
        forall(
          hom(a, b),
          eqlF(hom(ioO(ioO(a)), ioO(b)))(
            compF(quote(muIo(b)), ioMF(ioMF(pop(0)))),
            compF(ioMF(pop(0)), quote(muIo(a))),
          ),
        ),
      ),
    )

  def muCompIo(a: Obj): Spec =
    specSC(
      eql(hom(ioO(ioO(ioO(a))), ioO(a)))(
        comp(muIo(a), ioM(muIo(a))),
        comp(muIo(a), muIo(ioO(a))),
      ),
    )

  def muEtaCompRIo(a: Obj): Spec =
    specSC(eql(hom(ioO(a), ioO(a)))(comp(muIo(a), ioM(etaIo(a))), id(ioO(a))))

  def muEtaCompLIo(a: Obj): Spec =
    specSC(eql(hom(ioO(a), ioO(a)))(comp(muIo(a), etaIo(ioO(a))), id(ioO(a))))

  override def allSpecs: Seq[Spec] = super.allSpecs ++
    (allSets, allSets, allSets).mapN(functorCompIoSpec) ++
    allSets.map(ntInEtaIo) ++
    (allSets, allSets).mapN(ntCompEtaIo) ++
    allSets.map(ntInMuIo) ++
    (allSets, allSets).mapN(ntCompMuIo) ++
    allSets.map(muCompIo) ++
    allSets.map(muEtaCompRIo) ++
    allSets.map(muEtaCompLIo)

}

trait CategGetSetAlgebra extends CategAlgebra with ConstantsAlgebra {

  def getInt: Morphism
  def setInt: Morphism

  def setgetSpec: Spec =
    specSC(eql(hom(intSet, intSet))(comp(getInt, setInt), id(intSet)))

  def setgetPurenessSpec: Spec =
    specSC(
      ground(
        forall(
          hom(unit, intSet),
          eqlF(intSet)(
            quote(getInt),
            compF(quote(getInt), compF(quote(setInt), pop(0))),
          ),
        ),
      ),
      true,
    )

  def getIntSpec: Spec =
    specSC(elem(hom(unit, intSet))(getInt))

  def setIntSpec: Spec =
    specSC(elem(hom(intSet, unit))(setInt))

  override def allSpecs: Seq[Spec] =
    super.allSpecs :+
      getIntSpec :+
      setIntSpec :+
      setgetSpec :+
      setgetPurenessSpec

}

trait CategPureGetSetAlgebra extends IOAlgebra with ConstantsAlgebra {

  def getInt: Morphism
  def setInt: Morphism

  def setgetSpec: Spec =
    specSC(
      eql(hom(intSet, ioO(intSet)))(
        comp(muIo(intSet), comp(ioM(getInt), setInt)),
        etaIo(intSet),
      ),
    )

  def getIntSpec: Spec =
    specSC(elem(hom(unit, ioO(intSet)))(getInt))

  def setIntSpec: Spec =
    specSC(elem(hom(intSet, ioO(unit)))(setInt))

  override def allSpecs: Seq[Spec] =
    super.allSpecs :+
      getIntSpec :+
      setIntSpec :+
      setgetSpec

}

trait CategNatAlgebra extends CategAlgebra with SpecSet {

  def nat: Obj

  def succ: Morphism

  /**
   * просто некий тест для проверки
   */
  def natsuccid: Spec =
    specSC(eql(hom(nat, nat))(succ, succ))

  override def allSetsNamed: List[NamedSet] =
    super.allSetsNamed :+ NamedSet("nat", nat)

  override def allSpecs: Seq[Spec] =
    super.allSpecs :+ natsuccid

}
