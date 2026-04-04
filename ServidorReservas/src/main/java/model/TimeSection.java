/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.Serializable;

/**
 *
 * @author Cipriano
 */
public class TimeSection implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idSection;
    private String name;

    public TimeSection() {
    }

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
