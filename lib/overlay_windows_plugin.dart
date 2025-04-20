import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:overlay_windows_plugin/overlay_message.dart';
import 'package:overlay_windows_plugin/overlay_windows_api.g.dart';

class OverlayWindowsPlugin {
  final OverlayWindowApi api;

  static OverlayWindowsPlugin? _instance;

  static OverlayWindowsPlugin get defaultInstance {
    _instance ??= OverlayWindowsPlugin(OverlayWindowApi());
    return _instance!;
  }

  static final StreamController overlayMessageStreamController = StreamController.broadcast();

  final _messageChannelName = 'dev.ducdd.OverlayWindowApi.messageChannel';

  BasicMessageChannel? _messageChannel;

  final StreamController<OverlayMessage> _messageStreamController = StreamController.broadcast();
  final StreamController<OverlayMessage> _touchEventStreamController = StreamController.broadcast();

  // static List<OverlayWindowView> overlayViews = [];

  OverlayWindowsPlugin(this.api) {
    _messageChannel = BasicMessageChannel<dynamic>(_messageChannelName, const JSONMessageCodec());
    _messageChannel?.setMessageHandler((message) async {
      await onMessage(message);
    });
  }

  Future<void> onMessage(dynamic message) async {
    try {
      // log('OverlayWindowsPlugin ~ message:  $message');
      var overlayMessage = OverlayMessage.fromJson(message);
      if (overlayMessage.type == 'TouchEvent') {
        _touchEventStreamController.add(overlayMessage);
        return;
      } else if (overlayMessage.type == 'Action') {
        var method = overlayMessage.message as String;
        if (method == "Close") {
          await closeOverlayWindow(overlayMessage.overlayWindowId);
        }
        return;
      }
      _messageStreamController.add(overlayMessage);
    } catch (e) {
      log('onMessage error: $e');
    }
  }

  Future<bool> isPermissionGranted() async {
    try {
      return api.isPermissionGranted();
    } catch (e) {
      log('isPermissionGranted error: $e');
      return false;
    }
  }

  Future<void> requestPermission() async {
    try {
      return api.requestPermission();
    } catch (e) {
      log('requestPermission error: $e');
    }
  }

  Future<void> showOverlayWindow(String overlayWindowId, String entryPointName, OverlayWindowConfig config) async {
    try {
      await api.showOverlayWindows(overlayWindowId, entryPointName, config);
    } catch (e) {
      log('showOverlayWindow error: $e');
    }
  }

  Stream<OverlayMessage> get messageStream => _messageStreamController.stream;
  Stream<OverlayMessage> get touchEventStream => _touchEventStreamController.stream;

  Future sendMessage(String overlayWindowId, dynamic message) async {
    try {
      var result = OverlayMessage(overlayWindowId, message, "message");
      await _messageChannel?.send(result.toJson());
    } catch (e) {
      log('sendMessage error: $e');
    }
  }

  Future<void> closeOverlayWindow(String overlayWindowId) async {
    try {
      await api.closeOverlayWindows(overlayWindowId);
    } catch (e) {
      log('closeOverlayWindow error: $e');
    }
  }

  Future<bool> isActive(String overlayWindowId) async {
    try {
      var result = await api.isActive(overlayWindowId);
      return result;
    } catch (e) {
      log('isActive error: $e');
      return false;
    }
  }

  Future<void> updateShow(String overlayWindowId, bool isShow) async {
    try {
      await api.updateShow(overlayWindowId, isShow);
    } catch (e) {
      log('updateShow error: $e');
    }
  }

  Future<bool> isShow(String overlayWindowId) async {
    try {
      var result = await api.isShow(overlayWindowId);
      return result;
    } catch (e) {
      log('isShow error: $e');
      return false;
    }
  }

  Future<void> setFlags(String overlayWindowId, OverlayFlag flag) async {
    try {
      return await api.setFlags(overlayWindowId, flag);
    } catch (e) {
      log('setFlags error: $e');
    }
  }

  Future<void> resize(String overlayWindowId, int width, int height) async {
    try {
      return await api.resize(overlayWindowId, width, height);
    } catch (e) {
      log('resize error: $e');
    }
  }
}
