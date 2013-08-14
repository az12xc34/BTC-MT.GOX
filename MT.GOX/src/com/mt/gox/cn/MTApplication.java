package com.mt.gox.cn;

import java.util.List;

import android.app.Application;
import android.content.Intent;

import com.mt.gox.cn.fragment.MtgoxPersonInfoFragment;
import com.mt.gox.cn.model.Order;
import com.mt.gox.cn.model.Trade;
import com.mt.gox.cn.service.GetMtInfoService;

public class MTApplication extends Application {
	
	public static MTApplication _instance;
	
	public double btc = 0;
	public double usd = 0;
	public double unitPrice = 0;
	
	public List<Order> orders ;
	
	public List<Order>[] marketorders;
	
	public List<Trade> trades;
	
	public String mFragmentTag = MtgoxPersonInfoFragment.TAG;
	
	public boolean isUnlock = false;
	
	
	public void onCreate() {
		super.onCreate();
		_instance = this;
		
		startService(new Intent(this, GetMtInfoService.class));
	}

	public static MTApplication getInstance() {
		return _instance;
	}
	
    
}
