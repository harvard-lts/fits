---
layout: post
categories: data-dictionary
semantic-unit: 3.0 fileStatus
semantic-components: <span>3.01</span>messageElements<br><span>3.01.1</span>well-formed<br><span>3.01.2</span>valid<br><span>3.01.3</span>message
definition: This section contains validity information if the tools are able to identify a valid format.
rationale: Each file processed with FITS should have a declaration of validity.
data-constraint: Container
repeatability: Not repeatable
obligation: Automatic
usage-notes: Well-formed and valid elements will indicate a boolean value (true or false) depending on the validation status of the file.
---

##### Example

```
<filestatus>
    <well-formed toolname="Jhove" toolversion="1.20.1" status="SINGLE_RESULT">true</well-formed>
    <valid toolname="Jhove" toolversion="1.20.1" status="SINGLE_RESULT">true</valid>
</filestatus>
```