package fr.rsommerard.rs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Permet de tester le fonctionnement du serveur.
 */
@Path( "/status" )
public class StatusRestService {
    /**
     * Retourne une page html ok si le serveur fonctionne.
     */
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response get() {
        return Response.ok("Status: OK").build();
    }
}
