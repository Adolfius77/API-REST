package services;

import DTOS.profileDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import repos.ProfileRepo;

public class ProfileService {
    private final ProfileRepo repo;

    public ProfileService() {
        this.repo = new ProfileRepo();
    }

    public profileDTO agregar(profileDTO profile) throws SQLException {
        validar(profile);
        return repo.agregar(profile);
    }

    public profileDTO obtenerRandomPorCriterios(int edad, String pais, String genero) throws SQLException {
        List<profileDTO> candidatos = repo.buscarPorCriterios(edad, pais, genero);
        if (candidatos.isEmpty()) {
            return null;
        }
        if (candidatos.size() == 1) {
            return candidatos.get(0);
        }
        int indiceRandom = ThreadLocalRandom.current().nextInt(candidatos.size());
        return candidatos.get(indiceRandom);
    }

    private void validar(profileDTO profile) {
        if (profile == null) {
            throw new IllegalArgumentException("El perfil no puede ser nulo.");
        }
        if (esVacio(profile.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (profile.getEdad() <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a 0.");
        }
        if (esVacio(profile.getPais())) {
            throw new IllegalArgumentException("El país es obligatorio.");
        }
        if (esVacio(profile.getGenero())) {
            throw new IllegalArgumentException("El género es obligatorio.");
        }

        profile.setPais(normalizar(profile.getPais()));
        profile.setGenero(normalizar(profile.getGenero()));
    }

    private String normalizar(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT);
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
