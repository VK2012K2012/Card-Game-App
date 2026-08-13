package com.example.performance

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ProfilingManager
import android.os.ProfilingResult
import androidx.annotation.RequiresApi

/**
 * Explicit, local-only memory diagnostics for developers.
 *
 * The app never starts profiling in normal play. A profile is requested only after a developer
 * presses the debug-only control in Settings. ProfilingManager owns the result file in app storage;
 * this bridge does not copy, retain, display, or upload the generated data.
 */
object MemoryDiagnostics {
    const val MIN_SUPPORTED_API = Build.VERSION_CODES.VANILLA_ICE_CREAM

    fun isSupported(): Boolean = Build.VERSION.SDK_INT >= MIN_SUPPORTED_API

    fun requestHeapProfile(context: Context, onStatus: (String) -> Unit) {
        if (!isSupported()) {
            onStatus("Memory diagnostics require Android 15 or newer.")
            return
        }
        requestHeapProfileApi35(context.applicationContext, onStatus)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun requestHeapProfileApi35(context: Context, onStatus: (String) -> Unit) {
        val callbackExecutor = java.util.concurrent.Executor { runnable ->
            Handler(Looper.getMainLooper()).post(runnable)
            Unit
        }
        val profilingManager = context.getSystemService(ProfilingManager::class.java)
        if (profilingManager == null) {
            onStatus("ProfilingManager is unavailable on this device.")
            return
        }

        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_HEAP_PROFILE,
            Bundle(),
            "card_game_hub_memory_check",
            null,
            callbackExecutor
        ) { result ->
            onStatus(result.toStatusMessage())
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun ProfilingResult.toStatusMessage(): String {
        return if (errorCode == ProfilingResult.ERROR_NONE) {
            "Heap profile requested. Inspect the local result through Android debugging tools."
        } else {
            "Profiling request was not accepted: ${errorMessage ?: "unknown system response"}"
        }
    }
}
