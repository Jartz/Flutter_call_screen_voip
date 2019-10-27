import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCallScreenVoip {
  static const MethodChannel _channel =
      const MethodChannel('flutter_call_screen_voip');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Null> receiveCall(String msg) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("msg", () => msg);
    _channel.invokeMethod('getReceiveCall', args);
    return null;
  }
}
