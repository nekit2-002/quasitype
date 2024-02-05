package com.tylip.quasitype.opgp

import cats.effect.IO
import com.tylip.quasitype.corefinal._
import com.tylip.quasitype.ext.effectjs.JsAsync
import typings.openpgp

trait OpgpConstantsAutoAlgebra extends ScalaAutoConstantsAlgebra {
  implicit def userIdAutoSet: ScalaAutoSet[openpgp.mod.UserID]
  implicit def generateKeyOptionsformatoAutoSet
    : ScalaAutoSet[openpgp.anon.GenerateKeyOptionsformato]
  implicit def keyPairrevocationCertificAutoSet
    : ScalaAutoSet[openpgp.anon.KeyPairrevocationCertific]
  implicit def generateKeyOptionsformatoStrAutoSet
    : ScalaAutoSet[GenerateKeyOptionsformatoStr]
}

trait OpgpAutoAlgebra
    extends ScalaAutoAlgebra
    with CategAlgebra
    with OpgpConstantsAutoAlgebra
    with ScalaAutoIOAlgebra
    with CategConcreteAlgebra
    with IOAlgebra {

  def userIdConsSpec: AutoSpec =
    autoSpec((_: Unit) => openpgp.mod.UserID())

  def userIdSetCommentSpec: AutoSpec =
    autoSpec(((_: openpgp.mod.UserID).setComment(_: String)).tupled)

  def generateKeyOptionsformatoConsSpec: AutoSpec =
    autoSpec((x: openpgp.mod.UserID) =>
      openpgp.anon.GenerateKeyOptionsformato(x),
    )

  def generateKeyOptionsformatoSetPassphraseSpec: AutoSpec =
    autoSpec(
      ((_: openpgp.anon.GenerateKeyOptionsformato)
        .setPassphrase(_: String)).tupled,
    )

  def generateKeyOptionsformatoSetFormatSpec: AutoSpec =
    autoSpec(
      ((_: openpgp.anon.GenerateKeyOptionsformato)
        .setFormat(_: GenerateKeyOptionsformatoStr)).tupled,
    )

  def generateKeySpec: AutoSpec =
    autoSpec(
      (openpgp.mod
        .generateKey(_: openpgp.anon.GenerateKeyOptionsformato))
        .andThen(p => JsAsync.fromSjsPromise(IO(p))),
    )

  override def allSpecs: Seq[Spec] =
    userIdConsSpec.spec ::
      userIdSetCommentSpec.spec ::
      generateKeyOptionsformatoConsSpec.spec ::
      generateKeyOptionsformatoSetPassphraseSpec.spec ::
      generateKeyOptionsformatoSetFormatSpec.spec ::
// fails because of empty user ids and then because of user id format
//      generateKeySpec.spec ::
      Nil

}
