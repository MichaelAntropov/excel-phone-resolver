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

The program helps to format Ukrainian phone numbers (+380-xx-xxx-xx-xx) within the chosen selection.

Easy to choose only cells that you need to format and what sheet to work on, just like in Microsoft Excel:

![selection-demo.png](/assets/selection-demo.png)

#### Themes

The program offers two themes to choose from Nord Light & Nord Dark.
Based on [AtlantaFX](https://github.com/mkpaz/atlantafx).

#### Languages

The program currently supports two languages: English and Ukrainian.

#### Settings

Program saves the state of chosen languages and themes by saving them in external config file.

#### Help

Build in help is available with more detailed instructions on how to use the program.

## Run in IDE

### IntelliJ IDE

Open folder as Maven project in IntelliJJ.

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

More details on build script can be found in this repo: [JPackageScriptFX](https://github.com/dlemmermann/JPackageScriptFX).

#### Test Build

Very often application behaves differently when packaged compared to running it from an IDE.

To check how the program will behave without using installer, you can run this command in project directory:

```
{PATH_TO_JAVA_JDK}/bin/java -cp "target/installer/input/libs/*" com.hizencode.excelphoneresolver.main.Launcher
```

Note that `{PATH_TO_JAVA_JDK}` should point to Java __21__ or higher.