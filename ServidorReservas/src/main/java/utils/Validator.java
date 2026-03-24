/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author User
 */
public class Validator {
   
      public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
     
   // validar user_name 
   public static boolean isValidUsername(String username) {
        if (isEmpty(username)) return false;
        if (username.length() < 4) return false;
        if (username.length() > 50) return false;
        if (!username.matches("[a-zA-Z0-9_]+")) return false;
        return true;
    }
    
    //validar_password  
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        return true;
    }
    
    // validar cédula/identity card (9-20 caracteres alfanuméricos)
    public static boolean isValidIdentityCard(String identityCard) {
        if (isEmpty(identityCard)) return false;
        return identityCard.matches("^[A-Za-z0-9]{9,20}$");
    }

    // validar email
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
    
    // validar teléfono
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.matches("^[0-9]{8}$");
    }
    
    // validar primer nombre
    public static boolean isValidFName(String fName) {
        if (isEmpty(fName)) return false;
        return fName.matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$"); // solo letras y espacios, 2-50 caracteres
    }

    // validar primer apellido
    public static boolean isValidFSurname(String fSurname) {
        if (isEmpty(fSurname)) return false;
        return fSurname.matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$");
    }

    // validar segundo apellido
    public static boolean isValidMSurname(String mSurname) {
        if (isEmpty(mSurname)) return false; 
        return mSurname.matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$");
    }
     // Valida el valor de contacto según su tipo
    // Si tipo es PHONE → solo números, exactamente 8 dígitos
    // Si tipo es EMAIL → formato válido de correo

    public static boolean isValidContact(String type, String value) {
        if (isEmpty(type)) {
            return false;
        }
        if (isEmpty(value)) {
            return false;
        }

        switch (type.toUpperCase()) {
            case "PHONE":
                return isValidPhone(value); 
            case "EMAIL":
                return isValidEmail(value); 
            default:
                return value.trim().length() >= 2;
        }
    }
}
