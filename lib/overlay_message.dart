import 'dart:convert';

class OverlayMessage {
  String overlayWindowId;
  dynamic message;
  String type;

  OverlayMessage(this.overlayWindowId, this.message, this.type);

  factory OverlayMessage.fromJson(Map<String, dynamic> json) {
    var message = json['message'];
    var overlayMessage = message is Map ? json['message'] as dynamic : jsonDecode(json['message']) as dynamic;
    var id = json['overlayWindowId'] != null ? json['overlayWindowId'].toString() : "";
    return OverlayMessage(
      id,
      overlayMessage,
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
