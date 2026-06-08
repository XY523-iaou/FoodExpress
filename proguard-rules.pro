# FoodExpress ProGuard Rules

# Keep Firebase
-keepattributes Signature
-keepattributes *Annotation*

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Keep data models
-keep class com.foodexpress.core.model.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Coil
-dontwarn coil.**
-keep class coil.** { *; }
