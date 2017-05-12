var id;
var firstvisit=true;

var counter=0;
var interval=null;
function is_weixn(){
	var ua = navigator.userAgent.toLowerCase();
	 
	if(ua.match(/MicroMessenger/i)=="micromessenger") {
	return true;
	} else {
	return false;
	}
}

$(function(){
	 
	
	var reg=new RegExp("pay_no=([^=#&]+)")
	var type=new RegExp("type=([01])")
	id=reg.exec(document.location.href);
	
	type=type.exec(document.location.href);
	if(is_weixn()&&type[1]==1){
		$("body").empty()
		alert("支付宝支付无法从微信客户端发起")
		history.back();
		return;
	}
	 
	$(".tpaytype").text(type[1]==0?"微信":"支付宝")
	$("#icon").attr("src","../../images/"+(type[1]==0?"webchat":"alipay")+".jpg")
	if(id){
		 
		$(".tOrderId").text(id[1])
		$.getJSON("/pages/viewOrder.do?pay_no="+id[1],function(data){
			if(data){
				$("#goods").text(data.bundle)
				$("#price").text(data.disCountPrice)
				$(".tOrderBundle").text(data.bundle)
				$(".tOrderDate").text(data.reserveData)
			}
		})
	}
	
	$("#getBrandWCPayRequest").click(function(){
		if(!firstvisit){
			return;
		}
		firstvisit=false;
		$(this).text("正在发起支付请求.")
		if(type[1]==0){ //微信支付
			if(is_weixn()){ //是微信浏览器则微信支付
				weixinPay(id[1]);
			}else{//否则用微信二维码支付
				$.getJSON("/webchat/qrpay.do?trade_no="+id[1],function(data){
					$("body").empty()
					 
					if(data.return_code=="200"){
						if(data.extras){
							 
							if(data.extras.code_url){
								$("<div>微信付款二维码，请通过微信客户端扫码支付</div>").css({width:"100%","text-align":"center"}).appendTo($("body"))
								$("<img />").css({width:"100%"}).attr("src","/webchat/genQrImage.do?text="+encodeURIComponent(data.extras.code_url)).appendTo($("body"))
								$("<div></div>").attr("id","timeout").css({"color":"red",width:"100%","text-align":"center"}).appendTo($("body"))
								interval=setInterval(function(){
									counter++;
									if(counter>=3){
										clearInterval(interval);
										$("#timeout").text("二维码已过期，请重新下单")
										return;
									}
									$.getJSON("/pages/queryPay.do?trade_no="+id[1],function(data){ 
					 	          		 if(data.notifyed>0){
					 	          			clearInterval(interval);
											document.location="/view/pay/alipay.html?pay_no="+id[1]
					 	          		 }
					 	          	  }); 
								},5000)
							
							}else{
								alert(data.extra.return_msg)
							}
						}
					}else{
						alert("无法获取付款二维码")
					}
				})
			}
			
		}else{//支付宝支付
			location.href="/alipay/pay.do?orderId="+id[1];
		}
	})
})

function weixinPay(orderId){
	$.getJSON("/pages/startPay.do?trade_no="+orderId,function(data){
		if(data){
			if(data.return_code=="200"){
			WeixinJSBridge.invoke('getBrandWCPayRequest', data.extras, function(res){     
			 	           if(res.err_msg == "get_brand_wcpay_request:ok" ) {
			 	        	  $("#orderPreview").hide(); 
			 	          	  $("#queryPayResult").show();
			 	          	  $("#bdetail").hide();
			 	          	  interval=setInterval(function(){
			 	          		$.getJSON("/pages/queryPay.do?trade_no="+id[1],function(data){ 
				 	          		 if(data.notifyed>0){
				 	          			clearInterval(interval);
										document.location="/view/pay/alipay.html?pay_no="+id[1]
				 	          		 }
				 	          	  }); 
			 	          	  },2000)
			 	          	  
			 	           }else{//微信支付失败
			 	        	   alert(JSON.stringify(res))
			 	        		//alert("付款失败"+JSON.stringify(res))
			 	        		$("#orderPreview").hide();
			 	        		$("#failPayResult").show();
			 	           }
			 	       });
			}//返回非200，后台有错误
			else{
				alert(data.return_msg)
			}
		}else{
			alert("无法生成订单，系统内部错误")
		}
	})
}