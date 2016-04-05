import Dependencies._

lazy val tristate = (
  TristateProject("tristate", file("."))
    settings(
      packagedArtifacts := Map.empty // don't publish the default aggregate root project
    )
    aggregate(core, play, scalaz)
    dependsOn(core, play, scalaz)
)

lazy val core = (
  TristateProject("tristate-core")
    settings(
      name                 :=  "tristate-core",
      libraryDependencies ++= Seq()
    )
)

lazy val play = (
  TristateProject("tristate-play")
    settings(
      name                := "tristate-play",
      libraryDependencies ++= Seq(
        Libs.playJson,
        Libs.specs2
      )
    )
    dependsOn(core % "compile->compile;test->test")
)

lazy val scalaz = (
  TristateProject("tristate-scalaz")
    settings(
      name                :=  "tristate-scalaz",
      libraryDependencies ++= Seq(
        Libs.scalaz,
        Libs.scalazScalaCheck
      )
    )
    dependsOn(core % "compile->compile;test->test")
)
