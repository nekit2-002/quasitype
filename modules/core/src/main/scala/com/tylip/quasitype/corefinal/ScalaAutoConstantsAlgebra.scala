package com.tylip.quasitype.corefinal

import cats.effect.IO

trait ScalaAutoConstantsAlgebra extends ScalaAutoAlgebra with ConstantsAlgebra {

  final class HackToAvoidEmitErrorFunction1AutoProp[A, B] {
    def apply(implicit
      aScalaAutoSet: ScalaAutoSet[A],
      bScalaAutoProp: ScalaAutoSet[B],
    ): ScalaAutoProp[A => B] =
      simpleAutoProp(
        elem(hom(ScalaAutoSet[A].set, ScalaAutoSet[B].set)),
      )
  }

  implicit val unitAutoSet: ScalaAutoSet[Unit] =
    simpleAutoSet(unit)
  implicit val intAutoSet: ScalaAutoSet[Int] =
    simpleAutoSet(intSet)
  implicit val stringAutoSet: ScalaAutoSet[String] =
    simpleAutoSet(stringSet)
  implicit val stringAutoProp: ScalaAutoProp[String] =
    simpleAutoProp(stringMProp)
  implicit val intAutoProp: ScalaAutoProp[Int] =
    simpleAutoProp(intMProp)
  implicit val unitAutoProp: ScalaAutoProp[Unit] =
    simpleAutoProp(unitMProp)
  implicit def productAutoSet[A: ScalaAutoSet, B: ScalaAutoSet]
    : ScalaAutoSet[(A, B)] =
    simpleAutoSet(product(ScalaAutoSet[A].set, ScalaAutoSet[B].set))
  implicit def function1AutoProp[A: ScalaAutoSet, B: ScalaAutoSet]
    : ScalaAutoProp[A => B] =
    new HackToAvoidEmitErrorFunction1AutoProp[A, B]().apply

}

trait ScalaAutoIOAlgebra extends ScalaAutoAlgebra with IOAlgebra {
  implicit def ioAutoSet[A: ScalaAutoSet]: ScalaAutoSet[IO[A]] =
    simpleAutoSet(ioO(ScalaAutoSet[A].set))
}
