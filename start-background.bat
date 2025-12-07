@echo off
echo Starting Healix Medicare...
cd C:\Users\hp\Downloads\Doc
start /min cmd /c "mvn spring-boot:run"
echo Healix Medicare is starting in background...
echo Access at: http://localhost:8082
timeout /t 3
