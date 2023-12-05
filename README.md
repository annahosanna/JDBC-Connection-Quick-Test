# JDBC-Connection-Quick-Test

### What does this do
In a DevOps pipeline an application such as Liquibase or a test suite might need to connect to a database server instance. In the spirit of fail fast, rather than compiling the code and running through the pipeline only to fail because the database was down or the login credentials were not correct, this can be placed in an early step in the pipeline.

### DBTester vs. Generic
* DBTester tries to be smart and has some knowledge of specific classes. (Server, Username, Password and sometimes Domain)
* The generic functionality of DBTester relies on the user passing the class name on the command line as well as the three standard jdbc parameters (Server connection string, Username and Password). Some databases classes will not be compatible if they require more than the three standard parameters.