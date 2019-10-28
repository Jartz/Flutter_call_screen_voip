package elitedsh.flutter_call_screen_voip;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;


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
            Log.d("Answer", "onAnswer() called");
            setConnectionCapabilities(getConnectionCapabilities() | Connection.CAPABILITY_HOLD);
            setAudioModeIsVoip(true);
            Intent intent = new Intent(Intent.ACTION_MAIN,null);
            /*
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(context, FlutterCallScreenVoipPlugin.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);
            context.startActivity(intent);
             */
            intent.setClassName("elitedsh.tupsicologo", "elitedsh.tupsicologo.MainActivity");
            context.sendBroadcast(intent);
            setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
            destroy();

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




    }

