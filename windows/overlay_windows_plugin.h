#ifndef FLUTTER_PLUGIN_OVERLAY_WINDOWS_PLUGIN_H_
#define FLUTTER_PLUGIN_OVERLAY_WINDOWS_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace overlay_windows_plugin {

class OverlayWindowsPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  OverlayWindowsPlugin();

  virtual ~OverlayWindowsPlugin();

  // Disallow copy and assign.
  OverlayWindowsPlugin(const OverlayWindowsPlugin&) = delete;
  OverlayWindowsPlugin& operator=(const OverlayWindowsPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace overlay_windows_plugin

#endif  // FLUTTER_PLUGIN_OVERLAY_WINDOWS_PLUGIN_H_
