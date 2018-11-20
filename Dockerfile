###############################################################
###############################################################
#### BUILD IMAGE
#### OpenJDK image produces weird results with JLink (400mb + sizes)
FROM alpine:3.8 AS signalbuild
ENV JAVA_HOME=/opt/jdk \
    PATH=${PATH}:/opt/jdk/bin \
    LANG=C.UTF-8

RUN set -ex && \
    apk add --no-cache bash && \
    wget https://download.java.net/java/early_access/alpine/14/binaries/openjdk-12-ea+14_linux-x64-musl_bin.tar.gz -O jdk.tar.gz && \
    mkdir -p /opt/jdk && \
    tar zxvf jdk.tar.gz -C /opt/jdk --strip-components=1 && \
    rm jdk.tar.gz && \
    rm /opt/jdk/lib/src.zip

RUN mkdir -p /usr/src/mods/jars
RUN mkdir -p /usr/src/mods/compiled

COPY . /usr/src
WORKDIR /usr/src

# Compule all Java Code
RUN javac -Xlint:unchecked -d /usr/src/mods/compiled --module-source-path /usr/src/src $(find src -name "*.java")
# Create Module Jar for each module
RUN jar --create --file /usr/src/mods/jars/joostvdg.jpb.api.jar --module-version 1.0 -C /usr/src/mods/compiled/joostvdg.jpb.api .
RUN jar --create --file /usr/src/mods/jars/joostvdg.jpb.core.jar --module-version 1.0 -C /usr/src/mods/compiled/joostvdg.jpb.core .
RUN jar --create --file /usr/src/mods/jars/joostvdg.jpb.cli.jar --module-version 1.0  -e com.github.joostvdg.jpb.cli.JpbApp\
    -C /usr/src/mods/compiled/joostvdg.jpb.cli .
RUN jar --create --file /usr/src/mods/jars/joostvdg.jpb.core.test.jar --module-version 1.0  -e com.github.joostvdg.jpb.core.test.ParseChangeListTest \
    -C /usr/src/mods/compiled/joostvdg.jpb.core.test .

RUN rm -rf /usr/bin/jpb-test-image
RUN jlink \
    --verbose \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --strip-debug \
    --limit-modules java.base \
    --launcher jpb-test=joostvdg.jpb.core.test \
    --module-path /usr/src/mods/jars/:$JAVA_HOME/jmods \
    --add-modules joostvdg.jpb.core.test \
    --add-modules joostvdg.jpb.core \
    --add-modules joostvdg.jpb.api \
     --output /usr/bin/jpb-test-image
RUN /usr/bin/jpb-test-image/bin/java --list-modules
RUN /usr/bin/jpb-test-image/bin/jpb-test

RUN rm -rf /usr/bin/jpb-image
RUN jlink \
    --verbose \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --strip-debug \
    --limit-modules java.base \
    --launcher jpb=joostvdg.jpb.cli \
    --module-path /usr/src/mods/jars/:$JAVA_HOME/jmods \
    --add-modules joostvdg.jpb.cli \
    --add-modules joostvdg.jpb.api \
    --add-modules joostvdg.jpb.core \
     --output /usr/bin/jpb-image
RUN /usr/bin/jpb-image/bin/java --list-modules

###############################################################
###############################################################
##### RUNTIME IMAGE - ALPINE
FROM panga/alpine:3.8-glibc2.27
LABEL authors="Joost van der Griendt <joostvdg@gmail.com>"
LABEL version="0.1.0"
LABEL description="Docker image for running Jenkins Pipeline Binary"
ENV DATE_CHANGED="20181014-2035"
ENV JAVA_OPTS="-XX:+UseCGroupMemoryLimitForHeap -XX:+UnlockExperimentalVMOptions"
COPY --from=signalbuild /usr/bin/jpb-image/ /usr/bin/jpb
ENTRYPOINT ["/usr/bin/jpb/bin/jpb"]

