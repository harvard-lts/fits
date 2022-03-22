---
layout: post
categories: data-dictionary
semantic-unit: 1.0 identification
semantic-components: 1.1 identity; 1.2 tool; 1.3 version; 1.4 externalIdentifier
definition: This section contains the file format in one or more identity blocks.
rationale: Each file processed with FITS should have a unique format to support use and rendering.
data-constraint: Container
repeatability: Not repeatable
obligation: Automatic
usage-notes: If all the tools that processed the file and could identify it came up with the same format, there will only be one identity block. If there were tools that processed the file that came up with an alternative format, there will be multiple identity blocks. The tools that identified the format will be nested within the identity elements.<br><br>If multiple tools disagree on a format identity or other metadata values, a status attribute is added to the element with a value of "CONFLICT". If only a single tool reports a format identity or other metadata value, a status attribute is added to the element with a value of "SINGLE_RESULT". If multiple tools agree on an identity or value, and none disagree, the status attribute is omitted. A "PARTIAL" value is written when the format can only be partially identified, for example a format name is identified but not a MIME media type.
---

##### Example 1: Successful format identification

In this example, two tools (Jhove 1.5 and file utility 5.04) identified the format as Plain text with a MIME media type of text/plain.

```
<identification>
    <identity format="Plain text" mimetype="text/plain" toolname="FITS" toolversion="0.8.x">
        <tool toolname="Jhove" toolversion="1.5" />
        <tool toolname="file utility" toolversion="5.04" />
    </identity>
</identification>
```

##### Example 2: Format conflict

In this example, there is a "format conflict". The tool Exiftool 9.13 identified the format as PCD with MIME media type image/x-photo-cd, but the tool Tika 1.3 identified the format as MPEG-1 Audio Layer 3. Notice in this case that the identification element will carry an attribute status value of CONFLICT.

```
<identification status="CONFLICT">
    <identity format="PCD" mimetype="image/x-photo-cd" toolname="FITS" toolversion="0.8.x">
        <tool toolname="Exiftool" toolversion="9.13" />
    </identity>
    <identity format="MPEG-1 Audio Layer 3" mimetype="audio/mpeg" toolname="FITS" toolversion="0.8.x">
        <tool toolname="Tika" toolversion="1.3" />
    </identity>
</identification>
```