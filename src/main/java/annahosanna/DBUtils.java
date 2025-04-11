package annahosanna;

// import com.oracle.database.jdbc.ojdbc11;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

  // I hope the compiler does not try to optimize out the connection attempts b/c they are not used
  public static int connectToClass(
    String className,
    String connectionString,
    String user,
    String password
  ) {
    int exitCode = 1;
    try {
      Class.forName(className);
      exitCode = 0;
    } catch (ClassNotFoundException e1) {
      // throw new RuntimeException("Cannot find JDBC driver. Make sure the file postgresql-x.x-xxxx.jdbcx.jar is in the path");
      exitCode = 1;
    }
    try {
      DriverManager.getConnection(connectionString, user, password);
      exitCode = 0;
    } catch (SQLException e1) {
      // throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
      exitCode = 1;
    }
    return exitCode;
  }

  public static int connectToPostgreSQL(
    String server,
    String user,
    String password
  ) {
    int exitCode = 1;
    if (!server.contains("/")) exitCode = 1;
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
    return exitCode;
  }

  public static int connectToMySQL(
    String server,
    String user,
    String password
  ) {
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
    return exitCode;
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
    return exitCode;
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
  public static int connectToMSSQL(
    String server,
    String domain,
    String user,
    String password
  ) {
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

      String url =
        "jdbc:jtds:sqlserver://" +
        server +
        ";ssl=required" +
        ((domain == null || domain.length() == 0) ? "" : ";domain=" + domain);

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

  public static void handleDBWithDomain(
    String dbType,
    String domain,
    String server,
    String username,
    String password
  ) {
    int exitCode = 1;
    if (dbType.equalsIgnoreCase("MSSQL")) {
      exitCode = DBUtils.connectToMSSQL(server, domain, username, password);
    } else if (dbType.equalsIgnoreCase("ORACLE")) {
      /*
         exitCode = DBUtils.connectToOracle(
          server,
          domain,
          username,
          password
        );
        */
    } else {
      // None of the remaining db types
      exitCode = 1;
    }
  }
  /*
  public static int connectToOracle(
    String server,
    String domain,
    String user,
    String password
  ) {
    int exitCode = 1;
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      exitCode = 0;
    } catch (ClassNotFoundException e) {
      // throw new RuntimeException("Class not found exception: " + e.getMessage());
      exitCode = 1;
      return exitCode;
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
  */
}
