package com.tylip.quasitype.scalacheckimpl

import cats.Id
import cats.effect.IO
import cats.syntax.apply._
import cats.syntax.functor._
import com.tylip.quasitype.corefinal._
import munit.{CatsEffectSuite, ScalaCheckEffectSuite, TestOptions}
import org.scalacheck.effect.PropF
import com.tylip.quasitype.ext._
import org.scalacheck.{Arbitrary, Cogen, Gen}

import scala.concurrent.duration._

trait LogicAlgebraScalacheckImpl extends LogicAlgebra {

  type Prop = PropF[IO]

  type PFormula = List[Any] => Prop

  type UFormula = List[Any] => UU

  trait Set {
    def gen: Gen[Any]
    def cogen: Cogen[Any]
    def elem(m: Any): Prop
    def eq(x: Any, y: Any): Prop
  }

  def eql(s: Set)(x: UU, y: UU): PropF[IO] =
    s.eq(x, y)

  def eqlF(s: Set)(
    x: List[Any] => UU,
    y: List[Any] => UU,
  ): List[Any] => PropF[IO] = e => eql(s)(x(e), y(e))

}

trait CategUnitAlgebra extends CategAlgebra {}

trait CategAlgebraScalacheckImpl
    extends LogicAlgebraScalacheckImpl
    with CategAlgebra
    with CategConcreteAlgebra {

  def mkSet(
    genV: Gen[Any],
    cogenV: Cogen[Any],
    elemV: Any => Prop,
    eqV: (Any, Any) => Prop = { (x, y) =>
      val result = x == y
      if (!result) println(s"!!!! neq: x = $x, y = $y")
      PropF.boolean(result)
    },
  )(implicit
    name: sourcecode.Name,
    args: sourcecode.Args,
  ): Set =
    new Set {
      val gen: Gen[Any] = genV
      def cogen: Cogen[Any] = cogenV
      def elem(m: Any): Prop = elemV(m)
      def eq(x: Any, y: Any): Prop = eqV(x, y)
      override def toString: String = nameArgs
    }

  def forall(a: Set, j: PFormula): PFormula =
    env => PropF.forAllF(a.gen)(elem => j(elem :: env))

  case class Morphism(f: Any => Any, name: String) {
    override def toString: String = name
  }

  def mkMorphismFun(f: Any => Any)(implicit
    name: sourcecode.Name,
    args: sourcecode.Args,
  ): Morphism = Morphism(f, nameArgs)

  def hom(a: Obj, b: Obj): Set =
    mkSet(
      Gen.function1(b.gen)(a.cogen).map(mkMorphismFun),
      Cogen
        .function1(Arbitrary(a.gen), b.cogen)
        .contramap(_.asInstanceOf[Morphism].f),
      f => PropF.forAllF(a.gen)(x => b.elem(f.asInstanceOf[Morphism].f(x))),
      (f, g) =>
        PropF.forAllF(a.gen)(x =>
          b.eq(f.asInstanceOf[Morphism].f(x), g.asInstanceOf[Morphism].f(x)),
        ),
    )

  def comp(x: Morphism, y: Morphism): Morphism =
    mkMorphismFun(x.f.compose(y.f))

  def compF(
    x: List[Any] => Morphism,
    y: List[Any] => Morphism,
  ): List[Any] => Morphism =
    e => comp(x(e), y(e))

  def id(x: Set): Morphism =
    mkMorphismFun(identity)

  def quote(m: Morphism): UFormula =
    _ => m

  def ground(predicate: List[Any] => Prop, env: List[Any]): Prop =
    predicate(env)

  def unit: Obj =
    mkSet(
      Gen.const(()),
      Cogen.cogenUnit.contramap(_.asInstanceOf[Unit]),
      x => PropF.boolean(x.isInstanceOf[Unit]),
    )

  def pop(n: Int): List[Any] => Morphism =
    _(n).asInstanceOf[Morphism]

  def product(a: Set, b: Set): Set =
    mkSet(
      Gen.zip(a.gen, b.gen),
      Cogen.tuple2(a.cogen, b.cogen).contramap(_.asInstanceOf[(Any, Any)]),
      x => PropF.boolean(x.isInstanceOf[(_, _)]),
    )

  def propSet: Set = new Set {
    def gen: Gen[Any] = Gen.const(())
    def cogen: Cogen[Any] = Cogen.cogenUnit.contramap(_.asInstanceOf[Any])
    def elem(x: Any): Prop = PropF.boolean(x.isInstanceOf[Unit])
    def eq(x: Any, y: Any): Prop = PropF.boolean(true)
  }

  def elem(obj: Obj)(x: Morphism): Prop =
    obj.elem(x)

  def elemF(obj: Obj)(x: UFormula): PFormula =
    e => elem(obj)(x(e))

  def internalize(morphism: Morphism): Morphism =
    mkMorphismFun(_ => morphism.f)

  def paired(f: Morphism, g: Morphism): Morphism =
    mkMorphismFun(x => (f.f(x), g.f(x)))

  def pairedF(
    f: List[Any] => Morphism,
    g: List[Any] => Morphism,
  ): List[Any] => Morphism =
    e => paired(f(e), g(e))

  def pi1: Morphism =
    mkMorphismFun(_.asInstanceOf[(Any, Any)]._1)

  def pi2: Morphism =
    mkMorphismFun(_.asInstanceOf[(Any, Any)]._2)

}

//trait CategValueAlgebraScalacheckImpl
//    extends CategAlgebraScalacheckImpl
//    with ScalaTTCategValueAlgebra
//    with CategValueAlgebra {
//
//  def apl(x: Morphism, y: Value): Value = x.f(y)
//
//  def eqlV(x: Value, y: Value): Prop = x == y
//
//  def forallV(a: Set, b: Value => Prop): Prop =
//    Prop.forAll(a.gen)(b)
//
//}

abstract class SpecSetMunitImpl
    extends CatsEffectSuite
    with ScalaCheckEffectSuite
    with CategAlgebraScalacheckImpl
    with SpecSet {

  final case class Spec(
    name: String,
    prop: Prop,
    mustFail: Boolean = false,
  )

  def spec(
    prop: Prop,
    name: String,
    mustFail: Boolean = false,
  ): Spec =
    Spec(name, prop, mustFail)

  def testSpec(s: Spec): Unit = {
    val options =
      Id(new TestOptions(s.name))
        .map(if (s.mustFail) _.fail else identity)
    test(options)(s.prop)
  }

  def testAll(): Unit = allSpecs.foreach(testSpec)

}

trait SpecSetNoopImpl extends SpecSet {

  type Spec = Unit

  def spec(
    prop: Prop,
    name: String,
    mustFail: Boolean = false,
  ): Spec = ()

}

trait CategNatAlgebraScalacheckImpl
    extends CategAlgebraScalacheckImpl
    with CategNatAlgebra {

  def nat: Set =
    mkSet(
      Gen.posNum[Int],
      Cogen.cogenInt.contramap(_.asInstanceOf[Int]),
      x => PropF.boolean(x.isInstanceOf[Int]),
    )

  def succ: Morphism = mkMorphismFun { case x: Int => x + 1 }

}

trait CategIntNatAlgebraScalacheckImpl
    extends CategNatAlgebraScalacheckImpl
    with ConstantsAlgebraScalacheckImpl {

  def intnat: Spec =
    specSC(ground(forall(hom(unit, nat), intMPropF(pop(0)))))

  override def allSpecs: Seq[Spec] =
    super.allSpecs :+ intnat

}

trait IOAlgebraScalacheckImpl
    extends CategNatAlgebraScalacheckImpl
    with IOAlgebra {

  private val timeout = 10.seconds

//  val mutex: Mutex[IO] = Mutex[IO]

  def ioO(a: Obj): Obj = mkSet(
    a.gen.map(IO.pure),
    a.cogen.contramap(value => ???,
//      Await.result(
//        UnsafeRun[IO].unsafeToFuture(
//          TestControl.executeEmbed(value.asInstanceOf[IO[Any]]),
//        ),
//        timeout,
//      ),
    ),
    value => value.asInstanceOf[IO[Any]].map(a.elem),
    (x, y) =>
      (x.asInstanceOf[IO[Any]], y.asInstanceOf[IO[Any]]).tupled
        .map((a.eq(_: Any, _: Any)).tupled),
  )

  def etaIo(x: Obj): Morphism =
    mkMorphismFun((IO.pure(_: Any)).compose(id(x).f))

  def ioM(m: Morphism): Morphism =
    mkMorphismFun(_.asInstanceOf[IO[Any]].map(m.f))

  def ioMF(m: List[Any] => UU): List[Any] => UU = e => ioM(m(e))

  def muIo(x: Obj): Morphism =
    mkMorphismFun(_.asInstanceOf[IO[IO[Any]]].flatten)

}

class Spec
    extends SpecSetMunitImpl
    with CategNatAlgebraScalacheckImpl
    with ConstantsAlgebraScalacheckImpl
    with CategIntNatAlgebraScalacheckImpl {

  testAll()
}
