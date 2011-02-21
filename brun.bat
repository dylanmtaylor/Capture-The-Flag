@ECHO OFF
REM This script builds and runs the game. written by Dylan Taylor
REM Path=C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\Program Files\QuickTime\QTSystem\;C:\WINDOWS\system32\WindowsPowerShell\v1.0;C:\Program Files\Java\jdk1.6.0_21\bin
Path=%path%;C:\Program Files\Java\jdk1.6.0_21\bin
javac Main.java
java Main 
