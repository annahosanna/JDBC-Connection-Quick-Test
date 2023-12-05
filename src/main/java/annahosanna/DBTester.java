/*******************************************************************************
 * JDBC Drivers are not included and will need to be downloaded from vendors
 * Based on code
 * Copyright 2017 Observational Health Data Sciences and Informatics
 * 
 * This file is part of WhiteRabbit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package annahosanna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.text.html.Option;

// import org.ohdsi.databases.DBTester;

// import oracle.jdbc.pool.OracleDataSource;
import com.oracle.database.jdbc.ojdbc11;

// apache cli 1
// --username --password --server --domsin --dbtype

// Test if a connection can be openned without error
// and exit success (0)

public class DBTester {

	static String postgresqlClassname = "org.postgresql.Driver";
	static String mysqlClassname = "com.mysql.jdbc.Driver";
	static String sqlserverClassname = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	static String jtdsSqlserverClassname = "net.sourceforge.jtds.jdbc.Driver";
	static String oracleClassname = "oracle.jdbc.driver.OracleDriver";
	static String odbcClassname ="sun.jdbc.odbc.JdbcOdbcDriver";

	public static void main (String args []) {
		int exitCode = 1;
		Options options = new Options();
		options.addOption(new Option("u", "username", true, "Username."));
		options.addOption(new Option("p", "password", true, "Password."));
		options.addOption(new Option("s", "server", true, "Server."));
		options.addOption(new Option("t", "dbtype", true, "DB type (oracle, sqlserver, mysql,postgresql)."));
		options.addOption(new Option("d", "domain", true, "Domain (only applies to )."));
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		if(cmd.hasOption("t")) && (cmd.hasOption("u") && (cmd.hasOption("p"))){
			String dbType = cmd.getOptionValue("t");
			String username = cmd.getOptionValue("u");
			String password = cmd.getOptionValue("p");
			if ((dbType != null) && (!dbType.isEmpty()) && (username != null) && (!username.isEmpty()) && (password != null) && (!password.isEmpty())) {
				if (dbType.equalsIgnoreCase("MYSQL")) {
					exitCode = DBTester.connectToMySQL(server, user, password);
				} else if (dbType.equalsIgnoreCase("POSTGRESQL")) {
					exitCode = DBTester.connectToPostgreSQL(server, user, password);
				} else if (dbType.equalsIgnoreCase("ODBC")) {
					exitCode = DBTester.connectToODBC(server, user, password);
				} else if ((cmd.hasOption("d"))) {
					String domain = cmd.getOptionValue("d");
					if ((domain != null) && (!domain.isEmpty())) {
					 if (dbType.equalsIgnoreCase("MSSQL")) {
						exitCode = DBTester.connectToMSSQL(server, domain, user, password);
					} else if (dbType.equalsIgnoreCase("ORACLE")) {
						exitCode = DBTester.connectToOracle(server, domain, user, password);
					} else {
						exitCode = 1;
					}
				} else {
					exitCode = 1;	
				}
			} else {
				exitCode = 1;
			}
		}
		exit(exitCode);
	}

	// I hope the compiler does not try to optimize out the connection attempts b/c they are not used
	public static int connectToPostgreSQL(String server, String user, String password) {
		int exitCode = 1;
		if (!server.contains("/"))
			exitCode = 1;
			// throw new RuntimeException("For PostgreSQL, database name must be specified in the server field (<host>/<database>)");
		try {
			Class.forName("org.postgresql.Driver");
			exitCode = 0;
		} catch (ClassNotFoundException e1) {
			// throw new RuntimeException("Cannot find JDBC driver. Make sure the file postgresql-x.x-xxxx.jdbcx.jar is in the path");
			exitCode = 1;
		}
		String url = "jdbc:postgresql://" + server;
		try {
			DriverManager.getConnection(url, user, password);
			exitCode = 0;
		} catch (SQLException e1) {
			// throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
			exitCode = 1;
		}
	}

	public static int connectToMySQL(String server, String user, String password) {
		int exitCode = 1;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			exitCode = 0;
		} catch (ClassNotFoundException e1) {
			// throw new RuntimeException("Cannot find JDBC driver. Make sure the file mysql-connector-java-x.x.xx-bin.jar is in the path");
			exitCode = 1;
		}

		String url = "jdbc:mysql://" + server + ":3306/?useCursorFetch=true";

		try {
			DriverManager.getConnection(url, user, password);
			exitCode = 0;
		} catch (SQLException e1) {
			// throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
			exitCode = 1;
		}
	}

	public static int connectToODBC(String server, String user, String password) {
		int exitCode = 1;
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			exitCode = 0;
		} catch (ClassNotFoundException e1) {
			// throw new RuntimeException("Cannot find ODBC driver");
			exitCode = 1;
		}

		String url = "jdbc:odbc:" + server;

		try {
			DriverManager.getConnection(url, user, password);
			exitCode = 0;
		} catch (SQLException e1) {
			// throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
			exitCode = 1;
		}
	}

	/*
	 * public static Connection connectToMSSQL(String server, String domain, String user, String password) { try {
	 * Class.forName("net.sourceforge.jtds.jdbc.Driver");
	 * 
	 * } catch (ClassNotFoundException e1) { throw new RuntimeException("Cannot find JDBC driver. Make sure the file sqljdbc4.jar is in the path"); }
	 * 
	 * String url = "jdbc:jtds:sqlserver://"+server+(domain.length()==0?"":";domain="+domain);
	 * 
	 * try { return DriverManager.getConnection(url,user, password); } catch (SQLException e1) { throw new RuntimeException("Cannot connect to DB server: " +
	 * e1.getMessage()); } }
	 */
	public static int connectToMSSQL(String server, String domain, String user, String password) {
		int exitCode = 1;
		if (user == null || user.length() == 0) { // Use Windows integrated security
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				exitCode = 0;
			} catch (ClassNotFoundException e1) {
				// throw new RuntimeException("Cannot find JDBC driver. Make sure the file sqljdbc4.jar is in the path");
				exitCode = 1;
			}
			String url = "jdbc:sqlserver://" + server + ";integratedSecurity=true";

			try {
				DriverManager.getConnection(url, user, password);
				exitCode = 0;
			} catch (SQLException e1) {
				// throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
				exitCode = 1;
			}
		} else { // Do not use Windows integrated security
			try {
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
				exitCode = 0;
			} catch (ClassNotFoundException e1) {
				// throw new RuntimeException("Cannot find JDBC driver. Make sure the file jtds-1.3.0.jar is in the path");
				exitCode = 1;
			}

			String url = "jdbc:jtds:sqlserver://" + server + ";ssl=required" + ((domain == null || domain.length() == 0) ? "" : ";domain=" + domain);

			try {
				DriverManager.getConnection(url, user, password);
				exitCode = 0;
			} catch (SQLException e1) {
				// throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
				exitCode = 1;
			}
		}
		return exitCode;
	}

	public static int connectToOracle(String server, String domain, String user, String password) {
		int exitCode = 1;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			exitCode = 0;
		} catch (ClassNotFoundException e) {
			// throw new RuntimeException("Class not found exception: " + e.getMessage());
			exitCode = 1;
		}
		// First try OCI driver:
		String error = null;
		try {
			OracleDataSource ods;
			ods = new OracleDataSource();
			ods.setURL("jdbc:oracle:oci8:@" + server);
			ods.setUser(user);
			ods.setPassword(password);
			ods.getConnection();
			exitCode = 0;
			return exitCode;
		} catch (UnsatisfiedLinkError e) {
			error = e.getMessage();
			exitCode = 1;
		} catch (SQLException e) {
			error = e.getMessage();
			exitCode = 1;
		}
		// If fails, try THIN driver:
		if (error != null) {
			try {
				String host = "127.0.0.1";
				String sid = server;
				String port = "1521";
				if (server.contains("/")) {
					host = server.split("/")[0];
					if (host.contains(":")) {
						port = host.split(":")[1];
						host = host.split(":")[0];
					}
					sid = server.split("/")[1];
				}
				OracleDataSource ods;
				ods = new OracleDataSource();
				ods.setURL("jdbc:oracle:thin:@" + host + ":" + port + ":" + sid);
				ods.setUser(user);
				ods.setPassword(password);
				ods.getConnection();
				exitCode = 0;
			} catch (SQLException e) {
				// throw new RuntimeException("Cannot connect to DB server:\n- When using OCI: " + error + "\n- When using THIN: " + e.getMessage());
				exitCode = 1;
			}
		}
		return exitCode;
	}
}