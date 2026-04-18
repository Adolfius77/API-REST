/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package API;

import DTOS.profileDTO;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Path;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;
import services.ProfileService;

/**
 * REST Web Service
 *
 * @author USER
 */
@Path("perfiles")
@RequestScoped
public class PerfilesResource {

    @Context
    private UriInfo context;
    private static final Logger logger = Logger.getLogger(PerfilesResource.class.getName());
    private final ProfileService profileService = new ProfileService();
    /**
     * Creates a new instance of PerfilesResource
     */
    public PerfilesResource() {
    }

    /**
     * Retrieves representation of an instance of API.PerfilesResource
     * @return an instance of DTOS.profileDTO
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson(@QueryParam("edad") Integer edad,
            @QueryParam("pais") String pais,
            @QueryParam("genero") String genero) {
        if (edad == null || pais == null || genero == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Parámetros requeridos: edad, pais, genero."))
                    .build();
        }
        try {
            profileDTO resultado = profileService.obtenerRandomPorCriterios(edad, pais, genero);
            if (resultado == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "No se encontraron perfiles con al menos 2 criterios."))
                        .build();
            }
            return Response.ok(resultado).build();
        } catch (SQLException e) {
            logger.severe("Error consultando perfiles: " + e.getMessage());
            return Response.serverError().entity(Map.of("error", "Error de base de datos.")).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postJson(profileDTO content) {
        try {
            profileDTO guardado = profileService.agregar(content);
            logger.info("Perfil agregado: " + guardado.getNombre());
            return Response.status(Response.Status.CREATED).entity(guardado).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", e.getMessage())).build();
        } catch (SQLException e) {
            logger.severe("Error insertando perfil: " + e.getMessage());
            return Response.serverError().entity(Map.of("error", "Error de base de datos.")).build();
        }
    }
}
