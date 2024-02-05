package com.tylip.quasitype.examples

import com.tylip.quasitype.corefinal.{CategGetSetAlgebra, ParseInitial}
import com.tylip.quasitype.coreinitial._
import com.tylip.quasitype.scalacheckimpl._
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}

//noinspection ScalaFileName
object GetSetSpecifier
    extends CategGetSetAlgebra
    with SpecSetInitialImpl
    with CategGetSetAlgebraInitialImpl
    with CategAlgebraInitialImpl
    with ConstantsAlgebraInitialImpl

class GetSetToPropsRunSpec extends CatsEffectSuite with ScalaCheckEffectSuite {

  object ParserCheckerUnitGetSetScalaManual
      extends SpecSetMunitImpl
      with ParseInitial
      with CategAlgebraScalacheckImpl
      with ConstantsAlgebraScalacheckImpl
      with UnitGetSetScalaManualAlgebra

  private def testSpec(s: ParserCheckerUnitGetSetScalaManual.Spec): Unit =
    if (s.mustFail) test(s.name.fail)(s.prop)
    else test(s.name)(s.prop)

  GetSetToProps(GetSetSpecifier.allSpecs)
    .map(s =>
      ParserCheckerUnitGetSetScalaManual.Spec(
        s.name,
        ParserCheckerUnitGetSetScalaManual.parse(s.prop),
        s.mustFail,
      ),
    )
    .foreach(testSpec)

}
