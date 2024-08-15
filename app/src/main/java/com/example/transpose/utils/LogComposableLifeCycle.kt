package com.example.transpose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LogComposableLifecycle(screenName: String) {
    val lifecycleOwner = LocalLifecycleOwner.current

    SideEffect {
        Logger.d("$screenName: Composition")
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> Logger.d("$screenName: onCreate")
                Lifecycle.Event.ON_START -> Logger.d("$screenName: onStart")
                Lifecycle.Event.ON_RESUME -> Logger.d("$screenName: onResume")
                Lifecycle.Event.ON_PAUSE -> Logger.d("$screenName: onPause")
                Lifecycle.Event.ON_STOP -> Logger.d("$screenName: onStop")
                Lifecycle.Event.ON_DESTROY -> Logger.d("$screenName: onDestroy")
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Logger.d("$screenName: Disposed")
        }
    }
}