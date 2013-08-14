package com.mt.gox.cn.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mt.gox.cn.R;
import com.mt.gox.cn.model.Order;

public class OpenOrderAdapter extends BaseAdapter implements OnClickListener {

	private List<Order> mOrders;

	private LayoutInflater mInflater;
	
	private Context mContext;

	public OpenOrderAdapter(Context context, List<Order> orders) {
		mOrders = orders;
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		return mOrders.size();
	}

	@Override
	public Object getItem(int position) {
		return mOrders.get(position);
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
			convertView = mInflater.inflate(R.layout.market_list_item, null);
			holder.cancle = (Button) convertView
					.findViewById(R.id.market_order_item_cancel);
			holder.info = (TextView) convertView
					.findViewById(R.id.martket_order_item);
			holder.price = (TextView) convertView.findViewById(R.id.martket_order_price);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		Order order = mOrders.get(position);
		String info = (order.type.equals("ask")?"卖：":"买：") + " " + df.format(order.amount) + "(个)  "; 
		holder.info.setText(info);
		
		String price =  df.format(order.price) + "(美元)";
		holder.price.setText(price);
		
		holder.cancle.setTag(order.oid);
		
		holder.cancle.setOnClickListener(this);

		return convertView;
	}

	class ViewHolder {
		TextView info;
		TextView price;
		Button cancle;
	}

	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		String oid = (String)v.getTag();
		Intent intent = new Intent("action.btc.cancelOrder");
		intent.putExtra("oid", oid);
		mContext.sendBroadcast(intent);		
	}

}
