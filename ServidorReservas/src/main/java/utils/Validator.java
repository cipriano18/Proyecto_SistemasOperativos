package utils;

import java.util.ArrayList;
import java.util.List;
import model.Client;
import model.ClientRequest;
import model.Contact;
import model.User;

public class Validator {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        if (username.length() < 4) {
            return false;
        }
        if (username.length() > 50) {
            return false;
        }
        return username.matches("[a-zA-Z0-9_]+");
    }

    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        return true;
    }

    public static boolean isValidIdentityCard(String identityCard) {
        if (isEmpty(identityCard)) {
            return false;
        }
        return identityCard.matches("^[A-Za-z0-9]{9,20}$");
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return phone.matches("^[0-9]{8}$");
    }

    public static boolean isValidFName(String fName) {
        if (isEmpty(fName)) {
            return false;
        }
        return fName.matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$");
    }

    public static boolean isValidFSurname(String fSurname) {
        if (isEmpty(fSurname)) {
            return false;
        }
        return fSurname.matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$");
    }

    public static boolean isValidMSurname(String mSurname) {
        if (isEmpty(mSurname)) {
            return false;
        }
        return mSurname.matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$");
    }

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

    public static List<String> validateClientRequest(ClientRequest clientRequest) {
        List<String> errors = new ArrayList<>();

        if (clientRequest == null) {
            errors.add("La solicitud del cliente es obligatoria");
            return errors;
        }

        User user = clientRequest.getUser();
        Client client = clientRequest.getClient();
        Contact contact = clientRequest.getContact();

        if (user == null) {
            errors.add("El usuario es obligatorio");
        } else {
            if (isEmpty(user.getUsername())) {
                errors.add("El nombre de usuario es obligatorio");
            } else if (!isValidUsername(user.getUsername())) {
                errors.add("El nombre de usuario es inválido");
            }

            if (isEmpty(user.getPassword())) {
                errors.add("La contraseña es obligatoria");
            } else if (!isValidPassword(user.getPassword())) {
                errors.add("La contraseña es inválida");
            }
        }

        if (client == null) {
            errors.add("El cliente es obligatorio");
        } else {
            if (isEmpty(client.getfName())) {
                errors.add("El primer nombre es obligatorio");
            } else if (!isValidFName(client.getfName())) {
                errors.add("El primer nombre es inválido");
            }

            if (isEmpty(client.getfSurname())) {
                errors.add("El primer apellido es obligatorio");
            } else if (!isValidFSurname(client.getfSurname())) {
                errors.add("El primer apellido es inválido");
            }

            if (isEmpty(client.getmSurname())) {
                errors.add("El segundo apellido es obligatorio");
            } else if (!isValidMSurname(client.getmSurname())) {
                errors.add("El segundo apellido es inválido");
            }

            if (isEmpty(client.getIdentityCard())) {
                errors.add("La cédula es obligatoria");
            } else if (!isValidIdentityCard(client.getIdentityCard())) {
                errors.add("La cédula es inválida");
            }
        }

        if (contact == null) {
            errors.add("El contacto es obligatorio");
        } else {
            if (isEmpty(contact.getType())) {
                errors.add("El tipo de contacto es obligatorio");
            }

            if (isEmpty(contact.getContactValue())) {
                errors.add("El valor del contacto es obligatorio");
            } else if (!isValidContact(contact.getType(), contact.getContactValue())) {
                if ("PHONE".equalsIgnoreCase(contact.getType())) {
                    errors.add("El teléfono debe contener exactamente 8 dígitos");
                } else if ("EMAIL".equalsIgnoreCase(contact.getType())) {
                    errors.add("El correo debe tener un formato válido");
                } else {
                    errors.add("El valor del contacto es inválido");
                }
            }
        }

        return errors;
    }
}