import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/overlay_windows_api.g.dart',
  kotlinOut: 'android/src/main/kotlin/com/ducdd/overlay_windows_plugin/OverlayWindowApi.g.kt',
))

/// Placement of overlay within the screen.
enum OverlayAlignment { topLeft, topCenter, topRight, centerLeft, center, centerRight, bottomLeft, bottomCenter, bottomRight }

/// Type of dragging behavior for the overlay.
enum PositionGravity {
  /// The `PositionGravity.none` will allow the overlay to postioned anywhere on the screen.
  none,

  /// The `PositionGravity.right` will allow the overlay to stick on the right side of the screen.
  right,

  /// The `PositionGravity.left` will allow the overlay to stick on the left side of the screen.
  left,

  /// The `PositionGravity.auto` will allow the overlay to stick either on the left or right side of the screen depending on the overlay position.
  auto,
}

enum OverlayFlag {
  /// Window flag: this window can never receive touch events.
  /// Usefull if you want to display click-through overlay
  clickThrough,

  /// Window flag: this window won't ever get key input focus
  /// so the user can not send key or other button events to it.
  defaultFlag,

  /// Window flag: allow any pointer events outside of the window to be sent to the windows behind it.
  /// Usefull when you want to use fields that show keyboards.
  focusPointer,
}

/// The level of detail displayed in notifications on the lock screen.
enum NotificationVisibility {
  /// Show this notification in its entirety on all lockscreens.
  visibilityPublic,

  /// Do not reveal any part of this notification on a secure lockscreen.
  visibilitySecret,

  /// Show this notification on all lockscreens, but conceal sensitive or private information on secure lockscreens.
  visibilityPrivate
}

class OverlayWindowConfig {
  int? height;
  int? width;
  OverlayAlignment? alignment;
  NotificationVisibility? visibility;
  OverlayFlag? flag;
  String? overlayTitle;
  String? overlayContent;
  bool? enableDrag;
  PositionGravity? positionGravity;
}

@HostApi()
abstract class OverlayWindowApi {
  bool isPermissionGranted();
  void requestPermission();
  void showOverlayWindows(String overlayWindowId, String entryPointName, OverlayWindowConfig config);
  void closeOverlayWindows(String overlayWindowId);
  bool isActive(String overlayWindowId);
  bool updateShow(String overlayWindowId,bool isShow);
  bool isShow(String overlayWindowId);
  void setFlags(String overlayWindowId, OverlayFlag flag);
  void resize(String overlayWindowId, int width, int height);
}
