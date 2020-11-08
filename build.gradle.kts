plugins { kotlin("jvm").version("1.4.0") }

group = "com.github.exerosis.amex"
version = "1.0.0"

repositories { mavenCentral() }

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("io.mockk:mockk:1.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks.test { useJUnitPlatform() }
tasks.compileKotlin { kotlinOptions.jvmTarget = "1.8" }