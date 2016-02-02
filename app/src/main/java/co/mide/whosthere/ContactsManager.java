package co.mide.whosthere;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.LinkedList;

/**
 *
 * Created by Olumide on 1/30/2016.
 */
public class ContactsManager {

    public static boolean doesNumberExist(Context context, String number) {
        return (number.equals("") || !findByNumber(context, number).equals(""));
    }

    public static String getAllContacts(Context context){
        LinkedList<String> list = new LinkedList<>();

        ContentResolver cr = context.getContentResolver();
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, null);
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        try {
            if (cur!= null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[^0-9]", "");
                    if (phoneNo.length() == 10)
                        list.add(phoneNo);
                }
                return TextUtils.join(",", list);
            }
        }finally {
            if (cur != null) {
                cur.close();
            }
        }
        return "";
    }

    public static String findByName(Context context, String name){
        String number = "";
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor contactLookup = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToFirst();
                number = contactLookup.getString(0);
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        return number.replaceAll("[^0-9.+-/(/) ]", "");
    }

    public static String findByNumber(Context context, String number){
        number = number.replaceAll("[^0-9]", "");
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    public static String getOwnPhoneNumber(Context context){
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public static boolean isName(String content){
        try{
            long l = Long.parseLong(content);
            return false;
        }catch(NumberFormatException e){
            return true;
        }
    }

    private static boolean isNumberValid(String number, String areaCode){
        if(number.length() != 10)
            return false;
        if(areaCode.isEmpty())
            return false;
        return number.startsWith(areaCode);
    }
}
