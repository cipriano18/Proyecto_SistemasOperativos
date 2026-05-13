/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 * Reune constantes usadas para identificar secciones y estados del calendario.
 */
public class CalendarConstants {

    public static final int SECTION_MORNING = 1;
    public static final int SECTION_AFTERNOON = 2;
    public static final int SECTION_NIGHT = 3;

    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_RESERVED = "RESERVED";
    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_OWN_DRAFT = "OWN_DRAFT";

    /**
     * Evita la creacion de instancias de esta clase utilitaria.
     */
    private CalendarConstants() {
        // Evita que se creen objetos de esta clase
    }
}
