package com.physical.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 请求对账单,传此参数去获取特定日期的对账单
 * @author Jacky
 *
 */
@XmlRootElement(name="xml")
public class OrderBillRequest {
	//公众账号ID
	private String appid;
	//商户号
	private String 	mch_id;
	//随机字符串
	private String nonce_str;	
	//签名
	private String sign;
	//对账单日期yyyyMMdd
	private String bill_date;
	//账单类型
	private String bill_type;
		
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
	
	@XmlElement(name="sign")
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	@XmlElement(name="bill_date")
	public String getBill_date() {
		return bill_date;
	}
	public void setBill_date(String bill_date) {
		this.bill_date = bill_date;
	}
	
	@XmlElement(name="bill_type")
	public String getBill_type() {
		return bill_type;
	}
	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}
	
}
