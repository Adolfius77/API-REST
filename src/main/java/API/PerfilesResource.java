/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package API;

import DTOS.profileDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
            return jsonResponse(Response.Status.BAD_REQUEST,
                    Map.of("error", "Parámetros requeridos: edad, pais, genero."));
        }
        try {
            profileDTO resultado = profileService.obtenerRandomPorCriterios(edad, pais, genero);
            if (resultado == null) {
                return jsonResponse(Response.Status.NOT_FOUND,
                        Map.of("message", "No se encontraron perfiles con al menos 2 criterios."));
            }
            return jsonResponse(Response.Status.OK, resultado);
        } catch (SQLException e) {
            logger.severe("Error consultando perfiles: " + e.getMessage());
            return jsonResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error de base de datos."));
        }
    }

    @POST
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postJson(String body) {
        try {
            profileDTO content = OBJECT_MAPPER.readValue(body, profileDTO.class);
            profileDTO guardado = profileService.agregar(content);
            logger.info("Perfil agregado: " + guardado.getNombre());
            return jsonResponse(Response.Status.CREATED, guardado);
        } catch (JsonProcessingException e) {
            return jsonResponse(Response.Status.BAD_REQUEST,
                    Map.of("error", "JSON inválido en el body."));
        } catch (IllegalArgumentException e) {
            return jsonResponse(Response.Status.BAD_REQUEST,
                    Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            logger.severe("Error insertando perfil: " + e.getMessage());
            return jsonResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error de base de datos."));
        }
    }

    private Response jsonResponse(Response.Status status, Object body) {
        try {
            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(OBJECT_MAPPER.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\":\"Error serializando respuesta.\"}")
                    .build();
        }
    }
}
