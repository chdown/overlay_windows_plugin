import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'overlay_windows_plugin_method_channel.dart';

abstract class OverlayWindowsPluginPlatform extends PlatformInterface {
  /// Constructs a OverlayWindowsPluginPlatform.
  OverlayWindowsPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static OverlayWindowsPluginPlatform _instance = MethodChannelOverlayWindowsPlugin();

  /// The default instance of [OverlayWindowsPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelOverlayWindowsPlugin].
  static OverlayWindowsPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [OverlayWindowsPluginPlatform] when
  /// they register themselves.
  static set instance(OverlayWindowsPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
