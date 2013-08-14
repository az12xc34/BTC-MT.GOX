package com.mt.gox.cn.utils;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import com.mt.gox.cn.MTApplication;
import com.mt.gox.cn.api.ApiKeys;
/**
 * @ClassName: Utils 
 * @Description:  
 * @author LEIKANG 
 */
public class Utils {
     
    public static String readFromFile(String path) {
         
        File file = new File(path);
         
        StringBuilder fileContent = new StringBuilder();
        BufferedReader bufferedReader = null;
         
        try {
 
            bufferedReader = new BufferedReader(new FileReader(file));
             
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                fileContent.append(text);
            }
 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
                 
        return fileContent.toString();
    }
    
    public static String getUTF8StringFromGBKString(String gbkStr) {  
    	int n = gbkStr.length();  
        byte[] utfBytes = new byte[3 * n];  
        int k = 0;  
        for (int i = 0; i < n; i++) {  
            int m = gbkStr.charAt(i);  
            if (m < 128 && m >= 0) {  
                utfBytes[k++] = (byte) m;  
                continue;  
            }  
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
        }  
        if (k < utfBytes.length) {  
            byte[] tmp = new byte[k];  
            System.arraycopy(utfBytes, 0, tmp, 0, k);  
            return tmp.toString();  
        }  
        return utfBytes.toString();  

    }
    
    public static String getGBKStringFromUtf8(String utf8Str) {
    	String gbk = "";
		try {
			gbk = new String(utf8Str.getBytes("GBK"));
			gbk = gbk.trim();
			gbk = gbk.replace(" ", "");
			return gbk;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
    	
    	return gbk;
    	
    }
    

    public static void initSSL()
    {        
           
        // SSL Certificates  trustStore ----------------------------------------
        //Set the SSL certificate for mtgox - Read up on Java Trust store. 
        System.setProperty("javax.net.ssl.trustStore","res/ssl/mtgox.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","h4rdc0r_"); //I encripted the jks file using this pwd
        //System.setProperty("javax.net.debug","ssl"); //Uncomment for debugging SSL errors  
        
    }
    
     //readApiKeysFromFile
     public static ApiKeys readApiKeys(String pathToJsonFile) {
        //see https://code.google.com/p/json-simple/wiki/DecodingExamples
    	 
         JSONTokener parser= new JSONTokener(pathToJsonFile);
        ApiKeys apiKeys = null;
        String apiStr = Utils.readFromFile(pathToJsonFile);
        try {
            JSONObject obj2=(JSONObject)(parser.nextValue());
            apiKeys= new ApiKeys((String)obj2.get("mtgox_secret_key"), (String)obj2.get("mtgox_api_key"));
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return apiKeys;    
    }
     
     
     /**  
      * wifi是否打开  
      */   
     public static boolean isWifiEnabled(Context context) {   
         ConnectivityManager mgrConn = (ConnectivityManager) context   
                 .getSystemService(Context.CONNECTIVITY_SERVICE);   
         TelephonyManager mgrTel = (TelephonyManager) context   
                 .getSystemService(Context.TELEPHONY_SERVICE);   
         return ((mgrConn.getActiveNetworkInfo() != null && mgrConn   
                 .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel   
                 .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);   
     }   
    
     /**  
      * 判断当前网络是否是wifi网络  
      * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网  
      *   
      * @param context  
      * @return boolean  
      */   
     public static boolean isWifi(Context context) {   
         ConnectivityManager connectivityManager = (ConnectivityManager) context   
                 .getSystemService(Context.CONNECTIVITY_SERVICE);   
         NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();   
         if (activeNetInfo != null   
                 && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {   
             return true;   
         }   
         return false;   
     }   
    
     /**  
      * 判断当前网络是否是3G网络  
      *   
      * @param context  
      * @return boolean  
      */   
     public static boolean is3G(Context context) {   
         ConnectivityManager connectivityManager = (ConnectivityManager) context   
                 .getSystemService(Context.CONNECTIVITY_SERVICE);   
         NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();   
         if (activeNetInfo != null   
                 && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {   
             return true;   
         }   
         return false;   
     }   
     
     
     public static void hiddenInput(Context context) {
    	// 隐藏输入法 
    	 InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
    	 // 显示或者隐藏输入法 
    	 imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
     }
     
     private static SharedPreferences getSharedPreferences(Context context){
    	 SharedPreferences pref = context.getSharedPreferences(Global.PREF_APPID, 0);
    	 return pref;
     }
     
     public static String getApiKey(Context context) {
    	 return getSharedPreferences(context).getString(Global.PREF_API_TAG, Global.keys.getApiKey());
     }
     
     public static String getSecretKey(Context context) {
    	 return getSharedPreferences(context).getString(Global.PREF_SECRET_TAG, Global.keys.getPrivateKey());
     }
     
     public static void saveApiKey(Context context,String api) {
    	 getSharedPreferences(context).edit().putString(Global.PREF_API_TAG, api).commit();
     }
     
     public static void saveSecretKey(Context context,String secret) {
    	 getSharedPreferences(context).edit().putString(Global.PREF_SECRET_TAG, secret).commit();
     }
     
     
     
     public static String getHighPrice(Context context) {
    	 DecimalFormat df = new DecimalFormat("0.000");
    	 
    	 String high = getSharedPreferences(context).getString(Global.PREF_HIGH_PRICE_TAG, "");
    	 if(high.equals("")&&MTApplication.getInstance().unitPrice!=0) {
    		 saveHighPrice(context,df.format(MTApplication.getInstance().unitPrice*1.05));
    	 }
    	 
    	 return getSharedPreferences(context).getString(Global.PREF_HIGH_PRICE_TAG, df.format(MTApplication.getInstance().unitPrice*1.05));
     }
     
     public static String getLowPrice(Context context) {
    	 DecimalFormat df = new DecimalFormat("0.000");
    	 
    	 String high = getSharedPreferences(context).getString(Global.PREF_LOW_PRICE_TAG, "");
    	 if(high.equals("")&&MTApplication.getInstance().unitPrice!=0) {
    		 saveLowPrice(context,df.format(MTApplication.getInstance().unitPrice*0.95));
    	 }
    	 
    	 return getSharedPreferences(context).getString(Global.PREF_LOW_PRICE_TAG, df.format(MTApplication.getInstance().unitPrice*0.95));
     }
     
     public static void saveHighPrice(Context context,String high) {
    	 getSharedPreferences(context).edit().putString(Global.PREF_HIGH_PRICE_TAG, high).commit();
     }
     
     public static void saveLowPrice(Context context,String low) {
    	 getSharedPreferences(context).edit().putString(Global.PREF_LOW_PRICE_TAG, low).commit();
     }
     
     
     
}