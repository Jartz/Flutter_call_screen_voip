package elitedsh.flutter_call_screen_voip;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CallConnectionService extends ConnectionService {

    public CallConnectionService() {

    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.d("Service","incomming failed");
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
    }


    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.d("Service","incomming");
        Bundle extra = request.getExtras();

        String from = extra.getString("from");
        String name = extra.getString("name");
        CallConnection callConnection = new CallConnection(this);
        callConnection.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED);
        callConnection.setAddress(Uri.parse(from), TelecomManager.PRESENTATION_ALLOWED);
        callConnection.setVideoState(VideoProfile.STATE_BIDIRECTIONAL);
        callConnection.setRinging();
        callConnection.setInitializing();
        return callConnection;

    }

    @Override
    public Connection onCreateIncomingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        Log.d("Service","onCreateIncomingHandoverConnection");
        return super.onCreateIncomingHandoverConnection(fromPhoneAccountHandle, request);
    }
}
