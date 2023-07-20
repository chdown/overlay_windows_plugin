package com.ducdd.overlay_windows_plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngineCache
import java.util.*

@Suppress("DEPRECATION")
class OverlayView(val viewId: String, context: Context, windowManager: WindowManager,
                  val overlayViewName: String, windowConfig: WindowConfig) : View.OnTouchListener {
    private val _context: Context = context
    private val _windowManager = windowManager
    private var _windowConfig: WindowConfig = windowConfig
    private var flutterView: FlutterView? = null
    private var _windowSize: Point = Point()
    private var lastX = 0f
    private var lastY: Float = 0f
    private var lastYPosition = 0
    private var dragging = false
    private var mTrayAnimationTimer: Timer? = null
    private var mTrayTimerTask: TrayAnimationTimerTask? = null

    private val DEFAULT_NAV_BAR_HEIGHT_DP = 48
    private val DEFAULT_STATUS_BAR_HEIGHT_DP = 25
    private var mStatusBarHeight = -1
    private var mNavigationBarHeight = -1
    private val MAXIMUM_OPACITY_ALLOWED_FOR_S_AND_HIGHER = 0.8f

    private val clickableFlag: Int =
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

    fun remove(){
        if(flutterView != null){
            _windowManager.removeView(flutterView!!)
            flutterView!!.detachFromFlutterEngine()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun add(){
        val engine = FlutterEngineCache.getInstance()[overlayViewName]
        engine!!.lifecycleChannel.appIsResumed()

        flutterView = FlutterView(_context, FlutterTextureView(_context))
        flutterView!!.attachToFlutterEngine(FlutterEngineCache.getInstance()[overlayViewName]!!)
        flutterView!!.fitsSystemWindows = true
        flutterView!!.isFocusable = true
        flutterView!!.isFocusableInTouchMode = true
        flutterView!!.setBackgroundColor(Color.TRANSPARENT)

        _windowManager.defaultDisplay.getSize(_windowSize)

        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
            if (_windowConfig.width == -1999) -1 else _windowConfig.width,
            if (_windowConfig.height != -1999) _windowConfig.height else screenHeight(),
            0,
            -statusBarHeightPx(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            _windowConfig.flag or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && _windowConfig.flag == clickableFlag) {
            params.alpha = MAXIMUM_OPACITY_ALLOWED_FOR_S_AND_HIGHER
        }
        params.gravity = _windowConfig.gravity

        flutterView!!.setOnTouchListener(this);

        _windowManager.addView(flutterView, params)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun screenHeight(): Int {
        val display: Display = _windowManager.defaultDisplay
        val dm = DisplayMetrics()
        display.getRealMetrics(dm)
        return if (inPortrait()) dm.heightPixels + statusBarHeightPx() + navigationBarHeightPx() else dm.heightPixels + statusBarHeightPx()
    }

    @SuppressLint("InternalInsetResource")
    private fun statusBarHeightPx(): Int {
        if (mStatusBarHeight == -1) {
            val statusBarHeightId: Int =
                _context.resources!!.getIdentifier("status_bar_height", "dimen", "android")

            mStatusBarHeight = if (statusBarHeightId > 0) {
                _context.resources!!.getDimensionPixelSize(statusBarHeightId)
            } else {
                dpToPx(DEFAULT_STATUS_BAR_HEIGHT_DP)
            }
        }
        return mStatusBarHeight
    }


    @SuppressLint("InternalInsetResource")
    private fun navigationBarHeightPx(): Int {
        if (mNavigationBarHeight == -1) {
            val navBarHeightId: Int =
                _context.resources!!.getIdentifier("navigation_bar_height", "dimen", "android")
            if (navBarHeightId > 0) {
                mNavigationBarHeight = _context.resources!!.getDimensionPixelSize(navBarHeightId)
            } else {
                mNavigationBarHeight = dpToPx(DEFAULT_NAV_BAR_HEIGHT_DP)
            }
        }
        return mNavigationBarHeight
    }


    private fun getDrawableResourceId(resType: String, name: String): Int {
        return _context.resources.getIdentifier(
            String.format("ic_%s", name),
            resType,
            _context.packageName
        )
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            (dp.toString() + "").toFloat(),
            _context.resources!!.displayMetrics
        ).toInt()
    }

    private fun inPortrait(): Boolean {
        return _context.resources!!.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        val enableDrag = _windowConfig?.enableDrag ?: false
        if (enableDrag) {
            val params: WindowManager.LayoutParams =
                flutterView!!.layoutParams as WindowManager.LayoutParams
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    dragging = false
                    lastX = event.rawX
                    lastY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx: Float = event.rawX - lastX
                    val dy: Float = event.rawY - lastY
                    if (!dragging && dx * dx + dy * dy < 25) {
                        return false
                    }
                    lastX = event.rawX
                    lastY = event.rawY
                    val xx: Int = params.x + dx.toInt()
                    val yy: Int = params.y + dy.toInt()
                    params.x = xx
                    params.y = yy
                    _windowManager.updateViewLayout(flutterView, params)
                    dragging = true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    lastYPosition = params.y
                    if (_windowConfig.positionGravity != "none") {
                        _windowManager.updateViewLayout(flutterView, params)
                        mTrayAnimationTimer = Timer()
                        mTrayTimerTask = TrayAnimationTimerTask(
                            _windowManager,
                            flutterView!!,
                            _windowConfig,
                            _windowSize,
                            lastYPosition,
                            mTrayAnimationTimer!!
                        )
                        mTrayAnimationTimer?.schedule(mTrayTimerTask, 0, 25)
                    }
                    return false
                }
                else -> return false
            }
            return false
        }
        return false
    }
}