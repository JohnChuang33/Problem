# Problem

Codegen Failed when using command line but works in Intellij IDEA 2017
When using ```mvn spring-boot:run```，```Thread.currentThread().getClassLoader()``` is ```java.net.URLClassLoader```.
However, when running in IDEA, it's using ```sun.misc.Launcher$AppClassLoader```.
