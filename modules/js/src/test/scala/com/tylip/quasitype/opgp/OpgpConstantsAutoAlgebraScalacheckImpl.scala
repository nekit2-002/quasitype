package com.tylip.quasitype.opgp

import cats.effect.IO
import com.tylip.quasitype.scalacheckimpl.CategAlgebraScalacheckImpl
import org.scalacheck.effect.PropF
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen, Gen}
import typings.openpgp
import typings.openpgp.mod.MaybeArray

import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr

trait OpgpConstantsAutoAlgebraScalacheckImpl
    extends CategAlgebraScalacheckImpl
    with OpgpConstantsAutoAlgebra {

  implicit def jsUndefOrCogen[A: Cogen]: Cogen[UndefOr[A]] =
    Cogen[Option[A]].contramap(_.toOption)

  implicit def userIdArbitrary: Arbitrary[openpgp.mod.UserID] =
    Arbitrary(
      Gen
        .zip(
          Arbitrary.arbitrary[Option[String]].map(_.orUndefined),
          Arbitrary.arbitrary[Option[String]].map(_.orUndefined),
          Arbitrary.arbitrary[Option[String]].map(_.orUndefined),
        ).map { case (comment, email, name) =>
          val result = openpgp.mod.UserID()
          comment.foreach(result.setComment)
          email.foreach(result.setEmail)
          name.foreach(result.setName)
          result
        },
    )

  implicit def generateKeyOptionsformatoStrArbitrary
    : Arbitrary[GenerateKeyOptionsformatoStr] =
    Arbitrary(
      Gen.oneOf(
        openpgp.openpgpStrings.armored,
        openpgp.openpgpStrings.`object`,
        openpgp.openpgpStrings.binary,
      ),
    )

  // noinspection MatchToPartialFunction
  implicit def generateKeyOptionsformatoStrCogen
    : Cogen[GenerateKeyOptionsformatoStr] =
    Cogen((f: GenerateKeyOptionsformatoStr) =>
      f match {
        case x if x == openpgp.openpgpStrings.armored  => 0L
        case x if x == openpgp.openpgpStrings.`object` => 1L
        case x if x == openpgp.openpgpStrings.binary   => 2L
      },
    ).contramap(_.asInstanceOf[GenerateKeyOptionsformatoStr])

  implicit def userIdAutoSet: ScalaAutoSet[openpgp.mod.UserID] =
    simpleAutoSet(
      mkSet(
        Arbitrary.arbitrary[openpgp.mod.UserID],
        Cogen((seed: Seed, item: openpgp.mod.UserID) =>
          Cogen.perturbArray(seed, Array(item.comment, item.email, item.name)),
        ).contramap(_.asInstanceOf[openpgp.mod.UserID]),
        value =>
          IO(value.asInstanceOf[openpgp.mod.UserID].comment)
            .as(PropF.passed[IO]),
      ),
    )

  implicit def generateKeyOptionsformatoAutoSet
    : ScalaAutoSet[openpgp.anon.GenerateKeyOptionsformato] =
    simpleAutoSet(
      mkSet(
        Gen
          .zip(
            Arbitrary
              .arbitrary[Option[GenerateKeyOptionsformatoStr]].map(
                _.orUndefined,
              ),
            Arbitrary.arbitrary[Option[Double]].map(_.orUndefined),
            Arbitrary.arbitrary[Option[String]].map(_.orUndefined),
            Arbitrary.arbitrary[Option[Double]].map(_.orUndefined),
//            Gen
//              .nonEmptyListOf(
//                Arbitrary.arbitrary[typings.openpgp.mod.UserID],
//              )
            Arbitrary
              .arbitrary[Array[typings.openpgp.mod.UserID]]
              .map(_.toArray)
              .map[MaybeArray[typings.openpgp.mod.UserID]] { array =>
                if (array.length > 1) array.toJSArray
                else array.headOption.orNull[typings.openpgp.mod.UserID]
              },
          ).map {
            case (format, keyExpirationTime, passphrase, rsaBits, userIDs) =>
              val result = openpgp.anon.GenerateKeyOptionsformato(userIDs)
              format.foreach(result.setFormat)
              keyExpirationTime.foreach(result.setKeyExpirationTime)
              passphrase.foreach(result.setPassphrase)
              rsaBits.foreach(result.setRsaBits)
              result
          },
        Cogen((seed: Seed, item: openpgp.anon.GenerateKeyOptionsformato) =>
          Cogen.perturb(
            seed,
            item.passphrase,
          ),
        ).contramap(_.asInstanceOf[openpgp.anon.GenerateKeyOptionsformato]),
        value =>
          IO(value.asInstanceOf[openpgp.anon.GenerateKeyOptionsformato].config)
            .as(PropF.passed[IO]),
      ),
    )

  implicit def keyPairrevocationCertificAutoSet
    : ScalaAutoSet[openpgp.anon.KeyPairrevocationCertific] =
    simpleAutoSet(
      mkSet(
        Gen.const(()).map(_ => ???),
        Cogen((s, _) => s),
        value =>
          IO(
            value
              .asInstanceOf[openpgp.anon.KeyPairrevocationCertific].privateKey,
          )
            .as(PropF.passed[IO]),
      ),
    )

  implicit def generateKeyOptionsformatoStrAutoSet
    : ScalaAutoSet[GenerateKeyOptionsformatoStr] =
    simpleAutoSet(
      mkSet(
        Arbitrary.arbitrary[GenerateKeyOptionsformatoStr],
        Cogen[GenerateKeyOptionsformatoStr]
          .contramap(_.asInstanceOf[GenerateKeyOptionsformatoStr]),
        {
          case x if x == openpgp.openpgpStrings.armored  => PropF.passed
          case x if x == openpgp.openpgpStrings.`object` => PropF.passed
          case x if x == openpgp.openpgpStrings.binary   => PropF.passed
        },
      ),
    )

}
