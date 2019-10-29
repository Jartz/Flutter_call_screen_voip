import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCallScreenVoip {
  static const MethodChannel _channel =
      const MethodChannel('flutter_call_screen_voip');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Null> initialSetting(String nameApp) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("nameApp", () => nameApp);
    _channel.invokeMethod('initialSetting', args);
    return null;
  }

  static Future<Null> activeReceiveCall(
      String nameScreen, String numberScreen) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("nameScreen", () => nameScreen);
    args.putIfAbsent("numberScreen", () => numberScreen);
    _channel.invokeMethod('activeReceiveCall', args);
    return null;
  }
}
