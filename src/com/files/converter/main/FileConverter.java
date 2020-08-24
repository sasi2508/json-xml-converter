package com.files.converter.main;

import java.io.File;
import java.util.Scanner;

import com.files.converter.XMLJSONConverterI;
import com.files.converter.factory.FileConvertorFactory;

public class FileConverter {

	public static void main(String[] args) throws Exception {
		String filePath = "";
		String xmlFilePath = "";
		String directoryPath = "";
		String xmlDirectoryPath = "";
		String exit = "";
		FileConvertorFactory fileConvertorFactory = new FileConvertorFactory();
		XMLJSONConverterI xmljsonConverter = fileConvertorFactory.getConverter("json2xml");
		if(xmljsonConverter!=null) {
			if(args.length ==2) {
			xmljsonConverter.convertJSONtoXML(args[0], args[1]);
			System.out.println("Conversion ended.......");
			}else if(args.length ==1){
				File xml = new File(args[0]);
				if(xml.isFile()) {
					xmlFilePath = args[0].replace("json", "xml");
				}else if(xml.isDirectory()) {
					xmlFilePath = args[0];
				}
				xmljsonConverter.convertJSONtoXML(args[0], xmlFilePath);
				System.out.println("Conversion ended.......");
			}
			else {
				System.out.println("Do you want to convert single json file? Y/N");
				Scanner sc = new Scanner(System.in);
				String str = sc.nextLine();
				if(str.equals("Y") || str.equals("y")) {
					System.out.println("Enter the file path of json");
					 filePath = sc.nextLine();
					 File filePaths = new File(filePath);
					 if(filePaths.isFile()) {
						 System.out.println("Enter the file path of xml");
						 xmlFilePath = sc.nextLine();
						 xmljsonConverter.convertJSONtoXML(filePath, xmlFilePath);
						 System.out.println("Conversion ended.......");
						 
					 }else {
						 System.out.println("The given path is not file path or does not exist");
							System.out.println("Do you want to continue? Y/N");
							exit = sc.nextLine();
							if(exit.equals("Y")||exit.equals("y")) {
								main(args);
							}else {
								System.exit(1);
							}
					 }
				}else if(str.equals("N")|| str.equals("n")) {
					System.out.println("Enter the directory path of json");
					directoryPath = sc.nextLine();
					File directPath = new File(directoryPath);
					if(directPath.isDirectory()) {
						System.out.println("Enter the directory path of xml");
						xmlDirectoryPath = sc.nextLine();
						File xmlDirectPath = new File(xmlDirectoryPath);
						if(xmlDirectPath.isDirectory()) {
							xmljsonConverter.convertJSONtoXML(directoryPath, xmlDirectoryPath);
							System.out.println("Conversion ended.......");
						}else {
							System.out.println("The given path is not directory path or does not exist");
							System.out.println("Do you want to continue? Y/N");
							exit = sc.nextLine();
							if(exit.equals("Y")||exit.equals("y")) {
								main(args);
							}else {
								System.exit(1);
							}
						}
					}else {
						System.out.println("The given path is not directory path or does not exist");
						System.out.println("Do you want to continue? Y/N");
						exit = sc.nextLine();
						if(exit.equals("Y")||exit.equals("y")) {
							main(args);
						}else {
							System.exit(1);
						}
					}
				}else {
					System.out.println("Entered input is wrong");
					System.out.println("Do you want to continue? Y/N");
					exit = sc.nextLine();
					if(exit.equals("Y")||exit.equals("y")) {
						main(args);
					}else {
						System.exit(1);
					}
				}
				
			}
		}
	}

}
