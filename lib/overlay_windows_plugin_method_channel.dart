import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'overlay_windows_plugin_platform_interface.dart';

/// An implementation of [OverlayWindowsPluginPlatform] that uses method channels.
class MethodChannelOverlayWindowsPlugin extends OverlayWindowsPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('overlay_windows_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
