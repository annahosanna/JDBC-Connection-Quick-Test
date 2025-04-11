# JDBC-Connection-Quick-Test

### What does this do

In a DevOps pipeline an application such as Liquibase or a test suite might need to connect to a database server instance. In the spirit of fail fast, rather than compiling the code and running through the pipeline only to fail because the database was down or the login credentials were not correct, this can be placed in an early step in the pipeline.
