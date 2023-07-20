import 'package:flutter_test/flutter_test.dart';
import 'package:overlay_windows_plugin/overlay_windows_plugin.dart';
import 'package:overlay_windows_plugin/overlay_windows_plugin_method_channel.dart';
import 'package:overlay_windows_plugin/overlay_windows_plugin_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockOverlayWindowsPluginPlatform with MockPlatformInterfaceMixin implements OverlayWindowsPluginPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final OverlayWindowsPluginPlatform initialPlatform = OverlayWindowsPluginPlatform.instance;

  test('$MethodChannelOverlayWindowsPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelOverlayWindowsPlugin>());
  });

  test('getPlatformVersion', () async {
    OverlayWindowsPlugin overlayWindowsPlugin = OverlayWindowsPlugin();
    MockOverlayWindowsPluginPlatform fakePlatform = MockOverlayWindowsPluginPlatform();
    OverlayWindowsPluginPlatform.instance = fakePlatform;

    // expect(await overlayWindowsPlugin.getPlatformVersion(), '42');
  });
}
