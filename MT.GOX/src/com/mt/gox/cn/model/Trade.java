package com.mt.gox.cn.model;

public class Trade {
	// {"date":1374569186,"price":"93.23999","amount":"0.01","price_int":"9323999","amount_int":"1000000",
	//"tid":"1374569186526635","price_currency":"USD","item":
	//	"BTC","trade_type":"bid","primary":"N","properties":"market,mixed_currency"}
	
	public long date;
	public double price;
	public double amount;
	public int price_int;
	public int amount_int;
	public int tid;
	public String price_currency;
	public String item;
	public String trade_type;
	@Override
	public String toString() {
		return "Trade [date=" + date + ", price=" + price + ", amount="
				+ amount + ", price_int=" + price_int + ", amount_int="
				+ amount_int + ", tid=" + tid + ", price_currency="
				+ price_currency + ", item=" + item + ", trade_type="
				+ trade_type + "]";
	}
	
}
