::-----------------------------------------------------------------
:: Windows script to execute FITS
@echo off
setlocal

:: Set FITS_HOME
set PRGDIR=%~dp0
set FITS_HOME=%PRGDIR:~0,-1%

set JARS=
set CLASSPATH=

cd %FITS_HOME%

for %%i in (lib\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
# all subdirectories of ${FITS_HOME}\lib\ get loaded dynamically at runtime. DO NOT add here!

set CLASSPATH=%JARS%;%FITS_HOME%\xml\nlnz
rem echo %CLASSPATH%

java edu.harvard.hul.ois.fits.Fits %*
