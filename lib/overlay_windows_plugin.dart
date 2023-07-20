import 'package:overlay_windows_plugin/overlay_windows_api.g.dart';

class OverlayWindowsPlugin {
  final OverlayWindowApi api;

  static OverlayWindowsPlugin? _instance;

  static OverlayWindowsPlugin get defaultInstance {
    _instance ??= OverlayWindowsPlugin(OverlayWindowApi());
    return _instance!;
  }

  OverlayWindowsPlugin(this.api);

  Future<bool> isPermissionGranted() async {
    return api.isPermissionGranted();
  }

  Future<void> requestPermission() async {
    return api.requestPermission();
  }

  Future<void> showOverlayWindow(String overlayWindowId, String entryPointName, OverlayWindowConfig config) async {
    await api.showOverlayWindows(overlayWindowId, entryPointName, config);
  }

  Future<void> closeOverlayWindow(String overlayWindowId) async {
    await api.closeOverlayWindows(overlayWindowId);
  }

  Future<bool> isActive(String overlayWindowId) async {
    var result = await api.isActive(overlayWindowId);
    return result;
  }

  Future<void> setFlags(String overlayWindowId, OverlayFlag flag) async {
    return await api.setFlags(overlayWindowId, flag);
  }
}
