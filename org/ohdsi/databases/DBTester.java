/*****************************************************************************
 * JDBC Drivers are not included and will need to be downloaded from vendors *
 ****************************************************************************/
package annahosanna;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

// apache cli 1
// --username --password --server --domsin --dbtype

// Test if a connection can be openned without error
// and exit success (0)

public class DBTester {

  static String postgresqlClassname = "org.postgresql.Driver";
  static String mysqlClassname = "com.mysql.jdbc.Driver";
  static String sqlserverClassname =
    "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  // Appears the Oracle Thin driver is no longer available on Maven Central, (but perhaps in the Oracle Maven repository)
  // Try ojdbc8 from Maven Central
  static String oracleClassname = "oracle.jdbc.driver.OracleDriver";
  static String h2ClassName = "org.h2.Driver";

  public static void main(String args[]) {
    int exitCode = 1;
    Options options = new Options();
    options.addOption(new Option("u", "username", true, "Username."));
    options.addOption(new Option("p", "password", true, "Password."));
    options.addOption(new Option("s", "server", true, "Server."));
    options.addOption(
      new Option(
        "t",
        "dbtype",
        true,
        "DB type (oracle, sqlserver, mysql,postgresql, odbc)."
      )
    );
    options.addOption(
      new Option(
        "d",
        "domain",
        true,
        "Domain (only applies to mssql and oracle)."
      )
    );
    options.addOption(
      new Option("c", "connectionstring", true, "Connection string.")
    );
    options.addOption(new Option("n", "classname", true, "JDBC class name."));

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    } catch (Exception e) {
      System.err.println(
        "Error parsing command line arguments: " + e.getMessage()
      );
      System.exit(1);
    }
    // Handle dbtype
    if (
      (cmd.hasOption("u")) &&
      (!StringUtils.isBlank(cmd.getOptionValue("u"))) &&
      (cmd.hasOption("p")) &&
      (!StringUtils.isBlank(cmd.getOptionValue("p"))) &&
      (cmd.hasOption("t")) &&
      (cmd.hasOption("s")) &&
      (!StringUtils.isBlank(cmd.getOptionValue("t"))) &&
      (!StringUtils.isBlank(cmd.getOptionValue("s")))
    ) {
      String username = cmd.getOptionValue("u");
      String password = cmd.getOptionValue("p");
      String dbType = cmd.getOptionValue("t");
      String server = cmd.getOptionValue("s");
      if (dbType.equalsIgnoreCase("MYSQL")) {
        exitCode = DBUtils.connectToMySQL(server, username, password);
      } else if (dbType.equalsIgnoreCase("POSTGRESQL")) {
        exitCode = DBUtils.connectToPostgreSQL(server, username, password);
      } else if (dbType.equalsIgnoreCase("ODBC")) {
        exitCode = DBUtils.connectToODBC(server, username, password);
      } else if (
        (cmd.hasOption("d")) && (!StringUtils.isBlank(cmd.getOptionValue("d")))
      ) {
        String domain = cmd.getOptionValue("d");
        DBUtils.handleDBWithDomain(dbType, domain, server, username, password);
      } else if ((cmd.hasOption("n")) && (cmd.hasOption("c"))) {
        // A db type was not found - what about a class name
        String className = cmd.getOptionValue("n");
        String connectionString = cmd.getOptionValue("c");
        if (
          (!StringUtils.isBlank(className)) &&
          (!StringUtils.isBlank(connectionString))
        ) {
          exitCode = DBUtils.connectToClass(
            className,
            connectionString,
            username,
            password
          );
        } else {
          exitCode = 1;
        }
      } else {
        exitCode = 1;
      }
    } else {
      exitCode = 1;
    }
    System.exit(exitCode);
  }
}
