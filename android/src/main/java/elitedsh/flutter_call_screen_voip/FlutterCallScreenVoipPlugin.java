package elitedsh.flutter_call_screen_voip;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

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




  private TelecomManager tm;
  private PhoneAccountHandle phoneAccountHandle;
  private String to = "3183413899";
  private String from = "3212544443";

  int PERMISSION_ALL = 1;
  String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.MANAGE_OWN_CALLS};

  public static void registerWith(Registrar registrar) {

/*
    AssetManager assetManager = registrar.context().getAssets();
    String key = registrar.lookupKeyForAsset("icons/heart.png");
    AssetFileDescriptor fd = assetManager.openFd(key);
    */


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

    if (call.method.equals("getReceiveCall")) {
      String msg =  call.argument("msg").toString();
      Toast.makeText(activity,msg,Toast.LENGTH_LONG).show();
      initTelecom();
      receiveCall(activity);
    }
    
     else {
      result.notImplemented();
    }
  }



    public void initTelecom(){
      if(!hasPermissions(activity, PERMISSIONS)){
        activity.requestPermissions(PERMISSIONS, PERMISSION_ALL);
      }
      this.registerPhoneAccount();

    }


  private void registerPhoneAccount(){

    tm = (TelecomManager)
            activity.getSystemService(Context.TELECOM_SERVICE);

    phoneAccountHandle = new PhoneAccountHandle(new ComponentName(activity, CallConnectionService.class), "Ikow");
    PhoneAccount phoneAccount =
            PhoneAccount.builder(phoneAccountHandle, "Ikow App")
                    .setShortDescription("Ikow Asesores")
                    .addSupportedUriScheme(PhoneAccount.SCHEME_TEL)
                    .setSupportedUriSchemes(Arrays.asList("tel"))
                    .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                    //.setIcon(Icon.createWithResource(FlutterCallScreenVoipPlugin.this,R.drawable.ic_launcher_background))
                    .build();
    tm.registerPhoneAccount(phoneAccount);
  }

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


  void receiveCall(Context context) {
    if (this.checkAccountConnection(activity)){

      Log.d("Ikow Status", "Reicived call");
      Bundle callInfo = new Bundle();
      callInfo.putString("from",from);
      if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) != PackageManager.PERMISSION_GRANTED) {
        Log.d("myTag", "no tiene MANAGE_OWN_CALLS");
        return;
      }
      tm.addNewIncomingCall(phoneAccountHandle, callInfo);


    }else{
      this.activePhoneAccountManual();
    }
  }
  ///////////// END ACTION CALL ///////////////////////


  public void activePhoneAccountManual(){
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




}
