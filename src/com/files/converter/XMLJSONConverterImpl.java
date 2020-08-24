package com.files.converter;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class XMLJSONConverterImpl implements XMLJSONConverterI {
	public static final String nullValue = "null";
	public static final String intValue = "Number";
	public static final String stringValue = "String";
	public static final String boolValue = "Boolean";
	private JSONObject jsonObj;
	private String nameAttr;
	private Iterator keys;
	private Object value;
	private boolean nestedFlag = true;
	StringBuffer xmlStr = new StringBuffer();

	/* class for implementing multithreading when json file is more than one */
	class Task implements Runnable {
		private String jsonStr;
		private String xmlFileName;
		private String xmlFile;

		public Task(String jsonStr, String xmlFileName, String xmlFile) {
			this.jsonStr = jsonStr;
			this.xmlFileName = xmlFileName;
			this.xmlFile = xmlFile;
		}

		@Override
		public void run() {
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(xmlFile + "/" + xmlFileName);
				fileWriter.write(createXml(new JSONObject(jsonStr)));
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void convertJSONtoXML(String jsonFile, String xmlFile) throws Exception {
		String jsonStr;
		String jsonFileName;
		/* Throws exception when file does not exist */
		try {
			if (Files.notExists(Paths.get(jsonFile))) {
				throw new Exception("File does not exists");
			}
			File json = new File(jsonFile);
			File xml = new File(xmlFile);
			/*condition if both the given args are file path*/
			if (json.isFile() && xml.getName().endsWith(".xml")) {
				jsonStr = new String(Files.readAllBytes(Paths.get(jsonFile)));
				if (jsonStr.length() == 0) {
					throw new Exception("File is empty");
				}
				FileWriter fileWriter = new FileWriter(xmlFile);
				fileWriter.write(createXml(new JSONObject(jsonStr)));
				fileWriter.close();
			} 
			/*condition if given args for json is file path and xml is directory */
			else if (json.isFile() && xml.isDirectory()) {
				jsonStr = new String(Files.readAllBytes(Paths.get(jsonFile)));
				if (jsonStr.length() == 0) {
					throw new Exception("File is empty");
				}
				jsonFileName = json.getName();
				String xmlFileName = jsonFileName.replace("json", "xml");
				FileWriter fileWriter = new FileWriter(xmlFile + "/" + xmlFileName);
				fileWriter.write(createXml(new JSONObject(jsonStr)));
				fileWriter.close();
			} 
			/*condition if both the given args are directory path*/
			else if (json.isDirectory() && xml.isDirectory()) {
				ExecutorService service = Executors.newFixedThreadPool(10);
				for (final File fileEntry : json.listFiles()) {
					jsonFileName = fileEntry.getName();
					if (jsonFileName.endsWith(".json")) {
						jsonStr = new String(Files.readAllBytes(Paths.get(fileEntry.toString())));
						String xmlFileName = jsonFileName.replace("json", "xml");
						service.execute(new Task(jsonStr, xmlFileName, xmlFile));
					}
				}
			} 
			/* Throws exception when json path is directory and xml is file path */
			else if (json.isDirectory() && (xml.isFile() || xml.getName().endsWith(".xml"))) {
				throw new Exception("xml file path must should be a directory path not file path");
			}else if(json.isFile() && xml.isFile() && !xml.getName().endsWith(".xml")) {
				throw new Exception("File in xml path is not correct type");
			}
		} catch (JSONException e) {
			throw new Exception("Not a valid JSON");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkInstance(String nameAttr, Object value) throws Exception {
		if (value instanceof JSONObject) {
			convertObject(value, nameAttr);
		}
		if (value instanceof JSONArray) {
			convertArray(value, nameAttr);
		}
		if (value == null || String.valueOf(value).equals("null")) {
			appendXml(value, nullValue, nameAttr);
		}
		if (value instanceof String) {
			appendXml(value, stringValue, nameAttr);
		}

		if (value instanceof Integer) {
			appendXml(value, intValue, nameAttr);
		}

		if (value instanceof Double) {
			appendXml(value, intValue, nameAttr);
		}

		if (value instanceof Boolean) {
			appendXml(value, boolValue, nameAttr);
		}

	}

	private String createXml(Object object) throws Exception {
		xmlStr.delete(0, xmlStr.length());
		nameAttr = null;
		if (object instanceof JSONObject) {
			if (nameAttr != null) {
				if (!nameAttr.isEmpty() && nestedFlag) {
					xmlStr.append("<object name=\"" + nameAttr + "\">");
				} else {
					xmlStr.append("<object>");
				}
			} else {
				xmlStr.append("<object>");
			}
			jsonObj = (JSONObject) object;
			keys = jsonObj.keys();
			while (keys.hasNext()) {
				nameAttr = keys.next().toString();
				value = jsonObj.opt(nameAttr);
				checkInstance(nameAttr, value);
			}
		}

		if (!nameAttr.isEmpty()) {
			xmlStr.append("</object>");
		} else {
			xmlStr.append("/>");
		}
		return xmlStr.toString();
	}

	private void appendXml(Object value, String dataType, String nameAttr) {
		xmlStr.append("<" + dataType);
		if (!nameAttr.isEmpty()) {
			xmlStr.append(" name = \"" + nameAttr + "\">");
		} else {
			xmlStr.append('>');
		}
		if (!String.valueOf(value).isEmpty()) {
			xmlStr.append(value);
			xmlStr.append("</" + dataType + ">");
		} else {
			xmlStr.append("/>");
		}
	}

	private void convertObject(Object value2, String key2) throws Exception {
		if (value2 instanceof JSONObject) {
			if (key2 != null) {
				if (!key2.isEmpty()) {
					xmlStr.append("<object name=\"" + key2 + "\">");
				} else {
					xmlStr.append("<object>");
				}
			} else {
				xmlStr.append("<object>");
			}
			JSONObject jsonObj;
			Iterator keys;
			String nameAttr;
			jsonObj = (JSONObject) value2;
			keys = jsonObj.keys();
			while (keys.hasNext()) {
				nameAttr = keys.next().toString();
				value2 = jsonObj.opt(nameAttr);
				checkInstance(nameAttr, value2);
			}
			if (!key2.isEmpty()) {
				xmlStr.append("</object>");
			} else {
				xmlStr.append("/>");
			}
		} else {
			checkInstance(key2, value2);
		}
	}

	private void convertArray(Object value2, String key3) throws Exception {
		if (value2 instanceof JSONArray) {
			xmlStr.append("<array");
			if (!key3.isEmpty() && nestedFlag) {
				xmlStr.append(" name =\"" + nameAttr + "\">");
			} else {
				xmlStr.append('>');
			}
			convertSubType(value2);
			xmlStr.append("</array>");
		}
	}

	private void convertSubType(Object value3) throws Exception {
		JSONArray jsonArray;
		int length;
		int itr;
		jsonArray = (JSONArray) value3;
		length = jsonArray.length();
		for (itr = 0; itr < length; itr += 1) {
			Object temp = new Object();
			value3 = jsonArray.get(itr);
			temp = value3;
			if (temp instanceof JSONArray || temp instanceof JSONObject) {
				nestedFlag = false;
				checkInstance(nameAttr, value3);
			} else {
				checkInstance("", value3);
			}
		}
	}

}
