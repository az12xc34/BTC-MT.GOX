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
import com.mt.gox.cn.fragment.BtcChinaFragment.InJavaScriptLocalObj;
import com.mt.gox.cn.fragment.BtcChinaFragment.MyWebViewClient;
import com.mt.gox.cn.utils.Global;

public class MtgoxVolumeFragment extends Fragment {
  public static final String TAG = MtgoxVolumeFragment.class.getSimpleName();

  private static final String VOLUME_SCHEME = "mtgox_category";
  private static final String VOLUME_AUTHORITY = "volume";
  public static final Uri VOLUME_URI = new Uri.Builder()
  .scheme(VOLUME_SCHEME)
  .authority(VOLUME_AUTHORITY)
  .build();
  
  
  private BaseActivity mContext;
  
  private WebView vlumePage;
  public static final String APP_CACHE_PATH = "appcache";
  public static final String APP_DATABASE_PATH = "databases";
  public static final String APP_GEO_PATH = "geolocation";
  
  public static final String PAGE_URL = "http://bitcoinity.org/markets";
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	  final View v = inflater.inflate(R.layout.volume, container, false);
	  
	  vlumePage = (WebView)v.findViewById(R.id.volume_webview);
	  vlumePage.setWebViewClient(new MyWebViewClient());
	  vlumePage.setHorizontalScrollBarEnabled(false);
	  vlumePage.setHorizontalScrollbarOverlay(false);
	  vlumePage.setVerticalScrollBarEnabled(false);
	  vlumePage.setVerticalScrollbarOverlay(false);
	  vlumePage.setScrollbarFadingEnabled(false);
	  vlumePage.getSettings().setBuiltInZoomControls(false);
	  vlumePage.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
	  initWebView();
	  
	  mContext = (BaseActivity)getActivity();
	  
    return v;
  }
  
  private void initWebView() {
		WebSettings settings = vlumePage.getSettings();
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
		settings.setSupportMultipleWindows(false);
		vlumePage.loadUrl(PAGE_URL);
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
				vlumePage.loadUrl(PAGE_URL);
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
