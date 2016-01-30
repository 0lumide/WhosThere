package co.mide.whosthere;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Created by Olumide on 1/30/2016.
 */
public class ContactsManager {

    public static String getContactsWithAreaCode(Context context, String areaCode) {
        LinkedList<String> list = new LinkedList<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[^0-9]", "");
                        if(phoneNo.length() == 10 && makeNumberValid(phoneNo, areaCode))
                            list.add(phoneNo);
                    }
                    pCur.close();
                }
            }
            StringBuilder sb = new StringBuilder(11*list.size());
            int i = 0;
            for(String num : list){
                sb.append(num);
                if(++i < list.size())
                    sb.append(",");
            }
            return sb.toString();
        }
        return "";
    }

    public static boolean doesNumberExist(Context context, String number){
        if(number.equals(""))
            return true;
        return !findByNumber(context, number).equals("");
    }

    public static String getContactsWithoutAreaCode(Context context, String areaCode) {
        LinkedList<String> list = new LinkedList<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[^0-9]", "");
                        if(phoneNo.length() == 10 && !phoneNo.startsWith(areaCode))
                            list.add(phoneNo);
                    }
                    pCur.close();
                }
            }
            StringBuilder sb = new StringBuilder(11*list.size());
            int i = 0;
            for(String num : list){
                sb.append(num);
                if(++i < list.size())
                    sb.append(",");
            }
            return sb.toString();
        }
        return "";
    }

    public static String getAllContacts(Context context){
        LinkedList<String> list = new LinkedList<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[^0-9]", "");
                        if(phoneNo.length() == 10)
                            list.add(phoneNo);
                    }
                    pCur.close();
                }
            }
            StringBuilder sb = new StringBuilder(11*list.size());
            int i = 0;
            for(String num : list){
                sb.append(num);
                if(++i < list.size())
                    sb.append(",");
            }
            return sb.toString();
        }
        return "";
    }

    public static String findByName(Context context, String name){
        return find(context, name, true);
    }

    public static String findByNumber(Context context, String number){
        return find(context, number, false);
    }

    private static String find(Context context, String iden, boolean isName){
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[^0-9]", "");
                        if(phoneNo.length() == 10) {
                            if (phoneNo.equals(iden))
                                return name;
                            else if(isName && name!= null && name.toLowerCase().equals(iden.toLowerCase())){
                                return phoneNo;
                            }
                        }

                    }
                    pCur.close();
                }
            }
        }
        return "";
    }

    public static String getOwnPhoneNumber(Context context){
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }
    public static boolean isName(String content){
        try{
            Long.parseLong(content);
            return false;
        }catch(NumberFormatException e){
            return true;
        }
    }

    private static boolean makeNumberValid(String number, String areaCode){
        if(number.length() != 10)
            return false;
        if(areaCode.isEmpty())
            return false;
        return number.startsWith(areaCode);
    }
}
