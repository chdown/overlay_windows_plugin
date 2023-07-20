package com.ducdd.overlay_windows_plugin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi


class OverlayService : Service(), java.io.Serializable {
    companion object {
        const val CLOSE_OVERLAY_WINDOW = "close_overlay_window"
        const val OVERLAY_WINDOW_NAME = "overlay_window_name"
        const val WINDOW_CONFIG = "window_config"
        const val OVERLAY_WINDOW_ID = "overlay_window_id"
    }

    private var views = mutableListOf<OverlayView>()
    private var windowManager: WindowManager? = null

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onDestroy() {
        views.clear()
        Log.d("OverLay", "Destroying the overlay window service")
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var windowConfig = WindowConfig()
        var overlayViewName = "";
        var viewId = "";

        if (intent.extras!!.containsKey(OVERLAY_WINDOW_ID)) {
            viewId = intent.getStringExtra(OVERLAY_WINDOW_ID)!!
        }

        if(viewId == ""){
            Log.d("onStartCommand", "view id is not provided")
            return START_STICKY
        }

        if (intent.extras!!.containsKey(WINDOW_CONFIG)) {
            windowConfig = intent.getSerializableExtra(WINDOW_CONFIG) as WindowConfig
        }

        if (intent.extras!!.containsKey(OVERLAY_WINDOW_NAME)) {
            overlayViewName = intent.getStringExtra(OVERLAY_WINDOW_NAME)!!
        }

        val isCloseWindow: Boolean = intent.getBooleanExtra(CLOSE_OVERLAY_WINDOW, false)
        if (isCloseWindow) {
            if (windowManager != null) {
                Log.d("onStartCommand", "remove the view $overlayViewName")

                var view = views.find { x -> x.viewId == viewId }

                views.remove(view)

                view?.remove()
            }
            return START_STICKY
        }

        Log.d("onStartCommand", "Service started")

        var existView = views.find { x -> x.viewId == viewId }
        if (existView != null) {
            Log.d("onStartCommand", "the view $overlayViewName has existed")
            return START_STICKY
        }

        windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager

        var newOverlayView = OverlayView(viewId, applicationContext, windowManager!!, overlayViewName, windowConfig)
        newOverlayView.add()
        views!!.add(newOverlayView)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                OverlayConstants.CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager? = getSystemService(NotificationManager::class.java)
            assert(manager != null)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}