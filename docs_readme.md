# FITS Documentation

The docs directory contains the Jekyll-based documentation website for the File Information Tool Set (FITS) project. The documentation is automatically published to [https://harvard-lts.github.io/fits](https://harvard-lts.github.io/fits) via GitHub Pages.

## Prerequisites

To build and serve the documentation locally, you'll need:

- **Ruby** (version 2.7 or higher recommended)
- **Bundler** gem for managing Ruby dependencies
- **Git** (for version control)

### Installing Ruby and Bundler

#### macOS
```bash
# Using Homebrew
brew install ruby

# Or using rbenv for version management
brew install rbenv
rbenv install 3.0.0
rbenv global 3.0.0
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install ruby-full build-essential zlib1g-dev

# Add gem installation directory to PATH
echo '# Install Ruby Gems to ~/gems' >> ~/.bashrc
echo 'export GEM_HOME="$HOME/gems"' >> ~/.bashrc
echo 'export PATH="$HOME/gems/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

#### Install Bundler
```bash
gem install bundler
```

## Local Development Setup

1. **Navigate to the docs directory:**
   ```bash
   cd docs
   ```

2. **Install dependencies:**
   ```bash
   bundle install
   ```
   
   This will install all the Ruby gems specified in `Gemfile` and `Gemfile.lock`, including:
   - Jekyll (~3.10.0)
   - GitHub Pages gem (~232)
   - Various Jekyll plugins and dependencies

3. **Serve the site locally:**
   ```bash
   bundle exec jekyll serve
   ```
   
   The site will be available at `http://localhost:4000/fits`

4. **Serve with live reload (optional):**
   ```bash
   bundle exec jekyll serve --livereload
   ```
   
   This will automatically refresh the browser when files change.

## Building the Documentation

### For Local Testing
```bash
# Build the site into _site directory
bundle exec jekyll build

# Build for production (minified, optimized)
JEKYLL_ENV=production bundle exec jekyll build
```

### For GitHub Pages
The documentation is automatically built and deployed by GitHub Pages when changes are pushed to the repository. The configuration in `_config.yml` handles the GitHub Pages integration.

## Project Structure

```
docs/
├── _config.yml          # Jekyll configuration
├── _includes/           # Reusable HTML snippets
├── _layouts/            # Page templates
├── _posts/              # Blog posts and tool documentation
├── css/                 # Custom stylesheets
├── images/              # Documentation images
├── guides/              # User guides and tutorials
├── Gemfile              # Ruby gem dependencies
├── Gemfile.lock         # Locked gem versions
├── index.html           # Homepage
├── about.md             # About page
├── connect.md           # Community/contact page
├── guides.md            # Guides overview
├── news.md              # News and updates
└── quick-start.md       # Quick start guide
```

## Adding Content

### Adding a New Page
1. Create a new Markdown file in the root `docs/` directory
2. Add front matter at the top:
   ```yaml
   ---
   layout: page
   title: "Your Page Title"
   ---
   ```
3. Write your content in Markdown below the front matter

### Adding a New Tool Documentation
1. Create a new file in `_posts/` following the naming convention: `YYYY-MM-DD-tool-name.md`
2. Use the `post` layout and `tools` category:
   ```yaml
   ---
   layout: post
   categories: tools
   title: "Tool Name"
   maintenance-organization: "Organization Name"
   capabilities: "What the tool does"
   formats: "Supported formats"
   description: "Detailed description"
   usage-note: "Special usage notes"
   ---
   ```

### Adding a News Post
1. Create a new file in `_posts/` with date: `YYYY-MM-DD-news-title.md`
2. Use appropriate front matter for news posts

## Configuration

Key configuration options in `_config.yml`:

- **Site Information:** Title, description, URL settings
- **GitHub Integration:** Repository links and names  
- **Build Settings:** Theme, plugins, exclude patterns
- **Jekyll Settings:** Markdown processor, syntax highlighting

## Troubleshooting

### Common Issues

1. **Bundle install fails:**
   ```bash
   # Try updating bundler
   gem update bundler
   
   # Or install with specific bundle config
   bundle config set --local path 'vendor/bundle'
   bundle install
   ```

2. **Port already in use:**
   ```bash
   # Use a different port
   bundle exec jekyll serve --port 4001
   ```

3. **Changes not appearing:**
   - Restart the Jekyll server if you modified `_config.yml`
   - Clear browser cache
   - Check that files are saved

4. **Gem dependency conflicts:**
   ```bash
   # Update gems
   bundle update
   
   # Or reinstall clean
   rm Gemfile.lock
   bundle install
   ```

### Getting Help

- Check the [Jekyll documentation](https://jekyllrb.com/docs/)
- Review [GitHub Pages documentation](https://docs.github.com/en/pages)
- Search existing [FITS GitHub issues](https://github.com/harvard-lts/fits/issues)

## Deployment

### Automatic Deployment
Changes pushed to the main branch are automatically deployed to GitHub Pages. No manual deployment steps are required.

### Manual Deployment (if needed)
If manual deployment becomes necessary:

1. Build the production site:
   ```bash
   JEKYLL_ENV=production bundle exec jekyll build
   ```

2. The generated site will be in the `_site/` directory

## Contributing

When contributing to the documentation:

1. **Test locally** before submitting pull requests
2. **Follow the existing structure** and naming conventions  
3. **Update this README** if you add new processes or change the setup
4. **Check links** to ensure they work correctly
5. **Optimize images** for web use before adding them

For more information about contributing to FITS, see the main project [CONTRIBUTING.md](../CONTRIBUTING.md).