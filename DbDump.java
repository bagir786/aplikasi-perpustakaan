import java.sql.*;

public class DbDump {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/sistem_perpustakaan";
            String user = "root";
            String password = "";

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("=== CONNECTION SUCCESSFUL ===");
                
                String[] tables = {"anggota", "buku", "peminjaman", "detail_peminjaman", "pengembalian", "denda"};
                for (String table : tables) {
                    System.out.println("\n--- TABLE: " + table + " ---");
                    try (Statement stmt = conn.createStatement()) {
                        // Print columns and their types
                        try (ResultSet rs = stmt.executeQuery("DESCRIBE `" + table + "`")) {
                            System.out.printf("%-20s | %-20s | %-10s | %-10s%n", "Field", "Type", "Null", "Key");
                            System.out.println("------------------------------------------------------------------");
                            while (rs.next()) {
                                System.out.printf("%-20s | %-20s | %-10s | %-10s%n",
                                    rs.getString("Field"),
                                    rs.getString("Type"),
                                    rs.getString("Null"),
                                    rs.getString("Key")
                                );
                            }
                        }

                        // Print rows (up to 5)
                        try (ResultSet rs = stmt.executeQuery("SELECT * FROM `" + table + "` LIMIT 5")) {
                            ResultSetMetaData meta = rs.getMetaData();
                            int colCount = meta.getColumnCount();
                            System.out.println("Data:");
                            for (int i = 1; i <= colCount; i++) {
                                System.out.print(meta.getColumnName(i) + "\t");
                            }
                            System.out.println("\n------------------------------------------------------------------");
                            while (rs.next()) {
                                for (int i = 1; i <= colCount; i++) {
                                    System.out.print(rs.getObject(i) + "\t");
                                }
                                System.out.println();
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Error reading table " + table + ": " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
