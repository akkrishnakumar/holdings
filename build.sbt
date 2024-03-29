val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "holding-consolidator",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.apache.poi" % "poi" % "5.2.3",
      "com.lihaoyi" %% "os-lib" % "0.9.1",

      // Test Dependencies
      "org.scalactic" %% "scalactic" % "3.2.17",
      "org.scalatest" %% "scalatest" % "3.2.17" % "test"
    )
  )
