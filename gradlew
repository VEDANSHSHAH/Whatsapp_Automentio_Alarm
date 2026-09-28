#!/bin/sh
# Minimal Gradle Wrapper launcher. The checked-in wrapper JAR downloads Gradle 8.10.2 on first use.
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
exec java ${JAVA_OPTS:-} ${GRADLE_OPTS:-} -Dorg.gradle.appname=gradlew -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
