package com.physical.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
/**
 * 订单Bean
 * @author Jacky
 *
 */
public class Order    {
	//商户订单
	private String orderId; 	
	//支付方式，支付宝或微信
	private int payType;
	//姓名
	private String name;
	//电话
	private String phone; 
	//体检日期
	private String reserveData;
	//体检日期（日期格式）
	private Date dtReserveData;
	//套餐
	private String bundle;
	//原价
	private double rawPrice;
	//折后价
	private double disCountPrice; 
	public String status; 
	//通知次数，支付宝成功后，平台会下发通知结果，为避免多次操作数据库，限定操作次数不得超过5次
	private int notifyed;
	//平台交易编码（微信为prepayid，支付宝为trade_no)
	private String trade_no; 
	
	//订单日期（非体检日期）
	private Date orderDate;
	private static SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd_HHmmss");
	private static Random random=new Random();
	public Order(){
		Calendar calendar=Calendar.getInstance();		 
		this.orderId=formatter.format(calendar.getTime())+"_"+random.nextInt(10);
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getPayType() {
		return payType;
	}
	public void setPayType(int payType) {
		this.payType = payType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getReserveData() {
		return reserveData;
	}
	public void setReserveData(String reserveData) {
		this.reserveData = reserveData;
	}
	public Date getDtReserveData() {
		return dtReserveData;
	}
	public void setDtReserveData(Date dtReserveData) {
		this.dtReserveData = dtReserveData;
	}
	public String getBundle() {
		return bundle;
	}
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	public double getRawPrice() {
		return rawPrice;
	}
	public void setRawPrice(double rawPrice) {
		this.rawPrice = rawPrice;
	}
	public double getDisCountPrice() {
		return disCountPrice;
	}
	public void setDisCountPrice(double disCountPrice) {
		this.disCountPrice = disCountPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getNotifyed() {
		return notifyed;
	}
	public void setNotifyed(int notifyed) {
		this.notifyed = notifyed;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	
	
	 
}
 
