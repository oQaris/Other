-injars  build/libs/bvu.jar
-outjars build/libs/bvu-min.jar
-printmapping build/libs/bvu.map

-libraryjars <java.home>/lib/jrt-fs.jar
-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)

-optimizationpasses 3
-overloadaggressively
-repackageclasses ''
-allowaccessmodification

-keep class bot_version_updater.BotVersionUpdaterKt {
    void main(java.lang.String[]);
}

# Kotlin
-keep class kotlin.Metadata { *; }
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }

-ignorewarnings
