package fr.rsommerard.services;

import org.springframework.stereotype.Service;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service FTP qui envoie les commandes au serveur FTP.
 */
@Service
public class FTPService {
    /**
     * Instance qui permet de communiquer avec le serveur.
     */
    private FTPClient client;

    /**
     * Connecte le client au serveur (anonymous).
     */
    public boolean connect() {
        try {
            this.client = new FTPClient();
            this.client.connect("localhost", 2121);
            return this.client.login("anonymous", "");
        }
        catch(Exception e) {
            return false;
        }
    }

    /**
     * Connecte le client au serveur avec son login/password.
     */
    public boolean connect(String login, String password) {
        try {
            this.client = new FTPClient();
            this.client.connect("localhost", 2121);
            return this.client.login(login, password);
        }
        catch(Exception e) {
            return false;
        }
    }

    /**
     * Deconnecte le client du serveur.
     */
    public boolean disconnect() {
        try {
            this.client.disconnect();
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    /**
     * Supprime le fichier passe en parametre du serveur.
     */
    public boolean deleteFile(String filename) {
        try {
            return this.client.deleteFile(filename);
        }
        catch(Exception e) {
            return false;
        }
    }

    /**
     * Renvoie l'InputStream pour le telechargement de fichier.
     */
    public InputStream getFile(String filename) {
        try {
            return this.client.retrieveFileStream(filename);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Retourne la liste des fichiers.
     */
    public String[] getList() {
        String[] list = new String[0];
        try {
            list = this.client.listNames();
        } catch (IOException e) {
            // Nothing here, return an empty list if exception.
        }

        return list;
    }

    /**
     * Change le repertoire courant du serveur.
     */
    public boolean changeWorkingDirectory(String path) {
        try {
            return this.client.changeWorkingDirectory(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Retourne le repertoire courant.
     */
    public String printWorkingDirectory() {
        try {
            return this.client.printWorkingDirectory();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Retourne l'etat de la connexion entre le client et le serveur.
     */
    public boolean isConnected() {
        if(this.client == null) {
            return false;
        }

        return this.client.isConnected();
    }
}
