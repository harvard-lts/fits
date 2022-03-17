---
layout: documentation
title: Quick start
# subtitle: brief description of page
permalink: /quick-start
---

<nav markdown="1" class="sidebar">
* table of contents
{:toc}
</nav>

<div markdown="1" class="main">

## 1. System Requirements

FITS is a Java program and requires Java version 1.8 or higher. To find out your Java version type java -version in a command-line window.

## 2. Installation

Download the [latest release](https://github.com/harvard-lts/fits/releases). If this is your first time downloading FITS, create a directory for FITS:

- On Windows: C:\Program Files\Fits 
- On Mac OS X: /Applications/Fits 
- On *nix: /home/myuser/Fits 

Extract the contents of your ZIP file to your FITS directory. You should end up with a another directory under your top-level FITS directory that has a version number embedded in it, for example on Windows: C:\Program Files\Fits\fits-1.3.0

## 3. Running FITS

FITS can be run on a command-line or within a program using the Java API.

## 4. FITS from the command-line

Run FITS on the command-line using one of the start-up scripts (fits.bat on Windows, fits.sh on Mac OS X and *nix). 

On Windows 7:

- Open up a command line interface window: Click on Start -> Type in cmd in the lower-left box and hit enter 
- Navigate to the directory where you installed FITS, for example: cd "..\..\Program Files\fits\fits-1.3.0" 
- Execute FITS using the start-up script with the -h parameter to see the parameter options: fits.bat -h 

On *nix:

- Open up a terminal window. 
- Navigate to the directory where you installed FITS 
- If it not already, make the fits.sh file executable: chmod +x fits.sh 
- Run the script named fits.sh: ./fits.sh 

Here are a couple examples of running FITS to get you started. These are relatively simple examples assuming Windows - more complex examples can be found in the on-line user manual. 

- Run FITS against its release text file printing the FITS output to the terminal: 
  - ```fits.bat -i version.properties```
  - On Linux: ```./fits.sh -i version.properties```     
- Run FITS against its release text file saving the FITS output to a file: 
  - ```fits.bat -i version.properties -o myoutput.txt```    
- Output the technical metadata only (in the TextMD format) for the file to the terminal: 
  - ```fits.bat -x -i version.properties``` 
- Output the FITS output plus technical metadata (in the TextMD format) for the text file to the terminal: 
  - ```fits.bat -xc -i version.properties```

**NOTE**: It may be necessary to increase Java heap memory when processing large audio or video file. To do this, modify the command line startup script by adding the following right after "java":

- ```-Xmx<size>``` (where <size> is in megabytes or gigabytes)
- Example: ```java -Xmx512m or java -Xmx5g```

## 5. Using FITS' Java API

See the [Developer Manual](https://github.com/harvard-lts/fits/wiki/Developer-Manual).

## 6. Next steps

After you are up and running see the [User Manual]() for more documentation. 

</div>