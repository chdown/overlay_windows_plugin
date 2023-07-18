
import 'overlay_windows_plugin_platform_interface.dart';

class OverlayWindowsPlugin {
  Future<String?> getPlatformVersion() {
    return OverlayWindowsPluginPlatform.instance.getPlatformVersion();
  }
}
