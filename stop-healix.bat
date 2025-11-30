@echo off
echo Stopping Healix MediCare Application...
echo ======================================

REM Kill any running Java processes (be careful - this kills ALL Java processes)
taskkill /F /IM java.exe

echo Application stopped.
pause