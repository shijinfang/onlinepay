function getParam(name){
	var reg=new RegExp(name+"=([^?&#]+)")
	result=reg.exec(document.location.href)
	if(result&&result.length==2&&result[1]!="null"){
		return result[1]
	}
 
	return null;
} 
$(function(){
	
	 var openid=getParam("openid") 
	 if(openid&&openid.length>0){
		 $("#payMethod tr").first().find("td").last().find("div").removeClass("unselected").addClass("selected");
	 }else{
		 $("#payMethod tr").last().find("td").last().find("div").removeClass("unselected").addClass("selected");
	 }
	
    $(".TCon div").first().show();
	$("#options").on("change",function(){
		var raw_price=$("#options option:selected").attr("data-raw");
		var discount_price=$("#options option:selected").attr("data-discount");
		$(".priceF span").first().find("del").text("¥"+raw_price);
		$(".priceF span").last().text("¥"+discount_price);
		var idx=$("#options option").index($("#options option:selected"))-1;
		$(".TCon div").hide().eq(idx).show();
	});
     
    //日期时间
    var currYear = (new Date()).getFullYear();	
	var opt={};
	opt.date = {preset : 'date'};
	opt.datetime = {preset : 'datetime'};
	opt.time = {preset : 'time'};
	opt.default = {
		theme: 'android-ics light', //皮肤样式
        display: 'modal', //显示方式 
        mode: 'scroller', //日期选择模式
		dateFormat: 'yyyy年mm月dd日',
		lang: 'zh',
		showNow: true,
		nowText: "今天",
        startYear: currYear - 10, //开始年份
        endYear: currYear + 10 //结束年份
	};
  	$("#appDate").mobiscroll($.extend(opt['date'], opt['default']));
  	
    });
   
   $(function(){
	   
	  //选择支付方式
	 $(".paytype").click(function(){
		
		 $(".paytype").removeClass("selected").find(".checked").hide();
		 $(this).addClass("selected").find(".checked").show();
	 }) 
	 
	 $("#payMethod tr").click(function(){
		 $("#payMethod tr").each(function(){
			 $(this).find("td").last().find("div").removeClass("selected").addClass("unselected")
		 })
		 $(this).find("td").last().find("div").removeClass("unselected").addClass("selected");
	 })
	 
	 //预约
	 $("#makeReservation").click(function(){
		 
		 var name=$("#name").val();
		 if(!name){
			 alert("姓名不能为空！")
			 return;
		 }
		 var phone=$("#phone").val();
		 if(!phone){
			 alert("手机号不能为空！")
			 return;
		 }
		 var reg=/[0-9]{11}/;
		 if(!reg.test(phone)){
			 alert("手机号为11位数字，请正确填写！")
			 return;
		 }
		 
		 
		 var date=$("#appDate").val();
		 if(!date){
			 alert("预约时间不能为空！")
			 return;
		 }
		
		 var bundle=$("#options").val();
		 
		 type=$(".ckbx").index($(".selected"));
		 
		 if(type==-1){
			 alert("请选择支付方式")
			 return;
		 }
		 if(!firstvisit){
			 return;
		 }
		 firstvisit=false;
		 var raw_price=$("#options option:selected").attr("data-raw");
		 var discount_price=$("#options option:selected").attr("data-discount");
			
		 var data={
				 	payType:type,
				 	name:encodeURIComponent(name),
				 	phone:phone,
				 	reserveData:date,
		 			bundle:encodeURIComponent(bundle),
		 			rawPrice:parseFloat(raw_price),
		 			disCountPrice:parseFloat(discount_price)
		 		 };
		 
		 
		 $.ajax({ 
	            type:"POST", 
	            url:"/order/setOrder.do", 
	            dataType:"json",      
	            contentType:"application/json",               
	            data:JSON.stringify(data), 
	            success:function(data){ 
	               if(data.code==200){ 
	            	   document.location="/view/pay/payConfirm.html?pay_no="+data.extras+"&type="+type; 
	               }
	            },
	            error:function(res,code,msg){
	            	console.log(code+":"+msg);
	            }
	         }); 
		 
		 
	 });
   })
   
   var firstvisit=true;