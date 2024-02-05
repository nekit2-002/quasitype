package com.tylip.quasitype

package object ext {

  implicit def nameArgs(implicit
    name: sourcecode.Name,
    args: sourcecode.Args,
  ): String =
    s"${name.value}${args.value.map(_.map(_.value).mkString("(", ", ", ")")).mkString("")}"

}
