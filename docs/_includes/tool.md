{% assign sorted_posts = site.categories.tools | sort_natural: 'title' %}

{% for post in sorted_posts %}

<section id="categories" markdown="1">

#### {{ post.title }}

| --------- | ----------- |
| Maintenance organization | {% if post.maintenance-organization %} {{ post.maintenance-organization }} {% endif %} |
| Capabilities | {% if post.capabilities %} {{ post.capabilities }} {% endif %} |
| Formats supported | {% if post.formats %} {{ post.formats }} {% endif %} |
| Description | {% if post.description %} {{ post.description }} {% endif %} |
| Usage notes | {% if post.usage-note %}{{post.usage-note}}{% endif %} |
| More information | {% if post.more-info-url %}<a href="{{ post.more-info-url }}">{{ post.more-info-url }}</a>{% endif %} |

</section>

{% endfor %}