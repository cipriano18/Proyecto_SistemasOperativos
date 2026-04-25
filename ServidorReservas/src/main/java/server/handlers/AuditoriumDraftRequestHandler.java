/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.AuditoriumDraftController;
import dto.AuditoriumDraftRequest;
import service.Response;

/**
 *
 * @author User
 */
public class AuditoriumDraftRequestHandler {

    public static Response handle(String command, Object obj) {

        AuditoriumDraftRequest request = (AuditoriumDraftRequest) obj;

        switch (command.toUpperCase()) {

            case "START_AUDITORIUM_DRAFT":
                return AuditoriumDraftController.startAuditoriumDraft(request);
              
       

            default:
                return new Response(false, "Comando de auditorio no reconocido", null);
        }
    }
}
