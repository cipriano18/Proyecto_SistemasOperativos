/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.Serializable;

/**
 * Representa el resultado general de una operacion solicitada al servidor.
 */
public class Response implements Serializable {
    
    
    private boolean success;
    private String message;
    private Object data;

    /**
     * Crea una respuesta vacia.
     */
    public Response() {
    }

    /**
     * Crea una respuesta con su estado, mensaje y datos asociados.
     *
     * @param success indica si la operacion fue exitosa
     * @param message mensaje descriptivo del resultado
     * @param data carga de datos devuelta por la operacion
     */
    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}

