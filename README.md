Excel Phone Resolver
======

Modifies chosen range of phone numbers(Ukrainian format) in Excel file(*.xlsx, *.xls)

---

## Content

- [About](#about)
- [Run in IDE](#run-in-ide)
- [Build](#build)

---

## About

Program helps to format Ukrainian phone numbers (+380-xx-xxx-xx-xx) within chosen selection.

Easy to choose only cells that you need to format and what sheet to work on, just like in Microsoft Excel:

![selection-demo.png](/assets/selection-demo.png)

## Run in IDE

### IntellJ IDE

Open folder as Maven project in IntellJ.

Run Maven goal `mvn javafx:run`, it will compile and launch app automatically.

If you want to run without Maven then use `com.hizencode.excelphoneresolver.main.Launcher`. Copy/paste VM arguments from
`pom.xml`'s `javafx-maven-plugin`->`<configuration>`->`<options>` to Run\Debug Configurations:

![run-debug-config.png](/assets/run-debug-config.png)

#### Issues

In case `JAVA_HOME` is not set to 11 or greater, running from the Maven Projects window might fail. To avoid it, you can
add `JAVA_HOME` manually to Maven Runner:

![maven-runner-issue.png](/assets/maven-runner-issue.png)

This issue is also described in [javafx-maven-plugin issue](https://github.com/openjfx/javafx-maven-plugin/issues/21)
and [openjfx-docs](https://openjfx.io/openjfx-docs/#IDE-Intellij).

## Build

To create Windows MSI installer just run `mvn clean` and then `mvn install`. Resulting MSI will be in `target/installer`
folder.

Note that each build scrip has to be run in respective OS since `jpackage` can not build cross-platform installers.

Why not `javafx:jlink` in `javafx-maven-plugin`? `jlink` doesn't work with automatic modules and Excel Phone Resolver 
uses non-modularized 3rd party dependencies which result in automatic module error.

So, to build app `maven-dependency-plugin` and `exec-maven-plugin` are used with custom 
[build script](/build-scripts).

More details on build script can be found in [this repo](https://github.com/dlemmermann/JPackageScriptFX).