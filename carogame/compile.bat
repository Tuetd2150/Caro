@echo off
setlocal
cd /d "%~dp0"

if not exist "out" mkdir out

echo Compiling sources to out\ ...
javac -encoding UTF-8 -d out ^
  src\caro\values\Value.java ^
  src\caro\bean\*.java ^
  src\caro\dao\SettingDao.java ^
  src\caro\bo\Heuristic.java ^
  src\caro\bo\ai\*.java ^
  src\caro\bo\CaroAI.java ^
  src\caro\bo\AIBenchmark.java ^
  src\caro\bo\TestAI.java ^
  src\caro\view\Notification.java ^
  src\caro\view\App.java

if errorlevel 1 (
  echo Compile failed.
  exit /b 1
)

echo Done. Run game: java -cp out caro.view.App
exit /b 0
