package fr.rsommerard.global;

/**
 * Cette classe regroupe toutes les constantes utiles a l'application.
 */
public class Constants {
    /**
     * Squelette de la page qui liste les elements.
     */
    public static final String HTML_LIST_PAGE = "<!DOCTYPE><head><title>Passerelle-REST</title></head>" +
            "<html><h3>PATH</h3>CONTENT</html>";

    /**
     * Squelette de la page de connexion.
     */
    public static final String HTML_CONNECT_PAGE = "<!DOCTYPE><head><title>Passerelle-REST</title></head>" +
            "<html><h3>Connection</h3><form action=\"connect\" method=\"post\"><label for=\"login\">Login</label><input name=\"login\" type=\"text\" />" +
            "<br/><label for=\"password\">Password</label><input name=\"password\" type=\"password\" /><br/>" +
            "<input type=\"submit\" /></form></html>";

    /**
     * Squelette de creation de liens.
     */
    public static final String HTML_GET_LINK = "<a href=\"LINK_PATH\">LINK_NAME</a>";
}
