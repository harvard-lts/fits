# Builds FITS
build:
    mvn -DskipTests clean package

# Builds the Docker image
build-image: build
    docker build -f docker/Dockerfile -t fits .

# Executes FITS within a Docker container. This requires that the image has already been built.
run +ARGS:
    docker run --rm -v `pwd`:/work:z fits {{ARGS}}

# Builds the Docker image that's used for running the tests
test-build-image:
    docker build -f docker/Dockerfile-test -t fits-test .

# Runs the tests within a Docker container. This requires that the image has already been built.
test:
    docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test

# Runs the tests that match the pattern within a Docker container. This requires that the image has already been built.
test-filter PATTERN:
    docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test -Dtest={{PATTERN}}

# Applys the code formatter
format:
    mvn spotless:apply