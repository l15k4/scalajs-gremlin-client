import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import spray.revolver.RevolverPlugin.autoImport._

lazy val gremlinServerHome = settingKey[File]("Filesystem path of gremlin-server standalone build with lib and config subdirectories")
lazy val startGremlinServer = taskKey[Unit]("For scalaJS test suite")

lazy val gremlinServerSettings =
  Revolver.settings ++
    Seq(
      gremlinServerHome := new File("../../graphs/tinkerpop3/gremlin-server/target/gremlin-server-3.0.0-SNAPSHOT-standalone"),
      baseDirectory in reStart := gremlinServerHome.value,
      fullClasspath in reStart ++= Seq(
        gremlinServerHome.value / "lib/*",
        gremlinServerHome.value / "config/*"
      ),
      javaOptions in reStart ++= Seq(
        "-Dlog4j.configuration=file:conf/log4j-server.properties",
        "-Dlog4j.ignoreTCL=true"
      ),
      mainClass in reStart := Option("com.tinkerpop.gremlin.server.GremlinServer"),
      reStartArgs += "conf/gremlin-server-classic.yaml",
      startGremlinServer := reStart.toTask("").value
    )

lazy val js =
  project.in(file("js"))
    .enablePlugins(ScalaJSPlugin)
    .settings(gremlinServerSettings: _*)
    .settings(
      name := "scalajs-gremlin-client",
      organization := "com.pragmaxim",
      version := "0.0.4-SNAPSHOT",
      scalaVersion := "2.12.10",
      scalacOptions ++= Seq(
        "-unchecked", "-deprecation", "-feature", "-Xfatal-warnings",
        "-Xlint", "-Xfuture",
        "-Yinline-warnings", "-Ywarn-adapted-args", "-Ywarn-inaccessible",
        "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Yno-adapted-args"
      ),
      scalaJSStage := FastOptStage,
      testFrameworks += new TestFramework("utest.runner.Framework"),
      requiresDOM := true,
      test in Test := (test in Test).dependsOn(startGremlinServer).dependsOn(compile in Test).value,
      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "0.9.8",
        "com.lihaoyi" %%% "upickle" % "0.9.5",
        "com.pragmaxim" %%% "reactive-websocket-client" % "0.0.4-SNAPSHOT",
        "com.lihaoyi" %%% "utest" % "0.7.1" % "test"
      ),
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      pomExtra :=
        <url>https://github.com/l15k4/scalajs-gremlin-client</url>
          <licenses>
            <license>
              <name>The MIT License (MIT)</name>
              <url>http://opensource.org/licenses/MIT</url>
              <distribution>repo</distribution>
            </license>
          </licenses>
          <scm>
            <url>git@github.com:l15k4/scalajs-gremlin-client.git</url>
            <connection>scm:git:git@github.com:l15k4/scalajs-gremlin-client.git</connection>
          </scm>
          <developers>
            <developer>
              <id>l15k4</id>
              <name>Jakub Liska</name>
              <email>liska.jakub@gmail.com</email>
            </developer>
          </developers>
    )