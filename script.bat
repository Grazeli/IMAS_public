@echo off
REM Compile
javac -cp lib\commons-codec-1.3.jar;lib\jade.jar;lib\jFuzzyLogic.jar;lib\jFuzzyLogic_core.jar -d bin\ src\agents\*.java src\behaviours\*.java src\others\*.java src\format\*.java

REM Execute
java -Dfile.encoding=UTF-8 -cp bin\;lib\commons-codec-1.3.jar;lib\jade.jar;lib\jFuzzyLogic.jar;lib\jFuzzyLogic_core.jar jade.Boot -gui "user:agents.UserAgent;manager:agents.ManagerAgent"