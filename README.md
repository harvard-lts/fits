FITS
====

 Quick start
1. System Requirements

FITS is a Java program and requires Java version 1.6 or higher. To find out your Java version type java -version in a command-line window.
2. Installation

Download the latest official release from our Downloads page. If this is your first time downloading FITS, create a directory for FITS, for example:

    On Windows: C:\Program Files\Fits
    On Mac OS X: /Applications/Fits
    On *nix: /home/myuser/Fits

Extract the contents of your ZIP file to your FITS directory. You should end up with a another directory under your top-level FITS directory that has a version number embedded in it, for example on Windows: C:\Program Files\Fits\fits-0.6.2
3. Running FITS

FITS can be run on a command-line or within a program using the Java API.
4. FITS from the command-line

Run FITS on the command-line using one of the start-up scripts (fits.bat on Windows, fits.sh on Mac OS X and *nix). 

For example on Windows 7:

    Open up a command line interface window: Click on Start -> Type in cmd in the lower-left box and hit enter
    Navigate to the directory where you installed FITS, for example: cd "..\..\Program Files\fits\fits-0.2.6"
    Execute FITS using the start-up script with the -h parameter to see the parameter options: fits.bat -h

For example on *nix:

    Open up a terminal window.
    Navigate to the directory where you installed FITS
    If it not already, make the fits.sh file executable
        chmod +x fits.sh
    Run the script named fits.sh
        ./fits.sh

Here are a couple examples of running FITS to get you started. These are relatively simple examples assuming Windows - more complex examples can be found in the on-line user manual. 

    Run FITS against its release text file printing the FITS output to the terminal: fits.bat -i RELEASE.txt
    Run FITS against its release text file saving the FITS output to a file: fits.bat -i RELEASE.txt -o myoutput.txt
    Output the technical metadata for FITS' release text file in the TextMD format to the terminal: fits.bat -i RELEASE.txt -x
    Output the FITS output plus technical metadata for FITS' release text file in the TextMD format to the terminal: fits.bat -i RELEASE.txt -xc

5. Using FITS' Java API

See the Developer Manual.
6. Learn more

After you are up and running see the User Manual for more documentation.