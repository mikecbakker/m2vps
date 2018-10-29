@echo off

CALL ./_settings.cmd

echo Are you sure you want to create a dump?
set /p CONFIRMATION=Type 'yes' to proceed: 
IF NOT %CONFIRMATION%==yes GOTO end

echo Clear cache...

echo == Dumping DB ==
echo ================
C:\xampp\mysql\bin\mysqldump.exe -u root --opt %DB% vehicle_year_model_data vehicle_data_top_10 vehicle_data_system --comments > .\db\db.sql
C:\xampp\mysql\bin\mysqldump.exe -u root --opt %DB% vehicle_data --comments > .\db\db_vehicle_data.sql
echo.
goto completed


:end 
echo Database dump cancelled.
goto exit

:completed
echo Database dumped!
goto exit




:exit
echo.

pause