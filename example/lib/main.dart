import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_call_screen_voip/flutter_call_screen_voip.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterCallScreenVoip.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    try {
      await FlutterCallScreenVoip.initialSetting("iKow");
    } on PlatformException {
      print('Failed to get platform version.');
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
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
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              FlatButton(
                child: Text("Receive Call"),
                onPressed: () {
                  FlutterCallScreenVoip.activeReceiveCall(
                      "Maleja", "3183413899");
                },
              ),
              FlatButton(
                child: Text("Receive Call Background"),
                onPressed: () {
                  FlutterCallScreenVoip.receiveCallBackground(
                      "Maleja background", "3183413899");
                },
              ),
              FlatButton(
                child: Text("End Call"),
                onPressed: () {
                  FlutterCallScreenVoip.endCall();
                },
              ),
              FlatButton(
                child: Text("open App"),
                onPressed: () {
                  FlutterCallScreenVoip.openAppBackground();
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}
