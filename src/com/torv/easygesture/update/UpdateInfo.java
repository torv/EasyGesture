package com.torv.easygesture.update;

public class UpdateInfo {

	private String version;
	private String url;
	private String description;
	private String xmlServer;
	
	// xml server
	public String getXmlServer(){
		return xmlServer;
	}
	
	public void setXmlServer(String xmlServer){
		this.xmlServer = xmlServer;
	}
	
	// apk version
	public String getVersion(){
		return version;
	}
	
	public void setVersion(String version){
		this.version = version;
	}
	
	// apk url
	public String getUrl(){
		return url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	// description
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
}
