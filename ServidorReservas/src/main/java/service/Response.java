package service;

import java.io.Serializable;

/**
 * Representa una respuesta enviada entre cliente y servidor.
 */
public class Response implements Serializable {

    private boolean success;

    private String message;

    private Object data;

    /**
     * Crea una respuesta vacía.
     */
    public Response() {
    }

    /**
     * Crea una respuesta con estado, mensaje y datos.
     *
     * @param success indica si la operación fue exitosa
     * @param message mensaje descriptivo de la respuesta
     * @param data datos asociados a la respuesta
     */
    public Response(
            boolean success,
            String message,
            Object data
    ) {

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