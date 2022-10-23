# Lists available commands
default:
    just --list

# Builds FITS
build:
    mvn -DskipTests clean package

# Builds the Docker image
build-image: build
    docker build -f docker/Dockerfile -t fits .

# Executes FITS within a Docker container. This requires that the image has already been built (just build-image).
run +ARGS:
    docker run --rm -v `pwd`:/work:z fits {{ARGS}}

# Builds the Docker image that's used for running the tests
test-build-image:
    docker build -f docker/Dockerfile-test -t fits-test .

# Runs the tests within a Docker container. Requires the image to already exist (just test-build-image). The image does NOT need to be rebuilt between runs.
test:
    docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test

# Runs the tests that match the pattern within a Docker container. Requires the image to already exist (just test-build-image). The image does NOT need to be rebuilt between runs.
test-filter PATTERN:
    docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test -Dtest={{PATTERN}}

# Applies the code formatter
format:
    mvn spotless:apply