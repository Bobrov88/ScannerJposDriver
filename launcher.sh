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