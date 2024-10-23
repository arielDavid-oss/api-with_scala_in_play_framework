
version := "1.0-SNAPSHOT"


scalaVersion := "2.13.14"
enablePlugins(JavaAppPackaging)
// Dependencias principales
libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  "com.typesafe.play" %% "play-json" % "2.10.5",
  "com.typesafe.play" %% "play-slick" % "5.3.0",
  "org.postgresql" % "postgresql" % "42.7.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.3.0",
  "org.mindrot" % "jbcrypt" % "0.4"
)

// Versión de librerías
val jacksonVersion = "2.17.0"
val swaggerVersion = "2.2.21"
val gatlingVersion = "3.11.3"

// Dependencias Swagger
lazy val swaggerDependencies = Seq(
  "jakarta.ws.rs" % "jakarta.ws.rs-api" % "4.0.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "io.swagger.core.v3" % "swagger-jaxrs2-jakarta" % swaggerVersion
)

libraryDependencies ++= swaggerDependencies

// Configuración común
lazy val commonSettings = Seq(
  resolvers += "Nexus Releases" at "https://nexus-ci.qrsof.com/repository/maven-public",
  credentials += Credentials("Sonatype Nexus Repository Manager",
   
  libraryDependencies ++= commonDependencies,
  coverageExcludedPackages := ".*Module.*;.*Module",
  scalacOptions ++= Seq(
    "-Wunused",
    "-Wdead-code",
    "-Xlint",
    "-feature",
    "-deprecation", // Mostrar advertencias sobre clases obsoletas
    "-unchecked"    // Habilitar advertencias más estrictas sobre operaciones no comprobadas
  ),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
  Test / testOptions ++= Seq(
    Tests.Argument(TestFrameworks.ScalaTest, "-o"),
    Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
    Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports")
  )
)

lazy val commonDependencies = Seq(
  "com.qrsof.core.api" %% "swagger" % "1.0.0-01-SNAPSHOT"
)


lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "productos-app",
    commonSettings,
    libraryDependencies ++= Seq(
      guice,
      "net.codingwell" %% "scala-guice" % "6.0.0",
      "org.joda" % "joda-convert" % "2.2.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.4",
      "io.lemonlabs" %% "scala-uri" % "4.0.3",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
      "ch.qos.logback" % "logback-classic" % "1.5.6"
    )
  )
