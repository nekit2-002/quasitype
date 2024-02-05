package com.tylip.quasitype.ext.effectjs

import cats.effect.Async
import cats.effect.kernel.Sync
import cats.syntax.option._
import cats.syntax.flatMap._
import cats.syntax.functor._
import typings.std

import scala.scalajs.js
import scala.scalajs.js.{|, defined, Function1, JavaScriptException, Thenable}

//noinspection LanguageFeature
object JsAsync {

  def fromSjsPromise[F[_]: Async, A](iop: F[js.Promise[A]]): F[A] =
    fromThenable(iop.widen)

  def fromStPromise[F[_]: Async, A](iop: F[std.Promise[A]]): F[A] =
    iop.flatMap { t =>
      Async[F].async[A] { cb =>
        val onFulfilled: Function1[A, Unit | std.PromiseLike[Unit]] =
          (v: A) => cb(Right(v)): Unit | std.PromiseLike[Unit]

        val onRejected: Function1[Any, Unit | std.PromiseLike[Unit]] = {
          (a: Any) =>
            val e = a match {
              case th: Throwable => th
              case _             => JavaScriptException(a)
            }

            cb(Left(e)): Unit | std.PromiseLike[Unit]
        }

        Sync[F]
          .delay(t.`then`[Unit, Unit](onFulfilled, onRejected)).as(
            none,
          )
      }
    }

  def fromThenable[F[_]: Async, A](iot: F[Thenable[A]]): F[A] =
    iot.flatMap { t =>
      Async[F].async[A] { cb =>
        val onFulfilled: Function1[A, Unit | Thenable[Unit]] =
          (v: A) => cb(Right(v)): Unit | Thenable[Unit]

        val onRejected: Function1[Any, Unit | Thenable[Unit]] = { (a: Any) =>
          val e = a match {
            case th: Throwable => th
            case _             => JavaScriptException(a)
          }

          cb(Left(e)): Unit | Thenable[Unit]
        }

        Sync[F].delay(t.`then`[Unit](onFulfilled, defined(onRejected))).as(none)
      }
    }

}
