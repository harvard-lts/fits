::-----------------------------------------------------------------
:: Windows script to execute FITS
@echo off

:: Set FITS_HOME
set PRGDIR=%~dp0
set FITS_HOME=%PRGDIR:~0,-1%

set JARS=
set CLASSPATH=

for %%i in (%FITS_HOME%\lib\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (%FITS_HOME%\lib\droid\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (%FITS_HOME%\lib\jhove\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (%FITS_HOME%\lib\nzmetool\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (%FITS_HOME%\lib\nzmetool\adapters\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i

set CLASSPATH=%JARS%;%FITS_HOME%\xml\nlnz

java edu.harvard.hul.ois.fits.Fits %*
