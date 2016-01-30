package co.mide.whosthere;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Olumide on 1/30/2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        //TODO
    }
}