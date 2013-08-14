package com.mt.gox.cn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mt.gox.cn.BaseActivity;
import com.mt.gox.cn.MTApplication;
import com.mt.gox.cn.R;
import com.mt.gox.cn.utils.Global;

public class BtcChinaFragment extends Fragment {
  public static final String TAG = BtcChinaFragment.class.getSimpleName();

  private static final String BTCCHINA_SCHEME = "btcc_category";
  private static final String BTCCHINA_AUTHORITY = "btcchina";
  public static final Uri BTCCHINA_URI = new Uri.Builder()
  .scheme(BTCCHINA_SCHEME)
  .authority(BTCCHINA_AUTHORITY)
  .build();
  public static final String APP_CACHE_PATH = "appcache";
  public static final String APP_DATABASE_PATH = "databases";
  public static final String APP_GEO_PATH = "geolocation";
  
  private BaseActivity mContext;
  
  private WebView btcchina_webview;
  
  public static final String PAGE_URL = "https://btcchina.com";
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	  final View v = inflater.inflate(R.layout.btcchina, container, false);
	  
	  mContext = (BaseActivity)getActivity();
	  
	  btcchina_webview = (WebView)v.findViewById(R.id.btcchina_webview);
	  
	  btcchina_webview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
	  btcchina_webview.setWebViewClient(new MyWebViewClient());
	  btcchina_webview.setHorizontalScrollBarEnabled(false);
	  btcchina_webview.setHorizontalScrollbarOverlay(false);
	  btcchina_webview.setVerticalScrollBarEnabled(false);
	  btcchina_webview.setVerticalScrollbarOverlay(false);
	  btcchina_webview.setScrollbarFadingEnabled(false);
	  btcchina_webview.getSettings().setBuiltInZoomControls(false);
	  initWebView();
	  
    return v;
  }
  
  private void initWebView() {
		WebSettings settings = btcchina_webview.getSettings();
		settings.setLightTouchEnabled(false);
		settings.setNeedInitialFocus(false);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setLoadsImagesAutomatically(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setGeolocationEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setAppCacheEnabled(true);
		String databasePath = mContext.getDir(APP_DATABASE_PATH, 0).getPath();
		String geolocationDatabasePath = mContext.getDir(APP_GEO_PATH, 0)
				.getPath();
		String appCachePath = mContext.getDir(APP_CACHE_PATH, 0).getPath();
		settings.setGeolocationDatabasePath(geolocationDatabasePath);
		settings.setDatabasePath(databasePath);
		settings.setAppCachePath(appCachePath);
		settings.setSupportMultipleWindows(false);
		btcchina_webview.loadUrl(PAGE_URL);
  }
  
    
	public void onStart() {
		initBroadcast();
		super.onStart();
	}

	public void onStop() {
		mContext.unregisterReceiver(receive);
		super.onStop();
	}
	
	MyBroadcastReceive receive = new MyBroadcastReceive();

	private void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Global.ACTION_REFRESH_BTN);
		mContext.registerReceiver(receive, intentFilter);
	}

	private class MyBroadcastReceive extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Global.ACTION_REFRESH_BTN.equals(action) &&  MTApplication.getInstance().mFragmentTag.equals(TAG)) {
				btcchina_webview.loadUrl(PAGE_URL);
				mContext.getActionBarHelper().setRefreshActionItemState(false);
			}
		}
	}
	
	final class MyWebViewClient extends WebViewClient{  
		
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {   
	        view.loadUrl(url);   
	        return true;   
	    }  
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        super.onPageStarted(view, url, favicon);
	    }    
	    public void onPageFinished(WebView view, String url) {
	        view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
	                "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	        super.onPageFinished(view, url);
	    }
	}
	
	final class InJavaScriptLocalObj {
	    public void showSource(String html) {
	    }
	}
}
