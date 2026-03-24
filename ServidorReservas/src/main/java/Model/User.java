package model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idUser;
    private int idRole;
    private String username;
    private String password;
    private String status;

    public User() {
    }

    public User(int idUser, int idRole, String username, String password, String status) {
        this.idUser = idUser;
        this.idRole = idRole;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public User(int idRole, String username, String password, String status) {
        this.idRole = idRole;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  idUser + " " + username + " " + idRole;
    }

   
}
