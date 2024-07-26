name := "interview-project"

version      := "1.0"
organization := "ru.beeline"
scalaVersion := "2.13.10"
scalacOptions := Seq(
  "-explaintypes",
  "-language:higherKinds",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-encoding",
  "utf8",
  "-Xlint",
  "-Xlint:-byname-implicit",
  "-Xlint:-missing-interpolator"
)

mainClass / run := Some("Main")

val zioVersion     = "2.0.16"
val zioConfVersion = "4.0.0-RC16"
val http4sVersion  = "0.23.23"
val circeVersion   = "0.14.5"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                  % zioVersion,
  "dev.zio"       %% "zio-managed"          % zioVersion,
  "dev.zio"       %% "zio-streams"          % zioVersion,
  "dev.zio"       %% "zio-interop-cats"     % "23.0.0.8",
  "dev.zio"       %% "zio-test"             % zioVersion % Test,
  "dev.zio"       %% "zio-test-sbt"         % zioVersion % Test,
  "dev.zio"       %% "zio-test-magnolia"    % zioVersion % Test,
  "dev.zio"       %% "zio-config-typesafe"  % zioConfVersion,
  "dev.zio"       %% "zio-config-magnolia"  % zioConfVersion,
  "ch.qos.logback" % "logback-classic"      % "1.4.11",
  "dev.zio"       %% "zio-logging-slf4j"    % "2.1.4",
  "org.http4s"    %% "http4s-core"          % http4sVersion,
  "org.http4s"    %% "http4s-circe"         % http4sVersion,
  "org.http4s"    %% "http4s-dsl"           % http4sVersion,
  "org.http4s"    %% "http4s-ember-server"  % http4sVersion,
  "org.http4s"    %% "http4s-ember-client"  % http4sVersion,
  "io.circe"      %% "circe-core"           % circeVersion,
  "io.circe"      %% "circe-generic"        % circeVersion,
  "io.circe"      %% "circe-parser"         % circeVersion,
  "io.circe"      %% "circe-generic-extras" % "0.14.3",
  "io.getquill"   %% "quill-jdbc-zio"       % "4.8.0",
  "com.h2database" % "h2"                   % "2.1.214",
  "org.flywaydb"   % "flyway-core"          % "9.14.0"
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

Test / fork              := true
Test / parallelExecution := false
