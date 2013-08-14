package com.mt.gox.cn.utils;

import com.mt.gox.cn.api.ApiKeys;

public class Global {
	
	public static final ApiKeys keys = new ApiKeys(
			"",
			"");
	//刷新个人信息
	public static final String ACTION_UPDATE_PERSONEL_INFO = "action.btc.updateInfo";
	//取消订单
	public static final String ACTION_CANCEL_ORDER = "action.btc.cancelOrder";
	//刷新按钮点击
	public static final String ACTION_REFRESH_BTN = "action.menu.refresh";
	
	public static final String ACTION_REFRESH_API_AND_SECRET = "action.refresh_api_and_secret";
	
	public static final String ACTION_SCAN_ZXING_API = "action.scan.zing.api";
	
	public static final String ACTION_SCAN_ZXING_SECRET = "action.scan.zing.secret";
	
	public static final String PREF_APPID = "mtgox_pref_appid";
	
	public static final String PREF_API_TAG = "mtgox_pref_api_tag";
	
	public static final String PREF_SECRET_TAG = "mtgox_pref_secret_tag";
	
	public static final String SCAN_TYPE_TAG = "scan_type_tag";
	
	public static final String PREF_HIGH_PRICE_TAG = "pref_high_price_tag";
	
	public static final String PREF_LOW_PRICE_TAG = "pref_low_price_tag";
	
}
