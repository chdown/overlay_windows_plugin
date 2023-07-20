package com.ducdd.overlay_windows_plugin

import OverlayWindowApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** OverlayWindowsPlugin */
class OverlayWindowsPlugin: FlutterPlugin, ActivityAware, PluginRegistry.ActivityResultListener{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private var _context: Context? = null
  private var _mActivity: Activity? = null
  private var messenger: BasicMessageChannel<Any>? = null
  private var pendingResult: Result? = null
  private var _flutterBinding: FlutterPlugin.FlutterPluginBinding? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.i("PLUGIN", "onAttachedToEngine touch service plugin");
    _context = flutterPluginBinding.applicationContext;
    _flutterBinding = flutterPluginBinding;
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    OverlayWindowApi.setUp(binding.binaryMessenger, null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    _mActivity = binding.activity
    if (_flutterBinding == null || _context == null) {
      return
    }

    var serviceApi = OverlayWindowServiceApi(_context!!, _mActivity!!)
    OverlayWindowApi.setUp(_flutterBinding!!.binaryMessenger, serviceApi)

    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    TODO("Not yet implemented")
  }
}
