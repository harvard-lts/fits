{% assign sorted_posts = site.categories.tools | sort_natural: 'title' %}

{% for post in sorted_posts %}

<section id="categories" markdown="1">

#### {{ post.title }}

| --------- | ----------- |
| Maintenance organization | {% if post.maintenance-organization %} {{ post.maintenance-organization }} {% endif %} |
| Capabilities | {% if post.capabilities %} {{ post.capabilities }} {% endif %} |
| Formats supported | {% if post.formats %} {{ post.formats }} {% endif %} |
| Details | {% if post.details %} {{ post.details }} {% endif %} |
| More information | {% if post.more-info-url %} {% if post.more-info %}{{post.more-info}}<br><br>{% endif %}<a href="{{ post.more-info-url }}">{{ post.more-info-url }}</a>{% else if post.more-info %} {{ post.more-info }} {% endif %}|


</section>

{% endfor %}