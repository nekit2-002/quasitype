package com.tylip.quasitype.examples

import com.tylip.quasitype.scalacheckimpl._
class GetSetManualSpec
    extends SpecSetMunitImpl
    with UnitGetSetScalaManualAlgebra
    with ConstantsAlgebraScalacheckImpl {
  testAll()
}

class PureGetSetManualSpec
    extends SpecSetMunitImpl
    with IOAlgebraScalacheckImpl
    with UnitPureGetSetScalaManualAlgebra
    with ConstantsAlgebraScalacheckImpl {
  testAll()
}

//object GetSetTTAutoSpec extends Specification with CategGetSetAlgebra
//    with SpecSetScalacheckImpl
//    with UnitGetSetScalaAutoAlgebra
//    with ConstantsAlgebraScalacheckImpl
