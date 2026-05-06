/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import service.Response;

/**
 *
 * @author cipriano
 */
public class ResponseStore {

    private static Response response;

    public static synchronized void setResponse(Response resp) {
        response = resp;
        ResponseStore.class.notifyAll();
    }

    public static synchronized Response waitResponse() throws InterruptedException {
        while (response == null) {
            ResponseStore.class.wait();
        }

        Response temp = response;
        response = null;
        return temp;
    }
}