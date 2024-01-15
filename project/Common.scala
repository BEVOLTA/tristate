import Dependencies.Libs
import sbt.*
import sbt.Keys.*

object Common {

  val credentialsVal: Seq[Credentials] = {
    val realm    = "GitHub Package Registry"
    val host     = "maven.pkg.github.com"
    val cred = for {
      username <- scala.util.Try(sys.env("GITHUB_ACTOR")).toOption.orElse(Some("fabienfoerster"))
      password <- scala.util.Try(sys.env("GITHUB_TOKEN")).toOption
    } yield Credentials(realm, host, username, password)
    cred.toList
  }

  println("Credentials",credentialsVal)
  println("Env : ", sys.env)

  val commonSettings: Seq[Setting[?]] = Seq(
    scalaVersion := "3.3.1",
    version := "0.5.0",
    scalacOptions ++=  Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings"),
    Compile / doc / scalacOptions := (Compile / doc / scalacOptions).value.filter(_ != "-Xfatal-warnings"),

    updateOptions := updateOptions.value.withCachedResolution(true),
    resolvers     ++= Dependencies.resolvers,

    libraryDependencies ++= Seq(Libs.scalaCheck)
    ,

    autoAPIMappings := true,

    // Release options
    pomExtra :=
      <url>https://github.com/bevolta/tristate</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>http://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <developers>
          <developer>
            <id>fabienfoerster</id>
            <name>Fabien Foerster</name>
          </developer>
        </developers>,
    publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/bevolta/tristate"),
    credentials ++= credentialsVal,
    publishMavenStyle      := true,
    versionScheme := Some("early-semver"),
    Test / publishArtifact := false,
    pomIncludeRepository := { _ =>
      false
    }

  )

  /* strip test deps from pom */
  import scala.xml.*
  import scala.xml.transform.*
  lazy val pomPostProcessVal: Node => Node = { node: Node =>
    def stripIf(f: Node => Boolean) = new RewriteRule {
      override def transform(n: Node): NodeSeq = if (f(n)) NodeSeq.Empty else n
    }
    val stripTestScope: RewriteRule = stripIf(n => n.label == "dependency" && (n \ "scope").text == "test")
    new RuleTransformer(stripTestScope).transform(node)(0)
  }



}

object TristateProject {
  import Common.*

  def apply(name: String): Project = TristateProject(name, file(name))
  def apply(name: String, file: File): Project =  Project(name, file).settings(commonSettings *)
}
