package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static final String URL  = "jdbc:mysql://localhost:3306/db_inventori";
    private static final String USER = "root";
    private static final String PASS = ""; // sesuaikan dengan password MySQL Anda

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Koneksi ke database db_inventori GAGAL!\n"
                + "Pastikan MySQL (XAMPP) sudah menyala.\n\nDetail: " + e.getMessage(),
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}