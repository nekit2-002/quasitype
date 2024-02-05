package com.tylip.quasitype.examples

import cats.effect.IO
import com.tylip.quasitype.corefinal._

trait UnitGetSet {
  private var x: Int = 0
  def getImpl: Int = x
  def setImpl(v: Int): Unit = x = v
}

trait UnitGetSetScalaManualAlgebra
    extends CategGetSetAlgebra
    with CategAlgebra
    with ConstantsAlgebra
    with UnitGetSet
    with CategConcreteAlgebra {

  def getInt: Morphism =
    mkMorphismFun((_: Any) => getImpl)

  def setInt: Morphism =
    mkMorphismFun(x => setImpl(x.asInstanceOf[Int]))

}

trait UnitPureGetSetScalaManualAlgebra
    extends CategAlgebra
    with IOAlgebra
    with CategPureGetSetAlgebra
    with ConstantsAlgebra
    with UnitGetSet
    with CategConcreteAlgebra {

  def getInt: Morphism =
    mkMorphismFun((_: Any) => IO.delay(getImpl))

  def setInt: Morphism =
    mkMorphismFun(x => IO.delay(setImpl(x.asInstanceOf[Int])))

}

trait UnitGetSetScalaAutoAlgebra
    extends ScalaAutoAlgebra
    with CategAlgebra
    with CategGetSetAlgebra
    with ScalaAutoConstantsAlgebra
    with UnitGetSet
    with CategConcreteAlgebra {

  def value: Set = intSet

  def getIntAS: AutoSpec =
    autoSpec((_: Unit) => getImpl)

  def setIntAS: AutoSpec =
    autoSpec(setImpl)

  override def allSpecs: Seq[Spec] =
//    super.allSpecs :+
    getIntAS.spec :: setIntAS.spec :: Nil
//        :+ autoSpec(setImpl _, "setImpl")

}
