name := "pdfparser"

version := "1.5.5"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
 "org.slf4j" % "slf4j-api" % "1.7.32",
 "org.slf4j" % "slf4j-simple" % "1.7.32",
 "com.itextpdf" % "itextpdf" % "5.5.13.2" ,
 "org.bouncycastle" % "bcprov-jdk15on" % "1.69" ,
 "org.bouncycastle" % "bcmail-jdk15on" % "1.69" ,
 "org.apache.poi" % "poi" % "5.0.0" ,

 "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1",

 "net.sourceforge.htmlunit" % "htmlunit" % "2.56.0" ,

 "org.specs2" %% "specs2-core" % "4.13.1" % "test" ,
 "org.specs2" %% "specs2-junit" % "4.13.1" % "test" ,
 "org.specs2" %% "specs2-matcher-extra" % "4.13.1" % "test" ,
 "org.specs2" %% "specs2-mock" % "4.13.1" % "test" ,
 "org.specs2" %% "specs2-scalacheck" % "4.13.1" % "test"
)

assembly / assemblyMergeStrategy := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case _ => MergeStrategy.first
}
