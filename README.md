# overlay_windows_plugin

-   The Flutter plugin to integrate with the Window Manager Service in the Android for showing overlay windows over other apps.
-   There is no limit to the number of overlays that will be shown at the same time and each overlay window could use a different UI or layout as desired.
-   All the overlay windows could be able to communicate with the main view or others via a message channel.

This plugin currently supports Android only.

## Demo

![Demo](doc/demo_overlay_plugin.gif 'overlay windows plugin')

## How to use

-   Installation:

```
    dependencies:
        overlay_windows_plugin: any
```

-   Set up the permission in the AndroidManifest.xml in the Android folder:

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    ...
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <application>
        ...
        <service android:name="com.ducdd.overlay_windows_plugin.OverlayService"
            android:exported="false" />
        ...
    </application>
</manifest>
```

-   Indicate the entry points to build the layout & UI of the overlay windows. These entry points must be defined in the `main.dart` file.

```
@pragma("vm:entry-point")
void overlayMain1() {
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SafeArea(
        child: OverlayMain1(),
      ),
    ),
  );
}

@pragma("vm:entry-point")
void overlayMain2() {
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: OverlayMain2(),
    ),
  );
}
```

-   Declare the plugin instance

```
final _overlayWindowsPlugin = OverlayWindowsPlugin.defaultInstance;
```

-   Open a new overlay windows with the specific entry point

```
    _overlayWindowsPlugin.showOverlayWindow(
        id,
        "overlayMain1",
        OverlayWindowConfig(
        width: 300,
        height: 100,
        enableDrag: true,
    ));
```

-   The available options when opening a overlay window

```
  OverlayWindowConfig({
    this.height,
    this.width,
    this.alignment,
    this.flag,
    this.enableDrag,
    this.positionGravity,
  });
```

-   Close the overlay windows

```
_overlayWindowsPlugin.closeOverlayWindow("overlay window id");
```

-   Send message to all opened overlay windows from main view

```
_overlayWindowsPlugin.sendMessage("", "Hello from main");
```

-   To receive the messages from overlay windows or Touch events from overlay windows, list the corresponding stream

```
_overlayWindowsPlugin.messageStream.listen((event) {
      setState(() {
        message.add(event);
      });
    });

_overlayWindowsPlugin.touchEventStream.listen((event) {});
```

-   on each overlay window view, initiate a view to communicate with the main view

```
final view = OverlayWindowView();
```

```
view.stateChangedStream.listen((event) {
    setState(() {
        viewId = event;
    });
});

view.messageStream.listen((mes) {
    setState(() {
        message = mes.message as String;
    });
});

view.sendMessage('Hello from overlay $viewId');

```

### Overlay window plugin API

```
OverlayWindowApi {
    Stream<OverlayMessage> get messageStream;
    Stream<OverlayMessage> get touchEventStream;

    bool isPermissionGranted();
    void requestPermission();
    void showOverlayWindows(String overlayWindowId, String entryPointName, OverlayWindowConfig config);
    void closeOverlayWindows(String overlayWindowId);
    bool isActive(String overlayWindowId);
    void setFlags(String overlayWindowId, OverlayFlag flag);
    void resize(String overlayWindowId, int width, int height);
}
```

### Overlay window view API

```
class OverlayWindowView {
    Stream get stateChangedStream;  //stream for overlay window id changes
    Stream<OverlayMessage> get messageStream;

    Future<void> close();
    Future<void> sendMessage(dynamic message);
}
```
