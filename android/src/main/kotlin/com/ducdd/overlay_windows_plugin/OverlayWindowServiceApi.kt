package com.ducdd.overlay_windows_plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.JSONMessageCodec


class OverlayWindowServiceApi(
    binaryMessenger: BinaryMessenger,
    context: Context,
    activity: Activity
) : OverlayWindowApi {
    private var _context: Context = context
    private var _activity: Activity = activity
    private var engineGroup = FlutterEngineGroup(_context)



    companion object {
        const val REQUEST_CODE_FOR_OVERLAY_PERMISSION = 1248
        lateinit var mainMessageChannel: BasicMessageChannel<Any?>
    }

    private val messageChannel = BasicMessageChannel(
        binaryMessenger,
        OverlayConstants.MESSAGE_CHANNEL,
        JSONMessageCodec.INSTANCE
    )

    private var overlayMessageChannels = HashMap<String, BasicMessageChannel<Any?>?>();

    init {
        mainMessageChannel = messageChannel;
        messageChannel.setMessageHandler { message, _ ->
            try {
                Log.d("messageChannel", message.toString())
                //send message to overlay views when received message from main channel
                overlayMessageChannels.forEach {
                    Log.d("SendMessage: ", it.key)
                    it.value?.send(message)
                }
            }
            catch (ex: java.lang.Exception){
                Log.d("messageChannel error", ex.toString())
            }
        }
    }

    fun destroy(){
        overlayMessageChannels.clear();
        messageChannel.setMessageHandler(null);
    }

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

    override fun showOverlayWindows(
        overlayWindowId: String,
        entryPointName: String,
        overlayWindowConfig: OverlayWindowConfig
    ) {
        if (!isPermissionGranted()) {
            throw Exception("PERMISSION:overlay permission is not enabled");
        }

        var engine = FlutterEngineCache.getInstance().get(overlayWindowId)
        if (engine == null) {
            val dEntry = DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                entryPointName
            )

            engine = engineGroup.createAndRunEngine(_context, dEntry)
            FlutterEngineCache.getInstance().put(overlayWindowId, engine)


            var binaryMessenger = engine!!.dartExecutor.binaryMessenger;

            overlayMessageChannels[overlayWindowId] = BasicMessageChannel(
                binaryMessenger,
                OverlayConstants.MESSAGE_CHANNEL,
                JSONMessageCodec.INSTANCE
            )
        }

        var windowConfig = WindowConfig();

        windowConfig.width = overlayWindowConfig.width?.toInt() ?: -1
        windowConfig.height = overlayWindowConfig.height?.toInt() ?: -1
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

        intent.action = "showOverlayWindows";

        _context.startService(intent).runCatching { true }.getOrElse { false }
    }

    override fun closeOverlayWindows(overlayWindowId: String) {
        val intent = Intent(_context, OverlayService::class.java)
        intent.action = "closeOverlayWindows";
        intent.putExtra(OverlayService.CLOSE_OVERLAY_WINDOW, true)
        intent.putExtra(OverlayService.OVERLAY_WINDOW_ID, overlayWindowId)

        overlayMessageChannels[overlayWindowId] = null

        _context.startService(intent).runCatching { true }.getOrElse { false }
    }

    override fun isActive(overlayWindowId: String): Boolean {
        val intent = Intent(_context, OverlayService::class.java)
        intent.action = "isActive";
        intent.putExtra(OverlayService.CLOSE_OVERLAY_WINDOW, true)
        intent.putExtra(OverlayService.OVERLAY_WINDOW_ID, overlayWindowId)

        return _context.startService(intent).runCatching { true }.getOrElse { false }
    }

    override fun setFlags(overlayWindowId: String, flag: OverlayFlag) {
        val intent = Intent(_context, OverlayService::class.java)
        intent.action = "setFlags";
        intent.putExtra(OverlayService.OVERLAY_WINDOW_FLAGS, flag.name)
        intent.putExtra(OverlayService.OVERLAY_WINDOW_ID, overlayWindowId)

        _context.startService(intent).runCatching { true }.getOrElse { false }
    }

    override fun resize(overlayWindowId: String, width: Long, height: Long) {
        val intent = Intent(_context, OverlayService::class.java)
        intent.action = "resize";
        intent.putExtra(OverlayService.OVERLAY_WINDOW_SIZE_WIDTH, width.toInt())
        intent.putExtra(OverlayService.OVERLAY_WINDOW_SIZE_HEIGHT, height.toInt())
        intent.putExtra(OverlayService.OVERLAY_WINDOW_ID, overlayWindowId)

        _context.startService(intent).runCatching { true }.getOrElse { false }
    }
}