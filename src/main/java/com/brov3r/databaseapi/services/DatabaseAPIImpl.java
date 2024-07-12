package com.brov3r.databaseapi.services;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the DatabaseAPI interface for SQLite databases.
 */
public class DatabaseAPIImpl implements DatabaseAPI {
    /**
     * Creates a new table in the specified database file.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to create.
     * @param columns    The columns definition in SQL format (e.g., "id INTEGER PRIMARY KEY, name TEXT").
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void createTable(File file, String tableName, String columns) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");");
        }
    }

    /**
     * Inserts a record into the specified table in the database file.
     *
     * @param file     The database file.
     * @param tableName  The name of the table to insert into.
     * @param values    The values to insert in SQL format (e.g., "1, 'John Doe'").
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void insertRecord(File file, String tableName, String values) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO " + tableName + " VALUES (" + values + ");");
        }
    }

    /**
     * Reads records from the specified table in the database file.
     *
     * @param file      The database file.
     * @param tableName The name of the table to read from.
     * @param columns   The columns to select (e.g., "*" for all columns).
     * @return A list of maps where each map represents a record with column names as keys and corresponding values.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Map<String, Object>> readRecords(File file, String tableName, String columns) throws SQLException {
        List<Map<String, Object>> records = new ArrayList<>();

        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            boolean hasResults = stmt.execute("SELECT " + columns + " FROM " + tableName + ";");

            if (!hasResults) return records;

            try (ResultSet rs = stmt.getResultSet()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> record = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object columnValue = rs.getObject(i);
                        record.put(columnName, columnValue);
                    }
                    records.add(record);
                }
            }
        }

        return records;
    }

    /**
     * Updates records in the specified table in the database file based on a condition.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to update.
     * @param updates    The update clause (e.g., "name = 'Jane Doe'").
     * @param condition  The condition to specify which records to update (e.g., "id = 1").
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void updateRecords(File file, String tableName, String updates, String condition) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE " + tableName + " SET " + updates + " WHERE " + condition + ";");
        }
    }

    /**
     * Deletes records from the specified table in the database file based on a condition.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to delete from.
     * @param condition  The condition to specify which records to delete (e.g., "id = 1").
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void deleteRecords(File file, String tableName, String condition) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM " + tableName + " WHERE " + condition + ";");
        }
    }

    /**
     * Checks if the specified table exists in the database file.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to check.
     * @return true if the table exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean tableExists(File file, String tableName) throws SQLException {
        try (Connection conn = getConnection(file);
             ResultSet tables = conn.getMetaData().getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    /**
     * Drops (deletes) the specified table from the database file.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to drop.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void dropTable(File file, String tableName) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + tableName + ";");
        }
    }

    /**
     * Executes an arbitrary SQL statement on the database file.
     *
     * @param file  The database file.
     * @param sql   The SQL statement to execute.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void executeSql(File file, String sql) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Executes a SQL query using the provided file-based connection and returns the result as a list of maps.
     *
     * @param file the File object representing the database connection details.
     * @param sql the SQL query string to be executed.
     * @return a List<Map<String, Object>> containing the query results, where each map represents a row of data.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Map<String, Object>> executeQuery(File file, String sql) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int numColumns = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= numColumns; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                resultList.add(row);
            }
        }

        return resultList;
    }

    /**
     * Checks if there are any records in the specified table of the database file.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to check.
     * @return true if records exist in the table, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean recordsExist(File file, String tableName) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1;");
            return rs.next();
        }
    }

    /**
     * Checks if there are any records matching the specified condition in the table.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to check.
     * @param condition  The condition to match records (e.g., "id = 1").
     * @return true if records matching the condition exist, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean recordsExist(File file, String tableName, String condition) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + tableName + " WHERE " + condition + " LIMIT 1;");
            return rs.next();
        }
    }

    /**
     * Retrieves the count of records in the specified table of the database file.
     *
     * @param file       The database file.
     * @param tableName  The name of the table to retrieve record count from.
     * @return The number of records in the table.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public int getRecordCount(File file, String tableName) throws SQLException {
        try (Connection conn = getConnection(file); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM " + tableName + ";");
            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0; // No records found
            }
        }
    }

    /**
     * Retrieves a Connection object to the specified database file.
     *
     * @param file The database file.
     * @return A Connection object to the database.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Connection getConnection(File file) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }
}