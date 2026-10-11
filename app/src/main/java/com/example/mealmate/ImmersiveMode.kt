package com.example.mealmate

import android.app.Activity
import android.os.Build
import android.view.View

object ImmersiveMode {

    fun enable(activity: Activity) {

        @Suppress("DEPRECATION")
        activity.window.decorView.systemUiVisibility =
            (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.navigationBarDividerColor =
                android.graphics.Color.TRANSPARENT
        }
    }
}