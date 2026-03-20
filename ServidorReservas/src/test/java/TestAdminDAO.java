/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import database.AdminDAO;
import model.Admin;
import model.User;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Reyner
 */
public class TestAdminDAO {
    
public static void main(String[] args) {
    /*
    // 1. Crear un usuario válido
        User user = new User(
                0,                  // id_user (se genera automáticamente)
                1,                  // id_role (ejemplo: 1 = administrador)
                "admin_valid01",    // username válido
                "Password123",      // password válido (>=8, con mayúscula y número)
                "A"                 // status (Activo)
        );

        // 2. Crear un administrador válido
        Admin admin = new Admin(
                0,                  // id_admin (se genera automáticamente)
                0,                  // id_user (se asigna después del insert)
                "María",            // fName
                "José",             // mName
                "Ramírez",          // fSurname
                "López",            // mSurname
                "987654321"         // identityCard único
        );

        // 3. Crear lista de contactos válidos
        List<String[]> contacts = new ArrayList<>();
        contacts.add(new String[]{"email", "maria.ramirez@example.com"});
        contacts.add(new String[]{"phone", "8888-1111"});

        // 4. Insertar administrador con usuario y contactos
        boolean resultado = AdminDAO.insertAdmin(admin, user, contacts);

        // 5. Validar que se insertó correctamente
        assertTrue(resultado, "El administrador debería insertarse correctamente con datos válidos.");
        */
    
        // 6. Probar obtener lista de administradores
        System.out.println("Lista de administradores:");
        AdminDAO.getAllAdmins().forEach(a -> {
            System.out.println(a.getIdAdmin() + " - " + a.getFName() + " " + a.getFSurname());
        });
/*
        // 6. Probar soft delete
        boolean eliminado = AdminDAO.softDeleteAdmin(1); // ejemplo: inactivar admin con id_admin=1
        if (eliminado) {
            System.out.println("Administrador inactivado correctamente.");
        } else {
            System.out.println("Error al inactivar administrador.");
        }
*/
    }
}
