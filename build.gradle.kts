plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.arthur"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.lavalink.dev/releases")
    maven("https://jitpack.io")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Lavalink Plugin API
    compileOnly("dev.arbjerg.lavalink:plugin-api:4.0.8")
    
    // MongoDB Driver
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    
    // JSON Processing (já incluído no Lavalink via Jackson)
    // Não precisa adicionar org.json
}

tasks {
    shadowJar {
        archiveBaseName.set("lavalink-security-plugin")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
        
        // Relocate dependencies to avoid conflicts
        relocate("org.mongodb", "com.arthur.security.libs.mongodb")
        relocate("com.mongodb", "com.arthur.security.libs.mongodb.client")
        relocate("org.bson", "com.arthur.security.libs.bson")
        
        // Exclude unnecessary files
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
