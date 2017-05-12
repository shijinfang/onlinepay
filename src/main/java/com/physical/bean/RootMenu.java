package com.physical.bean;

import java.util.List;

public class RootMenu {
	private String name;
	private String type;
	private String key;
	private String url;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	private List<SubMenu> sub_button;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SubMenu> getSub_button() {
		return sub_button;
	}
	public void setSub_button(List<SubMenu> sub_button) {
		this.sub_button = sub_button;
	}
}
