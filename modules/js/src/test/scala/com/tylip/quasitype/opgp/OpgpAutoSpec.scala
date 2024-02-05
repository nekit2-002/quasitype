package com.tylip.quasitype.opgp

import com.tylip.quasitype.scalacheckimpl._

class OpgpAutoSpec
    extends SpecSetMunitImpl
    with OpgpAutoAlgebra
    with ConstantsAlgebraScalacheckImpl
    with OpgpConstantsAutoAlgebraScalacheckImpl
    with IOAlgebraScalacheckImpl {

  testAll()

}
