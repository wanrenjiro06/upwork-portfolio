# Keep the JavaScript bridge interface used by the WebView so R8/ProGuard
# does not strip methods called from injected JavaScript.
-keepclassmembers class com.fox.random.MainActivity$WebAppBridge {
    public *;
}
