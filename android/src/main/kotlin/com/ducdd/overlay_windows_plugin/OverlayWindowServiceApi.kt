package com.ducdd.overlay_windows_plugin

import OverlayFlag
import OverlayWindowApi
import OverlayWindowConfig
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor

class OverlayWindowServiceApi(context: Context, activity: Activity) : OverlayWindowApi {
    private var _context: Context = context
    private var _activity: Activity = activity

    private val REQUEST_CODE_FOR_OVERLAY_PERMISSION = 1248

    override fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(_context);
        }
        return true;
    }

    override fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + _activity.packageName)
            _activity.startActivityForResult(intent, REQUEST_CODE_FOR_OVERLAY_PERMISSION)
        }
    }

    override fun showOverlayWindows(overlayWindowId: String, entryPointName: String, overlayWindowConfig: OverlayWindowConfig) {
        if (!isPermissionGranted()) {
            throw Exception("PERMISSION:overlay permission is not enabled");
        }

        var engineGroup = FlutterEngineGroup(_context)

        val dEntry = DartExecutor.DartEntrypoint(
            FlutterInjector.instance().flutterLoader().findAppBundlePath(),
            entryPointName
        )

        val engine: FlutterEngine = engineGroup.createAndRunEngine(_context, dEntry)
        FlutterEngineCache.getInstance().put(entryPointName, engine)

        var windowConfig = WindowConfig();

        windowConfig.width = overlayWindowConfig.width?.toInt() ?: -1
        windowConfig.height = overlayWindowConfig.height?.toInt()  ?: -1
        windowConfig.enableDrag = overlayWindowConfig.enableDrag ?: false

        windowConfig.overlayTitle = overlayWindowConfig.overlayTitle ?: ""
        windowConfig.overlayContent = overlayWindowConfig.overlayContent ?: ""
        windowConfig.positionGravity = overlayWindowConfig.positionGravity?.name ?: ""
        windowConfig.setNotificationVisibility(overlayWindowConfig.visibility?.name ?: "")

        windowConfig.setGravityFromAlignment(overlayWindowConfig.alignment?.name ?: "center")
        windowConfig.setFlag(overlayWindowConfig.flag?.name ?: "flagNotFocusable")

        val intent = Intent(_context, OverlayService::class.java)

        intent.putExtra(OverlayService.WINDOW_CONFIG, windowConfig)
        intent.putExtra(OverlayService.OVERLAY_WINDOW_NAME, entryPointName)
        intent.putExtra(OverlayService.OVERLAY_WINDOW_ID, overlayWindowId)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        _context.startService(intent)
    }

    override fun closeOverlayWindows(overlayWindowId: String) {
        val i = Intent(_context, OverlayService::class.java)
        i.putExtra(OverlayService.CLOSE_OVERLAY_WINDOW, true)
        i.putExtra(OverlayService.OVERLAY_WINDOW_ID, overlayWindowId)
        _context.startService(i)
    }

    override fun isActive(entryPointName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun setFlags(entryPointName: String, flag: OverlayFlag) {
        TODO("Not yet implemented")
    }
}