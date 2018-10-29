@echo off

echo Are you sure you want to run setup?
echo All existing data in the database '%DB%' will be deleted.
set /p CONFIRMATION=Type 'yes' to proceed: 
IF NOT %CONFIRMATION%==yes GOTO end

CALL ./_settings.cmd

echo == Updating DB ==
echo =================

echo Drop user: %DB_USER%
C:\xampp\mysql\bin\mysql --max_allowed_packet=512M -u root -e "DROP USER %DB_USER%"
echo Create user: %DB_USER%
C:\xampp\mysql\bin\mysql --max_allowed_packet=512M -u root -e "CREATE USER %DB_USER%"
echo Creating %DB% DB
C:\xampp\mysql\bin\mysql --max_allowed_packet=512M -u root -e "CREATE DATABASE IF NOT EXISTS %DB%"
echo Adding permissions for users to DB
C:\xampp\mysql\bin\mysql --max_allowed_packet=512M --database=%DB% -u root -e "GRANT ALL ON %DB%.* to '%DB_USER%'@localhost identified by '%DB_PASS%'"
echo Importing %DB%
C:\xampp\mysql\bin\mysql --max_allowed_packet=512M --database=%DB% -u root < .\db\db.sql

echo.
goto completed

:end 
echo Database import cancelled.
goto exit

:completed
echo Database has been imported...
goto exit

:exit
echo.

pause