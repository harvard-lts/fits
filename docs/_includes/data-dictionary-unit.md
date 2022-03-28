{% assign sorted_posts = site.categories.data-dictionary | sort_natural: 'semantic-unit' %}

{% for post in sorted_posts %}

<section markdown="1">

#### {{ post.semantic-unit }}

| --------- | ----------- |
| **Semantic unit** | {% if post.semantic-unit %} {{ post.semantic-unit }} {% endif %} |
| **Semantic components** | {% if post.semantic-components %} {{ post.semantic-components }} {% endif %} |
| **Definition** | {% if post.definition %} {{ post.definition }} {% endif %} |
| **Rationale** | {% if post.rationale %} {{ post.rationale }} {% endif %} |
| **Data constraint** | {% if post.data-constraint %} {{ post.data-constraint }} {% endif %} |
| **Repeatability** | {% if post.repeatability %} {{ post.drepeatability }} {% endif %} |
| **Obligation** | {% if post.obligation %} {{ post.obligation }} {% endif %} |
| **Usage notes** | {% if post.usage-notes %}{{ post.usage-notes }}{% endif %} |
{% if post.metadata-standard %}| **Metadata standard** | {{ post.metadata-standard }} |
| **Short name** | {{ post.short-name }} |
| **Maintenance organization** | {{ post.maintenance-organization }} |
| **Website** | [{{ post.website }}]({{ post.website }}) |{% endif %}

{% if post.content %}{{ post.content }}{% endif %}

</section>

{% endfor %}