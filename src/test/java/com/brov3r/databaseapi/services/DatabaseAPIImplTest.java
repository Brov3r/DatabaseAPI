package com.brov3r.databaseapi.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseAPIImpl class, testing CRUD operations and table management.
 */
public class DatabaseAPIImplTest {
    private DatabaseAPI dbAPI;
    private File dbFile;

    /**
     * Sets up the test environment by initializing DatabaseAPIImpl instance
     * and creating a new database file.
     */
    @BeforeEach
    public void setUp() {
        dbAPI = new DatabaseAPIImpl();
        dbFile = new File("test.db");
    }

    /**
     * Cleans up the test environment by deleting the database file if it exists.
     */
    @AfterEach
    public void tearDown() {
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    /**
     * Tests the {@code readRecords} method to ensure correct reading of records from the database table.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testReadRecords() throws SQLException {
        dbAPI.executeSql(dbFile, "CREATE TABLE IF NOT EXISTS test_read_records (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "age INTEGER, " +
                "salary REAL);");

        assertTrue(dbAPI.tableExists(dbFile, "test_read_records"));

        dbAPI.executeSql(dbFile, "INSERT INTO test_read_records VALUES (1, 'John Doe', 30, 1000.50);");
        assertTrue(dbAPI.recordsExist(dbFile, "test_read_records"));

        List<Map<String, Object>> records = dbAPI.readRecords(dbFile, "test_read_records", "*");
        assertNotNull(records);
        assertEquals(1, records.size());

        Map<String, Object> record = records.get(0);
        assertEquals(1, record.get("id"));
        assertEquals("John Doe", record.get("name"));
        assertEquals(30, record.get("age"));
        assertEquals(1000.50, record.get("salary"));

        dbAPI.executeSql(dbFile, "DELETE FROM test_read_records;");
        assertFalse(dbAPI.recordsExist(dbFile, "test_read_records"));
    }

    /**
     * Tests reading records with multiple columns from a database table.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testReadRecordsWithMultipleColumns() throws SQLException {
        // Create a table with multiple columns
        dbAPI.executeSql(dbFile, "CREATE TABLE IF NOT EXISTS test_read_multiple (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "age INTEGER, " +
                "salary REAL);");

        assertTrue(dbAPI.tableExists(dbFile, "test_read_multiple"));

        // Insert multiple records into the table
        dbAPI.executeSql(dbFile, "INSERT INTO test_read_multiple VALUES (1, 'John Doe', 30, 1000.50);");
        dbAPI.executeSql(dbFile, "INSERT INTO test_read_multiple VALUES (2, 'Jane Smith', 25, 1200.75);");
        assertTrue(dbAPI.recordsExist(dbFile, "test_read_multiple"));

        // Read records with all columns selected
        List<Map<String, Object>> records = dbAPI.readRecords(dbFile, "test_read_multiple", "*");
        assertNotNull(records);
        assertEquals(2, records.size());

        // Validate the first record
        Map<String, Object> record1 = records.get(0);
        assertEquals(1, record1.get("id"));
        assertEquals("John Doe", record1.get("name"));
        assertEquals(30, record1.get("age"));
        assertEquals(1000.50, record1.get("salary"));

        // Validate the second record
        Map<String, Object> record2 = records.get(1);
        assertEquals(2, record2.get("id"));
        assertEquals("Jane Smith", record2.get("name"));
        assertEquals(25, record2.get("age"));
        assertEquals(1200.75, record2.get("salary"));

        // Clean up: delete test data and table
        dbAPI.executeSql(dbFile, "DELETE FROM test_read_multiple;");
        assertFalse(dbAPI.recordsExist(dbFile, "test_read_multiple"));
    }

    /**
     * Tests the {@code readRecords} method when no records are present in the database table.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testReadRecordsWhenNoRecords() throws SQLException {
        dbAPI.executeSql(dbFile, "CREATE TABLE IF NOT EXISTS test_read_records (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "age INTEGER, " +
                "salary REAL);");

        assertTrue(dbAPI.tableExists(dbFile, "test_read_records"));

        assertFalse(dbAPI.recordsExist(dbFile, "test_read_records"));

        List<Map<String, Object>> records = dbAPI.readRecords(dbFile, "test_read_records", "*");
        assertNotNull(records);
        assertEquals(0, records.size());

        dbAPI.executeSql(dbFile, "DELETE FROM test_read_records;");
        assertFalse(dbAPI.recordsExist(dbFile, "test_read_records"));
    }

    /**
     * Tests the creation of a new table and verifies its existence in the database.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testCreateTable() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
             ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table';")) {
            assertTrue(rs.next());
        }
    }

    /**
     * Tests inserting a record into a table and verifies its retrieval.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testInsertAndReadRecord() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        dbAPI.insertRecord(dbFile, "test_table", "1, 'John Doe'");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM test_table;")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("id"));
            assertEquals("John Doe", rs.getString("name"));
        }
    }

    /**
     * Tests updating a record in a table and verifies the updated value.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testUpdateRecord() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        dbAPI.insertRecord(dbFile, "test_table", "1, 'John Doe'");
        dbAPI.updateRecords(dbFile, "test_table", "name = 'Jane Doe'", "id = 1");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM test_table;")) {
            assertTrue(rs.next());
            assertEquals("Jane Doe", rs.getString("name"));
        }
    }

    /**
     * Tests deleting a record from a table and verifies its absence.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testDeleteRecord() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        dbAPI.insertRecord(dbFile, "test_table", "1, 'John Doe'");
        dbAPI.deleteRecords(dbFile, "test_table", "id = 1");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM test_table;")) {
            assertFalse(rs.next());
        }
    }

    /**
     * Tests checking if a table exists in the database.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testTableExists() throws SQLException {
        assertFalse(dbAPI.tableExists(dbFile, "non_existing_table"));

        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        assertTrue(dbAPI.tableExists(dbFile, "test_table"));
    }

    /**
     * Tests dropping a table from the database and verifies its absence.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testDropTable() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        assertTrue(dbAPI.tableExists(dbFile, "test_table"));

        dbAPI.dropTable(dbFile, "test_table");
        assertFalse(dbAPI.tableExists(dbFile, "test_table"));
    }

    /**
     * Tests checking if records exist in a table.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testRecordsExist() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        assertFalse(dbAPI.recordsExist(dbFile, "test_table"));

        dbAPI.insertRecord(dbFile, "test_table", "1, 'John Doe'");
        assertTrue(dbAPI.recordsExist(dbFile, "test_table"));
    }

    /**
     * Tests executing arbitrary SQL statements on the database.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testExecuteSql() throws SQLException {
        dbAPI.executeSql(dbFile, "CREATE TABLE IF NOT EXISTS test_execute_sql (id INTEGER PRIMARY KEY, name TEXT);");
        assertTrue(dbAPI.tableExists(dbFile, "test_execute_sql"));

        dbAPI.executeSql(dbFile, "INSERT INTO test_execute_sql VALUES (1, 'John Doe');");
        assertTrue(dbAPI.recordsExist(dbFile, "test_execute_sql"));

        dbAPI.executeSql(dbFile, "DELETE FROM test_execute_sql;");
        assertFalse(dbAPI.recordsExist(dbFile, "test_execute_sql"));
    }

    /**
     * Tests executing a SQL query and retrieving results as ResultSet.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testExecuteQueryWithMultipleColumns() throws SQLException {
        dbAPI.executeSql(dbFile, "CREATE TABLE IF NOT EXISTS test_execute_query (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "age INTEGER, " +
                "height REAL, " +
                "is_student BOOLEAN);");
        assertTrue(dbAPI.tableExists(dbFile, "test_execute_query"));

        dbAPI.executeSql(dbFile, "INSERT INTO test_execute_query VALUES (1, 'John Doe', 30, 175.5, 1);");
        assertTrue(dbAPI.recordsExist(dbFile, "test_execute_query"));

        List<Map<String, Object>> resultList = dbAPI.executeQuery(dbFile, "SELECT * FROM test_execute_query;");
        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        Map<String, Object> firstRow = resultList.get(0);
        assertEquals("John Doe", firstRow.get("name"));
        assertEquals(30, firstRow.get("age"));
        assertEquals(175.5, firstRow.get("height"));
        assertEquals(1, firstRow.get("is_student"));

        assertTrue(dbAPI.recordsExist(dbFile, "test_execute_query"));
        assertTrue(dbAPI.recordsExist(dbFile, "test_execute_query", "name = 'John Doe'"));
        assertEquals(1, dbAPI.getRecordCount(dbFile, "test_execute_query"));

        dbAPI.executeSql(dbFile, "DELETE FROM test_execute_query;");
        assertFalse(dbAPI.recordsExist(dbFile, "test_execute_query"));
    }


    /**
     * Tests checking if specific records exist in a table.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testRecordsExistWithCondition() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        dbAPI.insertRecord(dbFile, "test_table", "1, 'John Doe'");

        assertTrue(dbAPI.recordsExist(dbFile, "test_table", "id = 1"));
        assertFalse(dbAPI.recordsExist(dbFile, "test_table", "id = 2"));
    }

    /**
     * Tests retrieving the count of records in a table.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void testGetRecordCount() throws SQLException {
        dbAPI.createTable(dbFile, "test_table", "id INTEGER PRIMARY KEY, name TEXT");
        assertEquals(0, dbAPI.getRecordCount(dbFile, "test_table"));

        dbAPI.insertRecord(dbFile, "test_table", "1, 'John Doe'");
        dbAPI.insertRecord(dbFile, "test_table", "2, 'False Doe'");
        assertEquals(2, dbAPI.getRecordCount(dbFile, "test_table"));
    }
}