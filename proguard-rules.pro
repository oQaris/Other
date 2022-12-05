# injars = your shadowed jar
-injars  build/libs/Other.jar
# outjars = the name of the new obfuscated/minified jar
-outjars build/libs/Other-min.jar

-libraryjars "C:\Users\oQaris\Downloads\rt.jar"
-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)

-keep,allowobfuscation,allowoptimization class me.oqaris.properties_collector.StarterKt

# Kotlin
-keep class kotlin.Metadata { *; }
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }

-ignorewarnings
