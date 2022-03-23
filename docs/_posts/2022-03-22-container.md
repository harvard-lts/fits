---
layout: post
categories: data-dictionary
semantic-unit: 4.06 Container elements
semantic-components: N/A
definition: This section identifies the standard used to wrap file-specific sections.
rationale: The container element can extend established schemas or wrap the output of a characterization tool.
data-constraint: Container
repeatability: Repeatable
obligation: Automatic
usage-notes: The containerMD standard contains a description of the container and two levels of verbosity. it can be used to extend certain container formats, and the container element can include format-specific technical metadata.
metadata-standard: ContainerMD
short-name: ContainerMD
maintenance-organization: Bibliothèque Nationale de France
website: http://bibnum.bnf.fr/containerMD-v1_1/index.html
---

##### Example

```
<metadata>
  <container>
    <originalSize toolname="Droid" toolversion="6.4" status="SINGLE_RESULT">34318329</originalSize>
    <compressionMethod toolname="Droid" toolversion="6.4" status="SINGLE_RESULT">deflate</compressionMethod>
    <entries totalEntries="17" toolname="Droid" toolversion="6.4" status="SINGLE_RESULT">
      <format name="EPUB" number="1" />
      <format name="Extensible Markup Language" number="1" />
      <format name="Graphics Interchange Format" number="1" />
      <format name="JPEG 2000 JP2" number="1" />
      <format name="JPEG File Interchange Format" number="1" />
      <format name="MPEG-4" number="1" />
      <format name="Office Open XML Document" number="1" />
      <format name="OpenDocument Text" number="1" />
      <format name="PDF/A" number="1" />
      <format name="PDF/X" number="1" />
      <format name="Plain text" number="1" />
      <format name="Portable Network Graphics" number="1" />
      <format name="Rich Text Format (RTF)" number="1" />
      <format name="TIFF EXIF" number="1" />
      <format name="Waveform Audio" number="2" />
      <format name="ZIP Format" number="1" />
      </entries>
  </container>
</metadata>
```