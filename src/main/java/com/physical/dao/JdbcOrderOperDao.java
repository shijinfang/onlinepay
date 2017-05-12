package com.physical.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.physical.bean.Order;
import com.physical.bean.OrderHistory;

@Repository
public class JdbcOrderOperDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 保存订单
	 * @param order
	 */
	public void saveOrder(Order order){
		String sql="insert into orders "+
				"(orderId,payType,name,phone,reserveData,bundle,rawPrice,disCountPrice,status) "+ 
		  "values(?,?,?,?,?,?,?,?,?) "+
		  "on duplicate key update status=?,notifyed=? ";
		if(!StringUtils.isEmpty(order.getTrade_no())){
			sql+=String.format(",trade_no='%s'",order.getTrade_no());
		}
		jdbcTemplate.update(sql, new Object[]{
				order.getOrderId(),
				order.getPayType(),
				order.getName(),
				order.getPhone(),
				order.getReserveData(),
				order.getBundle(),
				order.getRawPrice(),
				order.getDisCountPrice(),
				order.getStatus(),
				order.getStatus(),
				order.getNotifyed()
				});
	
	}
	
	/**
	 * 根据orderid查询订单
	 * @param id
	 * @return
	 */
	public Order queryOrder(String id){
		Order order=jdbcTemplate.query(String.format("select * from orders where orderId='%s'",id), 
				new ResultSetExtractor<Order>(){
			@Override
			public Order extractData(ResultSet rs) throws SQLException, DataAccessException {
				Order order=new Order();
				if(rs.next()){
				order.setOrderId(rs.getString("orderid"));
				order.setTrade_no(rs.getString("trade_no"));
				order.setPayType(rs.getInt("payType"));
				order.setName(rs.getString("name"));
				order.setPhone(rs.getString("phone"));
				order.setReserveData(rs.getString("reserveData"));
				order.setBundle(rs.getString("bundle"));
				order.setRawPrice(rs.getDouble("rawPrice"));
				order.setDisCountPrice(rs.getDouble("disCountPrice"));
				order.setStatus(rs.getString("status"));
				order.setNotifyed(rs.getInt("notifyed"));
				order.setOrderDate(rs.getDate("orderDate"));
				}
				return order;
			}
		});
		return order;
		
	}
	
	/**
	 * 列表笔数
	 * @param date
	 * @param bundle
	 * @param name
	 * @param phone
	 * @param status
	 * @return
	 */
	public int orderCount(String date,String bundle,String name,String phone,String status){
		
		String sql="select count(*) from orders where 1=1 ";
		if(!StringUtils.isEmpty(date)){
			sql+=String.format("and reserveData='%s' ", date);
		}
		if(!StringUtils.isEmpty(bundle)){
			sql+=String.format("and bundle='%s' ", bundle);
		}
		if(!StringUtils.isEmpty(status)){
			sql+=String.format("and status='%s' ", status);
		}
		if(!StringUtils.isEmpty(phone)){
			sql+=String.format("and phone='%s' ", phone);
		}
		if(!StringUtils.isEmpty(name)){
			sql+=String.format("and name='%s' ", name);
		}
		return jdbcTemplate.queryForObject(sql, Integer.class);
 
		
	}
	
	
	/**
	 * 订单列表
	 * @param offset
	 * @param count
	 * @param date
	 * @param bundle
	 * @param name
	 * @param phone
	 * @param status
	 * @return
	 */
	public List<Order> listOrders(int offset,int count,String date,String bundle,String name,String phone,String status){
		String sql="select * from orders where 1=1 ";
		if(!StringUtils.isEmpty(date)){
			sql+=String.format("and reserveData='%s' ", date);
		}
		if(!StringUtils.isEmpty(bundle)){
			sql+=String.format("and bundle='%s' ", bundle);
		}
		if(!StringUtils.isEmpty(status)){
			sql+=String.format("and status='%s' ", status);
		}
		if(!StringUtils.isEmpty(phone)){
			sql+=String.format("and phone='%s' ", phone);
		}
		if(!StringUtils.isEmpty(name)){
			sql+=String.format("and name='%s' ", name);
		}
		sql+=String.format("order by reserveData limit %d,%d",offset,count);
		
		return jdbcTemplate.query(sql,new RowMapper<Order>(){
			@Override
			public Order mapRow(ResultSet rs, int arg1) throws SQLException {
				// TODO Auto-generated method stub
				Order order=new Order();
				order.setOrderId(rs.getString("orderid"));
				order.setTrade_no(rs.getString("trade_no"));
				order.setPayType(rs.getInt("payType"));
				order.setName(rs.getString("name"));
				order.setPhone(rs.getString("phone"));
				order.setReserveData(rs.getString("reserveData"));
				order.setBundle(rs.getString("bundle"));
				order.setRawPrice(rs.getDouble("rawPrice"));
				order.setDisCountPrice(rs.getDouble("disCountPrice"));
				order.setStatus(rs.getString("status"));
				order.setNotifyed(rs.getInt("notifyed"));
				order.setOrderDate(rs.getDate("orderDate"));
				return order;
			} 
		});
		
	}
	
	/**
	 * 订单操作记录
	 * @param orderHistory
	 */
	public void saveOrderStatusChange(OrderHistory orderHistory){
		String sql="insert into order_change_history(orderId,newstatus,oldstatus) "+ 
				   String.format("values('%s','%s','%s')",orderHistory.getOrderId(),orderHistory.getNewstatus(),orderHistory.getOldstatus());
		jdbcTemplate.update(sql);
	}
	
	
	/**
	 * 从数据库查询ACCESS_TOKEN
	 * @param key
	 * @return
	 */
	public String queryToken( String key){
		String sql="select token_value from t_tokens "+
				   "where token_date>date_sub(now(),interval valid second)"+
				   "and token_key='%s'";
		sql=String.format(sql, key);
		return jdbcTemplate.queryForObject(sql, String.class);
	}
	
	/**
	 * 保存或刷新ACCESS_TOKEN
	 * @param key
	 * @param value
	 * @param valid
	 */
	public void updateToken(String key,String value,int valid){
		String sql="insert into t_tokens(token_key,token_value,valid)"+
				   "values('%s','%s','%d')"+
				   "on duplicate key update token_value='%s'";
		sql=String.format(sql, key,value,valid,value);
		jdbcTemplate.execute(sql);
	}
}
