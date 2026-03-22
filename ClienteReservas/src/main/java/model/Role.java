package model;

import java.io.Serializable;

public class Role implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idRole;
    private String name;

    public Role() {}

    public Role(int idRole, String name) {
        this.idRole = idRole;
        this.name = name;
    }

    public int getIdRole() { return idRole; }
    public void setIdRole(int idRole) { this.idRole = idRole; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Role{id=" + idRole + ", name=" + name + "}";
    }
}