::-----------------------------------------------------------------
:: Windows script to execute FITS
@echo off
setlocal

:: Set FITS_HOME
set PRGDIR=%~dp0
set FITS_HOME=%PRGDIR:~0,-1%

:: Application classpath
:: As first entry, this directory needed in classpath when running thread for MetadataExtractor tool.
set JARS=%FITS_HOME%\xml\nlnz

cd %FITS_HOME%

:: Update JARS variable with JAR files in the following
:: NOTE--
:: all subdirectories of ${FITS_HOME}\lib\ get loaded dynamically at runtime. DO NOT add here!
for %%i in (lib\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i

:: The Java classpath can be set using either the -classpath option when calling a JDK tool (the preferred method) or by setting the CLASSPATH environment variable.
:: The -classpath option is preferred because you can set it individually for each application without affecting other applications and without other applications modifying its value.

set APPCLASSPATH=%JARS%
java -classpath %APPCLASSPATH% edu.harvard.hul.ois.fits.Fits %*
