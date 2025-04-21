package sio.gestionticket.Model;

public class User {
    // Attributs existants
    private int idUser;
    private String nomUser;
    private String statutUser;

    // Attributs manquants
    private String prenomUser;
    private String loginUser;
    private String pwdUser;

    // Constructeur existant pour la liste des utilisateurs (ne pas supprimer)
    public User(int idUser, String nomUser, String statutUser) {
        this.idUser = idUser;
        this.nomUser = nomUser;
        this.statutUser = statutUser;
    }

    // Constructeur complet pour l'authentification
    public User(int idUser, String nomUser, String prenomUser, String loginUser, String pwdUser, String statutUser) {
        this.idUser = idUser;
        this.nomUser = nomUser;
        this.prenomUser = prenomUser;
        this.loginUser = loginUser;
        this.pwdUser = pwdUser;
        this.statutUser = statutUser;
    }

    // Getters existants
    public int getIdUser() {
        return idUser;
    }

    public String getNomUser() {
        return nomUser;
    }

    public String getStatutUser() {
        return statutUser;
    }

    // Nouveaux getters
    public String getPrenomUser() {
        return prenomUser;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public String getPwdUser() {
        return pwdUser;
    }
}