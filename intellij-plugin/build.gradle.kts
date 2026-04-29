import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.kibernet"
version = "1.0.0"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        marketplace()
    }
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.1.7")
        plugin("com.tang", "1.4.20-IDEA251")
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    buildSearchableOptions = false
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
            untilBuild = provider { "251.*" }
        }
        changeNotes.set("""<ul><li>Initial 1.0.0 release with Windows Lua attach/launch debugging support.</li></ul>""")
    }
}
