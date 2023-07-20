import 'package:flutter/material.dart';
import 'package:overlay_windows_plugin/overlay_windows_api.g.dart';
import 'package:overlay_windows_plugin/overlay_windows_plugin.dart';

void main() {
  runApp(const MyApp());
}

@pragma("vm:entry-point")
void overlayMain1() {
  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SafeArea(
        child: Container(
          color: Colors.red,
          child: Center(
            child: ElevatedButton(
              onPressed: () {},
              child: const Text('Overlay 1'),
            ),
          ),
        ),
      ),
    ),
  );
}

@pragma("vm:entry-point")
void overlayMain2() {
  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SafeArea(
        child: Container(
          color: Colors.red,
          child: Center(
            child: ElevatedButton(
              onPressed: () {},
              child: const Text('Overlay 2'),
            ),
          ),
        ),
      ),
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

  @override
  void initState() {
    super.initState();
  }

  final List<String> _overlayWindowIds = [];

  void showOverlay(String entryPointName) async {
    var hasPermission = await _overlayWindowsPlugin.isPermissionGranted();
    if (!hasPermission) {
      _overlayWindowsPlugin.requestPermission();
      return;
    }
    var id = '$entryPointName-${_overlayWindowIds.length + 1}';
    _overlayWindowIds.add(id);
    _overlayWindowsPlugin.showOverlayWindow(id, entryPointName, OverlayWindowConfig(width: 300, height: 300, enableDrag: true));
  }

  void closeOverlay(String entryPointName) async {
    var ids = _overlayWindowIds.where((element) => element.startsWith(entryPointName)).toList();
    ids.forEach((element) async {
      _overlayWindowIds.remove(element);
      _overlayWindowsPlugin.closeOverlayWindow(element);
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
                    child: const Text("Show Overlay 1"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      closeOverlay("overlayMain1");
                    },
                    child: const Text("Close Overlay 1"),
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
                    child: const Text("Show Overlay 2"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      closeOverlay("overlayMain2");
                    },
                    child: const Text("Close Overlay 2"),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
