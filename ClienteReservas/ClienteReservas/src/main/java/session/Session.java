package session;

import dto.ClientRequest;
import dto.AdminRequest;

/**
 * Mantiene el estado de sesion actual del usuario autenticado.
 */
public class Session {

    private static Session instance;

    private ClientRequest client;
    private AdminRequest admin;
    private int currentEquipmentDraftId;

    /**
     * Crea el contenedor de sesion.
     */
    private Session() {
    }

    /**
     * Obtiene la instancia compartida de la sesion.
     *
     * @return instancia unica de {@code Session}
     */
    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // ===== CLIENT =====
    public ClientRequest getClient() {
        return client;
    }

    public void setClient(ClientRequest client) {
        this.client = client;
        this.admin = null; // limpia admin
    }

    // ===== ADMIN / SUPER ADMIN =====
    public AdminRequest getAdmin() {
        return admin;
    }

    public void setAdmin(AdminRequest admin) {
        this.admin = admin;
        this.client = null; // limpia cliente
    }

    /**
     * Limpia la informacion de sesion actualmente almacenada.
     */
    public void clear() {
        this.client = null;
        this.admin = null;
    }

    /**
     * Indica si existe un usuario autenticado en la sesion.
     *
     * @return {@code true} si hay cliente o administrador autenticado
     */
    public boolean isLoggedIn() {
        return client != null || admin != null;
    }

    /**
     * Obtiene el rol del usuario autenticado actualmente.
     *
     * @return identificador del rol o {@code 0} si no hay sesion valida
     */
    public int getRole() {
        if (admin != null && admin.getUser() != null) {
            return admin.getUser().getIdRole();
        }
        if (client != null && client.getUser() != null) {
            return client.getUser().getIdRole();
        }
        return 0;
    }

    public int getCurrentEquipmentDraftId() {
        return currentEquipmentDraftId;
    }

    public void setCurrentEquipmentDraftId(int currentEquipmentDraftId) {
        this.currentEquipmentDraftId = currentEquipmentDraftId;
    }

}
