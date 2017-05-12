package com.physical.bean;

/**
 * 订单修改历史
 * @author Jacky
 *
 */
public class OrderHistory {
	private String orderId;
	private String oldstatus;
	private String newstatus;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOldstatus() {
		return oldstatus;
	}
	public void setOldstatus(String oldstatus) {
		this.oldstatus = oldstatus;
	}
	public String getNewstatus() {
		return newstatus;
	}
	public void setNewstatus(String newstatus) {
		this.newstatus = newstatus;
	}
}
