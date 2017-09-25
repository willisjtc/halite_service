name := "halite_service"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies += "io.vertx" % "vertx-web-scala_2.12" % "3.4.2"

val stage = taskKey[Unit]("Stage task")

val Stage = config("stage")

stage := {
  (update in Stage).value.allFiles.foreach { f =>
    if (f.getName.matches("webapp-runner-[0-9\\.]+.jar")) {
      println("copying " + f.getName)
      IO.copyFile(f, baseDirectory.value / "target" / "webapp-runner.jar")
    }
  }
}
