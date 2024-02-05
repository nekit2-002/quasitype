package com.tylip.quasitype.scalacheckimpl

import com.tylip.quasitype.corefinal.ParseInitial
import com.tylip.quasitype.coreinitial
import com.tylip.quasitype.coreinitial._
import org.scalacheck.{Arbitrary, Gen, Prop}
import org.specs2.{ScalaCheck, Specification}

//noinspection ScalaFileName
object Roundtripper
    extends ParseInitial
    with CategAlgebraInitialImpl
    with ConstantsAlgebraInitialImpl
    with CategGetSetAlgebraInitialImpl
    with SpecSetInitialImpl

class RoundtripSpec extends Specification with ScalaCheck {

  def is =
    s2"""
      Initial and final shoud
        $have_set_isomorphism
        $have_prop_isomorphism
        $have_morphism_isomorphism
      """

  private def have_set_isomorphism = {
    import org.scalacheck.ScalacheckShapeless._
    Prop.forAll((item: coreinitial.Set) => Roundtripper.parse(item) == item)
  }

  private def have_prop_isomorphism = {
    import org.scalacheck.ScalacheckShapeless._
    implicit val arbAny: Arbitrary[Any] =
      Arbitrary(Gen.chooseNum[Int](-100, 100).map(x => x: Any))
    Prop.forAll((item: coreinitial.Prop) => Roundtripper.parse(item) == item)
  }

  private def have_morphism_isomorphism = {
    import org.scalacheck.ScalacheckShapeless._
    Prop.forAll((item: coreinitial.Morphism) =>
      Roundtripper.parse(item) == item,
    )
  }

}
