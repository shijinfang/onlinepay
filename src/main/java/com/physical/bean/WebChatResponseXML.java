package com.physical.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
public class WebChatResponseXML {
	
	private int return_code;
	private String return_msg;
	private String appid;
	private String mch_id;
	private String nonce_str;
	//openid
	private String openid;
	private String sign;
	private String result_code;
	private String prepay_id;
	private String trade_type;
	
	
	private String time_end;
	private String transaction_id;
	public String getTime_end() {
		return time_end;
	}
	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	//内容
	private String body;
	private String out_trade_no;
	
	//费用
	private int total_fee;
	
	//客户端地址
	private String spbill_create_ip;
	
	//通知地址
	private String notify_url; 
	
	@XmlElement(name="body")
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@XmlElement(name="out_trade_no")
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	
	@XmlElement(name="total_fee")
	public int getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}
	
	@XmlElement(name="spbill_create_ip")
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	
	@XmlElement(name="notify_url")
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	
	
	
	@XmlElement(name="return_code")
	public int getReturn_code() {
		return return_code;
	}
	public void setReturn_code(int return_code) {
		this.return_code = return_code;
	}
	
	@XmlElement(name="return_msg")
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	
	@XmlElement(name="appid")
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	@XmlElement(name="mch_id")
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	
	@XmlElement(name="nonce_str")
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	
	@XmlElement(name="openid")
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	@XmlElement(name="sign")
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	@XmlElement(name="result_code")
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	
	@XmlElement(name="prepay_id")
	public String getPrepay_id() {
		return prepay_id;
	}
	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
	
	@XmlElement(name="trade_type")
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	
}
