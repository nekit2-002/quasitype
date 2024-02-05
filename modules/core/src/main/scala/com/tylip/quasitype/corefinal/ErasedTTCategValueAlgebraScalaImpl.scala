package com.tylip.quasitype.corefinal

trait CategValueAlgebraScalaImpl extends CategAlgebra {
  type Value = Any
}

trait CategAlgebraScalaImpl extends CategAlgebra {

  case class Morphism(f: Any => Any)

  def comp(x: Morphism, y: Morphism): Morphism =
    Morphism(x.f.compose(y.f))

  def id(x: Set): Morphism =
    Morphism(identity)

}
