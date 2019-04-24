name := "pdfparser"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
 "org.slf4j" % "slf4j-api" % "1.7.5",
 "org.slf4j" % "slf4j-simple" % "1.7.5",
 "com.itextpdf" % "itextpdf" % "5.3.2" ,
 "org.bouncycastle" % "bcprov-jdk15on" % "1.47" ,
 "org.bouncycastle" % "bcmail-jdk15on" % "1.47" ,
 "org.apache.poi" % "poi" % "3.15" ,

 "net.sourceforge.htmlunit" % "htmlunit" % "2.26" ,

 "org.specs2" %% "specs2-core" % "3.7" % "test" ,
 "org.specs2" %% "specs2-junit" % "3.7" % "test" ,
 "org.specs2" %% "specs2-matcher-extra" % "3.7" % "test" ,
 "org.specs2" %% "specs2-mock" % "3.7" % "test" ,
 "org.specs2" %% "specs2-scalacheck" % "3.7" % "test"
)
