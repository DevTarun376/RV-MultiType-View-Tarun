# Public API — keep all public classes and their public/protected members so
# consumer apps can call them after R8 runs on the final APK.
-keep public class com.tech.multitypeview.** { public protected *; }

# MultiTypeItem is a data class used in data binding expressions (@{model.label},
# @{model.isExpanded}, etc.). Data binding generates Java code that calls the
# Kotlin-generated getter methods (getLabel(), isExpanded(), etc.), so all members
# must survive R8 even if they look unreferenced from the consumer's own code.
-keepclassmembers class com.tech.multitypeview.model.MultiTypeItem { *; }

# Keep MediaKind enum — enum values() and valueOf() are called reflectively by
# data binding and Kotlin serialisation helpers.
-keepclassmembers enum com.tech.multitypeview.model.MediaKind {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data binding generated classes — AGP generates these at build time from
# the library's XML layouts. R8 cannot see them as "used" until the consumer
# app's build wires them in, so they must be kept explicitly.
-keep class com.tech.multitypeview.databinding.** { *; }
