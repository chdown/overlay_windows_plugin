import 'dart:async';
import 'dart:convert';
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

  final StreamController _messageStreamController = StreamController.broadcast();
  final StreamController _touchEventStreamController = StreamController.broadcast();

  // static List<OverlayWindowView> overlayViews = [];

  OverlayWindowsPlugin(this.api) {
    _messageChannel = BasicMessageChannel<dynamic>(_messageChannelName, const JSONMessageCodec());
    _messageChannel?.setMessageHandler((message) async {
      await onMessage(message);
    });
  }

  Future<void> onMessage(dynamic message) async {
    try {
      log('OverlayWindowsPlugin ~ message:  $message');
      if (message['type'] == 'TouchEvent') {
        _touchEventStreamController.add(message);
        return;
      } else if (message['type'] == 'Action') {
        var method = jsonDecode(message['message']) as String;
        if (method == "Close") {
          await closeOverlayWindow(message['overlayWindowId'] as String);
        }
        return;
      }
      _messageStreamController.add(message);
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

  Stream<dynamic> get messageStream => _messageStreamController.stream;
  Stream<dynamic> get touchEventStream => _touchEventStreamController.stream;

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
