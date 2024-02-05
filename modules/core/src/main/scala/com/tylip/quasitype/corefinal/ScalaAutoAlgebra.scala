package com.tylip.quasitype.corefinal

import com.tylip.quasitype.ext._

trait ScalaAutoAlgebra extends CategConcreteAlgebra {

  final case class AutoSpec(morphism: Morphism, spec: Spec)

  final class AutoSpecBuilber[A, B](
    value: A => B,
    mustFail: Boolean,
  ) {

    def apply()(implicit
      name: sourcecode.Name,
      args: sourcecode.Args,
      scalaAutoProp: ScalaAutoProp[A => B],
    ): AutoSpec = {
      val morphism = mkMorphismFun(value.compose(_.asInstanceOf[A]))
      AutoSpec(
        morphism,
        spec(
          scalaAutoProp.predicate(morphism),
          nameArgs,
          mustFail,
        ),
      )
    }

  }

//  final case class HackToAvoidEmitErrorAutoMorphism[A, B](f: A => B) {
//    def apply(implicit scalaAutoSetA: ScalaAutoSet[A], scalaAutoSetB: ScalaAutoSet[B]): Morphism =
//      mkMorphismF(
//            ScalaAutoSet[A].set,
//            ScalaAutoSet[B].set,
//            f.asInstanceOf[Any => Any],
//          )
//  }

  trait ScalaAutoSet[T] {
    def set: Set
  }

  def ScalaAutoSet[T](implicit scalaAutoSet: ScalaAutoSet[T]): ScalaAutoSet[T] =
    scalaAutoSet

  def simpleAutoSet[T](setV: Set): ScalaAutoSet[T] =
    new ScalaAutoSet[T] { val set: Set = setV }

  trait ScalaAutoProp[T] {
    def predicate: UU => Prop
  }

  def ScalaAutoProp[T](implicit
    scalaAutoProp: ScalaAutoProp[T],
  ): ScalaAutoProp[T] = scalaAutoProp

  def simpleAutoProp[T](predV: UU => Prop): ScalaAutoProp[T] =
    new ScalaAutoProp[T] {
      def predicate: UU => Prop = predV
    }

  def autoSpec[A, B](
    value: A => B,
    mustFail: Boolean = false,
  )(implicit
    name: sourcecode.Name,
    args: sourcecode.Args,
    scalaAutoProp: ScalaAutoProp[A => B],
  ): AutoSpec = new AutoSpecBuilber(value, mustFail)()

}
