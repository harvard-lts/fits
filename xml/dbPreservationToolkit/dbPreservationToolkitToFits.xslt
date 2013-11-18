<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="yes" />
	<xsl:strip-space elements="*"/>
  <xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
	<xsl:choose>
		<xsl:when test="/databaseCharacterizationResult"> 
				
			<identification>
				<identity>

				<xsl:attribute name="format">
					<xsl:value-of select="string('DBXML')"/>
				</xsl:attribute> 
				<xsl:attribute name="mimetype">		
					<xsl:value-of select="string('text/xml')"/>
				</xsl:attribute> 
				<externalIdentifier type="puid">
					<xsl:value-of select="string('fmt/101')"/>
				</externalIdentifier>
				</identity>
			</identification>
			<filestatus>
				<well-formed>true</well-formed>
				<valid>true</valid>
			</filestatus>
			<metadata>
				<document>
					<xsl:variable name="databaseName" select="//name"/>
					<xsl:variable name="productName" select="//productName"/>
					<xsl:variable name="productVersion" select="//productVersion"/>
					<xsl:variable name="totalNumberOfRecords" select="//totalNumberOfRecords"/>
			
					<xsl:element name="databaseName">
		     				<xsl:value-of select="$databaseName"/>
					</xsl:element>
					<xsl:element name="productName">
		     				<xsl:value-of select="$productName"/>
					</xsl:element>
					<xsl:element name="productVersion">
		     				<xsl:value-of select="$productVersion"/>
					</xsl:element>
					<xsl:element name="totalNumberOfRecords">
		     				<xsl:value-of select="$totalNumberOfRecords"/>
					</xsl:element>
					<!--
					<xsl:for-each select="//table">
						<xsl:variable name="tableName" select="name"/>

						<xsl:element name="table_{$tableName}_numberOfRecords">
		     					<xsl:value-of select="numberOfRecords"/>
						</xsl:element>
						<xsl:element name="table_{$tableName}_numberOfColumns">
		     					<xsl:value-of select="count(columns/column)"/>
						</xsl:element>
					</xsl:for-each>
					-->
					<xsl:element name="numberOfTables">
						<xsl:value-of select="count(//table)"/>
					</xsl:element>
				</document>
			</metadata>
		</xsl:when>
		<xsl:otherwise>
			<filestatus>
				<well-formed>false</well-formed>
				<valid>false</valid>
			</filestatus>
		</xsl:otherwise>
	</xsl:choose>
    </fits>
  </xsl:template>
</xsl:stylesheet>
