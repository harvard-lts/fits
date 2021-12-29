# Tika jar acquisition steps

The following steps can be used to generate the contents of this
directory:

``` shell
PATH_TO_FITS=
TIKA_VERSION=

cd /var/tmp
git clone -b ${TIKA_VERSION} git@github.com:apache/tika.git
cd tika/tika-bundles/tika-bundle-standard
mvn dependency:copy-dependencies -DincludeScope=runtime
rm "${PATH_TO_FITS}/lib/tika/*.jar"
cp target/dependency/* "${PATH_TO_FITS}/lib/tika"
# Another copy of SLF4J here causes runtime issues
rm "${PATH_TO_FITS}/lib/tika/slf4j*"
```
