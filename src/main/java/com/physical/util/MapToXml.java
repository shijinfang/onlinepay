package com.physical.util;

import java.util.Map;

public class MapToXml {
	public static String toXml(Map<String,Object> map){
		StringBuilder sb=new StringBuilder();
		 
		sb.append("<xml>");
		for(String key:map.keySet()){
			sb.append(String.format("<%s>%s</%s>", key,map.get(key),key));
		}
		sb.append("</xml>");
		return sb.toString();
	}
	
	public static String toXmlString(Map<String,String> map){
		StringBuilder sb=new StringBuilder();
		 
		sb.append("<xml>");
		for(String key:map.keySet()){
			sb.append(String.format("<%s>%s</%s>", key,map.get(key),key));
		}
		sb.append("</xml>");
		return sb.toString();
	}
}
