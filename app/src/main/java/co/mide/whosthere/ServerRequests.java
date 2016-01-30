package co.mide.whosthere;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

/**
 * Created by Olumide on 1/30/2016.
 */
public class ServerRequests {
    Context context;
    public ServerRequests(Context context){
        this.context = context;
    }

    public void storeUserKeyInBackground(String phoneNumber, String key, ServerRequests.ServerRequestCallback callback){
        new StoreUserGCMKey(phoneNumber, key, callback).execute();
    }

    public void cleanUp(String phoneNumber, ServerRequests.ServerRequestCallback callback){
        new CleanUp(phoneNumber, callback).execute();
    }

    public void findName(String ownNumber, String mysteriousNumber, String contacts, ServerRequestCallback callBack){
        new FindName(ownNumber, mysteriousNumber, contacts, callBack).execute();
    }

    public void findPhoneNumber(String name, String number, String contacts, ServerRequestCallback callBack){
        new FindPhoneNumber(name, number, contacts, callBack).execute();
    }

    public void found(String query, String result, String token, ServerRequestCallback callBack){
        new Found(query, result, token, callBack).execute();
    }

    public static interface ServerRequestCallback{
        void done();
    }

    public class StoreUserGCMKey extends AsyncTask<Void, Void, Void> {
        String key;
        public static final int CONNECTION_TIMEOUT = 1000 * 15;
        public static final String SERVER_ADDRESS = "http://olu.mide.co/Random/PCS/v1/";
        String phoneNumber;
        ServerRequestCallback callBack;

        public StoreUserGCMKey(String number, String key, ServerRequestCallback callBack){
            this.phoneNumber = number.substring(Math.max(number.length() - 10, 0));
            this.key = key;
            this.callBack = callBack;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> datatoSend = new ArrayList<>();

            datatoSend.add(new BasicNameValuePair("name", phoneNumber));
            datatoSend.add(new BasicNameValuePair("gcm", key));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "register.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(datatoSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.v("response", result);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callBack.done();
            super.onPostExecute(aVoid);
        }
    }

    public class FindName extends AsyncTask<Void, Void, Void> {
        public static final int CONNECTION_TIMEOUT = 1000 * 15;
        String contacts;
        public static final String SERVER_ADDRESS = "http://olu.mide.co/Random/PCS/v1/";
        String ownNumber, mysteryNumber;
        ServerRequestCallback callBack;

        public FindName(String ownNumber, String mysteriousNumber, String contacts, ServerRequestCallback callBack){
            this.ownNumber = ownNumber.substring(Math.max(ownNumber.length() - 10, 0));
            this.callBack = callBack;
            this.mysteryNumber = mysteriousNumber;
            this.contacts = contacts;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> datatoSend = new ArrayList<>();

            datatoSend.add(new BasicNameValuePair("phone_num", ownNumber));
            datatoSend.add(new BasicNameValuePair("DEBUG", "true"));
            datatoSend.add(new BasicNameValuePair("q", mysteryNumber));
            datatoSend.add(new BasicNameValuePair("phone_numbers", contacts));


            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "request.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(datatoSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.e("findName", result);
            }catch(Exception e){
                e.printStackTrace();
                Log.e("findName", "error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(callBack != null)
                callBack.done();
            super.onPostExecute(aVoid);
        }
    }

    public class FindPhoneNumber extends AsyncTask<Void, Void, Void> {
        public static final int CONNECTION_TIMEOUT = 1000 * 15;
        public static final String SERVER_ADDRESS = "http://olu.mide.co/Random/PCS/v1/";
        String name;
        String contacts;
        String phoneNumber;
        ServerRequestCallback callBack;

        public FindPhoneNumber(String name, String number, String contacts, ServerRequestCallback callBack){
            this.name = name;
            this.contacts = contacts;
            this.callBack = callBack;
            this.phoneNumber = number.substring(Math.max(number.length() - 10, 0));
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> datatoSend = new ArrayList<>();

            datatoSend.add(new BasicNameValuePair("phone_num", phoneNumber));
            datatoSend.add(new BasicNameValuePair("DEBUG", "true"));
            datatoSend.add(new BasicNameValuePair("isName", "true"));
            datatoSend.add(new BasicNameValuePair("q", name));
            datatoSend.add(new BasicNameValuePair("phone_numbers", contacts));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "request.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(datatoSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.v("responsse", result);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(callBack != null)
                callBack.done();
            super.onPostExecute(aVoid);
        }
    }

    public class CleanUp extends AsyncTask<Void, Void, Void> {
        public static final int CONNECTION_TIMEOUT = 1000 * 15;
        public static final String SERVER_ADDRESS = "http://olu.mide.co/Random/PCS/v1/";
        String phoneNumber;
        ServerRequestCallback callBack;

        public CleanUp(String number, ServerRequestCallback callBack){
            this.callBack = callBack;
            this.phoneNumber = number.substring(Math.max(number.length() - 10, 0));
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> datatoSend = new ArrayList<>();

            datatoSend.add(new BasicNameValuePair("phone_num", phoneNumber));
            datatoSend.add(new BasicNameValuePair("DEBUG", "true"));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "clean.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(datatoSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.v("response", result);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(callBack != null)
                callBack.done();
            super.onPostExecute(aVoid);
        }
    }

    public class Found extends AsyncTask<Void, Void, Void> {
        public static final int CONNECTION_TIMEOUT = 1000 * 15;
        public static final String SERVER_ADDRESS = "http://olu.mide.co/Random/PCS/v1/";
        String query;
        String result;
        String token;
        ServerRequestCallback callBack;

        public Found(String query, String result, String token, ServerRequestCallback callBack){
            this.callBack = callBack;
            this.query = query;
            this.token = token;
            this.result = result;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> datatoSend = new ArrayList<>();

            datatoSend.add(new BasicNameValuePair("q", query));
            datatoSend.add(new BasicNameValuePair("DEBUG", "true"));
            datatoSend.add(new BasicNameValuePair("token", token));
            datatoSend.add(new BasicNameValuePair("result", result));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "found.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(datatoSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.v("response found", result);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(callBack != null)
                callBack.done();
            super.onPostExecute(aVoid);
        }
    }
}
