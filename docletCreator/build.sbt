import AssemblyKeys._

assemblySettings

organization := "pw.kremser.javadocToDoclet"

name := "javadoc-to-doclet"

version := "0.1"

//scalaVersion := "2.10.2"
scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

libraryDependencies ++=
  "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5" ::
  "org.xerial" % "sqlite-jdbc" % "3.6.20" ::
  //"com.h2database" % "h2" % "1.2.140" ::
  Nil
