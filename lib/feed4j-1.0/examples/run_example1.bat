@ECHO OFF
IF "%JAVA_HOME%" == "" GOTO errorJavaHome

SET JAVA_OPTS=

SET CP="example1\example1.jar";"..\feed4j.jar"
FOR %%i IN (..\lib\*.jar) DO CALL .\cpappender.bat "%%i"

ECHO JAVA_HOME: %JAVA_HOME%
ECHO JAVA_OPTS: %JAVA_OPTS%
ECHO CLASSPATH: %CP%

"%JAVA_HOME%\bin\java.exe" -cp %CP% %JAVA_OPTS% it.sauronsoftware.feed4j.example1.FeedExample
