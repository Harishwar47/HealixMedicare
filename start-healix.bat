@echo off
echo Starting Healix MediCare Application...
echo =====================================

REM Change to the project directory
cd /d "C:\Users\hp\Downloads\Doc"

REM Run the JAR file
echo Starting server on http://localhost:8082
echo Press Ctrl+C to stop the server
java -jar target\doctor-appointment-0.0.1-SNAPSHOT.jar

pause