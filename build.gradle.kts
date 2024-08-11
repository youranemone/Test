plugins {
    id("java")
}

group = "org.testtask"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.mockito:mockito-junit-jupiter:3.12.4")
    testImplementation("org.mockito:mockito-inline:3.10.0")
}

tasks.test {
    useJUnitPlatform()
}