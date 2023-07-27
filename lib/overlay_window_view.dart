import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:overlay_windows_plugin/overlay_message.dart';

class OverlayWindowView {
  String? overlayWindowId;

  final MethodChannel methodChannel = const MethodChannel('dev.ducdd.OverlayWindowApi.methodChannel');
  final StreamController<String> _stateStreamController = StreamController.broadcast();

  BasicMessageChannel<dynamic>? messageChannel = const BasicMessageChannel<dynamic>('dev.ducdd.OverlayWindowApi.messageChannel', JSONMessageCodec());
  final StreamController<OverlayMessage> _messageStreamController = StreamController.broadcast();

  Stream get stateChangedStream => _stateStreamController.stream;
  Stream<OverlayMessage> get messageStream => _messageStreamController.stream;

  OverlayWindowView() {
    methodChannel.setMethodCallHandler((call) async {
      onMethodHandler(call);
    });

    messageChannel?.setMessageHandler((message) async {
      onMessage(message);
    });
  }

  void onMethodHandler(MethodCall call) {
    try {
      if (call.method == "setOverlayWindowId") {
        var viewId = call.arguments as String?;
        if (viewId != null && overlayWindowId == null) {
          overlayWindowId = viewId;
          _stateStreamController.add(overlayWindowId!);
          log('overlayWindowId: $overlayWindowId');
        }
      } else {
        throw Exception('not implemented ${call.method}');
      }
    } catch (e) {
      log('onMethodHandler error: $e');
    }
  }

  void onMessage(dynamic message) {
    try {
      log('OverlayWindowView  ~ message:  $message');
      var overlayMessage = OverlayMessage.fromJson(message);
      _messageStreamController.add(overlayMessage);
    } catch (e) {
      log('onMessage error: $e');
    }
  }

  Future<void> close() async {
    try {
      if (overlayWindowId != null) {
        await messageChannel?.send(OverlayMessage(overlayWindowId!, "Close", 'Action').toJson());
      }
    } catch (e) {
      log('close error: $e');
    }
  }

  Future<void> sendMessage(dynamic message) async {
    try {
      if (overlayWindowId != null) {
        await messageChannel?.send(OverlayMessage(overlayWindowId!, message, 'Message').toJson());
      }
    } catch (e) {
      log('sendMessage error: $e');
    }
  }
}
