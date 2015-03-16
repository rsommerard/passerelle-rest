Passerelle REST en Java
Romain SOMMERARD
15/03/15

# Introduction

Ce projet est une implémentation de passerelle REST qui fait le lien entre un navigateur web et un serveur FTP.
La passerelle permet de se connecter au serveur avec un login/password, de lister les éléments d'un dossier, de télécharger des fichiers et d'en supprimer.

Le serveur utilisé est disponible en suivant ce lien:
> http://mina.apache.org/ftpserver-project/index.html

# Informations
## Utilisateurs disponibles

Pour se connecter au serveur, deux comptes existent (login:motDePasse):

- admin:admin
- anonymous:


## Comment tester l'application ?

### Lancer le serveur FTP

Aller dans le dossier server:

```
cd server/
```

Lancer le serveur avec la configuration "typical":

```
bin/ftpd.sh res/conf/ftpd-typical.xml
```

### Lancer la passerelle

```
java -jar Passerelle-REST.jar
```

L'URL de base du serveur est celle-ci:
> http://0.0.0.0:8080/rest/api/ftp/

# Architecture

## API REST

| Méthode HTTP | URL | Description |
|--------------|-----|-------------|
| GET | /connect | Permet d'accéder au formulaire de connexion. |
| POST | /connect | Effectue la connexion avec login/password du formulaire. |
| GET | /disconnect | Effectue la deconnexion. |
| GET | /list | Retourne la liste des fichiers de la racine du serveur. |
| GET | /list/*\<path\>* | Retourne la liste les fichiers du *\<path\>*. |
| GET | /download/*\<path\>* | Télécharge le fichier *\<path\>*. |
| GET | /delete/*\<path\>* | Supprime le fichier *\<path\>*. |
| DELETE | /delete/*\<path\>* | Supprime le fichier *\<path\>*. |

## Packages

| Nom | Description |
|-----|-------------|
| config | Ce package contient la classe de configuration. |
| global | Ce package contient la classe de variables globales. |
| rs | Ce package contient les services REST qui permettent de définir les routes. |
| services | Ce package contient les services qui effectuent les traitements. |

# Code Samples

- Classe FTPRestService, traitement de la connexion au serveur FTP via le FTPService.

```java
    @Produces(MediaType.TEXT_HTML)
    @Path("/connect")
    @POST
    public Response connect(@FormParam("login") String login, @FormParam("password") String password) {
        // Si la connexion est ok, on renvoie un code 200.
        if(this.ftpService.connect(login, password)) {
            return Response.ok("Connection: OK").build();
        }

        // Sinon Forbidden.
        return Response.status(Response.Status.FORBIDDEN).build();
    }
```

- Classe FTPRestService, création de la liste des fichiers au format HTML.

```java
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
```

- Classe FTPService, récupération de la liste. Si une exception est levée, on retourne une liste vide.

```java
    public String[] getList() {
        String[] list = new String[0];
        try {
            list = this.client.listNames();
        } catch (IOException e) {
            // Nothing here, return an empty list if exception.
        }

        return list;
    }
```