package elitedsh.flutter_call_screen_voip;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telecom.ConnectionService;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;


import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;



/** FlutterCallScreenVoipPlugin */

public class FlutterCallScreenVoipPlugin implements MethodCallHandler {

  private Activity activity;
  private final MethodChannel channel;

  private VoiceBroadcastReceiver voiceBroadcastReceiver;
  private boolean isReceiverRegistered = false;


  private TelecomManager tm;
  private PhoneAccountHandle phoneAccountHandle;

  public static final String ACTION_ANSWER_CALL = "ACTION_ANSWER_CALL";


  int PERMISSION_ALL = 1;
  String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.MANAGE_OWN_CALLS,Manifest.permission.ANSWER_PHONE_CALLS};

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_call_screen_voip");
    channel.setMethodCallHandler(new FlutterCallScreenVoipPlugin(registrar.activity(),channel));

  }



  private FlutterCallScreenVoipPlugin(Activity activity,MethodChannel channel){
    this.activity = activity;
    this.channel = channel;
    this.channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);

    }
    else if (call.method.equals("initialSetting")) {
      String nameApp =  call.argument("nameApp").toString();
      Log.d("nameApp",nameApp);
      initTelecom(nameApp);

    }
    else if (call.method.equals("activeReceiveCall")) {
      String nameScreen =  call.argument("nameScreen").toString();
      String numberScreen =  call.argument("numberScreen").toString();
      receiveCall(activity,nameScreen,numberScreen);
    }
    else if (call.method.equals("endCall")) {
      fininshCall(activity);
    }
     else {
      result.notImplemented();
    }
  }



    public void initTelecom(String nameApp){
      if(!hasPermissions(activity, PERMISSIONS)){
        activity.requestPermissions(PERMISSIONS, PERMISSION_ALL);
      }
      this.registerPhoneAccount(nameApp);
      if (!this.checkAccountConnection(activity)){
        AlertDialog alertDialog = new AlertDialog.Builder(activity,AlertDialog.THEME_HOLO_LIGHT).create();
        alertDialog.setTitle("Activa Tu Telefono");
        alertDialog.setMessage("Debes activar la opcion del telefono apra receibir llamadas");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    goToScreenSttingPhone();
                    dialog.dismiss();
                  }
                });
        alertDialog.show();
      }
      voiceBroadcastReceiver = new VoiceBroadcastReceiver();
      registerReceiver();
    }


  private void registerReceiver() {
    if (!isReceiverRegistered) {
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(ACTION_ANSWER_CALL);
      LocalBroadcastManager.getInstance(activity).registerReceiver(voiceBroadcastReceiver, intentFilter);
      isReceiverRegistered = true;
    }
  }

    //permissions
  public static boolean hasPermissions(Context context, String... permissions) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
      for (String permission : permissions) {
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  private void registerPhoneAccount(String nameApp){
    tm = (TelecomManager)
            activity.getSystemService(Context.TELECOM_SERVICE);
    Log.d("registerPhoneAccount",nameApp);

    phoneAccountHandle = new PhoneAccountHandle(new ComponentName(activity, CallConnectionService.class), nameApp);
    PhoneAccount phoneAccount =
            PhoneAccount.builder(phoneAccountHandle, nameApp+" App")
                    .setShortDescription(nameApp+" Asesores")
                    .addSupportedUriScheme(PhoneAccount.SCHEME_TEL)
                    .setSupportedUriSchemes(Arrays.asList("tel"))
                    .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                    //.setIcon(Icon.createWithResource(FlutterCallScreenVoipPlugin.this,R.drawable.ic_launcher_background))
                    .build();
    tm.registerPhoneAccount(phoneAccount);
  }

  private boolean checkAccountConnection(Context context) {
    boolean isConnected = false;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
        final List<PhoneAccountHandle> enabledAccounts = tm.getCallCapablePhoneAccounts();
        for (PhoneAccountHandle account : enabledAccounts) {
          if (account.getComponentName().getClassName().equals(CallConnectionService.class.getCanonicalName())) {
            isConnected = true;
            break;
          }
        }
      }

    }
    return isConnected;
  }


  void receiveCall(Context context,String nameScreen,String numberScreen) {
    if (this.checkAccountConnection(activity)){
      Log.d("Ikow Status", "Received call");
      Bundle callInfo = new Bundle();
      callInfo.putString("from",numberScreen);
      callInfo.putString("name",nameScreen);
      Log.d("extras",numberScreen);
      Log.d("extras",nameScreen);

      if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) != PackageManager.PERMISSION_GRANTED) {
        Log.d("Error", "no tiene MANAGE_OWN_CALLS");
        return;
      }
      tm.addNewIncomingCall(phoneAccountHandle, callInfo);
    }else{
      this.goToScreenSttingPhone();
    }
  }

   void fininshCall(Context context){
    if (context.checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
      Log.d("Error", "no tiene ANSWER_PHONE_CALLS");
      return;
    }
    tm.endCall();
  }


  public void goToScreenSttingPhone(){
    if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
      Intent intent = new Intent();
      intent.setComponent(new
              ComponentName("com.android.server.telecom",
              "com.android.server.telecom.settings.EnableAccountPreferenceActivity"));
      activity.startActivity(intent);
    } else {
      activity.startActivity(new
              Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS));

    }
  }


  private class VoiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d("onReceive","Broadcast");
      switch (intent.getAction()) {
        case ACTION_ANSWER_CALL:
          Log.d("ACTION_ANSWER_CALL","active");
          PackageManager pm = context.getPackageManager();
          Log.d("Open",context.getPackageName());
          Intent launchIntent = pm.getLaunchIntentForPackage(context.getPackageName());
          launchIntent.putExtra("some_data", "value");
          context.startActivity(launchIntent);
          CallConnection conn = new CallConnection(context);
          conn.setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
          conn.destroy();
          break;
      }
    }
  }

}
