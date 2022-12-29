@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  A-Discord-Adventure startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and A_DISCORD_ADVENTURE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\A-Discord-Adventure-1.0-SNAPSHOT.jar;%APP_HOME%\lib\sqlite-jdbc-3.36.0.3.jar;%APP_HOME%\lib\json-20220320.jar;%APP_HOME%\lib\javacord-core-3.6.0.jar;%APP_HOME%\lib\logging-interceptor-4.9.3.jar;%APP_HOME%\lib\okhttp-4.9.3.jar;%APP_HOME%\lib\okio-2.8.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.4.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.4.10.jar;%APP_HOME%\lib\kotlin-stdlib-1.7.0.jar;%APP_HOME%\lib\procedural_generation-1-ALPHA.1.jar;%APP_HOME%\lib\javacord-api-3.6.0.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.7.0.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\guava-30.1.1-jre.jar;%APP_HOME%\lib\jnoise-3.0.2.jar;%APP_HOME%\lib\commons-math3-3.6.1.jar;%APP_HOME%\lib\jackson-databind-2.12.6.jar;%APP_HOME%\lib\nv-websocket-client-2.14.jar;%APP_HOME%\lib\xsalsa20poly1305-0.11.0.jar;%APP_HOME%\lib\log4j-api-2.17.2.jar;%APP_HOME%\lib\vavr-0.10.4.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.8.0.jar;%APP_HOME%\lib\error_prone_annotations-2.5.1.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\jackson-annotations-2.12.6.jar;%APP_HOME%\lib\jackson-core-2.12.6.jar;%APP_HOME%\lib\bcprov-jdk15on-1.60.jar;%APP_HOME%\lib\vavr-match-0.10.4.jar


@rem Execute A-Discord-Adventure
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %A_DISCORD_ADVENTURE_OPTS%  -classpath "%CLASSPATH%" io.github.alexiscomete.lapinousecond.MainKt %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable A_DISCORD_ADVENTURE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%A_DISCORD_ADVENTURE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
