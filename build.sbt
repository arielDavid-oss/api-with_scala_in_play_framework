name := """productos"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

// Dependencias
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.10.5",
)

// Añadir otras dependencias
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.3.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.3.0"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
