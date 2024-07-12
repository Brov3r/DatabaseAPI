<div align="center">
    <h1>Database API</h1>
</div>

<p align="center">
    <img alt="PZ Version" src="https://img.shields.io/badge/Project_Zomboid-41.78.16-blue">
    <img alt="Java version" src="https://img.shields.io/badge/Java-17-orange">
    <img alt="Environment" src="https://img.shields.io/badge/Environment-client/server-green">
    <img alt="Avrix" src="https://img.shields.io/badge/AvrixLoader->=1.2.0-red">
</p>

**DataBase API** - a unified set of tools for working with the DataBase API from other plugins

# How to use

1) Install [AvrixLoader](https://github.com/Brov3r/Avrix)
2) Download the plugin from the releases page
2) Move to the `plugins` folder
3) Start the server and shut down
4) Configure the plugin in the plugin settings folder `plugins/database-api`

# For developers

## How to use

1) Download and include the library as `compileOnly`
2) In the right place in the plugin (preferably in the main class) get the API object:

```java
DatabaseAPI databaseAPI = ServiceManager.getService(DatabaseAPI.class);
```

## API

```java
/**
 * Creates a new table in the specified database file.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to create.
 * @param columns    The columns definition in SQL format (e.g., "id INTEGER PRIMARY KEY, name TEXT").
 * @throws SQLException If a database access error occurs.
 */
void createTable(File file, String tableName, String columns) throws SQLException;

/**
 * Inserts a record into the specified table in the database file.
 *
 * @param file     The database file.
 * @param tableName  The name of the table to insert into.
 * @param values    The values to insert in SQL format (e.g., "1, 'John Doe'").
 * @throws SQLException If a database access error occurs.
 */
void insertRecord(File file, String tableName, String values) throws SQLException;

/**
 * Reads records from the specified table in the database file.
 *
 * @param file      The database file.
 * @param tableName The name of the table to read from.
 * @param columns   The columns to select (e.g., "*" for all columns).
 * @return A list of maps where each map represents a record with column names as keys and corresponding values.
 * @throws SQLException If a database access error occurs.
 */
List<Map<String, Object>> readRecords(File file, String tableName, String columns) throws SQLException;

/**
 * Updates records in the specified table in the database file based on a condition.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to update.
 * @param updates    The update clause (e.g., "name = 'Jane Doe'").
 * @param condition  The condition to specify which records to update (e.g., "id = 1").
 * @throws SQLException If a database access error occurs.
 */
void updateRecords(File file, String tableName, String updates, String condition) throws SQLException;

/**
 * Deletes records from the specified table in the database file based on a condition.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to delete from.
 * @param condition  The condition to specify which records to delete (e.g., "id = 1").
 * @throws SQLException If a database access error occurs.
 */
void deleteRecords(File file, String tableName, String condition) throws SQLException;

/**
 * Checks if the specified table exists in the database file.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to check.
 * @return true if the table exists, false otherwise.
 * @throws SQLException If a database access error occurs.
 */
boolean tableExists(File file, String tableName) throws SQLException;

/**
 * Drops (deletes) the specified table from the database file.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to drop.
 * @throws SQLException If a database access error occurs.
 */
void dropTable(File file, String tableName) throws SQLException;

/**
 * Executes an arbitrary SQL statement on the database file.
 *
 * @param file  The database file.
 * @param sql   The SQL statement to execute.
 * @throws SQLException If a database access error occurs.
 */
void executeSql(File file, String sql) throws SQLException;

/**
 * Executes a SQL query using the provided file-based connection and returns the result as a list of maps.
 *
 * @param file the File object representing the database connection details.
 * @param sql the SQL query string to be executed.
 * @return a List<Map<String, Object>> containing the query results, where each map represents a row of data.
 * @throws SQLException If a database access error occurs.
 */
List<Map<String, Object>> executeQuery(File file, String sql) throws SQLException;

/**
 * Checks if there are any records in the specified table of the database file.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to check.
 * @return true if records exist in the table, false otherwise.
 * @throws SQLException If a database access error occurs.
 */
boolean recordsExist(File file, String tableName) throws SQLException;

/**
 * Checks if there are any records matching the specified condition in the table.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to check.
 * @param condition  The condition to match records (e.g., "id = 1").
 * @return true if records matching the condition exist, false otherwise.
 * @throws SQLException If a database access error occurs.
 */
boolean recordsExist(File file, String tableName, String condition) throws SQLException;

/**
 * Retrieves the count of records in the specified table of the database file.
 *
 * @param file       The database file.
 * @param tableName  The name of the table to retrieve record count from.
 * @return The number of records in the table.
 * @throws SQLException If a database access error occurs.
 */
int getRecordCount(File file, String tableName) throws SQLException;

/**
 * Retrieves a Connection object to the specified database file.
 *
 * @param file The database file.
 * @return A Connection object to the database.
 * @throws SQLException If a database access error occurs.
 */
Connection getConnection(File file) throws SQLException;
```

# Disclaimer

This software is provided "as is", without warranty of any kind, express or implied, including but not limited to the
warranties of merchantability, fitness for a particular purpose, and noninfringement of third party rights. Neither the
author of the software nor its contributors shall be liable for any direct, indirect, incidental, special, exemplary, or
consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability,
or tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the
possibility of such damage.

By using this software, you agree to these terms and release the author of the software and its contributors from any
liability associated with the use of this software.

# License

This project is licensed under [MIT License](./LICENSE).