import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin.Revolver
import spray.revolver.RevolverPlugin.Revolver._

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.PhantomJSEnv

object Build extends sbt.Build {

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
      .settings(
        name := "scalajs-gremlin-client",
        organization := "com.viagraphs",
        version := "0.0.2-SNAPSHOT",
        scalaVersion := "2.11.4"
      )
      .settings(scalaJSStage := FastOptStage)
      .settings(testFrameworks += new TestFramework("utest.runner.Framework"))
      .settings(requiresDOM := true)
      .settings(gremlinServerSettings: _*)
      .settings(test in Test := (test in Test).dependsOn(startGremlinServer).dependsOn(compile in Test).value)
      .settings(
        libraryDependencies ++= Seq(
          "org.scala-js" %%% "scalajs-dom" % "0.7.1-SNAPSHOT",
          "com.lihaoyi" %%% "upickle" % "0.2.6-M3",
          "com.viagraphs.reactive-websocket" %%% "client" % "0.0.2-SNAPSHOT",
          "com.lihaoyi" %%% "utest" % "0.2.5-M3-SNAPSHOT" % "test"
        )
      )
}
