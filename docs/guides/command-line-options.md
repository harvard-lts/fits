### Command-line options

When you run FITS on the command-line, the following options are available:

| Option      | Description |
| :---------: | ----------- |
| -h | Prints a help message to the screen. |
| -i | Indicates that a file or directory to process will follow. (required) |
| -o | Directs the FITS output to a file (if -i is a directory) rather than console. (optional) |
| -r | Causes FITS to recursively process all files when the input is a directory. All output files are placed in the same directory as configured in -o unless -n is set. (optional) |
| -n | When -r is set and -i is a directory, output files are placed in nested directories in the same way the input directories are nested. (optional) |
| -v | Outputs tool version information. |
| -x | Transforms the FITS output into standard XML schemas. (Only standard schema metadata is output.) |
| -xc | Outputs the FITS output plus the FITS output transformed into standard XML schemas. |
| -f | Path to an alternate fits.xml configuration file rather than using the default within FITS. (optional) |



Many of the options can be used together. For example: 

```.\fits.bat -i myFileToProcess.pdf -o theOutput.txt```

When processing multiple files contained in a single directory whose output goes to another directory while using an alternate FITS configuration file:

```./fits.sh -i /input-files-directory -o /output-directory -xc -f /alternate-fits-config/fits.xml```

---