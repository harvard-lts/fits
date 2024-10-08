---
layout: page
title: News
subtitle: The lastest news, presentations, and blog posts
permalink: /news
---

<ul class='news-grid'>
  {% for post in site.categories.blog %}
    <li>
      <h2>
        <a class="" href="{{ post.url | prepend: site.baseurl }}">{{ post.title }}</a>
      </h2>
      <p class='post-snippet'>{{ post.snippet }}</p>
      <p class='post-metadata'>
        <span class='post-date'>{{ post.date | date: "%B %-d, %Y" }}</span>
        {% if post.author %}
            - {{ post.author | join: ' and ' }}
        {% endif %} 
      </p>
    </li>
    <hr>
  {% endfor %}
</ul>