package com.physical.bean;
/**
 * 订单查询参数
 * @author Jacky
 *
 */
public class TradeQueryParam {
	//单笔查询
	private String out_trade_no;
	private String trade_no; 
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	
	//单日对账单查询
		private String bill_type;
		private String bill_date;
		public String getBill_type() {
			return bill_type;
		}
		public void setBill_type(String bill_type) {
			this.bill_type = bill_type;
		}
		public String getBill_date() {
			return bill_date;
		}
		public void setBill_date(String bill_date) {
			this.bill_date = bill_date;
		}
	
}
