lazy val root = (
  project in file(".")
).settings(
  name := "hello-okhttp",
  version := "1.0",
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "com.squareup.okhttp3" % "okhttp" % "3.2.0",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test")
)
