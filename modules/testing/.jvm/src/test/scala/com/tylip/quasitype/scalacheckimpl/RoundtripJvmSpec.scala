package com.tylip.quasitype.scalacheckimpl

import com.tylip.quasitype.coreinitial
import org.scalacheck.Prop
import org.specs2.{ScalaCheck, Specification}

class RoundtripJvmSpec extends Specification with ScalaCheck {

  // по какой-то причине перенесенные сюда спецификации не работают в JS

  def is =
    s2"""
      Initial and final shoud
        $have_uFormula_isomorphism
        $have_pFormula_isomorphism
      """

  private def have_uFormula_isomorphism = {
    import org.scalacheck.ScalacheckShapeless._
    Prop.forAll((item: coreinitial.UFormula) =>
      Roundtripper.parse(item) == item,
    )
  }

  private def have_pFormula_isomorphism = {
    import org.scalacheck.ScalacheckShapeless._
    Prop.forAll((item: coreinitial.PFormula) =>
      Roundtripper.parse(item) == item,
    )
  }

}
