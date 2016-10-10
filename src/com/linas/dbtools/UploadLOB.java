package com.linas.dbtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class UploadLOB {

	// settings loaded from properties file
	public static String connectionString = null;
	public static String dbdriver = null;
	public static String user = null;
	public static String password = null;

	// arguments
	public static String tablename = null;
	public static String fieldname = null;
	public static String fieldtype = null;
	public static String whereclause = null;
	public static String filename = null;

	public static void main(String[] args) throws Exception {

		boolean settingsSuccess = loadSettings();
		boolean argumentSuccess = parseArgs(args);
		
		if (!settingsSuccess || !argumentSuccess) {
			
			System.out.println("FAILED: configuration file or arguments are not correct");
			return;
		}
				
		Class.forName(dbdriver);
		Connection cn = DriverManager.getConnection(connectionString, user, password);
		
		File fileToLoad = new File(filename);
		FileInputStream fin = new FileInputStream(fileToLoad);
		
		PreparedStatement pst = cn.prepareStatement("update " + tablename + " set " + fieldname + " = ? where " + whereclause);

		if (fieldtype.equals("blob")) {
			pst.setBinaryStream(1, fin);
		} else {
			pst.setCharacterStream(1, new InputStreamReader(fin));
		}
		
		int updatedCount = pst.executeUpdate();
		
		
		System.out.println("OK - number of updated records is " + updatedCount);

	}

	private static boolean parseArgs(String[] args)  {

		if (args.length != 5) {
			System.out.println("You must specify the following arguments:");
			System.out.println("  tablename lobfieldname fieldtype(blob|clob) whereclause filename");
			System.out.println("For example:");
			System.out.println("  xsltemplateinst templatedoc clob \"xsltemplateinstid=2025\" myfilename.xml");
			
			return false;
		}
		
		tablename = args[0];
		fieldname = args[1];
		fieldtype = args[2].toLowerCase();
		whereclause = args[3];
		filename = args[4];
		
		if (!fieldtype.equals("clob") && !fieldtype.equals("blob")) {
			System.out.println("Field type must be set to blob or clob. It was set to '" + fieldtype + "' ");
			return false;
		}
		
		if (!new File(filename).exists()) {
			System.out.println("Input file '" + filename  + "' does not exist");
			return false;
		}
		
		
		
		return true;
		
		
	}

	private static boolean loadSettings() throws FileNotFoundException, IOException {
		
		File f = new File("loadlob.properties");
		if (!f.exists()) {
			System.out.println("Config file '" + f.getAbsolutePath() + "' does not exist!");
			return false;
		}
		
		Properties configurationProperties = new Properties();
		configurationProperties.load(new FileReader("loadlob.properties"));
		
		dbdriver = configurationProperties.getProperty("driver");
		boolean driverValueSuccess = validateProperty("driver", dbdriver);
	
		user = configurationProperties.getProperty("user");
		boolean userValueSuccess = validateProperty("user", user);
				
		password = configurationProperties.getProperty("pass");
		boolean passwordValueSuccess = validateProperty("pass", password);
		
		connectionString = configurationProperties.getProperty("connectionstring");
		boolean connectionStringSuccess = validateProperty("connectionstring", connectionString);
		
		if (driverValueSuccess && userValueSuccess && passwordValueSuccess && connectionStringSuccess) {
			return true;
		}
		
		return false;
				
	}

	private static boolean validateProperty(String key, String val) {
		if (val == null || val.trim().isEmpty()) {
			System.out.println("loadlob.properties file is missing the value for the key " + key);
			return false;
		}
		
		return true;
		
	}

}
