package com.files.converter.factory;

import com.files.converter.XMLJSONConverterI;
import com.files.converter.XMLJSONConverterImpl;

public class FileConvertorFactory {
	
	public XMLJSONConverterI getConverter(String type) {
		if(type == null ) {
			return null;
		}else if(type.equalsIgnoreCase("JSON2XML")) {
			return new XMLJSONConverterImpl();
		}
		return null;
	}
}
