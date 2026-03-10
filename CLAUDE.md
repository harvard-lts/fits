# FITS Project Maintenance Guide

## ⚠️ CRITICAL: Local Testing Must Use Docker

**All local testing and development work MUST be performed using Docker containers.**
Native Maven testing produces inconsistent, platform-dependent results that will not match CI.

Use the Docker commands in this guide - never run `mvn test` directly on your host system.

## 🏛️ Digital Preservation Mission Critical

**FITS is heavily relied upon by the digital preservation community worldwide.** Libraries, archives, museums, and government institutions depend on FITS for long-term digital preservation workflows.

**Maintenance Philosophy: STABILITY OVER FEATURES**
- Prioritize stability and backward compatibility over new features
- Thoroughly test all changes - institutions rely on consistent behavior
- Avoid experimental or breaking changes
- When in doubt, choose the more conservative approach
- FITS must be rock solid - data preservation cannot fail

## Project Overview
FITS (File Information Tool Set) is a Java-based metadata extraction tool developed at Harvard University Information Technology. It aggregates multiple format-specific tools into a single command-line utility for comprehensive file analysis.

- **Version**: 1.6.1-SNAPSHOT
- **Language**: Java 11+
- **Build System**: Apache Maven
- **License**: Apache 2.0 / LGPL hybrid
- **Repository**: https://github.com/harvard-lts/fits
- **Main Branch**: `main` (PRs go to `dev`)

## Key Architecture Components

### Core Classes
- `edu.harvard.hul.ois.fits.Fits` - Main application class (src/main/java/edu/harvard/hul/ois/fits/Fits.java:67)
- `edu.harvard.hul.ois.fits.tools.ToolBelt` - Manages tool execution
- `edu.harvard.hul.ois.fits.consolidation.OISConsolidator` - Consolidates tool outputs

### Configuration Files
- `xml/fits.xml` - Main configuration, defines tool execution order and file extensions
- `pom.xml` - Parent POM with dependency versions
- `fits-pom.xml` - Main FITS application build
- `tool-poms/*.xml` - External tool installation definitions

### External Tools Integrated
- **MediaInfo**: Video/audio metadata (avi,mov,mp4,etc.)
- **DROID**: File format identification
- **JHOVE**: PDF,TIFF,JPEG validation (Harvard)
- **Tika**: Apache metadata extraction
- **ExifTool**: Image metadata
- **Jpylyzer**: JPEG 2000 validation
- **NLNZ**: WordPerfect, ODF support

## Build & Development Commands

### Maven Commands
```bash
# Full build with tests
mvn clean package

# Build without tests
mvn clean package -DskipTests

# Install external tools only
mvn generate-resources

# Code formatting (required before commits)
mvn spotless:apply

# Update DROID signatures (commit changes after)
mvn -P update-droid-sigs generate-resources
```

### Just Commands (Optional Convenience)
```bash
just build          # Build FITS
just test           # Run tests in Docker
just format         # Apply code formatting
just install-tools  # Install external tools
just update-droid-sigs  # Update DROID signatures
```

### Docker Testing (REQUIRED for Local Development)

**⚠️ IMPORTANT: Always use Docker for local testing - native Maven tests are unreliable due to platform-specific tool behavior.**

Docker provides:
- Consistent Linux environment matching CI
- Reproducible test results across platforms
- Proper MediaInfo/tool binary compatibility
- Isolation from local Ruby/system dependencies

```bash
# Build test container (run once)
docker build -f docker/Dockerfile-test -t fits-test .

# Run all tests (primary development workflow)
docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test

# Run specific test pattern
docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test -Dtest=TestPattern

# Regenerate test expectations (USE CAREFULLY - review all XML diffs)
docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn -Doverwrite=true clean test
```

**Note**: Running `mvn test` directly on the host may produce different results due to platform-specific tool behavior, especially with MediaInfo on macOS/Windows.

## Regular Maintenance Tasks

**⚠️ CONSERVATIVE APPROACH REQUIRED**: FITS is mission-critical infrastructure for digital preservation institutions worldwide. Always prioritize stability and thorough testing over speed of delivery.

### 1. Dependency Security Updates
- **Monitor**: Dependabot PRs for security vulnerabilities
- **Action**: Review and merge security updates promptly, but test thoroughly
- **Testing**: Always run full Docker test suite before merging
- **Recent example**: PR #410 addressed multiple vulnerabilities

### 2. DROID Signature Updates (Quarterly)
```bash
mvn -P update-droid-sigs generate-resources
git add tools/droid/DROID_SignatureFile_*.xml
git commit -m "Update DROID signature files"
```

### 3. Tool Version Updates
**APPROACH WITH EXTREME CAUTION** - Tool updates can change metadata output formats and break institutional workflows:

- Tika (currently 2.8.0)
- DROID (currently 6.5.2)
- JHOVE (currently 1.28.0)

**Process for tool updates**:
1. Update versions in parent `pom.xml`
2. Run comprehensive Docker test suite
3. Manually test with representative file samples
4. Compare output XML for breaking changes
5. Consider providing transition period for institutions
6. Document any output format changes clearly

### 4. Code Quality
- **Formatting**: Always run `mvn spotless:apply` before commits
- **Style**: Follows Google Java Style Guide via Palantir formatter
- **Testing**: All changes must pass existing test suite in Docker

## Common Issues & Solutions

### MediaInfo Platform Issues
**Problem**: MediaInfo fails on non-Ubuntu Linux distributions
**Solution**:
1. Delete `tools/mediainfo/linux/libmediainfo.so.0` and `libzen.so.0`
2. Either install system MediaInfo via package manager, or
3. Download appropriate binaries from https://mediaarea.net and place in `tools/mediainfo/linux/`

### Test Failures After Updates
**Approach**:
1. **ALWAYS run tests in Docker** - never trust native Maven test results
2. Tool output changes may require updating test expectations
3. Use `-Doverwrite=true` carefully to regenerate expectations (Docker only)
4. Always review XML diffs before committing
5. If tests pass locally but fail in CI, you likely tested outside Docker

### Performance Issues
**Configuration**:
- Thread limit: `xml/fits.xml` `<max-threads>20</max-threads>`
- Memory: Increase JVM heap for large files
- Large file processing: Some tools have size limits

### Tool Integration Problems
**Debug steps**:
1. Use `-d` flag for debug output
2. Use `-t` flag to see raw tool outputs
3. Check `fits.log` for detailed errors
4. Verify tool binaries are executable and compatible

## File Structure Guide

```
/src/main/java/           # Java source code
/xml/                     # Configuration files and XSLT mappings
  /fits.xml              # Main tool configuration
  /jhove/                # JHOVE-specific configs
  /exiftool/             # ExifTool mappings
  /tika/                 # Tika configurations
/tools/                   # External tool binaries
  /droid/               # DROID signature files
  /mediainfo/           # MediaInfo binaries by platform
/lib/                     # JAR dependencies by tool
/lib-local/               # Local Maven repo for non-public deps
/testfiles/               # Test inputs and expected outputs
  /input/               # Sample files for testing
  /output/              # Expected FITS XML outputs
/tool-poms/              # Maven modules for tool installation
/docker/                 # Dockerfiles for testing and deployment
```

## Important Dependencies (lib-local/)

Non-public dependencies requiring manual management:
- `com/therockquarry/aes/` - AES metadata schemas
- `nz/govt/natlib/metadata/` - NLNZ metadata extractor
- `com/portalmedia/embarc/` - EmbARC DPX tool
- `edu/harvard/lts/tools/` - Harvard-specific tools
- `edu/harvard/huit/lts/ots/` - OTS schemas

## Release Process

**CRITICAL**: FITS releases affect digital preservation workflows at institutions worldwide. Follow this process rigorously:

1. **Update version** in parent `pom.xml`
2. **Run full test suite** in Docker environment - ALL tests must pass
3. **Manual testing** with diverse file samples from different domains
4. **Regression testing** - compare outputs with previous version for breaking changes
5. **Update documentation** including any behavior changes
6. **Create release artifacts**:
   - `fits-<version>.jar` (application)
   - `fits-<version>.zip` (distribution)
7. **Tag release** in git
8. **Deploy** to Harvard LTS Artifactory (configured in distributionManagement)
9. **Announce** to digital preservation community with clear change documentation

**Never rush releases** - institutions depend on FITS stability for long-term preservation.

## Security Considerations

- **JAR verification**: Maven verifies integrity during build
- **External processes**: Some tools (ExifTool, MediaInfo) run as separate processes
- **File processing security**: Depends on underlying tool security
- **Input validation**: FITS validates its own XML output against schema
- **Logging**: Sensitive data may appear in `fits.log` - review log retention policies

## Configuration Management

### Tool Execution Order
Order in `xml/fits.xml` determines precedence for conflicting metadata. MediaInfo listed first has highest priority.

### Extension Mapping
Each tool configured with:
- `include-exts`: Only process these extensions
- `exclude-exts`: Skip these extensions
- No mapping: Process all files

### Custom Configurations
- Alternative config: `fits -f custom-config.xml`
- Tool-specific configs in `/xml/` subdirectories
- Logging: `log4j2.xml` in project root

## Troubleshooting Checklist

1. **Build failures**: Check Java 11+, Maven 3.6+
2. **Tool failures**: Verify binary permissions and dependencies
3. **Test failures**: Use Docker for consistent environment
4. **Memory issues**: Increase JVM heap size
5. **Permission issues**: Check file/directory permissions
6. **Network issues**: Maven dependency downloads may fail
7. **Platform issues**: Some tools are platform-specific

## Contact & Resources

- **Primary maintainers**: Harvard LTS team
- **Issues**: https://github.com/harvard-lts/fits/issues
- **Documentation**: http://fitstool.org
- **Developer manual**: http://fitstool.org/developer-manual
- **User manual**: http://fitstool.org/user-manual

---

*This file should be updated when significant architectural changes are made to the project.*

**Remember**: Your changes to FITS impact digital preservation workflows at libraries, archives, museums, and cultural heritage institutions worldwide. Prioritize stability and thorough testing in all maintenance activities.
