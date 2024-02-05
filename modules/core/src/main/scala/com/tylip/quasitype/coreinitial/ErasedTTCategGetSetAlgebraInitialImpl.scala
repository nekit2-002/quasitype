package com.tylip.quasitype.coreinitial

import com.tylip.quasitype.corefinal.{
  CategGetSetAlgebra,
  CategPureGetSetAlgebra,
}

trait CategGetSetAlgebraInitialImpl
    extends CategGetSetAlgebra
    with CategAlgebraInitialImpl {
  def getInt: Morphism = GetInt
  def setInt: Morphism = SetInt
}

trait CategPureGetSetAlgebraInitialImpl
    extends CategPureGetSetAlgebra
    with CategAlgebraInitialImpl {
  def getInt: Morphism = GetInt
  def setInt: Morphism = SetInt
}
