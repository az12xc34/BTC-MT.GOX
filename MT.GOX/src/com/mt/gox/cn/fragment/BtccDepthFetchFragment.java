package com.mt.gox.cn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CursorJoiner.Result;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;

import com.mt.gox.cn.BaseActivity;
import com.mt.gox.cn.MTApplication;
import com.mt.gox.cn.R;
import com.mt.gox.cn.adapter.DepthFetchAdapter;
import com.mt.gox.cn.api.MtGox;
import com.mt.gox.cn.utils.Global;
import com.mt.gox.cn.utils.Utils;

public class BtccDepthFetchFragment extends Fragment {
  public static final String TAG = BtccDepthFetchFragment.class.getSimpleName();

  private static final String DEPTH_FETCH_SCHEME = "btcc_category";
  private static final String DEPTH_FETCH_AUTHORITY = "btcchina_depth";
  public static final Uri DEPTH_FETCH_URI = new Uri.Builder()
  .scheme(DEPTH_FETCH_SCHEME)
  .authority(DEPTH_FETCH_AUTHORITY)
  .build();
  
  public static final String APP_CACHE_PATH = "appcache";
  public static final String APP_DATABASE_PATH = "databases";
  public static final String APP_GEO_PATH = "geolocation";
  
  private String PAGE_URL = "http://info.btc123.com/index_btcchina.php";
  
  
  private BaseActivity mContext;
  
  private WebView depth_webview;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	  final View v = inflater.inflate(R.layout.btcc_depth_fetch, container, false);
	  
	  mContext = (BaseActivity)getActivity();
	  
	  depth_webview = (WebView)v.findViewById(R.id.btcc_depth_webview);
	  depth_webview.setOnLongClickListener(new WebView.OnLongClickListener() {
           public boolean onLongClick(View v) {
               return true;
           }
       });
	  
	  depth_webview.setOnTouchListener(new OnTouchListener() {
          public boolean onTouch(View v, MotionEvent event) {
                  if(event.getAction() == (MotionEvent.EDGE_LEFT | MotionEvent.EDGE_RIGHT))
                          return false;
                  else
                          return true;
          }
	  });
	  
	  depth_webview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
	  depth_webview.setWebViewClient(new MyWebViewClient());
	  depth_webview.setHorizontalScrollBarEnabled(false);
	  depth_webview.setHorizontalScrollbarOverlay(false);
	  depth_webview.setVerticalScrollBarEnabled(false);
	  depth_webview.setVerticalScrollbarOverlay(false);
	  depth_webview.setScrollbarFadingEnabled(false);
	  depth_webview.getSettings().setBuiltInZoomControls(false);
	  depth_webview.setInitialScale(99);
	  initWebView();
	  
    return v;
  }
  
  private void initWebView() {
		WebSettings settings = depth_webview.getSettings();
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
		depth_webview.loadUrl(PAGE_URL);
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
				depth_webview.loadUrl(PAGE_URL);
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
