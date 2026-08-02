# Keep kotlinx.serialization rules
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keep,includedescriptorclasses class com.example.wheelpicker.**$$serializer { *; }
-keepclassmembers class com.example.wheelpicker.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.wheelpicker.** {
    kotlinx.serialization.KSerializer serializer(...);
}
