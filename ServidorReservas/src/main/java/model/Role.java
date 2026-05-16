package model;

import java.io.Serializable;

/**
 * Representa un rol dentro del sistema.
 */
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idRole;
    private String name;

    /**
     * Crea un rol vacío.
     */
    public Role() {
    }

    /**
     * Crea un rol con su identificador y nombre.
     *
     * @param idRole identificador único del rol
     * @param name nombre del rol
     */
    public Role(int idRole, String name) {
        this.idRole = idRole;
        this.name = name;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Devuelve la información del rol en formato texto.
     *
     * @return representación textual del rol
     */
    @Override
    public String toString() {
        return "Role{id=" + idRole + ", name=" + name + "}";
    }
}