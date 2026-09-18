# Firebase Realtime Database serializes/deserializes these model classes
# via reflection — field names must stay intact to match DB keys.
-keep class abhishek.aniassist.data.model.** { *; }

# Keep constructors/signatures for anything Firebase instantiates
-keepclassmembers class abhishek.aniassist.data.model.** {
    <init>(...);
    <fields>;
    <methods>;
}
