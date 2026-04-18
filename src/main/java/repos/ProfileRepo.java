package repos;

import DTOS.profileDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.ConexionBD;

public class ProfileRepo {

    public ProfileRepo() {
        try {
            crearTablaSiNoExiste();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo inicializar la tabla profiles", e);
        }
    }

    public profileDTO agregar(profileDTO profile) throws SQLException {
        String sql = "INSERT INTO profiles (nombre, edad, pais, genero) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, profile.getNombre());
            stmt.setInt(2, profile.getEdad());
            stmt.setString(3, profile.getPais());
            stmt.setString(4, profile.getGenero());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    profile.setId(rs.getLong(1));
                }
            }
            return profile;
        }
    }

    public List<profileDTO> buscarPorCriterios(int edad, String pais, String genero) throws SQLException {
        String sql = "SELECT id, nombre, edad, pais, genero "
                + "FROM profiles "
                + "WHERE ("
                + " (CASE WHEN edad = ? THEN 1 ELSE 0 END) +"
                + " (CASE WHEN LOWER(pais) = LOWER(?) THEN 1 ELSE 0 END) +"
                + " (CASE WHEN LOWER(genero) = LOWER(?) THEN 1 ELSE 0 END)"
                + ") >= 2";

        List<profileDTO> resultados = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, edad);
            stmt.setString(2, pais);
            stmt.setString(3, genero);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profileDTO dto = new profileDTO();
                    dto.setId(rs.getLong("id"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setEdad(rs.getInt("edad"));
                    dto.setPais(rs.getString("pais"));
                    dto.setGenero(rs.getString("genero"));
                    resultados.add(dto);
                }
            }
        }
        return resultados;
    }

    private void crearTablaSiNoExiste() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS profiles ("
                + "id BIGINT PRIMARY KEY AUTO_INCREMENT, "
                + "nombre VARCHAR(120) NOT NULL, "
                + "edad INT NOT NULL, "
                + "pais VARCHAR(120) NOT NULL, "
                + "genero VARCHAR(50) NOT NULL"
                + ")";

        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
        }
    }
}
