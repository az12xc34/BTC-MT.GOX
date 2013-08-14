package com.mt.gox.cn.fragment;

import java.text.DecimalFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CursorJoiner.Result;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mt.gox.cn.BaseActivity;
import com.mt.gox.cn.MTApplication;
import com.mt.gox.cn.R;
import com.mt.gox.cn.adapter.OpenOrderAdapter;
import com.mt.gox.cn.api.ApiKeys;
import com.mt.gox.cn.api.MtGox;
import com.mt.gox.cn.utils.Global;
import com.mt.gox.cn.utils.Utils;

public class MtgoxPersonInfoFragment extends Fragment implements OnClickListener,
		TextWatcher {
	public static final String TAG = MtgoxPersonInfoFragment.class.getSimpleName();

	private static final String PERSONINFO_SCHEME = "mtgox_category";
	private static final String PERSONINFO_AUTHORITY = "personinfo";
	public static final Uri PERSONINFO_URI = new Uri.Builder().scheme(PERSONINFO_SCHEME)
			.authority(PERSONINFO_AUTHORITY).build();

	private TextView btcNumber;
	private TextView usdNumber;

	private TextView unitPrice;
	private TextView totalCount;

	private TextView butCountString;
	private TextView sellCountString;

	private EditText buyBtc;
	private EditText sellBtc;

	private EditText buyprice;
	private EditText sellprice;

	private Button buyButton;
	private Button sellButton;

	private ListView orderList;

	private BaseActivity mContext;

	private MtGox trade;

	private boolean isCancelOrder = false;

	DecimalFormat df = new DecimalFormat("0.000");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.main, container, false);

		btcNumber = (TextView) v.findViewById(R.id.btcnumber);
		usdNumber = (TextView) v.findViewById(R.id.usdnumber);

		totalCount = (TextView) v.findViewById(R.id.totalcount);

		orderList = (ListView) v.findViewById(R.id.orders);

		butCountString = (TextView) v.findViewById(R.id.buycount);
		sellCountString = (TextView) v.findViewById(R.id.sellcount);

		buyBtc = (EditText) v.findViewById(R.id.buybtc);
		sellBtc = (EditText) v.findViewById(R.id.sellbtc);
		buyBtc.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		sellBtc.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		buyBtc.addTextChangedListener(this);
		sellBtc.addTextChangedListener(this);

		buyprice = (EditText) v.findViewById(R.id.buyprice);
		sellprice = (EditText) v.findViewById(R.id.sellprice);
		buyprice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		sellprice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		buyprice.addTextChangedListener(this);
		sellprice.addTextChangedListener(this);

		buyButton = (Button) v.findViewById(R.id.buy_btn);
		sellButton = (Button) v.findViewById(R.id.sell_btn);
		buyButton.setOnClickListener(this);
		sellButton.setOnClickListener(this);

		buyButton.setEnabled(false);
		sellButton.setEnabled(false);

		unitPrice = (TextView) v.findViewById(R.id.unitprice);

		trade = new MtGox(Global.keys);

		mContext = (BaseActivity) getActivity();

		updateInfo();
		
		updateOrders();
		
		return v;
	}

	public void onResume() {
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

	@Override
	public void afterTextChanged(Editable s) {
		double buyP = 0.000;
		double sellP = 0.000;
		double buyN = 0.000;
		double sellN = 0.000;

		if (!buyprice.getText().toString().equals("")
				&& buyprice.getText().toString() != null) {
			buyP = Double.parseDouble(buyprice.getText().toString());
		}

		if (!sellprice.getText().toString().equals("")
				&& sellprice.getText().toString() != null) {
			sellP = Double.parseDouble(sellprice.getText().toString());
		}

		if (!buyBtc.getText().toString().equals("")
				&& buyBtc.getText().toString() != null) {
			buyN = Double.parseDouble(buyBtc.getText().toString());
		}

		if (!sellBtc.getText().toString().equals("")
				&& sellBtc.getText().toString() != null) {
			sellN = Double.parseDouble(sellBtc.getText().toString());
		}

		if (buyP > 0 && buyN > 0)
			buyButton.setEnabled(true);
		else
			buyButton.setEnabled(false);
		if (sellP > 0 && sellN > 0)
			sellButton.setEnabled(true);
		else
			sellButton.setEnabled(false);

		butCountString.setText(" = " + df.format(buyP * buyN));

		sellCountString.setText(" = " + df.format(sellP * sellN));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onClick(View v) {
		Utils.hiddenInput(mContext);

		if (buyButton == v) {
			BuyTask tast = new BuyTask();
			tast.execute("");
		} else if (v == sellButton) {
			SellTask tast = new SellTask();
			tast.execute("");
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
	
	class GetOpenOrdersTask extends AsyncTask<String, Integer, Result> {

		@Override
		protected void onPreExecute() {
			mContext.getActionBarHelper().setRefreshActionItemState(true);
			super.onPreExecute();
		}

		protected Result doInBackground(String... arg0) {
			updateTrade();
			MTApplication.getInstance().orders = trade.getOpenOrders();

			return null;
		}

		protected void onPostExecute(Result result) {

			updateOrders();
			mContext.getActionBarHelper().setRefreshActionItemState(false);
			super.onPostExecute(result);
		}
	}

	class GetInfoTast extends AsyncTask<String, Integer, Result> {
		@Override
		protected Result doInBackground(String... params) {
			updateTrade();
			double[] balance = trade.getBalance();
			double balanceBTC = balance[0]; // balance in BTC
			double balanceUSD = balance[1]; // balance in USD
			MTApplication.getInstance().btc = balanceBTC;
			MTApplication.getInstance().usd = balanceUSD;

			return null;
		}

		@Override
		protected void onPostExecute(Result result) {
			mContext.getActionBarHelper().setRefreshActionItemState(false);
			updateInfo();

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			mContext.getActionBarHelper().setRefreshActionItemState(true);
			super.onPreExecute();
		}
	}

	private void updateInfo() {
		DecimalFormat df = new DecimalFormat("0.000");
		btcNumber.setText(df.format(MTApplication.getInstance().btc));
		usdNumber.setText(df.format(MTApplication.getInstance().usd));
		unitPrice.setText(df.format(MTApplication.getInstance().unitPrice));
		double total = MTApplication.getInstance().unitPrice
				* MTApplication.getInstance().btc
				+ MTApplication.getInstance().usd;
		totalCount.setText(df.format(total));
	}

	private void updateOrders() {
		if (MTApplication.getInstance().orders != null) {
			OpenOrderAdapter adapter = new OpenOrderAdapter(mContext,
					MTApplication.getInstance().orders);
			orderList.setAdapter(adapter);
			isCancelOrder = false;
			mContext.setTitle("MT.GOX");
		}
	}

	class BuyTask extends AsyncTask<String, Integer, Result> {

		@Override
		protected void onPreExecute() {
			buyButton.setEnabled(false);
			mContext.getActionBarHelper().setRefreshActionItemState(true);
			mContext.setTitle("正在提交买单");
			super.onPreExecute();
		}

		protected Result doInBackground(String... params) {
			Double number = Double.parseDouble(buyBtc.getText().toString());
			Double price = Double.parseDouble(buyprice.getText().toString());
			updateTrade();
			trade.buyBTC(number, price);
			return null;
		}

		protected void onPostExecute(Result result) {
			mContext.setTitle("已提交买单");
			buyButton.setEnabled(true);
			mContext.getActionBarHelper().setRefreshActionItemState(false);
			buyBtc.setText("");
			buyprice.setText("");

			// 刷新订单
			GetOpenOrdersTask orderTask = new GetOpenOrdersTask();
			orderTask.execute();

			super.onPostExecute(result);
		}
	}

	class SellTask extends AsyncTask<String, Integer, Result> {

		@Override
		protected void onPreExecute() {
			sellButton.setEnabled(false);
			mContext.getActionBarHelper().setRefreshActionItemState(true);
			mContext.setTitle("正在提交卖单");
			super.onPreExecute();
		}

		protected Result doInBackground(String... params) {
			Double number = Double.parseDouble(sellBtc.getText().toString());
			Double price = Double.parseDouble(sellprice.getText().toString());
			updateTrade();
			trade.sellBTC(number, price);
			return null;
		}

		protected void onPostExecute(Result result) {
			mContext.setTitle("已提交卖单");
			sellButton.setEnabled(true);

			sellBtc.setText("");
			sellprice.setText("");
			mContext.getActionBarHelper().setRefreshActionItemState(false);
			GetOpenOrdersTask orderTask = new GetOpenOrdersTask();
			orderTask.execute();

			super.onPostExecute(result);
		}
	}

	class CancelOrderTask extends AsyncTask<String, Integer, Result> {

		@Override
		protected void onPreExecute() {
			//Toast.makeText(mContext, "正在取消订单", Toast.LENGTH_SHORT).show();
			mContext.setTitle("正在取消订单");
			mContext.getActionBarHelper().setRefreshActionItemState(true);
			super.onPreExecute();
		}

		protected Result doInBackground(String... params) {
			String oid = params[0];
			updateTrade();
			trade.cancelOrder(oid);
			return null;
		}

		protected void onPostExecute(Result result) {
			mContext.setTitle("已取消订单");
			mContext.getActionBarHelper().setRefreshActionItemState(true);
			GetOpenOrdersTask orderTask = new GetOpenOrdersTask();
			orderTask.execute();

			super.onPostExecute(result);
		}
	}

	MyBroadcastReceive receive = new MyBroadcastReceive();

	private void initBroadcast() {

		IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(Global.ACTION_UPDATE_PERSONEL_INFO);
		intentFilter.addAction(Global.ACTION_CANCEL_ORDER);
		intentFilter.addAction(Global.ACTION_REFRESH_BTN);

		mContext.registerReceiver(receive, intentFilter);
	}

	private class MyBroadcastReceive extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Global.ACTION_UPDATE_PERSONEL_INFO.equals(action)) {
				updateInfo();
				if (!isCancelOrder)
					updateOrders();
			} else if (Global.ACTION_CANCEL_ORDER.equals(action)) {
				String oid = intent.getStringExtra("oid");
				isCancelOrder = true;
				CancelOrderTask task = new CancelOrderTask();
				task.execute(oid);
			} else if (Global.ACTION_REFRESH_BTN.equals(action) &&  MTApplication.getInstance().mFragmentTag.equals(TAG)) {
                mContext.setTitle("正在获取个人信息");
                GetInfoTast task = new GetInfoTast();
                task.execute();
                
                GetOpenOrdersTask orderTask = new GetOpenOrdersTask();
                orderTask.execute();
			}
		}
	}

}
