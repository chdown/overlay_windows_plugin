import 'package:flutter/material.dart';
import 'package:overlay_windows_plugin/overlay_message.dart';
import 'package:overlay_windows_plugin/overlay_windows_api.g.dart';
import 'package:overlay_windows_plugin/overlay_windows_plugin.dart';
import 'package:overlay_windows_plugin_example/overlay_main1.dart';
import 'package:overlay_windows_plugin_example/overlay_main2.dart';

void main() {
  runApp(const MyApp());
}

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

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _overlayWindowsPlugin = OverlayWindowsPlugin.defaultInstance;

  List<OverlayMessage> message = [];

  @override
  void initState() {
    super.initState();
    _overlayWindowsPlugin.messageStream.listen((event) {
      setState(() {
        message.add(event);
      });
    });
    _overlayWindowsPlugin.touchEventStream.listen((event) {});
  }

  final List<String> _overlayWindowIds = [];

  int index = 0;

  void showOverlay(String entryPointName) async {
    var hasPermission = await _overlayWindowsPlugin.isPermissionGranted();
    if (!hasPermission) {
      _overlayWindowsPlugin.requestPermission();
      return;
    }
    var id = '$entryPointName-${index + 1}';
    setState(() {
      _overlayWindowIds.add(id);
    });

    if (entryPointName == "overlayMain1") {
      _overlayWindowsPlugin.showOverlayWindow(
          id,
          entryPointName,
          OverlayWindowConfig(
            width: 300,
            height: 100,
            enableDrag: true,
          ));
    } else {
      _overlayWindowsPlugin.showOverlayWindow(
          id,
          entryPointName,
          OverlayWindowConfig(
            width: 300,
            height: 300,
            // alignment: OverlayAlignment.bottomCenter,
            flag: OverlayFlag.defaultFlag,
            enableDrag: true,
            positionGravity: PositionGravity.left,
          ));
    }

    index++;
  }

  void closeOverlay(String entryPointName) async {
    var ids = _overlayWindowIds.where((element) => element.startsWith(entryPointName)).toList();
    ids.forEach((element) async {
      setState(() {
        _overlayWindowIds.remove(element);
      });

      _overlayWindowsPlugin.closeOverlayWindow(element);
    });
  }

  void sendMessage() {
    _overlayWindowsPlugin.sendMessage("", "Hello from main");
  }

  int clickTime = 0;
  void setOverlayFlag(String entryPointName) {
    var ids = _overlayWindowIds.where((element) => element.startsWith(entryPointName)).toList();
    if (clickTime > 2) {
      clickTime = 0;
    }
    ids.forEach((element) async {
      if (clickTime == 0) {
        _overlayWindowsPlugin.setFlags(element, OverlayFlag.clickThrough);
      } else if (clickTime == 1) {
        _overlayWindowsPlugin.setFlags(element, OverlayFlag.focusPointer);
      } else if (clickTime == 2) {
        _overlayWindowsPlugin.setFlags(element, OverlayFlag.defaultFlag);
      }
    });
    clickTime++;
  }

  int initWidth = 300;
  int initHeight = 300;
  void increaseSize() {
    setState(() {
      initWidth += 20;
      initHeight += 20;
      _overlayWindowsPlugin.resize(_overlayWindowIds.first, initWidth, initHeight);
    });
  }

  void decreaseSize() {
    setState(() {
      initWidth -= 20;
      initHeight -= 20;
      _overlayWindowsPlugin.resize(_overlayWindowIds.first, initWidth, initHeight);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  ElevatedButton(
                    onPressed: () {
                      showOverlay("overlayMain1");
                    },
                    child: const Text("Show 1"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      closeOverlay("overlayMain1");
                    },
                    child: const Text("Close 1"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      setOverlayFlag("overlayMain1");
                    },
                    child: const Text("Update Flag"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      increaseSize();
                    },
                    child: const Text("Big"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      decreaseSize();
                    },
                    child: const Text("Small"),
                  ),
                ],
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  ElevatedButton(
                    onPressed: () {
                      showOverlay("overlayMain2");
                    },
                    child: const Text("Show 2"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      closeOverlay("overlayMain2");
                    },
                    child: const Text("Close 2"),
                  ),
                ],
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  ElevatedButton(
                    onPressed: () {
                      sendMessage();
                    },
                    child: const Text("Send Message"),
                  ),
                ],
              ),
              Expanded(
                child: ListView.builder(
                  itemCount: message.length,
                  itemBuilder: (context, index) {
                    return Text('${message[index].overlayWindowId}: ${message[index].message}');
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
