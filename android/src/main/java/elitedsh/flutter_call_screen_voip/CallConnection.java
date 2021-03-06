package elitedsh.flutter_call_screen_voip;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static elitedsh.flutter_call_screen_voip.FlutterCallScreenVoipPlugin.ACTION_ANSWER_CALL;


public class CallConnection extends Connection {

        private Context context;

        public CallConnection(Context context){
            this.context = context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                setConnectionProperties(PROPERTY_SELF_MANAGED);
            }

            setAudioModeIsVoip(true);
        }

        @Override
        public void onAnswer(){
            super.onAnswer();
            Log.d("Answer", "onAnswer() called");
            setConnectionCapabilities(getConnectionCapabilities() | Connection.CAPABILITY_HOLD);
            setActive();
            sendCallRequestToActivity(ACTION_ANSWER_CALL);

        }

        @Override
        public void onDisconnect(){
            Log.d("Disconnect", "onDisconnect() called");
            super.onDisconnect();
            setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
            destroy();
            //Accept the Call
        }

        @Override
        public void onReject(){
            Log.d("Disconnect", "onReject() called");
            super.onReject();
            setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
            destroy();
        }

    @Override
    public void onHold() {
        super.onHold();
        Log.d("OnHold", "onHold() called");
    }

    @Override
    public void onShowIncomingCallUi() {
        super.onShowIncomingCallUi();
        Log.d("onShowIncomingCallUi", "onShowIncomingCallUi() called");
    }

    private void sendCallRequestToActivity(final String action) {
        final CallConnection instance = this;
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(action);
                    Bundle extras = new Bundle();
                    extras.putSerializable("message", 1000);
                    intent.putExtras(extras);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }


}

