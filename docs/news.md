---
layout: page
title: News
subtitle: News, presentations, and blog posts go here
permalink: /news
---

<ul class='unstyled'>
  {% for post in site.categories.blog %}
    <li>
      <h4>
        <a class="" href="{{ post.url | prepend: site.baseurl }}">{{ post.title }}</a>
      </h4>
      <p class='post-snippet'>{{ post.snippet }}</p>
      <p class='post-metadata'>
        {% if post.author %}
            {{ post.author | join: ' and ' }}
        {% endif %} – <span class='post-date'>{{ post.date | date: "%B %-d, %Y" }}</span>
      </p>
    </li>
    <hr>
  {% endfor %}
</ul>