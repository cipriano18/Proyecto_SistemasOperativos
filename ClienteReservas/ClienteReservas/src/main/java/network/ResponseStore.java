/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import service.Response;

/**
 * Almacena temporalmente la ultima respuesta recibida del servidor.
 */
public class ResponseStore {

    private static Response response;

    /**
     * Guarda una respuesta y despierta a los hilos en espera.
     *
     * @param resp respuesta recibida desde el servidor
     */
    public static synchronized void setResponse(Response resp) {
        response = resp;
        ResponseStore.class.notifyAll();
    }

    /**
     * Espera hasta que exista una respuesta disponible y la devuelve.
     *
     * @return respuesta almacenada
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     */
    public static synchronized Response waitResponse() 
            throws InterruptedException {
        
        while (response == null) {
            ResponseStore.class.wait();
        }

        Response temp = response;
        response = null;
        return temp;
    }
}
