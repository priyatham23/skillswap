@echo off
if not exist "bin" mkdir bin
dir /s /B src\main\java\*.java > sources.txt
javac -encoding UTF-8 -cp "lib/*;src/main/java" -d bin @sources.txt
del sources.txt
echo Compilation complete.

