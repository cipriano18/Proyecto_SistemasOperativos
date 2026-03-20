package controller;

import database.ContactDAO;
import model.Contact;
import utils.Validator;

public class ContactController {
    
    public static String createContact(Contact contact) {
        // Validaciones de contactos
        if (contact.getType().equalsIgnoreCase("email") && !Validator.isValidEmail(contact.getContactValue())) {
            return "ERROR: El correo debe tener un formato válido (ejemplo: usuario@dominio.com).";          
        }

        if (contact.getType().equalsIgnoreCase("phone") && !Validator.isValidPhone(contact.getContactValue())) {
            return "ERROR: El número de teléfono debe contener exactamente 8 dígitos.";          
        }
        
        boolean insertedContact = ContactDAO.insertContact(contact);
        if (insertedContact) {
            return "SUCCESS: Contacto creado correctamente.";
        }
        return "ERROR:No se pudo crear el contacto";
    }

}
