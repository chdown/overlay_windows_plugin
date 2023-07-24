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
        const val OVERLAY_WINDOW_FLAGS = "overlay_window_flags"
        const val OVERLAY_WINDOW_SIZE_WIDTH = "overlay_window_size_x"
        const val OVERLAY_WINDOW_SIZE_HEIGHT = "overlay_window_size_y"
    }

    private var _views = mutableListOf<OverlayView>()
    private var _windowManager: WindowManager? = null

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onDestroy() {
        _views.forEach {
            it.remove()
        }
        _views.clear()
        _windowManager = null;
        Log.d("OverLay", "Destroying the overlay window service")
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {


            var viewId = "";
            if (intent.extras!!.containsKey(OVERLAY_WINDOW_ID)) {
                viewId = intent.getStringExtra(OVERLAY_WINDOW_ID)!!
            }

            if (viewId == "") {
                Log.d("onStartCommand", "view id is not provided")
                return START_STICKY
            }

            val action = intent.action

            if (action == "closeOverlayWindows") {
                if (_windowManager != null) {
                    Log.d("onStartCommand", "remove the view $viewId")
                    var view = _views.find { x -> x.viewId == viewId }

                    _views.remove(view)

                    view?.remove()
                }
//            return START_STICKY
            } else if (action == "showOverlayWindows") {
                var windowConfig = WindowConfig()

                if (intent.extras!!.containsKey(WINDOW_CONFIG)) {
                    windowConfig = intent.getSerializableExtra(WINDOW_CONFIG) as WindowConfig
                }

                var overlayViewName = "";
                if (intent.extras!!.containsKey(OVERLAY_WINDOW_NAME)) {
                    overlayViewName = intent.getStringExtra(OVERLAY_WINDOW_NAME)!!
                }

                Log.d("onStartCommand", "Service started")

                var existView = _views.find { x -> x.viewId == viewId }
                if (existView != null) {
                    Log.d("onStartCommand", "the view $overlayViewName has existed")
                    return START_STICKY
                }

                _windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager

                var newOverlayView = OverlayView(
                    viewId,
                    applicationContext,
                    _windowManager!!,
                    overlayViewName,
                    windowConfig
                )
                newOverlayView.add()
                _views!!.add(newOverlayView)
            } else if (action == "isActive") {
                var existView: OverlayView? =
                    _views.find { x -> x.viewId == viewId } ?: throw Exception("Inactive")
            } else if (action == "setFlags") {

                var existView: OverlayView? =
                    _views.find { x -> x.viewId == viewId } ?: throw Exception("Inactive")

                var flag = "";
                if (intent.extras!!.containsKey(OVERLAY_WINDOW_FLAGS)) {
                    flag = intent.getStringExtra(OVERLAY_WINDOW_FLAGS)!!
                }

                Log.d("setFlags", "set flag $flag to view $viewId")

                existView?.updateOverlayFlag(flag);
            } else if (action == "resize") {
                var existView: OverlayView? =
                    _views.find { x -> x.viewId == viewId } ?: throw Exception("Inactive")

                var width = 0;
                if (intent.extras!!.containsKey(OVERLAY_WINDOW_SIZE_WIDTH)) {
                    width = intent.getIntExtra(OVERLAY_WINDOW_SIZE_WIDTH, -1)
                }

                var height = 0;
                if (intent.extras!!.containsKey(OVERLAY_WINDOW_SIZE_HEIGHT)) {
                    height = intent.getIntExtra(OVERLAY_WINDOW_SIZE_HEIGHT, -1)
                }

                Log.d("resize", "width: $width, height: $height to view $viewId")

                existView?.resize(width, height);
            }
        } catch (e: java.lang.Exception) {
            Log.d("onStartCommand", e.toString())
        }
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