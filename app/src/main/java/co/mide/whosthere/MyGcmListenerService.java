package co.mide.whosthere;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

/**
 *
 * Created by Olumide on 1/30/2016.
 */
public class MyGcmListenerService  extends GcmListenerService {
    private static int id = 0;
    private static ResultsListener resultsListener;

    public static void setResultsListener(ResultsListener listener){
        resultsListener = listener;
    }

    public void onMessageReceived(String from, final Bundle data) {
        switch (data.getString("type")){
            case "FIND":
                if(data.getString("DEBUG").equals("true")){
                    sendNotification("Searching for " + data.getString("q"));
                }
                if(data.getString("isName").equals("true")){
                    final String name = data.getString("q");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String found = ContactsManager.findByName(MyGcmListenerService.this, name);
                            if(!found.equals("")){
                                if(data.getString("DEBUG").equals("true")) {
                                    sendNotification("found " + found);
                                }
                                final ServerRequests serverRequests = new ServerRequests(MyGcmListenerService.this);
                                serverRequests.found(name, found, data.getString("token"), null);
                            }
                        }
                    }).run();
                }else{
                    final String number = data.getString("q");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String found = ContactsManager.findByName(MyGcmListenerService.this, number);
                            if(!found.equals("")){
                                if(data.getString("DEBUG").equals("true")) {
                                    sendNotification("found " + found);
                                }
                                final ServerRequests serverRequests = new ServerRequests(MyGcmListenerService.this);
                                serverRequests.found(number, found, data.getString("token"), null);
                            }
                        }
                    }).run();
                }
                break;
            case "RESPONSE":
                String result = data.getString("result");
                String query = data.getString("q");
                if(resultsListener != null){
                    resultsListener.onResultGotten(query, result);
                }
                if(data.getString("DEBUG").equals("true")){
                    sendNotification("result delivered " + result);
                }
                break;
        }
    }

    private void sendNotification(String stuff) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Message received")
                .setContentText(stuff)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id++ /* ID of notification */, notificationBuilder.build());
    }
}

interface ResultsListener{
    void onResultGotten(String query, String result);
}
