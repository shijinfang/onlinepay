package com.physical.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.physical.bean.DatagridData;
import com.physical.bean.Order;
import com.physical.bean.OrderBillRequest;
import com.physical.bean.OrderHistory;
import com.physical.bean.RequestResult;
import com.physical.bean.UserInfo;
import com.physical.bean.WebChatResponseXML;
import com.physical.service.OverallService;
import com.physical.util.Constants;
import com.physical.util.HttpUtils;
import com.physical.util.JAXBUtils;
import com.physical.util.MD5MessageDigest;


@Controller
 
public class OnlinePayController {
	
	public OnlinePayController(){
		System.out.println("pay controller");
		URL url=this.getClass().getClassLoader().getResource("../log4j.properties");
		PropertyConfigurator.configure(url);
		log.info("pay controller started");
	}
	
	Logger log=Logger.getLogger(OnlinePayController.class);
	
	@Autowired
	private OverallService service;

 
	
	/**
	 * 提交支付宝支付后返回给用户的支付宝网页收银系统页面
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="/alipay/pay.do",produces={"text/html"})
	@ResponseBody
	public String alipay(@RequestParam String orderId,HttpServletRequest request){
		Order order=service.queryOrder(orderId);
		order.setStatus("正在发起支付");
		service.saveOrderOper(order);
		String urlPrefix=getRequestUrl(request);
		return service.getPayUrl(order,urlPrefix);
	}
	
	
	private String getRequestUrl(HttpServletRequest request){
		return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
	}
	/**
	 * 支付完成后，支付宝通知地址
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/alipay/notify.do")
	@ResponseBody
	public String notify(HttpServletRequest request){
		service.processNotify(request);
		return "OK";
	}
	
	/**
	 * 支付完成后支付宝返回给用户的地址
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/alipay/return.do") 
	public String returns(HttpServletRequest request,ModelMap map,HttpServletResponse response){
		Order order=service.processReturn(request); 
		map.put("order", order);
		
		return "redirect:/view/pay/alipay.html?pay_no="+order.getOrderId();
		//return "pay/alipay";
	}
	
	/**
	 * 订单查询
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="/alipay/query.do",produces={"text/plain"})
	@ResponseBody
	public String query(@RequestParam String orderId){
		String result=service.queryOrderFromAlipay(orderId);
		return result;
	}
	
	/**
	 * 账单查询
	 * @param bill_type
	 * @param bill_date
	 * @return
	 */
	@RequestMapping(value="/alipay/queryBill.do",produces={"text/plain"})
	@ResponseBody
	public String queryBill(@RequestParam(defaultValue="trade") String bill_type,@RequestParam(defaultValue="2017-03-12") String bill_date){
		String result=service.queryBillList(bill_type, bill_date);
		return result;
	}
	
	/**
	 * 应用网关
	 * @param request
	 * @return
	 */
	@RequestMapping("/alipay/gateway.do")
	@ResponseBody
	public String gateway(HttpServletRequest request){
		
		Map<String,String[]> params=request.getParameterMap();
		return "ok";
	}
	
	/**
	 * 授权回调地址
	 * @param request
	 * @return
	 */
	@RequestMapping("/alipay/callback.do")
	@ResponseBody
	public String callback(HttpServletRequest request){
		
		Map<String,String[]> params=request.getParameterMap();
		
		return "ok";
	}
	
	/******************************************************************************************/
	/**
	 * 获取对账单
	 * @param date
	 * @return
	 */
	@RequestMapping(value="/webchat/billlist.do",produces={"text/plain","text/html","text/xml"})
	@ResponseBody
	public  String getBillList(@RequestParam String date){
		OrderBillRequest request=new OrderBillRequest();
		request.setBill_date(date);
		return service.downloadBilllist(request );
	}
	
	
	
	/**
	 * 打开体检套餐页面
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/pages/reserve.do")
	public void reserve(HttpServletRequest request,ModelMap map,HttpServletResponse response) throws Exception{
		HttpSession session=(HttpSession)request.getSession();
		String userName=(String)session.getAttribute("userName");
		String openid="";
		 
		if(StringUtils.isEmpty(userName)){
			log.info("not user name exist in session ");
			String code=request.getParameter("code");
			openid=code;
			log.info("code from weixin is "+code);
			UserInfo userinfo= service.getUserInfoFromCode(code);
			if(userinfo!=null){
				
				userName=userinfo.getNickname();
				session.setAttribute("userName", userName);
				session.setAttribute("openId",userinfo.getOpenid());
				map.addAttribute("userName", userName); 
				log.info("get user name and open id from code: "+userName+":"+userinfo.getOpenid());
				
			}else{
				log.info("can not get userinof from code: "+code);
			}
		}else{
			openid="nameinsession";
			log.info("user name exist in session "+userName);
		}
		
		response.sendRedirect(request.getContextPath()+"/view/pay/reserve.html?openid="+openid);
		//return "pages/online_reservations";
		 
	}
	
	
	
	/**
	 * 发起支付
	 * @param trade_no
	 * @param request
	 * @return
	 */
	@RequestMapping("/pages/startPay.do")
	@ResponseBody
	public RequestResult startPay(@RequestParam String trade_no,HttpServletRequest request){
		RequestResult rst=new RequestResult();
		log.info("start pay ");
		Order order=service.queryOrder(trade_no);
		//测试价格，后续需删除
		//order.setDisCountPrice(0.01);
		
		HttpSession session=(HttpSession)request.getSession();
		String openid=(String)session.getAttribute("openId");
		log.info("openid in session is "+openid);
		if(StringUtils.isEmpty(openid)){
			rst.setReturn_code("500");
			rst.setReturn_msg("无法获取用户微信openid,请确认是否从微信浏览器中打开");
			return rst ;
		} 
		String remoteip=HttpUtils.getIpAddr(request);
		if(StringUtils.isEmpty(remoteip)){
			rst.setReturn_code("500");
			rst.setReturn_msg("无法获取用户客户端IP");
			return rst ;
		}
		rst.setReturn_code("200");
		rst.setReturn_msg("OK");	 
		rst.setExtras(service.getPrepayOrder(order,remoteip,openid));
		log.info("get order deatil ");
		return rst ;
	}
	
	/**
	 * 微信二维码支付
	 * @param trade_no
	 * @param request
	 * @return
	 */
	@RequestMapping("/webchat/qrpay.do")
	@ResponseBody
	public RequestResult qrpay(@RequestParam String trade_no,HttpServletRequest request){
		RequestResult rst=new RequestResult();
		log.info("start pay ");
		Order order=service.queryOrder(trade_no);
		String remoteip=HttpUtils.getIpAddr(request);
		if(StringUtils.isEmpty(remoteip)){
			rst.setReturn_code("500");
			rst.setReturn_msg("无法获取用户客户端IP");
			return rst ;
		}
		rst.setReturn_code("200");
		rst.setReturn_msg("OK");	 
		rst.setExtras(service.getQRCodeOrder(order,remoteip));
		return rst;
	}
	@RequestMapping("/webchat/genQrImage.do")
	public void genQrImage(@RequestParam String text,HttpServletRequest request,HttpServletResponse response){
		
		Hashtable hints=new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hints.put(EncodeHintType.MARGIN, 2);
	 
		try {
			text=URLDecoder.decode(text,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		try {
			BitMatrix matrix=new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200,hints);
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(matrix, "png", out);
			response.setHeader("Content-Type", "inline;filename=qrcode.png");
			response.getOutputStream().write(out.toByteArray());
			response.flushBuffer();
			
		} catch (WriterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 接受支付结果通知
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/pay/notify.do")
	@ResponseBody
	public RequestResult paynotify(HttpServletRequest request){
		RequestResult backresult=new RequestResult();
		try {
			InputStream in=request.getInputStream();
			byte[] txt=new byte[1024];
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			while((in.read(txt,0,1024))!=-1){
				out.write(txt);
			}
			String xmlStr=new String(out.toByteArray(),"UTF-8");
			if(!StringUtils.isEmpty(xmlStr)&&xmlStr.indexOf("</xml")!=-1){
				xmlStr=xmlStr.substring(0,xmlStr.indexOf("</xml>")+6);
			JAXBUtils<WebChatResponseXML> utl=new JAXBUtils<WebChatResponseXML>(WebChatResponseXML.class);
			WebChatResponseXML data=utl.getObject(xmlStr);
			String orderId=data.getOut_trade_no();
			String result_code=data.getResult_code();
			String return_code=data.getResult_code();
			if(!StringUtils.isEmpty(orderId)){
				Order order=service.queryOrder(orderId);
				if(null!=order){
					if(!StringUtils.isEmpty(orderId)&&"SUCCESS".equalsIgnoreCase(result_code)&&"SUCCESS".equalsIgnoreCase(return_code)){
						order.setStatus("支付成功");
						
						backresult.setReturn_code("SUCCESS");
						backresult.setReturn_msg("OK");
					}
					else{
						order.setStatus("支付失败");
						backresult.setReturn_code("FAIL");
						backresult.setReturn_msg("支付失败");
					}
					
					//通知超过5次，不在写入数据库
					if(order.getNotifyed()<5){
						order.setNotifyed(order.getNotifyed()+1);
						service.saveOrderOper(order);
					}
				}
			}
			}else{
				backresult.setReturn_code("FAIL");
				backresult.setReturn_msg("无法获得数据");
			}
			 
			return backresult;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			backresult.setReturn_code("FAIL");
			backresult.setReturn_msg(e.getMessage());
		}
		
		return backresult;
		
	}
	
	
	/******************************************************************************************/
	

	/**
	 * 从数据库查询订单
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/pages/viewOrder.do")
	@ResponseBody
	public Order payConfirm(HttpServletRequest request,ModelMap model){
		String id=request.getParameter("pay_no");
		Order order=service.queryOrder(id); 
		return order;
	}
	
	/**
	 * 订单管理页面每页订单明细
	 * @param page
	 * @param rows
	 * @param date
	 * @param bundle
	 * @param name
	 * @param phone
	 * @param status
	 * @param request
	 * @return
	 */
	@RequestMapping("/pages/orders.do")
	@ResponseBody
	public DatagridData<Order> orders(@RequestParam(defaultValue="1") String page,
			   				  @RequestParam(defaultValue="10") String rows,
			   				  @RequestParam(defaultValue="") String date,
			   				  @RequestParam(defaultValue="全部套餐") String bundle,
			   				  @RequestParam(defaultValue="") String name,
			   				  @RequestParam(defaultValue="") String phone,
			   				  @RequestParam(defaultValue="全部") String status,
			   				  HttpServletRequest request
							  ){
		
		List<Order> orders=new ArrayList<Order>();
		int iPage=Integer.valueOf(page);
		int iRows=Integer.valueOf(rows);
		
		try{
			date=StringUtils.isEmpty(date)?null:date;
			name=StringUtils.isEmpty(name)?null:name;
			phone=StringUtils.isEmpty(phone)?null:phone;
			bundle="全部套餐".equalsIgnoreCase(bundle)?null:bundle;
			status="全部".equals(status)?null:status;
			bundle=StringUtils.isEmpty(bundle)?null:URLDecoder.decode(bundle,"UTF-8");
			status=StringUtils.isEmpty(status)?null:URLDecoder.decode(status,"UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
 
		
		orders=service.listOrders((iPage-1)*iRows, iRows,date,bundle,name,phone,status);
		DatagridData<Order> data=new DatagridData<Order>();
		data.setRows(orders);
		data.setTotal(service.orderCount(date,bundle,name,phone,status));
		return data;
	}
	
	/**
	 * 微信配置检查，没业务逻辑
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/notify.do")
	@ResponseBody
	public String notify(HttpServletRequest request,HttpServletResponse response){
		String signature=request.getParameter("signature");
		String timestamp=request.getParameter("timestamp");
		String nonce=request.getParameter("nonce");
		String echostr=request.getParameter("echostr");
		 
		if(StringUtils.isEmpty(signature)||
				StringUtils.isEmpty(timestamp)||
				StringUtils.isEmpty(signature)){
			return "error";
		}
		List<String> params=new ArrayList<String>();
		params.add(Constants.WEBCHAT_TOKEN);
		params.add(timestamp);
		params.add(nonce);
		Collections.sort(params);
		
		String paramStr=String.join("", params);
		 
		
		try {
			MessageDigest digest=MessageDigest.getInstance("sha1");
			digest.update(paramStr.getBytes());
			paramStr=MD5MessageDigest.MD5Encode(paramStr,"UTF-8","sha1");
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return echostr;
		
	}
	
 
	/**
	 * 创建公共号菜单逻辑
	 * @param request
	 * @return
	 */
	@ResponseBody 
	@RequestMapping("/createMenu.do")
	public String createMenu(HttpServletRequest request){
		 
		return service.createMenu(request);
		  
	}
	
	/**
	 * 选择套餐，返回套餐ID
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping(value="/order/setOrder.do",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public RequestResult setOrder(@RequestBody Order order,HttpServletRequest request,HttpServletResponse response) {
		 try {
			order.setName(URLDecoder.decode(order.getName(),"UTF-8"));
			order.setBundle(URLDecoder.decode(order.getBundle(),"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		 order.setStatus("已选择套餐");
		 
		 RequestResult obj=new RequestResult();
		 try{
			 service.saveOrderOper(order);
			 log.info("order saved");
			 
			 obj.setCode(200);
			 obj.setMsg("OK"); 
			 obj.setExtras(order.getOrderId());
		 }catch(Exception e){
			 log.info("order save error "+e.getMessage());
			 obj.setCode(500);
			 obj.setMsg(e.getMessage());
		 } 
		 return obj;
	}
	
	
	
	
	
	/**
	 * 获取特定订单
	 */
	@RequestMapping("/pages/queryPay.do")
	@ResponseBody
	public Order queryPay(@RequestParam String trade_no,HttpServletRequest request){
		Order order=service.queryOrder(trade_no);
		return order;
		
	}
	 
	/**
	 * 订单核销
	 * @param orderId
	 * @param status
	 * @param request
	 * @return
	 */
	@RequestMapping("/pages/customaticSetPaid.do")
	@ResponseBody
	public RequestResult customaticSetPaid(@RequestParam String orderId,@RequestParam String status,HttpServletRequest request){
		RequestResult result=new RequestResult();
		try {
			status=URLDecoder.decode(status,"UTF-8");
			Order order=service.queryOrder(orderId);
			if(null==order){
				throw new Exception("无法查到订单");
			}
			OrderHistory orderHistory=new OrderHistory();
			orderHistory.setOrderId(orderId);
			orderHistory.setNewstatus(status);
			orderHistory.setOldstatus(order.getStatus());
			service.saveOrderStatusChange(orderHistory);
			order.setStatus(status);
			service.saveOrderOper(order);
			result.setReturn_code("200");
			result.setReturn_msg("更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setReturn_code("500");
			result.setReturn_msg(e.getMessage());
		}
		
		
		return result;
		
	}
	
	/**
	 * 微信H5测试支付（PENDING）
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/pay/webpay.do")
	@ResponseBody
	public RequestResult webpay(HttpServletRequest request){
		RequestResult result=new RequestResult();
		Order order=service.queryOrder("20170312_225332_8");
		String data=service.getWebPrepay(order,request);
		result.setExtras(data);
		
		return result;
	}
	
	
	
	
	/******************************************************************************************/
}
