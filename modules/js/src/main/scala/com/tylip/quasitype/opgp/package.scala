package com.tylip.quasitype

import typings.openpgp

import scala.scalajs.js.|

package object opgp {
  type GenerateKeyOptionsformatoStr =
    openpgp.openpgpStrings.armored |
      openpgp.openpgpStrings.`object` |
      openpgp.openpgpStrings.binary
}
