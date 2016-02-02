package co.mide.whosthere;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStateMonitor extends Service {

    @Override
    public void onCreate(){
        super.onCreate();
        StateListener yourListener = new StateListener(this);
        TelephonyManager yourmanager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        yourmanager.listen(yourListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

class StateListener extends PhoneStateListener {
    Context context;

    public StateListener(Context context){
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, final String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
                if(!ContactsManager.doesNumberExist(context, incomingNumber)){
                    Log.e("findName", "unknown");
                    final ServerRequests serverRequests = new ServerRequests(context);
                    (new Thread() {
                        public void run() {
                            MyGcmListenerService.setResultsListener(
                                    new ResultsListener() {
                                        @Override
                                        public void onResultGotten(String query, String result) {
                                            //TODO show overlay results
                                            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                                            MyGcmListenerService.setResultsListener(null);
                                        }
                                    }
                            );
                            serverRequests.findName(ContactsManager.getOwnPhoneNumber(context),
                                    incomingNumber, ContactsManager.getAllContacts(context), null);
                        }
                    }).start();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                break;
            case TelephonyManager.CALL_STATE_IDLE:

                break;

        }
    }
}
