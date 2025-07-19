@ECHO OFF

rem ------ ENVIRONMENT --------------------------------------------------------
rem The script depends on various environment variables to exist in order to
rem run properly. The java version we want to use, the location of the java
rem binaries (java home), and the project version as defined inside the pom.xml
rem file, e.g. 1.0-SNAPSHOT.
rem
rem PROJECT_NAME: name used in pom.xml, e.g. excel-phone-resolver
rem PROJECT_VERSION: version used in pom.xml, e.g. 1.0-SNAPSHOT
rem APP_VERSION: the application version, e.g. 1.0.0, shown in "about" dialog
rem JAVA_VERSION: version used in pom.xml, e.g. 17

set JAVA_VERSION=%JAVA_VERSION%
set MAIN_JAR=%PROJECT_NAME%-%PROJECT_VERSION%.jar

rem Set desired installer type: "app-image", "msi" or "exe".
set INSTALLER_TYPE=msi

rem ------ CHANGE WORKING DIR -------------------------------------------------
rem Change working directory to project root
cd ../

rem ------ SETUP DIRECTORIES AND FILES ----------------------------------------
rem Remove previously generated java runtime and installers. Copy all required
rem jar files into the input/libs folder.

IF EXIST target\java-runtime rmdir /S /Q  .\target\java-runtime
IF EXIST target\installer rmdir /S /Q target\installer

xcopy /S /Q target\libs\* target\installer\input\libs\
copy target\%MAIN_JAR% target\installer\input\libs\

rem ------ REQUIRED MODULES ---------------------------------------------------
rem Use jlink to detect all modules that are required to run the application.
rem Starting point for the jdep analysis is the set of jars being used by the
rem application.

echo Detecting required modules...

"%JAVA_HOME%\bin\jdeps" ^
  -q ^
  --multi-release %JAVA_VERSION% ^
  --ignore-missing-deps ^
  --class-path "target\installer\input\libs\*" ^
  --print-module-deps target\classes\com\hizencode\excelphoneresolver\main\App.class > target\temp.txt

set /p detected_modules=<target\temp.txt

echo Detected modules: %detected_modules%

rem ------ MANUAL MODULES -----------------------------------------------------
rem Example: set manual_modules=,jdk.crypto.ec,jdk.localedata
rem
rem jdk.crypto.ec has to be added manually bound via --bind-services or
rem otherwise HTTPS does not work.
rem
rem See: https://bugs.openjdk.java.net/browse/JDK-8221674
rem
rem In addition we need jdk.localedata if the application is localized.
rem This can be reduced to the actually needed locales via a jlink parameter,
rem e.g., --include-locales=en,de.
rem
rem Don't forget the leading ','!

set manual_modules=,jdk.localedata
echo Manual modules: %manual_modules%

rem ------ RUNTIME IMAGE ------------------------------------------------------
rem Use the jlink tool to create a runtime image for our application. We are
rem doing this in a separate step instead of letting jlink do the work as part
rem of the jpackage tool. This approach allows for finer configuration and also
rem works with dependencies that are not fully modularized, yet.

echo Creating java runtime image...

call "%JAVA_HOME%\bin\jlink" ^
  --strip-native-commands ^
  --no-header-files ^
  --no-man-pages ^
  --compress=zip-0 ^
  --strip-debug ^
  --add-modules %detected_modules%%manual_modules% ^
  --include-locales=en,ua^
  --output target/java-runtime


rem ------ PACKAGING ----------------------------------------------------------
rem In the end we will find the package inside the target/installer directory.

call "%JAVA_HOME%\bin\jpackage" ^
  --type %INSTALLER_TYPE% ^
  --dest target/installer ^
  --input target/installer/input/libs ^
  --name %APP_NAME% ^
  --main-class com.hizencode.excelphoneresolver.main.Launcher ^
  --main-jar %MAIN_JAR% ^
  --runtime-image target/java-runtime ^
  --icon src/main/resources/icons/icon.ico ^
  --app-version %APP_VERSION% ^
  --vendor "Hizencode(Michael Antropov)" ^
  --copyright "Copyright © 2025 Hizencode(Michael Antropov)" ^
  --win-dir-chooser ^
  --win-shortcut ^
  --win-per-user-install ^
  --win-menu
