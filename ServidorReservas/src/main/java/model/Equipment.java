/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Cipriano
 */
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idEquipment;
    private String name;
    private int availableQuantity; 

    public Equipment() {
    }

    public Equipment(int idEquipment, String name, int availableQuantity) {
        this.idEquipment = idEquipment;
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    public Equipment(String name, int availableQuantity) {
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    public int getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(int idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return "Equipment{" + "idEquipment=" + idEquipment + ", name=" + name + ", availableQuantity=" + availableQuantity + '}';
    }
}
