package com.physical.util;

public class Constants {
	//统一下单接口链接
	public static final String unifiedorder="https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	/******************************************************************************************/
	//支付宝支付成功后通知地址
	public static final String ALIPAY_NOTIFY_URL="/alipay/notify.do";
	
	//支付宝支付成功后跳转地址
	public static final String ALIPAY_RETURN_URL="/alipay/return.do";
	
	//开发者APPID
	//public static final String ALIPAY_APPID="2016080300155416";
	//public static final String ALIPAY_APPID="2017030806117568";
	
	//商户ID
	//public static final String ALIPAY_MERCID="2088102169724853";
	
	//支付网关
	//public static final String ALIPAY_GATEWAY="https://openapi.alipaydev.com/gateway.do";
	//public static final String ALIPAY_GATEWAY="https://openapi.alipay.com/gateway.do";
	
	//公钥
	//public static final String ALIPAY_PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
	//public static final String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzsp9x8/OZvrLUQMrRwIpG6A7JN55nY9Y6qwU8+BsWDHQdRSGOyRkblkcAkx4LSD/e2TrcebOSEFO/GreYEOB58FAuKLsoOhni5H8GZivS2e1J30Fz6xXSj47jKADSvNxX2xb/FCtS4T4WsfnoG1jS8Ok1gejGGduKKF3ba1r/pxtcOa2Bs51P/VBJabSc/d4eF30tO7uGaCileTJX/oU+Y0Uu/MJiTCQqJs7JNtIuiBHrORAxwmuEjr+jLXdfAxbOPZOqmWP1YqI5yAQcwpr9xZbbBhmSRCm7T9r6J3LeulzHxjs1yTg6s0wykCTvI8LM0wczWx2zmr3Xu6Mm8ytjwIDAQAB";
	/******************************************************************************************/
	
	public static final String WEBCHAT_APPID="wxe8f05c381c68b30e";
	
	public static final String WEBCHAT_MERCHANTID="1440573902";
	
	public static final String WEBCHAT_APIKEY="nanjingulouyiyuanjituanaqshyy888";
	
	public static final String WEBCHAT_APPSECRET="fc244cd7de6d985526863ba243222012";
	
	//统一下单地址
	public static final String WEBCHAT_UNIFIEDORDER_URL="https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	//验证服务器TOKEN
	public static final String WEBCHAT_TOKEN="485F714B8C96171F899B92995CED6CFA";
	
 
	//微信认证根据code获取access_token接口地址
	public static final String WEBCHAT_PAGEACCTOKEN_URL="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	
	
	//获取用户详细信息地址
	public static final String WEBCHAT_PAGEUSERINFO_URL="https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
		
	
	//微信认证地址，返回code给notify_url
	public static final String WEBCHAT_WEBLOGIN_URL="https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=STATE#wechat_redirect";
	//创建菜单地址
	public static final String WEBCHAT_MENUCREATE1_URL="https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";

	//下载对账单
	public static final String WEBCHAT_QUERY_BILL="https://api.mch.weixin.qq.com/pay/downloadbill";
		
	//获取微信access_token
	public static final String WEBCHAT_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	
	//缓存ACCESS_TOEKN标识
	public static String WEBCHAT_TOKEN_NAME="access_token";
	/******************************************************************************************/
	

}
