package com.mt.gox.cn.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Toast;

import com.mt.gox.cn.MTApplication;
import com.mt.gox.cn.R;
import com.mt.gox.cn.api.ApiKeys;
import com.mt.gox.cn.api.MtGox;
import com.mt.gox.cn.api.MtGox.Currency;
import com.mt.gox.cn.utils.Global;
import com.mt.gox.cn.utils.Utils;

@SuppressLint("HandlerLeak")
public class GetMtInfoService extends Service{
	
    private MtGox trade ;
    
    private double btc = 0;
    private double usd = 0;
    
    private double currentPrice = 0;
    
    private Context mContext;
    
    private MediaPlayer mp;
	
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		
		String sect = Utils.getGBKStringFromUtf8(Utils.getSecretKey(mContext));
		String api = Utils.getGBKStringFromUtf8(Utils.getApiKey(mContext));
		
		ApiKeys keys = new ApiKeys(sect, api);
		trade = new MtGox(keys);
		handler.sendEmptyMessageAtTime(0, 0);
		
		mp=MediaPlayer.create(this,R.raw.promise);
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			InfoTask task = new InfoTask();
			task.execute("");
			super.handleMessage(msg);
		}
    };
    
    
    class InfoTask extends AsyncTask<String, Integer, Result> {
    	
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Result doInBackground(String... params) {
			
			getBalance();
			
			getOpenOrders();
			
			getCurrentPrice();
			return null;
		}

		protected void onPostExecute(Result result) {
			handler.sendEmptyMessageDelayed(0, 10000);
			if(currentPrice>0)
				Toast.makeText(mContext, " "+ currentPrice, Toast.LENGTH_SHORT).show();
			
			if(currentPrice > Double.parseDouble(Utils.getHighPrice(mContext)) || 
					currentPrice < Double.parseDouble(Utils.getLowPrice(mContext))) {
				if(!mp.isPlaying()) {
					mp.start();
					Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					//vibrator.vibrate( new long[]{100,10,100,10000},-1);
				}
			}
			
			Intent intent = new Intent(Global.ACTION_UPDATE_PERSONEL_INFO);
			sendBroadcast(intent);
			
			super.onPostExecute(result);
		}
    }
    
    private void updateTrade() {
    	String sect = Utils.getGBKStringFromUtf8(Utils.getSecretKey(mContext));
		String api = Utils.getGBKStringFromUtf8(Utils.getApiKey(mContext));
		
		System.out.println("=="+sect);
		System.out.println("=="+api);
		ApiKeys keys = new ApiKeys(sect, api);
		trade = new MtGox(keys);
    }
	
	private void getBalance() {
		if (MTApplication.getInstance().btc == 0 && MTApplication.getInstance().usd == 0) {
			updateTrade();
			double[] balance = trade.getBalance();
			double balanceBTC = balance[0]; // balance in BTC
			double balanceUSD = balance[1]; // balance in USD
			btc = balanceBTC;
			usd = balanceUSD;
			
			System.out.println(btc+ "   " + usd);
			
			MTApplication.getInstance().btc = btc;
			MTApplication.getInstance().usd = usd;
		}
	}
	
	private void getOpenOrders() {
		
		updateTrade();
		
		if(MTApplication.getInstance().orders == null)
			MTApplication.getInstance().orders = trade.getOpenOrders();
	}
	
	private void getCurrentPrice() {
		
		updateTrade();
		
		currentPrice = trade.getLastPrice(Currency.USD);
		
		MTApplication.getInstance().unitPrice = currentPrice;
	}
	

}
