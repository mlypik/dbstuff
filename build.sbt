name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

lazy val elastic4sVersion = "6.2.9"

lazy val akkaVersion = "2.5.12"

libraryDependencies ++= Seq(

  "com.softwaremill.sttp" %% "core" % "1.2.1",
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.2.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,

  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-tcp" % elastic4sVersion,

  "com.sksamuel.elastic4s" %% "elastic4s-streams" % "5.6.6",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",

  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.20",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.5",

  "org.tpolecat" %% "doobie-core"     % "0.5.3",
  "org.tpolecat" %% "doobie-postgres" % "0.5.3",
  "org.tpolecat" %% "doobie-specs2"   % "0.5.3",

  "io.monix" %% "monix" % "3.0.0-RC1",
  "io.monix" %% "monix-eval" % "3.0.0-RC1"
)
