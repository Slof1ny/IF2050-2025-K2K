package main.Database;

public class DatabaseConfig {
    // SQLite database file path
    public static final String DB_FILE = "user.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    
    // SQLite doesn't require username/password for local files
    public static final String DB_USERNAME = "";
    public static final String DB_PASSWORD = "";
    
    // Connection settings
    public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    // SQL statements for pelanggan table (SQLite syntax)
    public static final String CREATE_PELANGGAN_TABLE = """
        CREATE TABLE IF NOT EXISTS pelanggan (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nama TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL,
            no_hp TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    """;
    
    // SQL statement for creating trigger to auto-update updated_at
    public static final String CREATE_UPDATE_TRIGGER = """
        CREATE TRIGGER IF NOT EXISTS update_pelanggan_timestamp 
        AFTER UPDATE ON pelanggan
        BEGIN
            UPDATE pelanggan SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
        END
    """;
    
    // Private constructor to prevent instantiation
    private DatabaseConfig() {
        throw new AssertionError("DatabaseConfig should not be instantiated");
    }
}
