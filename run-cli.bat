@echo off
if not exist bin\traceit\Main.class (
    echo Compiling TraceIt...
    call compile.bat
)
java -cp bin traceit.Main --cli

