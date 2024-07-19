@echo off
setlocal EnableExtensions EnableDelayedExpansion
cls
Echo.
Echo.
Echo.
Echo    Please wait while loading the JavaPos POSTest application .....
Echo.
Echo.

if /i {%PROCESSOR_ARCHITECTURE%} == {AMD64} set path=%windir%\SysWow64;%path%

set javakey=HKLM\SOFTWARE\JavaSoft\Java Development Kit
for /F "tokens=1,2*" %%i in ('reg.exe query "%javakey%" /v CurrentVersion 2^>nul') DO (
 if /i "%%i"=="CurrentVersion" (
  for /F "tokens=1,2*" %%a in ('reg.exe query "%javakey%\%%k"  /v JavaHome  2^>nul') DO (
   if /i "%%a"=="JavaHome" (
		if not defined JAVA_HOME set JAVA_HOME=%%c
		set path=%%c\bin;!path!
   )
  )
 )
)

set javakey=HKLM\SOFTWARE\JavaSoft\Java Runtime Environment
for /F "tokens=1,2*" %%i in ('reg.exe query "%javakey%" /v CurrentVersion 2^>nul') DO (
 if /i "%%i"=="CurrentVersion" (
  for /F "tokens=1,2*" %%a in ('reg.exe query "%javakey%\%%k"  /v JavaHome  2^>nul') DO (
   if /i "%%a"=="JavaHome" (
   		if not defined JRE_HOME set JRE_HOME=%%c
		set path=%%c\bin;!path!
   )
  )
 )
)

cd /d %~dp0
set oldcp=%classpath%
set classpath=DriverTest-1.0.jar
set classpath=%classpath%;jpos1141.jar
set classpath=%classpath%;xerces.jar
set classpath=%classpath%;jssc-2.9.6.jar
set classpath=%classpath%;JposDriver.jar
set classpath=%classpath%;log4j-1.2.17.jar
set classpath=%classpath%;json-simple-1.1.1.jar
set classpath=%classpath%;"%cd%"\
set classpath=%classpath%;"%cd%"\jpos\jpos114-controls.jar

java -XX:+ShowMessageBoxOnError -cp %classpath%  test.PosTestMainWindow
set classpath=%oldcp%