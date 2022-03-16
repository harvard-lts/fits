### Tools

#### Included tools
The latest version of FITS is configured to use these tools for identifying, validating, and extracting technical metadata:

<details>
<summary>ADL Tool</summary>

<dl>
  <dt>Maintenance organization</dt>
  <dd>Harvard Library</dd>
  <dt>Formats supported</dt>
  <dd>Audio Decision List files</dd>
</dl>
</details>

<details>
<summary>Apache Tiki</summary>

<dl>
  <dt>Capabilities</dt>
  <dd>Identifies file formats</dd>
  <dt>Maintenance organization</dt>
  <dd>Apache</dd>
  <dt>Formats supported</dt>
  <dd>see <a href="http://tika.apache.org/1.19/formats.html">supported document formats</a></dd>
</dl>
</details>

#### Potential tools

Capabilities
Maintenance organization
Details
Formats supported
URL


<!-- {% assign tools = site.tools | sort: 'name' %} -->
    {% for tool in site.tools %}
      {% if tool.name %}
        <h4>{{ tool.name }}</h4>
        <p>{{ tool.capabilities }}</p>
      </a>
      {% endif %}
    {% endfor %}

---