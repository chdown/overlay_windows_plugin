import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:overlay_windows_plugin/overlay_window_view.dart';

class OverlayMain1 extends StatefulWidget {
  const OverlayMain1({Key? key}) : super(key: key);

  @override
  State<OverlayMain1> createState() => _OverlayMain1State();
}

class _OverlayMain1State extends State<OverlayMain1> {
  final view = OverlayWindowView();

  String? viewId;
  String? message;

  @override
  void initState() {
    super.initState();
    view.stateChangedStream.listen((event) {
      setState(() {
        viewId = event;
      });
    });

    view.messageStream.listen((mes) {
      // var mes = OverlayMessage.fromJson(event);
      // log('$viewId: $mes');
      setState(() {
        // var tempMes = JsonDecoder(mes.message) as String;
        log('$viewId: $mes');
        message = mes;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.red,
      height: 300,
      padding: const EdgeInsets.all(4),
      child: SizedBox(
        height: 300,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  view.sendMessage('Hello from overlay $viewId');
                },
                child: const Text('Send message'),
              ),
            ),
            const SizedBox(height: 8),
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  // view.sendMessage('Hello from overlay $viewId');
                },
                child: Text('main: $message'),
              ),
            ),
            const SizedBox(height: 8),
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  view.close();
                },
                child: const Text('Close}'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
