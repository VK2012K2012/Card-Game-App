# Card Game Hub release configuration.
# R8 is enabled by the release build type with Android's optimized default rule set.

# Room discovers the database implementation through generated names at runtime.
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }

# Keep useful information in release crash traces while allowing R8 to optimize application code.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
