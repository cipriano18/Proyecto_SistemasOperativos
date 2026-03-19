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

}
