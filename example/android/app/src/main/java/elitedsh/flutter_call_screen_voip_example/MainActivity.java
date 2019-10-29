package elitedsh.flutter_call_screen_voip_example;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

public class MainActivity extends FlutterActivity {

  BroadcastReceiver mBroadcastReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast", "onReceive");
        String action = intent.getAction();
        switch (action) {
        case "msg":
          String mess = intent.getStringExtra("message");
          break;
        }
      }
    };

    IntentFilter filter = new IntentFilter("msg");
    registerReceiver(mBroadcastReceiver, filter);
  }

}
