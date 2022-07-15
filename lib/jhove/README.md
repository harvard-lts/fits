# JHOVE jar acquisition steps

The following steps can be used to generate the contents of this
directory:

``` shell
PATH_TO_FITS=

cd /var/tmp
mkdir jhove-downloader
cd jhove-downloader
cp "${PATH_TO_FITS}/lib/jhove/pom.xml" .
mvn dependency:copy-dependencies
cd target/dependency
# The next command removes any jar signatures from the Jhove module jars. This is necessary because some of them are
# signed and some of them aren't, which creates issues for the class loader which requires that all jars that contain
# classes in the same package have the same signature
find . -name '*-hul-*.jar' -exec zip -d '{}' 'META-INF/*.SF' 'META-INF/*.RSA' ';'
rm "${PATH_TO_FITS}"/lib/jhove/*.jar
cp * "${PATH_TO_FITS}/lib/jhove"
cd /var/tmp
rm -r jhove-downloader
cd "${PATH_TO_FITS}"
```