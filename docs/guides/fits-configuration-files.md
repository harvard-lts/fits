### FITS configuration files

The FITS configuration files are located in the xml directory.

**The FITS XML output is highly affected by how FITS is configured. In particular, the order of tools near the top of the fits.xml configuration file specifies which tools FITS should prefer when they give conflicting information and if FITS should ignore tool output for particular formats. FITS comes pre-configured based on testing different tools with different formats and the default configuration should only be changed with a great deal of care and testing.**

{% include_relative guides/fits-config_fits_xml.md %}
{% include_relative guides/fits-config_fits_format_tree_xml.md %}
{% include_relative guides/fits-config_fits_xml_map_xml.md %}
{% include_relative guides/fits-config_format_map_txt.md %}
{% include_relative guides/fits-config_mime_map_txt.md %}
{% include_relative guides/fits-config_mime_to_format_map_txt.md %}

---