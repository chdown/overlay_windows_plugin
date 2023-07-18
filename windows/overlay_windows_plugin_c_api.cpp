#include "include/overlay_windows_plugin/overlay_windows_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "overlay_windows_plugin.h"

void OverlayWindowsPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  overlay_windows_plugin::OverlayWindowsPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
