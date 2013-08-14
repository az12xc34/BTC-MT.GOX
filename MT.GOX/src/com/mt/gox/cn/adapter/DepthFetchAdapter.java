package com.mt.gox.cn.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mt.gox.cn.R;
import com.mt.gox.cn.model.Order;

public class DepthFetchAdapter extends BaseAdapter {

	private List<Order> mSellOrders;
	
	private List<Order> mBuyOrders;

	private LayoutInflater mInflater;
	
	private int count = 0;
	
    DecimalFormat df = new DecimalFormat("0.0000");
	
	public DepthFetchAdapter(Context context, List<Order> sellorders, List<Order> buyorders) {
		mSellOrders = sellorders;
		mBuyOrders = buyorders;
		mInflater = LayoutInflater.from(context);
		
		count = Math.max(mSellOrders.size(), mBuyOrders.size());
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.depth_fetch_item, null);
			holder.buyNumber = (TextView) convertView.findViewById(R.id.depth_fetch_item_buy_number);
			holder.buyPrice = (TextView) convertView.findViewById(R.id.depth_fetch_item_buy_price);
			holder.sellPrice = (TextView) convertView.findViewById(R.id.depth_fetch_item_sell_price);
			holder.sellNumber = (TextView) convertView.findViewById(R.id.depth_fetch_item_sell_number);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position < mSellOrders.size()) {
			Order sellorder = mSellOrders.get(position);
		
			holder.sellPrice.setText(df.format(sellorder.price));
			holder.sellNumber.setText(df.format(sellorder.amount));
			
			if(sellorder.amount>100)
				holder.sellNumber.setTextColor(Color.RED);
			else 
				holder.sellNumber.setTextColor(Color.BLACK);
		}
		
		if(position < mBuyOrders.size()) {
		
			Order buyorder = mBuyOrders.get(position);
		
			holder.buyPrice.setText(df.format(buyorder.price));
			holder.buyNumber.setText(df.format(buyorder.amount));
			
			if(buyorder.amount>100)
				holder.buyNumber.setTextColor(Color.RED);
			else 
				holder.buyNumber.setTextColor(Color.BLACK);
		}
		
		return convertView;
	}

	class ViewHolder {
		TextView buyNumber;
		TextView buyPrice;
		TextView sellPrice;
		TextView sellNumber;
	}


}
