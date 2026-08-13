# Research sources

The redesign and performance decisions in this project were based on the following official sources.

| Topic | Source | Implementation use |
|---|---|---|
| Material 3 Expressive | https://m3.material.io/blog/building-with-m3-expressive | Deliberate hierarchy through color, shape, containment, and size. |
| Material 3 in Compose | https://developer.android.com/develop/ui/compose/designsystems/material3 | Compose `MaterialTheme`, semantic colors, and Material components. |
| Android memory efficiency | https://developer.android.com/blog/posts/prioritizing-memory-efficiency-essential-steps-for-android-17 | R8 optimization and memory-conscious production design. |
| ProfilingManager overview | https://developer.android.com/topic/performance/tracing/profiling-manager/overview | Explicit, version-gated developer diagnostics only. |
| App-driven profiling | https://developer.android.com/topic/performance/tracing/profiling-manager/how-to-capture | Heap profiles requested asynchronously, with no automatic collection or upload. |
