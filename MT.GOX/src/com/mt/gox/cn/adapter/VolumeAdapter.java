package com.mt.gox.cn.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mt.gox.cn.R;
import com.mt.gox.cn.model.Trade;

public class VolumeAdapter extends BaseAdapter {

	private List<Trade> mTrades;
	
	private LayoutInflater mInflater;
	
    DecimalFormat df = new DecimalFormat("0.00000");
	
	public VolumeAdapter(Context context, List<Trade> trades) {
		mTrades = trades;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mTrades.size();
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
			convertView = mInflater.inflate(R.layout.volume_item, null);
			holder.tradeTime = (TextView) convertView.findViewById(R.id.volume_item_time);
			holder.tradePrice = (TextView) convertView.findViewById(R.id.volume_item_price);
			holder.tradeNumber = (TextView) convertView.findViewById(R.id.volume_item_number);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		Trade trade = mTrades.get(position);
		
		String datetime = DateFormat.format("kk:mm:ss", trade.date).toString();
		holder.tradeTime.setText(datetime);
		holder.tradePrice.setText(df.format(trade.price));
		holder.tradeNumber.setText(df.format(trade.amount));
		
		if(trade.trade_type.equals("ask"))
			holder.tradePrice.setTextColor(Color.RED);
		else
			holder.tradePrice.setTextColor(Color.GREEN);		
		return convertView;
	}

	class ViewHolder {
		TextView tradeTime;
		TextView tradePrice;
		TextView tradeNumber;
	}


}
