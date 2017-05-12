package com.physical.service;

 
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.physical.bean.BizContent;
import com.physical.bean.MenuBar;
import com.physical.bean.Order;
import com.physical.bean.OrderBillRequest;
import com.physical.bean.OrderHistory;
import com.physical.bean.PageToken;
import com.physical.bean.RootMenu;
import com.physical.bean.SubMenu;
import com.physical.bean.TokenBean;
import com.physical.bean.TradeQueryParam;
import com.physical.bean.UserInfo;
import com.physical.dao.JdbcOrderOperDao;
import com.physical.util.Constants;
import com.physical.util.HttpUtils;
import com.physical.util.JAXBUtils;
import com.physical.util.MD5MessageDigest;
import com.physical.util.MapToXml;
import com.physical.util.PrepayResult;

/**
 * 合并Service代码的service
 * @author Jacky
 *
 */
@Service
public class OverallService {
	@Autowired
	private JdbcOrderOperDao orderOperDao;
	
	private AlipayClient alipayClient;
	private static Properties p=new Properties();
	static{
		
		try {
			p.load(OverallService.class.getClassLoader().getResourceAsStream("payconfig.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 
			e.printStackTrace();
		}
	}
	private String readFile(String filepath){
		try{ 
			InputStream in=OverallService.class.getClassLoader().getResourceAsStream(filepath); 
			byte[] bs=new byte[1024];
			ByteArrayOutputStream bo=new ByteArrayOutputStream();
			int len=-1;
			while((len=in.read(bs, 0, 1024))!=-1){
				bo.write(bs, 0, len);
			}
			return new String(bo.toByteArray());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}
	public OverallService(){ 
			String app_private_key=readFile("app_private_key"); 
			 
			alipayClient = new DefaultAlipayClient(
					p.getProperty("ALIPAY_GATEWAY"), 
					p.getProperty("ALIPAY_APPID"), 
					app_private_key, 
					"json", 
					"GBK", 
					p.getProperty("ALIPAY_PUBLIC_KEY"),
					"RSA2");
			 
		 
	}

	/**
	 * 保存订单
	 * @param order
	 */
	public void saveOrderOper(Order order){
		orderOperDao.saveOrder(order);
	}
	
 

	/**
	 * 查询订单
	 * @param id
	 * @return
	 */
	public Order queryOrder(String id) {
		// TODO Auto-generated method stub
		return orderOperDao.queryOrder(id);
	}

	/**
	 * 订单列表
	 * @param offset
	 * @param count
	 * @param date
	 * @param bundler
	 * @param name
	 * @param phone
	 * @param status
	 * @return
	 */
	public List<Order> listOrders(int offset, int count,String date,String bundler,String name,String phone,String status) {
		// TODO Auto-generated method stub
		
		return orderOperDao.listOrders(offset, count,  date,  bundler,  name,  phone,  status);
	}

	
	/**
	 * 列表条目数
	 * @param date
	 * @param bundler
	 * @param name
	 * @param phone
	 * @param status
	 * @return
	 */
	public int orderCount(String date,String bundler,String name,String phone,String status) {
		// TODO Auto-generated method stub
		return orderOperDao.orderCount(date,  bundler,  name,  phone,  status);
	}
	

	/**
	 * 订单操作历史
	 * @param orderHistory
	 */
	public void saveOrderStatusChange(OrderHistory orderHistory){
		orderOperDao.saveOrderStatusChange(orderHistory);
	}
	
	/******************************************************************************************/
	/******************************************************************************************/
 
	
	/**
	 * 获取支付宝收银台地址
	 * @param order
	 * @return
	 */
	public String getPayUrl(Order order,String urlPrefix) {
			
			AlipayTradeWapPayRequest  request=new AlipayTradeWapPayRequest();
			BizContent biz=new BizContent();
			biz.setOut_trade_no(order.getOrderId()); 
			biz.setProduct_code("QUICK_WAP_PAY");
			try {
				biz.setSubject(URLEncoder.encode(order.getBundle(),"GBK"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 
			biz.setTotal_amount(String.valueOf(order.getDisCountPrice()));
			if(p.containsKey("ALIPAY_MERCID")){
				biz.setSeller_id(p.getProperty("ALIPAY_MERCID"));
			}
			
			Gson gson=new Gson();
			String json=gson.toJson(biz); 
			request.setNotifyUrl(urlPrefix+Constants.ALIPAY_NOTIFY_URL);
			request.setReturnUrl(urlPrefix+Constants.ALIPAY_RETURN_URL);
			request.setBizContent(json);
			
			try {
				String form = alipayClient.pageExecute(request).getBody();
				return form;
			} catch (AlipayApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			
		 
		}
	
	/**
	 * 接受支付结果通知
	 * @param request
	 * @return
	 */
	public Order processReturn(HttpServletRequest request){
		String out_trade_no=request.getParameter("out_trade_no");
		String trade_no=request.getParameter("trade_no");
		//out_trade_no="20170312_223430_2";
		Order order=new Order();
		order.setOrderId(null);
		if(!StringUtils.isEmpty(out_trade_no)){
			order=orderOperDao.queryOrder(out_trade_no);
			if(null!=order&&order.getNotifyed()<=5){
				//防止将失败状态改成成功
				if(!order.getStatus().equals("支付失败"))
					order.setStatus("支付成功");
				order.setTrade_no(trade_no);
				order.setNotifyed(order.getNotifyed()+1);
				orderOperDao.saveOrder(order);
			}
		
		}
		return order;
	}
	
	
	/**
	 * 支付成功后跳转地址
	 */
	public void processNotify(HttpServletRequest request){
		String trade_status=request.getParameter("trade_status");
		String trade_no=request.getParameter("trade_no");
		String out_trade_no=request.getParameter("out_trade_no");
		Order order=orderOperDao.queryOrder(out_trade_no);
		if(null!=order&&order.getNotifyed()<=5){
			if(trade_status.equals("TRADE_SUCCESS"))
				order.setStatus("支付成功");
			else
				order.setStatus("支付失败");
			order.setTrade_no(trade_no);
			order.setNotifyed(order.getNotifyed()+1);
			orderOperDao.saveOrder(order);
		}
		
		
	}
	
  /**
   * 查询订单
   * @param orderId
   * @return
   */
	public String queryOrderFromAlipay(String orderId){
		Order order=orderOperDao.queryOrder(orderId);
		if(null!=order){
			AlipayTradeQueryRequest request=new AlipayTradeQueryRequest();
			TradeQueryParam param=new TradeQueryParam();
			param.setOut_trade_no(orderId);
			param.setTrade_no(order.getTrade_no());
			request.setBizContent(new Gson().toJson(param));
			try{
			AlipayTradeQueryResponse response=alipayClient.execute(request);
			return response.getBody();
			}catch(Exception e){
				return e.getMessage();
			}
		}
		return "order not exists";
	}

	/**
	 * 查询对账单
	 * @param bill_type
	 * @param bill_date
	 * @return
	 */
	public String queryBillList(String bill_type,String bill_date){
		AlipayDataDataserviceBillDownloadurlQueryRequest  request=new AlipayDataDataserviceBillDownloadurlQueryRequest();
		TradeQueryParam param=new TradeQueryParam();
		param.setBill_type(bill_type);
		param.setBill_date(bill_date);
		request.setBizContent(new Gson().toJson(param));
		try{
		AlipayDataDataserviceBillDownloadurlQueryResponse response=alipayClient.execute(request);
		return response.getBody();
		}catch(Exception e){
			return e.getMessage();
		}
	}
	
	/******************************************************************************************/
	/******************************************************************************************/
	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 */ 
	public UserInfo getUserInfoFromCode(String code){ 
		if(!StringUtils.isEmpty(code)){
			String url=String.format(Constants.WEBCHAT_PAGEACCTOKEN_URL, Constants.WEBCHAT_APPID,Constants.WEBCHAT_APPSECRET,code);
			String result=HttpUtils.sendPost(url, null, false);
			if(!StringUtils.isEmpty(result)){
				PageToken pageToken=new Gson().fromJson(result, PageToken.class);
				if(pageToken!=null){
					url=String.format(Constants.WEBCHAT_PAGEUSERINFO_URL, pageToken.getAccess_token(),pageToken.getOpenid());
					result=HttpUtils.sendPost(url, null, false);
					if(!StringUtils.isEmpty(result)){
						UserInfo info=new Gson().fromJson(result, UserInfo.class); 
						return info;
					} 
				}
			}
		}
		return null; 
	}
	/**
	 * 根据订单，生成预订单详情给微信浏览器前台发起支付
	 * @param order
	 * @return
	 */
 
	public Map<String,String> getPrepayOrder(Order order,String remoteIp,String openid){
		//构造prepay信息 
		Map<String,String> map=new HashMap<String,String>();
		map.put("appid", Constants.WEBCHAT_APPID);
		map.put("openid", openid);
		try {
			map.put("body", URLEncoder.encode("微信支付-"+order.getBundle(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		map.put("mch_id", Constants.WEBCHAT_MERCHANTID);
		map.put("nonce_str", MD5MessageDigest.MD5Encode("nonce_str"+order.getOrderId(), "UTF-8", "MD5"));		
		map.put("out_trade_no", order.getOrderId());
		map.put("spbill_create_ip", remoteIp);
		map.put("total_fee", String.valueOf((int)(order.getDisCountPrice()*100)));		
		map.put("notify_url", "http://www.aqshyyzf.com/pay/notify.do");
		map.put("trade_type","JSAPI");		
		String sign=MD5MessageDigest.sign(map, Constants.WEBCHAT_APIKEY);
		map.put("sign", sign);
		String sendData=MapToXml.toXmlString(map);
		//获取prepay信息
		String xml=HttpUtils.sendPost(Constants.WEBCHAT_UNIFIEDORDER_URL, sendData,true);
		 
		//构建返回给前台的map，用于调用微信支付
		JAXBUtils<PrepayResult> jaxb=new JAXBUtils<PrepayResult>(PrepayResult.class);
		PrepayResult result=jaxb.getObject(xml);
		if(result.getReturn_msg().equalsIgnoreCase("OK")){ 
			Map<String,String> data=new HashMap<String,String>();
			data.put("appId", result.getAppid());
			data.put("timeStamp", String.valueOf(System.currentTimeMillis()));
			data.put("nonceStr",result.getNonce_str());
			data.put("package", "prepay_id="+result.getPrepay_id());
			data.put("signType", "MD5");
			sign=MD5MessageDigest.sign(data,Constants.WEBCHAT_APIKEY);
			data.put("paySign", sign);
			//更新订单状态
			order.setStatus("正在发起支付");
			order.setTrade_no(result.getPrepay_id());
			orderOperDao.saveOrder(order);
			return data;
		}
		return null;
	}
	
	
	/**
	 * 原生二维码支付
	 * @param order
	 * @param remoteIp
	 * @param openid
	 * @return
	 */
	public Map<String,String> getQRCodeOrder(Order order,String remoteIp){
		//构造prepay信息 
		Map<String,String> map=new HashMap<String,String>();
		map.put("appid", Constants.WEBCHAT_APPID); 
		try {
			map.put("body", URLEncoder.encode("微信支付-"+order.getBundle(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		map.put("mch_id", Constants.WEBCHAT_MERCHANTID);
		map.put("nonce_str", MD5MessageDigest.MD5Encode("nonce_str"+order.getOrderId(), "UTF-8", "MD5"));		
		map.put("out_trade_no", order.getOrderId());
		map.put("spbill_create_ip", remoteIp);
		map.put("total_fee", String.valueOf((int)(order.getDisCountPrice()*100)));		
		map.put("notify_url", "http://www.aqshyyzf.com/pay/notify.do");
		map.put("trade_type","NATIVE");		
		String sign=MD5MessageDigest.sign(map, Constants.WEBCHAT_APIKEY);
		map.put("sign", sign);
		String sendData=MapToXml.toXmlString(map);
		//获取prepay信息
		String xml=HttpUtils.sendPost(Constants.WEBCHAT_UNIFIEDORDER_URL, sendData,true);
		 
		//构建返回给前台的map，用于调用微信支付
		JAXBUtils<PrepayResult> jaxb=new JAXBUtils<PrepayResult>(PrepayResult.class);
		PrepayResult result=jaxb.getObject(xml);
		if(result.getReturn_msg().equalsIgnoreCase("OK")){ 
			Map<String,String> data=new HashMap<String,String>();
			data.put("appId", result.getAppid());
			data.put("timeStamp", String.valueOf(System.currentTimeMillis()));
			data.put("nonceStr",result.getNonce_str());
			data.put("package", "prepay_id="+result.getPrepay_id());
			data.put("signType", "MD5");
			sign=MD5MessageDigest.sign(data,Constants.WEBCHAT_APIKEY);
			data.put("paySign", sign);
			data.put("code_url", result.getCode_url());
			//更新订单状态
			order.setStatus("正在发起支付");
			order.setTrade_no(result.getPrepay_id());
			orderOperDao.saveOrder(order);
			return data;
		}
		return null;
	}
	
	
	/**
	 * 创建微信公众号菜单
	 * @param request
	 * @return
	 */
	public String createMenu(HttpServletRequest request){
		
		SubMenu sub=new SubMenu();
		sub.setName("预约体检");
		sub.setType("view");
		//配置微信公众号内嵌页面地址
		String url="http://www.aqshyyzf.com/pages/reserve.do"; 
		try{
			url=URLEncoder.encode(url,"UTF-8");
			url=String.format(Constants.WEBCHAT_WEBLOGIN_URL,Constants.WEBCHAT_APPID, url,"snsapi_userinfo");
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		sub.setUrl(url);
		List<SubMenu> subMenus=new ArrayList<SubMenu>();
		subMenus.add(sub);
		
		
		RootMenu rootMenu=new RootMenu();
		rootMenu.setName("就诊服务");
		rootMenu.setSub_button(subMenus);
		
		RootMenu rootMenu1=new RootMenu();
		rootMenu1.setName("自主服务");
		rootMenu1.setType("view");
		rootMenu1.setUrl("http://183.167.250.108:8018/physicalExamination/app/app_2/index.jsp");
		 
		
	 
		
		RootMenu rootMenu2=new RootMenu();
		rootMenu2.setName("关于医院"); 
		rootMenu2.setType("view");
		rootMenu2.setUrl("http://baike.baidu.com/item/%E5%AE%89%E5%BA%86%E5%B8%82%E5%AE%9C%E5%9F%8E%E5%8C%BB%E9%99%A2/6526592?sefr=cr");
		 
		List<RootMenu> rootMenus=new ArrayList<RootMenu>();
		rootMenus.add(rootMenu);
		rootMenus.add(rootMenu1);
		rootMenus.add(rootMenu2);
		
		MenuBar menubar=new MenuBar();
		menubar.setButton(rootMenus);
		this.updateToken();
		String token=orderOperDao.queryToken(Constants.WEBCHAT_TOKEN_NAME);
		String json=new GsonBuilder().disableHtmlEscaping().create().toJson(menubar);
		String result=HttpUtils.sendPost(String.format(Constants.WEBCHAT_MENUCREATE1_URL, token), json, true);
		return result;
		
	}

	/**
	 * 刷新ACCESS_TOKEN
	 */
	public void updateToken() {
		TokenBean tokenbean=getTokenFromNet();
		if(null!=tokenbean)
		orderOperDao.updateToken(Constants.WEBCHAT_TOKEN_NAME, tokenbean.getAccess_token(), tokenbean.getExpires_in()-200);
		
	}
	
	/**
	 * 从微信公众平台获取token，有效期2小时
	 * @return
	 */
	protected TokenBean getTokenFromNet(){
		String token_str=HttpUtils.sendPost(String.format(Constants.WEBCHAT_TOKEN_URL, Constants.WEBCHAT_APPID,Constants.WEBCHAT_APPSECRET), null, false);
		TokenBean obj=new TokenBean();
		if(!StringUtils.isEmpty(token_str)){
			 Gson gson=new Gson();
			 obj=gson.fromJson(token_str, TokenBean.class);
			 return obj;
		}else{
			return null;
		}
		
	}

	/**
	 * H5页面支付测试代码(PENDING)
	 */
	public String getWebPrepay(Order order,HttpServletRequest request){
		 
		String remoteip="";
		String nonce_str=MD5MessageDigest.MD5Encode("nonce_str"+order.getOrderId(), "GBK", "MD5"); 		 
		Map<String,String> wxOrderMap=new HashMap<String,String>();
		wxOrderMap.put("appid", Constants.WEBCHAT_APPID); 
		wxOrderMap.put("body", "微信支付-"+order.getBundle()); 
		wxOrderMap.put("mch_id", Constants.WEBCHAT_MERCHANTID);
		wxOrderMap.put("nonce_str", nonce_str);		
		wxOrderMap.put("out_trade_no", order.getOrderId());
		//wxOrderMap.put("spbill_create_ip", remoteip);
		wxOrderMap.put("total_fee", String.valueOf((order.getDisCountPrice()*100)));		
		wxOrderMap.put("notify_url", "https://www.babesky.cn/pay/notify");
		wxOrderMap.put("trade_type","MWEB");
		 
		try {
			String sign=MD5MessageDigest.sign(wxOrderMap, Constants.WEBCHAT_APIKEY);
			 
			wxOrderMap.put("sign", sign);
			String sendData=MapToXml.toXmlString(wxOrderMap);
			String result=HttpUtils.sendPost(Constants.WEBCHAT_UNIFIEDORDER_URL, sendData,true);
			return result;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return null;
	}
	
	
	
	/**
	 * 获取对账单(PENDING)
	 * @param request
	 * @return
	 */
	public String downloadBilllist(OrderBillRequest request){
		if(null==request){
			request=new OrderBillRequest();
		}
		String billtype=request.getBill_type();
		String date=request.getBill_date();
		String mercId=request.getMch_id();
		String appId=request.getAppid();
		String nonstr=request.getNonce_str();
		if(StringUtils.isEmpty(date)){
			date=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()); 
			request.setBill_date(date);
		}
		if(StringUtils.isEmpty(appId)){
			appId=Constants.WEBCHAT_APPID;
			request.setAppid(appId);
		}
		if(StringUtils.isEmpty(mercId)){
			mercId=Constants.WEBCHAT_MERCHANTID;
			request.setMch_id(mercId);
		}
		if(StringUtils.isEmpty(nonstr)){
			nonstr=String.valueOf(System.currentTimeMillis());
			request.setNonce_str(nonstr);
		}
		if(StringUtils.isEmpty(billtype)){
			billtype="ALL";
			request.setBill_type(billtype);
		}
		
		
		Map<String,String> map=new HashMap<String,String>();
		map.put("appid", appId);
		map.put("mch_id", mercId);
		map.put("nonce_str", nonstr);
		map.put("bill_date", date); 
		map.put("bill_type",billtype);
		String sign=MD5MessageDigest.sign(map, Constants.WEBCHAT_APIKEY);
		request.setSign(sign);
		
		JAXBUtils<OrderBillRequest> util=new JAXBUtils<OrderBillRequest>(OrderBillRequest.class);
		String xml=util.getXMl(request);
		String result=HttpUtils.sendPost(Constants.WEBCHAT_QUERY_BILL, xml,true);
		if(result.indexOf("xml")==-1){
			
			int hdr= result.indexOf("`");
			if(hdr>0){
				Map<String,String> toRows=new HashMap<String,String>();
				String[] columns=result.substring(0, hdr).split(",");
				int columnCnt=columns.length;
				result=result.substring(hdr);
				int sumIndex=result.indexOf("总交易单数");
				String sumText=result.substring(sumIndex);
				
				
				result=result.substring(0,sumIndex);
				
				List<String> cells=Arrays.asList(result.split("`|(\\,`)"));
				cells=cells.subList(1, cells.size());
				int allCnt=cells.size();
				StringBuilder sb=new StringBuilder();
				sb.append("<style>td{white-space:nowrap;}</style>");
				sb.append("<table border='1' cellpadding='2' cellspacing='0' style='width:auto'><tr style='background-color:#ddd'>");
				for(int i=0;i<columnCnt;i++){
					sb.append("<td>"+columns[i]+"</td>");
				}
				sb.append("</tr><tr>");
				for(int i=0;i<allCnt;i++){
					if(i>0&&i%columnCnt==0){
							sb.append("</tr><tr>");
					}
					sb.append("<td>"+cells.get(i).replace("`", "")+"</td>"); 
				}
				sb.append("</tr><tr style='background-color:#ddd'>");
				String[] sumHeader=sumText.substring(0, sumText.indexOf("`")).split(",");
				String[] sumData=sumText.substring(sumText.indexOf("`")).split(",");
				for(int i=0;i<sumHeader.length;i++){
					sb.append("<td>"+sumHeader[i]+"</td>");
				}
				sb.append("<td colspan='"+(columnCnt-sumHeader.length)+"'></td>");
				sb.append("</tr><tr>");
				for(int i=0;i<sumData.length;i++){
					sb.append("<td>"+sumData[i].replace("`", "")+"</td>");
				}
				sb.append("<td colspan='"+(columnCnt-sumHeader.length)+"'></td>");
				
				sb.append("</tr></table>");
				result=sb.toString();
			}
		}else{
			
		}
		return result;
	}
	/******************************************************************************************/
	/******************************************************************************************/
	/******************************************************************************************/
	/******************************************************************************************/
	
}
