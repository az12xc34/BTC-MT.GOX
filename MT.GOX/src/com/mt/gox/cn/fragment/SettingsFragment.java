package com.mt.gox.cn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mt.gox.cn.BaseActivity;
import com.mt.gox.cn.CaptureActivity;
import com.mt.gox.cn.MTApplication;
import com.mt.gox.cn.R;
import com.mt.gox.cn.utils.Global;
import com.mt.gox.cn.utils.Utils;

public class SettingsFragment extends Fragment implements OnClickListener {
  public static final String TAG = SettingsFragment.class.getSimpleName();

  private static final String SETTINGS_SCHEME = "other_category";
  private static final String SETTINGS_AUTHORITY = "settings";
  public static final Uri SETTINGS_URI = new Uri.Builder()
  .scheme(SETTINGS_SCHEME)
  .authority(SETTINGS_AUTHORITY)
  .build();
  
  private BaseActivity mContext;
  
  private Button apiKeyBtn;
  private Button secretBtn;
  
  private Button highBtn;
  private Button lowBtn;
  
  
  private EditText apikeyText;
  private EditText secretkeyText;
  
  private EditText highPriceText;
  private EditText lowPriceText;
  
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	  final View v = inflater.inflate(R.layout.settings, container, false);
	  mContext = (BaseActivity)getActivity();
	  
	  apiKeyBtn = (Button)v.findViewById(R.id.api_key_btn);
	  secretBtn = (Button)v.findViewById(R.id.secret_key_btn);
	  apiKeyBtn.setOnClickListener(this);
	  secretBtn.setOnClickListener(this);
	  highBtn = (Button)v.findViewById(R.id.high_btn);
	  lowBtn = (Button)v.findViewById(R.id.low_btn);
	  highBtn.setOnClickListener(this);
	  lowBtn.setOnClickListener(this);
	  
	  
	  apikeyText = (EditText)v.findViewById(R.id.api_key_text);
	  secretkeyText = (EditText)v.findViewById(R.id.secret_key_text);
	  
	  highPriceText = (EditText)v.findViewById(R.id.highprice_text);
	  lowPriceText = (EditText)v.findViewById(R.id.lowprice_text);
	  
    return v;
  }

  
    
	@Override
public void onResume() {
		
		apikeyText.setText(Utils.getApiKey(mContext));
		secretkeyText.setText(Utils.getSecretKey(mContext));
		highPriceText.setText(Utils.getHighPrice(mContext));
		lowPriceText.setText(Utils.getLowPrice(mContext));
		
		Intent intent = new Intent(Global.ACTION_REFRESH_API_AND_SECRET);
		mContext.sendBroadcast(intent);
		
	super.onResume();
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
				mContext.getActionBarHelper().setRefreshActionItemState(false);
			} 
		}
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = new Intent(mContext, CaptureActivity.class);
		
		switch (v.getId()) {
		case R.id.api_key_btn:
			intent.putExtra(Global.SCAN_TYPE_TAG, 0);
			startActivity(intent);
			break;
		case R.id.secret_key_btn:
			intent.putExtra(Global.SCAN_TYPE_TAG, 1);
			startActivity(intent);
			break;
		case R.id.high_btn:
			Utils.saveHighPrice(mContext, highPriceText.getText().toString());
			break;
		case R.id.low_btn:
			Utils.saveLowPrice(mContext, lowPriceText.getText().toString());		
			break;
		}
		
		
		
	}
	
}
