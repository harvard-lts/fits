<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
            <identification>
                <xsl:for-each select="/cad-tool-result/identity">
                    <identity>
                        <xsl:attribute name="format" select="@format"/>
                        <xsl:attribute name="mimetype" select="@mimetype"/>
                        <xsl:if test="@version">
                            <xsl:attribute name="version" select="@version"/>
                        </xsl:if>
                    </identity>
                </xsl:for-each>
            </identification>
            <fileinfo>
                <xsl:for-each select="/cad-tool-result/created">
                    <created>
                        <xsl:value-of select="."/>
                    </created>
                </xsl:for-each>
                <xsl:for-each select="/cad-tool-result/modified">
                    <lastmodified>
                        <xsl:value-of select="."/>
                    </lastmodified>
                </xsl:for-each>
                <xsl:for-each select="/cad-tool-result">
                    <filename>
                        <xsl:value-of select="@file"/>
                    </filename>
                </xsl:for-each>
                <xsl:for-each select="/cad-tool-result/creatingApplicationName">
                    <creatingApplicationName>
                        <xsl:value-of select="."/>
                    </creatingApplicationName>
                </xsl:for-each>
                <!-- copyrightNote?, -->
            </fileinfo>
            <metadata>
                <cad>
                    <xsl:for-each select="/cad-tool-result/title">
                        <title>
                            <xsl:value-of select="."/>
                        </title>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/author">
                        <author>
                            <xsl:value-of select="."/>
                        </author>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/embedded-3d-content">
                        <embedded-3d-content>
                            <xsl:attribute name="bytes" select="@bytes"/>
                            <xsl:value-of select="@type"/>
                        </embedded-3d-content>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/pdf-3d-annotation">
                        <pdf-3d-annotation>
                            <xsl:value-of select="."/>
                        </pdf-3d-annotation>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/unique-id">
                        <unique-id>
                            <xsl:value-of select="."/>
                        </unique-id>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/description">
                        <description>
                            <xsl:value-of select="."/>
                        </description>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/measurement-system">
                        <measurement-system>
                            <xsl:value-of select="."/>
                        </measurement-system>
                    </xsl:for-each>
                    <xsl:for-each select="/cad-tool-result/extent">
                        <Extent>
                            <xsl:for-each select="./dimension">
                                <Dimension>
                                    <xsl:attribute name="magnitude" select="@magnitude"/>
                                    <xsl:attribute name="axis" select="@axis"/>
                                </Dimension>
                            </xsl:for-each>
                        </Extent>
                    </xsl:for-each>
                </cad>
            </metadata>
        </fits>
    </xsl:template>
</xsl:stylesheet>