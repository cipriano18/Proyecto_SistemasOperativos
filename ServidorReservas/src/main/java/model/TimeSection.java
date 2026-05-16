package Model;

import java.io.Serializable;

/**
 * Representa una sección horaria dentro del sistema.
 */
public class TimeSection implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idSection;
    private String name;

    /**
     * Crea una sección horaria vacía.
     */
    public TimeSection() {
    }

    /**
     * Crea una sección horaria con su identificador y nombre.
     *
     * @param idSection identificador de la sección
     * @param name nombre de la sección
     */
    public TimeSection(int idSection, String name) {
        this.idSection = idSection;
        this.name = name;
    }

    public int getIdSection() {
        return idSection;
    }

    public void setIdSection(int idSection) {
        this.idSection = idSection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}