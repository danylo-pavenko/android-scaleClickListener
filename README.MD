### OnScaleClickListener for Android views (Kotlin)
--------
#### Quick start
1. Call view method `setOnTouchListener`.
2. Add as listener `OnScaleClickListener` `object : OnScaleClickListener() { ... }`.

This all! YOu can user it.

```
Options:
private val duration: Long = DURATION,  // 100L - default
private val scale: Float = SCALE,       // 0.9f - default
private val withDebounced: Boolean = true,
private val debounceTime: Int = 1000

Set by OnScaleClickListener constructor
```