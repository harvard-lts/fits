::-----------------------------------------------------------------
:: Windows script to execute FITS
@echo off

:: Set FITS_HOME
set PRGDIR=%~p0
set FITS_HOME=%PRGDIR:~0,-1%

cd %FITS_HOME%

set JARS=
set CLASSPATH=

for %%i in (lib\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\droid\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\jhove\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\nzmetool\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\nzmetool\adapters\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i

set CLASSPATH=%JARS%;%FITS_HOME%\xml\nlnz

java edu.harvard.hul.ois.fits.Fits %*
