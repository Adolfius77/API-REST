/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexionBD {
    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String BASE_DATOS = System.getenv().getOrDefault("DB_NAME", "profiles_db");
    private static final String USUARIO = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "123");
    private static final String URL_BASE = "jdbc:mysql://" + HOST + ":" + PORT
            + "/?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + BASE_DATOS
            + "?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            crearBaseDatosSiNoExiste();
        } catch (ClassNotFoundException e) {
            System.err.println("Error cargando driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error creando base de datos: " + e.getMessage());
        }
    }
    
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
    
    private static void crearBaseDatosSiNoExiste() throws SQLException {
        try (Connection conexion = DriverManager.getConnection(URL_BASE, USUARIO, PASSWORD);
             var statement = conexion.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS " + BASE_DATOS);
        }
    }
}
