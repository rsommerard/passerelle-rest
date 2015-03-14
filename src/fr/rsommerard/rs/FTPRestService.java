package fr.rsommerard.rs;

import com.sun.tools.internal.jxc.apt.Const;
import fr.rsommerard.global.Constants;
import fr.rsommerard.services.FTPService;
import org.apache.commons.net.ftp.FTPFile;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Service principal de la passerelle. Definit les actions en fonction des routes.
 */
@Path( "/ftp" )
public class FTPRestService {
    /**
     * Service FTP effectuant les actions comme le listing ...
     */
    @Inject
    private FTPService ftpService;

    /**
     * Renvoie la page html de connexion.
     */
    @Produces(MediaType.TEXT_HTML)
    @Path("/connect")
    @GET
    public Response connect() {
        return Response.ok(Constants.HTML_CONNECT_PAGE).build();
    }

    /**
     * Recupere les parametres de connexion et connecte le client.
     */
    @Produces(MediaType.TEXT_HTML)
    @Path("/connect")
    @POST
    public Response connect(@FormParam("login") String login, @FormParam("password") String password) {
        if(this.ftpService.connect(login, password)) {
            return Response.ok("Connection: OK").build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    /**
     * Permet de telecharger un fichier.
     */
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/download/{path: .*}")
    @GET
    public Response getFile(@PathParam("path") String path) {
        if(!this.ftpService.isConnected()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        InputStream is = this.ftpService.getFile("/" + path);

        if(is == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(is).build();
    }

    /**
     * Permet de supprimer un fichier en get (via les liens de l'interface).
     */
    @Produces(MediaType.TEXT_HTML)
    @Path("/delete/{path: .*}")
    @GET
    public Response deleteFile(@PathParam("path") String path) {
        if(!this.ftpService.isConnected()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if(!this.ftpService.deleteFile("/" + path)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok("File Deleted.").build();
    }

    /**
     * Permet de supprimer un fichier en delete.
     */
    @Produces(MediaType.TEXT_HTML)
    @Path("/delete/{path: .*}")
    @DELETE
    public Response deleteFileWithDelete(@PathParam("path") String path) {
        if(!this.ftpService.isConnected()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if(!this.ftpService.deleteFile("/" + path)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok("File Deleted.").build();
    }

    /**
     * Permet de lister les elements du path.
     */
    @Produces(MediaType.TEXT_HTML)
    @Path("/list/{path: .*}")
    @GET
    public Response getListWithPath(@PathParam( "path" ) String path) {
        if(!this.ftpService.isConnected()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String content = this.getList("/" + path);

        if(content == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        content = Constants.HTML_LIST_PAGE.replace("CONTENT", content);
        content = content.replace("PATH", this.ftpService.printWorkingDirectory());

        return Response.ok(content).build();
    }

    /**
     * Permet de lister les elements de la racine.
     */
    @Produces(MediaType.TEXT_HTML)
    @Path("/list")
    @GET
    public Response getListWithoutPath() {
        if(!this.ftpService.isConnected()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String content = this.getList("/");

        if(content == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        content = Constants.HTML_LIST_PAGE.replace("CONTENT", content);
        content = content.replace("PATH", this.ftpService.printWorkingDirectory());

        return Response.ok(content).build();
    }

    /**
     * Retourne la liste des elements au format HTML.
     */
    private String getList(String path) {
        if(!this.ftpService.changeWorkingDirectory(path)) {
            return null;
        }

        String list = "<ul>";
        list += "<li>";
        list += Constants.HTML_GET_LINK.replace("LINK_NAME", "..");

        String parent = "";
        for(String element : path.split("/")) {
            if(!path.endsWith(element)) {
                parent += "/" + element;
            }
        }

        list = list.replace("LINK_PATH", "/rest/api/ftp/list" + parent);
        list += "</li>";

        String[] files = this.ftpService.getList();

        for(String file : files) {
            list += "<li>";
            if(!file.contains(".")) {
                // Directory
                list += Constants.HTML_GET_LINK.replace("LINK_NAME", file)
                        .replace("LINK_PATH", "/rest/api/ftp/list" + (path.equals("/") ? "" : "/" + path) + "/" + file);
            }
            else {
                // File
                list += file;
                list += " (" + Constants.HTML_GET_LINK.replace("LINK_NAME", "download")
                        .replace("LINK_PATH", "/rest/api/ftp/download" +
                                (path.equals("/") ? "" : "/" + path) + "/" + file) + ", ";
                list += Constants.HTML_GET_LINK.replace("LINK_NAME", "delete")
                        .replace("LINK_PATH", "/rest/api/ftp/delete" +
                                (path.equals("/") ? "" : "/" + path) + "/" + file) + ")";
            }

            list += "</li>";
        }

        list += "</ul>";

        return list;
    }
}