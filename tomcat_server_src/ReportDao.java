package com.example.civic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO (Data Access Object) lato Server.
 * Gestisce la persistenza JDBC SQLite delle segnalazioni in linea con il pattern d'esame.
 */
public class ReportDao {
    private static final String DB_URL = "jdbc:sqlite:civic_reports_server.db";

    public ReportDao() {
        // Carica dinamicamente il driver SQLite JDBC
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Impossibile caricare il driver JDBC SQLite!");
        }

        // Crea la tabella automatizzando l'inizializzazione database del server
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            String createTableSql = "CREATE TABLE IF NOT EXISTS reports (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "category TEXT," +
                    "severity TEXT," +
                    "sender TEXT," +
                    "timestamp INTEGER," +
                    "latitude REAL," +
                    "longitude REAL," +
                    "ambientLight REAL," +
                    "batteryLevel INTEGER" +
                    ")";
            stmt.execute(createTableSql);
            
            // Inserisci un record d'esempio iniziale se vuoto
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM reports");
            if (rs.next() && rs.getInt(1) == 0) {
                String insertPrep = "INSERT INTO reports (title, description, category, severity, sender, timestamp, latitude, longitude, ambientLight, batteryLevel) VALUES " +
                        "('Guasto semaforo Corso Vittorio', 'Semaforo giallo lampeggiante su incrocio ad alta frequenza. Pericolo urti.', 'Altro', 'Alta', 'Docente Loreti', " + System.currentTimeMillis() + ", 41.8964, 12.4722, 120.0, 95)";
                stmt.execute(insertPrep);
            }
        } catch (SQLException e) {
            System.err.println("Errore nell'inizializzazione del DB SQLite Server: " + e.getMessage());
        }
    }

    /**
     * SELECT ALL GET
     */
    public List<ReportModel> getAllReports() throws SQLException {
        List<ReportModel> reports = new ArrayList<>();
        String query = "SELECT * FROM reports ORDER BY timestamp DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reports.add(new ReportModel(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("severity"),
                        rs.getString("sender"),
                        rs.getLong("timestamp"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getFloat("ambientLight"),
                        rs.getInt("batteryLevel")
                ));
            }
        }
        return reports;
    }

    /**
     * INSERT POST
     */
    public int insertReport(ReportModel report) throws SQLException {
        String query = "INSERT INTO reports (title, description, category, severity, sender, timestamp, latitude, longitude, ambientLight, batteryLevel) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, report.getTitle());
            ps.setString(2, report.getDescription());
            ps.setString(3, report.getCategory());
            ps.setString(4, report.getSeverity());
            ps.setString(5, report.getSender());
            ps.setLong(6, report.getTimestamp());
            ps.setDouble(7, report.getLatitude());
            ps.setDouble(8, report.getLongitude());
            ps.setFloat(9, report.getAmbientLight());
            ps.setInt(10, report.getBatteryLevel());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }
}
