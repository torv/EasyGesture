package com.torv.easygesture.update;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class UpdataInfoParser {

	public static UpdateInfo getUpdateInfo(InputStream is) throws Exception{

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();

		UpdateInfo info = new UpdateInfo();

		while(type != XmlPullParser.END_DOCUMENT){
			switch(type){
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					info.setVersion(parser.nextText());
				}else if("url".equals(parser.getName())){
					info.setUrl(parser.nextText());
				}else if("description".equals(parser.getName())){
					info.setDescription(parser.nextText());
				}else if("url_server".equals(parser.getName())){
					info.setXmlServer(parser.nextText());
				}
				break;
			}
			type = parser.next();
		}
		
		return info;
	}
}
