package com.physical.util;

import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MD5MessageDigest {

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",  
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" }; 
    
    private static String byteArrayToHexString(byte b[]) {  
        StringBuffer resultSb = new StringBuffer();  
        for (int i = 0; i < b.length; i++)  
            resultSb.append(byteToHexString(b[i]));  
  
        return resultSb.toString();  
    }  
  
    private static String byteToHexString(byte b) {  
        int n = b;  
        if (n < 0)  
            n += 256;  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return hexDigits[d1] + hexDigits[d2];  
    } 
    public static String MD5Encode(String origin, String charsetname,String algorithm) {  
        String resultString = null;  
        try {  
            resultString = new String(origin);  
            MessageDigest md = MessageDigest.getInstance(algorithm);  
            if (charsetname == null || "".equals(charsetname))  
                resultString = byteArrayToHexString(md.digest(resultString  
                        .getBytes()));  
            else  
                resultString = byteArrayToHexString(md.digest(resultString  
                        .getBytes(charsetname)));  
        } catch (Exception exception) {  
        }  
        return resultString;  
    }  
	public static String digest(String str) throws Exception{
		 try {
		        // 生成一个MD5加密计算摘要
		        MessageDigest md = MessageDigest.getInstance("MD5");
		        // 计算md5函数
		        md.update(str.getBytes());
		        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
		        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
		        return byteArrayToHexString(md.digest()); 
		    } catch (Exception e) {
		        throw new Exception("MD5加密出现错误");
		    }
		
	}
	
	public static String sign(Map<String,String> map,String appkey){
		Map<String,Object> keySortedMap=new TreeMap<String,Object>(new Comparator<String>(){

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		});
		for(String key:map.keySet()){
			keySortedMap.put(key, map.get(key));
		}
		StringBuilder sb=new StringBuilder();
		for(String key:keySortedMap.keySet()){
			 
				sb.append(key+"="+String.valueOf(keySortedMap.get(key))+"&");
			 
		}
		String paramStr=sb.toString()+"key="+appkey;
		try {
			String sign=MD5MessageDigest.MD5Encode(paramStr,"UTF-8","MD5").toUpperCase();
			return sign;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
