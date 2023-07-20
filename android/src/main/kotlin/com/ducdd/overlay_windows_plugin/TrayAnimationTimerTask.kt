package com.ducdd.overlay_windows_plugin

import android.graphics.Point
import android.os.Handler
import android.view.WindowManager
import io.flutter.embedding.android.FlutterView
import java.util.*
import kotlin.math.abs

class TrayAnimationTimerTask(
    windowManager: WindowManager,
    flutterView: FlutterView,
    windowConfig: WindowConfig,
    windowSize: Point,
    lastYPosition: Int,
    timer: Timer
) : TimerTask() {
    private var _windowManager = windowManager;
    private var _flutterView = flutterView;
    private var _timer = timer;
    var szWindow = windowSize
    var mDestX = 0
    var mDestY: Int
    var params: WindowManager.LayoutParams =
        flutterView.layoutParams as WindowManager.LayoutParams

    private val mAnimationHandler: Handler = Handler()

    init {
        mDestY = lastYPosition
        when (windowConfig.positionGravity) {
            "auto" -> {
                mDestX =
                    if ((params.x + (flutterView.width / 2)) <= szWindow.x / 2) 0 else szWindow.x - flutterView.width
//                return
            }
            "left" -> {
                mDestX = 0
//                return
            }
            "right" -> {
                mDestX = szWindow.x - flutterView.width
//                return
            }
            else -> {
                mDestX = params.x
                mDestY = params.y
//                return
            }
        }
    }

    override fun run() {
        mAnimationHandler.post {
            params.x = (2 * (params.x - mDestX)) / 3 + mDestX
            params.y = (2 * (params.y - mDestY)) / 3 + mDestY
            _windowManager.updateViewLayout(_flutterView, params)
            if (abs(params.x - mDestX) < 2 && abs(params.y - mDestY) < 2) {
                this.cancel()
                _timer.cancel()
            }
        }
    }
}