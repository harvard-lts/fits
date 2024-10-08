---
layout: page
title: About
permalink: /about
---

<div markdown="1">
<h2>FITS</h2>

The File Information Tool Set (FITS) identifies, validates and extracts technical metadata for a wide range of file formats. It acts as a wrapper, invoking and managing the output from several other open source tools. Output from these tools are converted into a common format, compared to one another and consolidated into a single XML output file. 

Note: FITS is written in Java and is **compatible with Java 1.8 or higher**.

<p><a class="page-link" href="https://github.com/harvard-lts/fits/releases"><svg class="svg-icon"><use xlink:href="/fits/assets/minima-social-icons.svg#github"></use></svg>Release Notes & Source Code</a></p>
</div>

---

<div markdown="1">
<h2>FITS Web Service</h2>

The FITS Web Service is a project that allows FITS to be deployed as a service on either Tomcat or JBoss. The code has been built and test using Java 7 and Java 8 and tested on Tomcat 7, Tomcat 8, and minimally on JBoss 7.1. The path to the service will be the WAR file name plus the service name. (E.g. - For release 1.1.1 which provides the release artifact fits-1.1.1.war, use http://localhost:8080/fits-1.1.1/examine/ as the base URL). This can be adjusted by either changing the WAR filename or using server-specific settings.

Note: The latest and future versions of this project are built and tested using Java 8. 

<p><a class="page-link" href="https://github.com/harvard-lts/FITSservlet/releases"><svg class="svg-icon"><use xlink:href="/fits/assets/minima-social-icons.svg#github"></use></svg>Release Notes & Source Code</a></p>
</div>