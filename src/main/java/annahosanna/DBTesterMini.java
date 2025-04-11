package annahosanna;

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

public class DBTesterMini {

  public static void help() {
    String help =
      "" +
      "Usage: java -jar DBTesterMini.jar [options]\n" +
      "      Options:\n" +
      "  -h, --help      Display this help message\n" +
      "  -d, --driver    JDBC driver class name\n" +
      "  -u, --url       JDBC connection URL\n" +
      "  -u, --username  JDBC username\n" +
      "  -p, --password  JDBC password\n" +
      "\n" +
      "Purpose: This program serves the simple purpose of being a fail fast test in a build pipeline.\n" +
      "It prevents the time consuming problem of going through a build, code scanning, component scanning,\n" +
      "and unit tests only to get to integration testing to find out the liquibase or some other database\n" +
      "interaction fails because the database is not running or the password is wrong or expired. If\n" +
      "authentication fails the software should exit with an error.\n" +
      "\n" +
      "Class Names Examples:\n" +
      "  Postgresql = org.postgresql.Driver\n" +
      "  Mysql = com.mysql.jdbc.Driver\n" +
      "  MS SQL Server = com.microsoft.sqlserver.jdbc.SQLServerDriver\n" +
      "  Oracle Database = oracle.jdbc.driver.OracleDriver\n" +
      "  H2 Database = org.h2.Driver\n" +
      "  Note: Oracle has established their own private Maven repository which\n" +
      "   may have more recent versions available. Additionally Oracle Thin\n" +
      "   is no longer available on Maven Central.\n" +
      "\n" +
      "JDBC Connection String Examples (localhost can be replaced with the actual hostname):\n" +
      "  Postgresql = jdbc:postgresql://localhost:5432/mydatabase\n" +
      "  Mysql = jdbc:mysql://localhost:3306/mydatabase?useCursorFetch=true\n" +
      "  MS SQL Server = jdbc:sqlserver://localhost:1433;databaseName=mydatabase,integratedSecurity=true\n" +
      "  Oracle Database using Thin = jdbc:oracle:thin:@localhost:1521:orcl\n" +
      "  Oracle Database using OCI = jdbc:oracle:oci8:@localhost\n" +
      "  H2 Database = jdbc:h2:mem:mydatabase\n";
    System.out.println(help);
    System.exit(0);
  }

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption(new Option("u", "username", true, "JDBC Username."));
    options.addOption(new Option("p", "password", true, "JDBC Password."));
    options.addOption(
      new Option("c", "url", true, "JDBC Connection String URL.")
    );
    options.addOption(new Option("h", "help", false, "Help."));
    options.addOption(
      new Option("d", "driver", true, "JDBC Driver Class Name.")
    );

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

    if (cmd.hasOption("h")) {
      help();
    }

    if (
      (cmd.hasOption("u")) &&
      (!StringUtils.isBlank(cmd.getOptionValue("u"))) &&
      (cmd.hasOption("p")) &&
      (!StringUtils.isBlank(cmd.getOptionValue("p"))) &&
      (cmd.hasOption("c")) &&
      (cmd.hasOption("d")) &&
      (!StringUtils.isBlank(cmd.getOptionValue("c"))) &&
      (!StringUtils.isBlank(cmd.getOptionValue("d")))
    ) {
      String username = cmd.getOptionValue("u");
      String password = cmd.getOptionValue("p");
      String url = cmd.getOptionValue("c");
      String driver = cmd.getOptionValue("d");

      int exitCode = 0;
      try {
        Class.forName(driver);
        try (
          Connection conn = DriverManager.getConnection(url, username, password)
        ) {
          System.out.println("Connection success");
          exitCode = 0;
        } catch (Exception e) {
          System.out.println("Connection failed!");
          e.printStackTrace();
          exitCode = 1;
        }
      } catch (Exception e) {
        System.out.println("Connection failed!");
        e.printStackTrace();
        exitCode = 1;
      }
      System.exit(exitCode);
    }
  }
}
