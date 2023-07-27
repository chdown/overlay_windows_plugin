import 'package:flutter/material.dart';
import 'package:overlay_windows_plugin/overlay_message.dart';
import 'package:overlay_windows_plugin/overlay_window_view.dart';

class OverlayMain2 extends StatefulWidget {
  const OverlayMain2({Key? key}) : super(key: key);

  @override
  State<OverlayMain2> createState() => _OverlayMain2State();
}

class _OverlayMain2State extends State<OverlayMain2> {
  OverlayWindowView? view;

  @override
  void initState() {
    super.initState();

    view = OverlayWindowView();
    view?.messageStream.listen(onMessage);
  }

  String message = "";

  void onMessage(OverlayMessage mes) {
    setState(() {
      message = mes.message as String;
    });
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Container(
        padding: const EdgeInsets.all(12),
        color: Colors.green,
        child: Column(
          children: [
            const Expanded(
              child: Padding(
                padding: EdgeInsets.all(8.0),
                child: Text('Overlay 2', style: TextStyle(color: Colors.black, fontSize: 14)),
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Text('Message: $message', style: const TextStyle(color: Colors.black, fontSize: 14)),
              ),
            ),
            Expanded(
              child: Center(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ElevatedButton(
                      onPressed: () {
                        view?.sendMessage('Hello from overlay 2');
                      },
                      child: const Text('Send message'),
                    ),
                    ElevatedButton(
                      onPressed: () {
                        view?.close();
                      },
                      child: const Text('Close'),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
