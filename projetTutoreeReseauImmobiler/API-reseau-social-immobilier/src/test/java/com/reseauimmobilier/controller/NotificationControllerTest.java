package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.Notification;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification1;
    private Notification notification2;
    private Utilisateur expediteur;
    private Utilisateur destinataire;

    @BeforeEach
    void setUp() {
        // Création des utilisateurs de test
        expediteur = new Utilisateur();
        expediteur.setIdUtilisateur(1);
        expediteur.setNom("John Expediteur");
        expediteur.setEmail("expediteur@test.com");

        destinataire = new Utilisateur();
        destinataire.setIdUtilisateur(2);
        destinataire.setNom("Jane Destinataire");
        destinataire.setEmail("destinataire@test.com");

        // Création des notifications de test
        notification1 = new Notification();
        notification1.setIdNotification(1);
        notification1.setType(Notification.Type.PAIEMENT);
        notification1.setContenu("Nouveau paiement reçu");
        notification1.setDateCreation(LocalDateTime.now());
        notification1.setLu(false);
        notification1.setExpediteur(expediteur);
        notification1.setDestinataire(destinataire);

        notification2 = new Notification();
        notification2.setIdNotification(2);
        notification2.setType(Notification.Type.VISITE);
        notification2.setContenu("Demande de visite");
        notification2.setDateCreation(LocalDateTime.now());
        notification2.setLu(true);
        notification2.setExpediteur(expediteur);
        notification2.setDestinataire(destinataire);
    }

    @Test
    void testCreerNotification_Success() throws Exception {
        // Arrange
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "PAIEMENT");
        notificationData.put("contenu", "Nouveau paiement");
        notificationData.put("expediteurId", 1);
        notificationData.put("destinataireId", 2);

        when(notificationService.creerNotification(
                eq(Notification.Type.PAIEMENT),
                eq("Nouveau paiement"),
                any(), // expediteur
                any()) // destinataire
        ).thenReturn(notification1);

        // Act & Assert
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNotification").value(1))
                .andExpect(jsonPath("$.type").value("PAIEMENT"))
                .andExpect(jsonPath("$.contenu").value("Nouveau paiement reçu"));

        verify(notificationService).creerNotification(
                eq(Notification.Type.PAIEMENT),
                eq("Nouveau paiement"),
                any(),
                any()
        );
    }


    @Test
    void testCreerNotification_BadRequest() throws Exception {
        // Arrange : type invalide (non reconnu par l'enum)
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "INVALID_TYPE");
        notificationData.put("contenu", "Texte quelconque");

        // Pas besoin de mocker notificationService ici : la validation échoue AVANT l'appel au service

        // Act & Assert
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Type de notification invalide"));

        // On vérifie que le service n'est jamais appelé dans ce cas
        verify(notificationService, never()).creerNotification(any(), anyString(), any(), any());
    }


    @Test
    void testObtenirNotificationsUtilisateur_Success() throws Exception {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.obtenirNotificationsUtilisateur(2))
                .thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idNotification").value(1))
                .andExpect(jsonPath("$[1].idNotification").value(2));

        verify(notificationService).obtenirNotificationsUtilisateur(2);
    }

    @Test
    void testObtenirNotificationsUtilisateur_BadRequest() throws Exception {
        // Arrange
        when(notificationService.obtenirNotificationsUtilisateur(999))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testObtenirNotificationsNonLues_Success() throws Exception {
        // Arrange
        List<Notification> notificationsNonLues = Arrays.asList(notification1);
        when(notificationService.obtenirNotificationsNonLues(2))
                .thenReturn(notificationsNonLues);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/2/non-lues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lu").value(false));

        verify(notificationService).obtenirNotificationsNonLues(2);
    }

    @Test
    void testCompterNotificationsNonLues_Success() throws Exception {
        // Arrange
        when(notificationService.compterNotificationsNonLues(2)).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/2/count-non-lues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        verify(notificationService).compterNotificationsNonLues(2);
    }

    @Test
    void testCompterNotificationsNonLues_BadRequest() throws Exception {
        // Arrange
        when(notificationService.compterNotificationsNonLues(999))
                .thenThrow(new RuntimeException("Erreur"));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/999/count-non-lues"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMarquerCommeLue_Success() throws Exception {
        // Arrange
        notification1.setLu(true);
        when(notificationService.marquerCommeLue(1)).thenReturn(notification1);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/1/lu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNotification").value(1))
                .andExpect(jsonPath("$.lu").value(true));

        verify(notificationService).marquerCommeLue(1);
    }

    @Test
    void testMarquerCommeLue_NotFound() throws Exception {
        // Arrange
        when(notificationService.marquerCommeLue(999))
                .thenThrow(new RuntimeException("Notification non trouvée"));

        // Act & Assert
        mockMvc.perform(put("/api/notifications/999/lu"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMarquerToutesCommeLues_Success() throws Exception {
        // Arrange
        doNothing().when(notificationService).marquerToutesCommeLues(2);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/2/marquer-toutes-lues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Toutes les notifications ont été marquées comme lues"));

        verify(notificationService).marquerToutesCommeLues(2);
    }

    @Test
    void testMarquerToutesCommeLues_BadRequest() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Erreur")).when(notificationService).marquerToutesCommeLues(999);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/999/marquer-toutes-lues"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testObtenirNotificationParId_Success() throws Exception {
        // Arrange
        when(notificationService.obtenirNotificationParId(1))
                .thenReturn(Optional.of(notification1));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNotification").value(1))
                .andExpect(jsonPath("$.type").value("PAIEMENT"));

        verify(notificationService).obtenirNotificationParId(1);
    }

    @Test
    void testObtenirNotificationParId_NotFound() throws Exception {
        // Arrange
        when(notificationService.obtenirNotificationParId(999))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/notifications/detail/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenirNotificationsParType_Success() throws Exception {
        // Arrange
        List<Notification> notificationsPaiement = Arrays.asList(notification1);

        // Le contrôleur attend un Notification.Type, donc on prépare le bon appel mock
        when(notificationService.obtenirNotificationsParType(2, Notification.Type.PAIEMENT))
                .thenReturn(notificationsPaiement);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/2/type/PAIEMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("PAIEMENT"));

        // Vérifie que le service a été appelé avec les bons arguments
        verify(notificationService).obtenirNotificationsParType(2, Notification.Type.PAIEMENT);
    }



    @Test
    void testObtenirNotificationsParType_BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notifications/2/type/INVALID_TYPE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Type de notification invalide"));

        // Vérifie que le service n'est jamais appelé
        verify(notificationService, never()).obtenirNotificationsParType(anyInt(), any());
    }


    @Test
    void testSupprimerNotification_Success() throws Exception {
        // Arrange
        doNothing().when(notificationService).supprimerNotification(1);

        // Act & Assert
        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification supprimée avec succès"));

        verify(notificationService).supprimerNotification(1);
    }

    @Test
    void testSupprimerNotification_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Notification non trouvée"))
                .when(notificationService).supprimerNotification(999);

        // Act & Assert
        mockMvc.perform(delete("/api/notifications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenirToutesLesNotifications_Success() throws Exception {
        // Arrange
        List<Notification> toutesNotifications = Arrays.asList(notification1, notification2);
        when(notificationService.obtenirToutesLesNotifications())
                .thenReturn(toutesNotifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/admin/toutes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(notificationService).obtenirToutesLesNotifications();
    }

    @Test
    void testObtenirToutesLesNotifications_BadRequest() throws Exception {
        // Arrange
        when(notificationService.obtenirToutesLesNotifications())
                .thenThrow(new RuntimeException("Erreur serveur"));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/admin/toutes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCompterToutesNotificationsNonLues_Success() throws Exception {
        // Arrange
        when(notificationService.compterToutesNotificationsNonLues()).thenReturn(10L);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/admin/count-total-non-lues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNonLues").value(10));

        verify(notificationService).compterToutesNotificationsNonLues();
    }

    @Test
    void testCompterToutesNotificationsNonLues_BadRequest() throws Exception {
        // Arrange
        when(notificationService.compterToutesNotificationsNonLues())
                .thenThrow(new RuntimeException("Erreur"));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/admin/count-total-non-lues"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTesterNotificationPaiement_Success() throws Exception {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("montant", 100000);
        data.put("titreAnnonce", "Studio à Douala");

        // Act & Assert
        mockMvc.perform(post("/api/notifications/test/paiement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notifications de paiement créées"));
    }

    @Test
    void testTesterNotificationPaiement_BadRequest() throws Exception {
        // Arrange - données invalides
        Map<String, Object> data = new HashMap<>();
        data.put("montant", "invalid"); // Type incorrect

        // Act & Assert
        mockMvc.perform(post("/api/notifications/test/paiement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    // Tests d'intégration pour vérifier les endpoints avec différents paramètres

    @Test
    void testMultiplesNotificationsParUtilisateur() throws Exception {
        // Test avec utilisateur ayant plusieurs notifications
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.obtenirNotificationsUtilisateur(2))
                .thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].expediteur.nom").value("John Expediteur"))
                .andExpect(jsonPath("$[1].destinataire.email").value("destinataire@test.com"));
    }

    @Test
    void testFiltreNotificationsParTypeMultiples() throws Exception {
        String[] types = {"PAIEMENT", "VISITE", "CONTRAT", "CONVOITISE", "MESSAGE"};

        for (String typeStr : types) {
            Notification.Type typeEnum = Notification.Type.valueOf(typeStr);

            when(notificationService.obtenirNotificationsParType(2, typeEnum))
                    .thenReturn(Arrays.asList(notification1));

            mockMvc.perform(get("/api/notifications/2/type/" + typeStr))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
                    

            verify(notificationService, times(1)).obtenirNotificationsParType(2, typeEnum);
            reset(notificationService); 
        }
    }


    @Test
    void testValidationParametresRequis() throws Exception {
        // Test avec paramètres manquants ou invalides
        
        // Test avec ID utilisateur invalide (négatif)
        mockMvc.perform(get("/api/notifications/-1"))
                .andExpect(status().isBadRequest());

        // Test avec type invalide
        mockMvc.perform(get("/api/notifications/1/type/TYPE_INEXISTANT"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSequenceCompleteNotification() throws Exception {
        // 1. Création
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", "PAIEMENT");
        notificationData.put("contenu", "Test complet");

        when(notificationService.creerNotification(any(Notification.Type.class), anyString(), any(), any()))
                .thenReturn(notification1);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationData)))
                .andExpect(status().isOk());

        // 2. Lecture
        when(notificationService.obtenirNotificationParId(1))
                .thenReturn(Optional.of(notification1));

        mockMvc.perform(get("/api/notifications/detail/1"))
                .andExpect(status().isOk());

        // 3. Marquer comme lu
        notification1.setLu(true);
        when(notificationService.marquerCommeLue(1)).thenReturn(notification1);

        mockMvc.perform(put("/api/notifications/1/lu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lu").value(true));

        // 4. Suppression
        doNothing().when(notificationService).supprimerNotification(1);

        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isOk());
    }
}