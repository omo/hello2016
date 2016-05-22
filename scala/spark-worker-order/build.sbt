name := "Simple Project"
version := "1.0"
scalaVersion := "2.11.7"
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1"  % "provided"
assemblyJarName in assembly := "Hello.jar"
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)