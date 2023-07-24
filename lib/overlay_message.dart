import 'dart:convert';

class OverlayMessage {
  String overlayWindowId;
  dynamic message;
  String type;

  OverlayMessage(this.overlayWindowId, this.message, this.type);

  factory OverlayMessage.fromJson(Map<String, dynamic> json) {
    return OverlayMessage(
      json['overlayWindowId'] as String,
      jsonDecode(json['message']) as dynamic,
      json['type'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'overlayWindowId': overlayWindowId,
      'message': jsonEncode(message),
      'type': type,
    };
  }
}
