/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idEquipment;
    private String name;
    private int totalQuantity;

    public Equipment() {
    }

    public Equipment(int idEquipment, String name, int totalQuantity) {
        this.idEquipment = idEquipment;
        this.name = name;
        this.totalQuantity = totalQuantity;
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

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public String toString() {
        return "Equipment{" + "idEquipment=" + idEquipment + ", name=" + name + ", totalQuantity=" + totalQuantity + '}';
    }
    
}
