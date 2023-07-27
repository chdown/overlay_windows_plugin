package com.ducdd.overlay_windows_plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry


/** OverlayWindowsPlugin */
class OverlayWindowsPlugin : FlutterPlugin, ActivityAware, PluginRegistry.ActivityResultListener {


    private var _context: Context? = null
    private var _mActivity: Activity? = null

    private var _flutterBinding: FlutterPlugin.FlutterPluginBinding? = null

    private var _serviceApi: OverlayWindowServiceApi? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.d("PLUGIN", "onAttachedToEngine overlay window service plugin");
        _context = flutterPluginBinding.applicationContext;
        _flutterBinding = flutterPluginBinding;
    }

    private fun dispose() {
        _flutterBinding = null;
        _context = null;
        _serviceApi?.destroy();
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        dispose();
        OverlayWindowApi.setUp(binding.binaryMessenger, null)
        Log.d("PLUGIN", "onDetachedFromEngine overlay window service plugin");
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        _mActivity = binding.activity
        if (_flutterBinding == null || _context == null) {
            return
        }

        _serviceApi =
            OverlayWindowServiceApi(_flutterBinding!!.binaryMessenger, _context!!, _mActivity!!)
        OverlayWindowApi.setUp(_flutterBinding!!.binaryMessenger, _serviceApi)

        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        dispose();
        _serviceApi?.destroy();
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding);
    }

    override fun onDetachedFromActivity() {
        dispose();
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(_context)
        } else true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Log.d(
            "onActivityResult",
            "requestCode: $requestCode, resultCode: $resultCode, data: ${data.toString()}"
        )
        if (requestCode == OverlayWindowServiceApi.REQUEST_CODE_FOR_OVERLAY_PERMISSION) {
            if (checkOverlayPermission())
                return true;
        }
        return false;
    }
}
