package com.tylip.quasitype.corefinal

trait CategConcreteAlgebra extends CategAlgebra {

  def mkMorphismFun(f: Any => Any)(implicit
    name: sourcecode.Name,
    args: sourcecode.Args,
  ): Morphism

}
