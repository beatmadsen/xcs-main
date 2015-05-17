name := "Xcs Main"

version := "1.0"

scalaVersion := "2.11.5"

organization := "com.madsen"

resolvers += Resolver.mavenLocal


libraryDependencies ++= Seq(
  "com.madsen" % "xsc-interop" % "1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "org.scalacheck" %% "scalacheck" % "1.11.5" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.specs2" %% "specs2" % "2.4" % "test",
  // JUnit is used for some Java interop. examples. A driver for JUnit:
  "junit" % "junit-dep" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)

