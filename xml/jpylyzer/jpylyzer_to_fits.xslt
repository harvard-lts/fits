<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
                xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:jpylyzer="http://openpreservation.org/ns/jpylyzer/v2/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://openpreservation.org/ns/jpylyzer/v2/ http://jpylyzer.openpreservation.org/jpylyzer-v-2-1.xsd"
                exclude-result-prefixes="jpylyzer">

    <xsl:output method="xml" indent="yes" />
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
            <identification>
                <identity format="JPEG 2000 JP2" mimetype="image/jp2" />
            </identification>
            <filestatus>
                <xsl:choose>
                    <xsl:when test="jpylyzer:jpylyzer/jpylyzer:file/jpylyzer:isValid = 'True'">
                        <well-formed>true</well-formed>
                        <valid>true</valid>
                    </xsl:when>
                    <xsl:otherwise>
                        <well-formed>false</well-formed>
                        <valid>false</valid>
                    </xsl:otherwise>
                </xsl:choose>
            </filestatus>
        </fits>
    </xsl:template>
</xsl:stylesheet>