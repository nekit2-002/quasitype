val specs2Version = "4.20.4"

Global / stRemoteCache := RemoteCache.S3(
  bucket = sys.env.get("ST_CACHE_BUCKET").filter(_.nonEmpty)
    .getOrElse("st-cache-stage"),
  pull = new URI(
    sys.env.get("ST_CACHE_URL").filter(_.nonEmpty)
      .getOrElse("https://st-cache-stage.storage.yandexcloud.net/scalablytyped"),
  )
).withEndpoint(sys.env.get("ST_CACHE_ENDPOINT").filter(_.nonEmpty)
  .getOrElse("https://storage.yandexcloud.net"))
  .withStaticCredentials(
    sys.env.get("ST_CACHE_ACCESS_KEY").filter(_.nonEmpty)
      .getOrElse("WbWJfOUeGMsfu8BXwXyf"),
    sys.env.get("ST_CACHE_SECRET_KEY").filter(_.nonEmpty)
      .getOrElse("Vk4YgGqWe-ttUdGae5mcSrTVjGQgCUOm6K4JETwv"),
  )
  .withRegion("eu-central-1")
  .withPrefix("scalablytyped")

val commonSettings = Seq(
  scalaVersion := "2.13.12",
  Test/testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-verbosity", "2"),
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Ywarn-dead-code", // Warn when dead code is identified.
  ),
  Test/scalacOptions ++= Seq("-Yrangepos"),
  scalafmtOnCompile := !insideCI.value,
)

val coreModule =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/core"))
    .settings(commonSettings)
    .settings(
      name := "quasitype-core",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-effect" % "3.5.2",
        "com.lihaoyi" %%% "sourcecode" % "0.3.1",
      ),
    )

val testingModule =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/testing"))
    .settings(commonSettings)
    .settings(
      name := "quasitype-testing",
      libraryDependencies ++= Seq(
        "org.specs2" %%% "specs2-core" % specs2Version,
        "org.specs2" %%% "specs2-scalacheck" % specs2Version,
        "org.typelevel" %%% "cats-effect-testing-specs2" % "1.5.0",
        "org.typelevel" %%% "cats-effect-testkit" % "3.5.2",
        "com.github.alexarchambault" %%% "scalacheck-shapeless_1.16" % "1.3.1",
        "org.typelevel" %%% "scalacheck-effect" % "1.0.4",
        "org.typelevel" %%% "scalacheck-effect-munit" % "1.0.4",
        "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7",
      ),
    )
    .dependsOn(coreModule)

val examplesModule =
  project
    .in(file("modules/examples"))
    .settings(commonSettings)
    .settings(
      name := "quasitype-examples",
    )
    .dependsOn(coreModule.jvm, testingModule.jvm % "test->compile")

val jsModule =
  project.in(file("modules/js"))
    .settings(commonSettings)
    .settings(
      name := "quasitype-js",
      scalaJSUseMainModuleInitializer := true,
      Compile / npmDependencies ++= Seq(
        "openpgp" -> "5.5.0",
      ),
      Compile / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      useYarn := true,
    )
    .enablePlugins(
      ScalaJSPlugin, ScalaJSBundlerPlugin,
      ScalablyTypedConverterPlugin,
    )
    .dependsOn(coreModule.js, testingModule.js % "test->compile")
