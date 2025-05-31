package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.Message;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private Utilisateur expediteur;
    private Utilisateur destinataire;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        // Création des utilisateurs de test
        expediteur = new Utilisateur();
        expediteur.setIdUtilisateur(1);
        expediteur.setNom("Alice Mbarga");
        expediteur.setEmail("alice@example.com");

        destinataire = new Utilisateur();
        destinataire.setIdUtilisateur(2);
        destinataire.setNom("Bruno Nji");
        destinataire.setEmail("bruno@example.com");

        // Création des messages de test
        message1 = new Message();
        message1.setIdMessage(1);
        message1.setContenu("Bonjour, je suis intéressé par votre annonce.");
        message1.setExpediteur(expediteur);
        message1.setDestinataire(destinataire);
        message1.setDateEnvoi(LocalDateTime.now());
        message1.setLu(false);

        message2 = new Message();
        message2.setIdMessage(2);
        message2.setContenu("Merci pour votre message.");
        message2.setExpediteur(destinataire);
        message2.setDestinataire(expediteur);
        message2.setDateEnvoi(LocalDateTime.now());
        message2.setLu(true);
    }

    @Test
    void testEnvoyerMessage_Success() throws Exception {
        // Arrange
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("expediteurId", 1);
        messageData.put("destinataireId", 2);
        messageData.put("contenu", "Bonjour, je suis intéressé par votre annonce.");

        when(messageService.envoyerMessage(1, 2, "Bonjour, je suis intéressé par votre annonce."))
                .thenReturn(message1);

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage").value(1))
                .andExpect(jsonPath("$.contenu").value("Bonjour, je suis intéressé par votre annonce."))
                .andExpect(jsonPath("$.lu").value(false));

        verify(messageService).envoyerMessage(1, 2, "Bonjour, je suis intéressé par votre annonce.");
    }

    @Test
    void testEnvoyerMessage_MissingFields() throws Exception {
        // Arrange - Message sans expediteurId
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("destinataireId", 2);
        messageData.put("contenu", "Test message");

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Les champs expediteurId, destinataireId et contenu sont obligatoires"));
    }

    @Test
    void testEnvoyerMessage_ServiceException() throws Exception {
        // Arrange
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("expediteurId", 1);
        messageData.put("destinataireId", 2);
        messageData.put("contenu", "Test message");

        when(messageService.envoyerMessage(1, 2, "Test message"))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageData)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé"));
    }

    @Test
    void testObtenirConversation_Success() throws Exception {
        // Arrange
        List<Message> conversation = Arrays.asList(message1, message2);
        when(messageService.obtenirConversation(1, 2)).thenReturn(conversation);

        // Act & Assert
        mockMvc.perform(get("/api/messages/conversation/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idMessage").value(1))
                .andExpect(jsonPath("$[1].idMessage").value(2));

        verify(messageService).obtenirConversation(1, 2);
    }

    @Test
    void testObtenirConversation_ServiceException() throws Exception {
        // Arrange
        when(messageService.obtenirConversation(1, 2))
                .thenThrow(new RuntimeException("Erreur lors de la récupération"));

        // Act & Assert
        mockMvc.perform(get("/api/messages/conversation/1/2"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erreur lors de la récupération"));
    }

    @Test
    void testObtenirTousLesMessages_Success() throws Exception {
        // Arrange
        List<Message> messages = Arrays.asList(message1, message2);
        when(messageService.obtenirTousLesMessages(1)).thenReturn(messages);

        // Act & Assert
        mockMvc.perform(get("/api/messages/utilisateur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(messageService).obtenirTousLesMessages(1);
    }

    @Test
    void testObtenirMessagesRecus_Success() throws Exception {
        // Arrange
        List<Message> messagesRecus = Arrays.asList(message1);
        when(messageService.obtenirMessagesRecus(2)).thenReturn(messagesRecus);

        // Act & Assert
        mockMvc.perform(get("/api/messages/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idMessage").value(1));

        verify(messageService).obtenirMessagesRecus(2);
    }

    @Test
    void testObtenirMessagesEnvoyes_Success() throws Exception {
        // Arrange
        List<Message> messagesEnvoyes = Arrays.asList(message1);
        when(messageService.obtenirMessagesEnvoyes(1)).thenReturn(messagesEnvoyes);

        // Act & Assert
        mockMvc.perform(get("/api/messages/envoyes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(messageService).obtenirMessagesEnvoyes(1);
    }

    @Test
    void testObtenirMessagesNonLus_Success() throws Exception {
        // Arrange
        List<Message> messagesNonLus = Arrays.asList(message1);
        when(messageService.obtenirMessagesNonLus(2)).thenReturn(messagesNonLus);

        // Act & Assert
        mockMvc.perform(get("/api/messages/non-lus/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lu").value(false));

        verify(messageService).obtenirMessagesNonLus(2);
    }

    @Test
    void testCompterMessagesNonLus_Success() throws Exception {
        // Arrange
        when(messageService.compterMessagesNonLus(2)).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/messages/non-lus/count/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        verify(messageService).compterMessagesNonLus(2);
    }

    @Test
    void testMarquerCommeLu_Success() throws Exception {
        // Arrange
        Map<String, Integer> userData = new HashMap<>();
        userData.put("utilisateurId", 2);

        Message messageMarque = new Message();
        messageMarque.setIdMessage(1);
        messageMarque.setLu(true);

        when(messageService.marquerCommeLu(1, 2)).thenReturn(messageMarque);

        // Act & Assert
        mockMvc.perform(put("/api/messages/1/lu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage").value(1))
                .andExpect(jsonPath("$.lu").value(true));

        verify(messageService).marquerCommeLu(1, 2);
    }

    @Test
    void testMarquerCommeLu_MissingUserId() throws Exception {
        // Arrange
        Map<String, Integer> userData = new HashMap<>();
        // Pas d'utilisateurId

        // Act & Assert
        mockMvc.perform(put("/api/messages/1/lu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ID de l'utilisateur est obligatoire"));
    }

    @Test
    void testMarquerConversationCommeLue_Success() throws Exception {
        // Arrange
        Map<String, Integer> userData = new HashMap<>();
        userData.put("destinataireId", 2);

        doNothing().when(messageService).marquerConversationCommeLue(1, 2, 2);

        // Act & Assert
        mockMvc.perform(put("/api/messages/conversation/1/2/lu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conversation marquée comme lue"));

        verify(messageService).marquerConversationCommeLue(1, 2, 2);
    }

    @Test
    void testMarquerConversationCommeLue_MissingDestinataireId() throws Exception {
        // Arrange
        Map<String, Integer> userData = new HashMap<>();
        // Pas de destinataireId

        // Act & Assert
        mockMvc.perform(put("/api/messages/conversation/1/2/lu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ID du destinataire est obligatoire"));
    }

    @Test
    void testSupprimerMessage_Success() throws Exception {
        // Arrange
        doNothing().when(messageService).supprimerMessage(1, 1);

        // Act & Assert
        mockMvc.perform(delete("/api/messages/1")
                        .param("utilisateurId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message supprimé avec succès"));

        verify(messageService).supprimerMessage(1, 1);
    }

    @Test
    void testSupprimerMessage_ServiceException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Message non trouvé"))
                .when(messageService).supprimerMessage(1, 1);

        // Act & Assert
        mockMvc.perform(delete("/api/messages/1")
                        .param("utilisateurId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Message non trouvé"));
    }

    @Test
    void testObtenirPartenairesConversation_Success() throws Exception {
        // Arrange
        List<Utilisateur> partenaires = Arrays.asList(destinataire);
        when(messageService.obtenirPartenairesConversation(1)).thenReturn(partenaires);

        // Act & Assert
        mockMvc.perform(get("/api/messages/partenaires/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idUtilisateur").value(2));

        verify(messageService).obtenirPartenairesConversation(1);
    }

    @Test
    void testObtenirMessagesRecents_Success() throws Exception {
        // Arrange
        List<Message> messagesRecents = Arrays.asList(message1, message2);
        when(messageService.obtenirMessagesRecents(24)).thenReturn(messagesRecents);

        // Act & Assert
        mockMvc.perform(get("/api/messages/recents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(messageService).obtenirMessagesRecents(24);
    }

    @Test
    void testObtenirMessagesRecents_CustomHours() throws Exception {
        // Arrange
        List<Message> messagesRecents = Arrays.asList(message1);
        when(messageService.obtenirMessagesRecents(12)).thenReturn(messagesRecents);

        // Act & Assert
        mockMvc.perform(get("/api/messages/recents")
                        .param("heures", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(messageService).obtenirMessagesRecents(12);
    }

    @Test
    void testObtenirStatistiquesMessages_Success() throws Exception {
        // Arrange
        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("totalMessages", 10);
        statistiques.put("messagesEnvoyes", 6);
        statistiques.put("messagesRecus", 4);
        statistiques.put("messagesNonLus", 2);

        when(messageService.obtenirStatistiquesMessages(1)).thenReturn(statistiques);

        // Act & Assert
        mockMvc.perform(get("/api/messages/statistiques/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMessages").value(10))
                .andExpect(jsonPath("$.messagesEnvoyes").value(6))
                .andExpect(jsonPath("$.messagesRecus").value(4))
                .andExpect(jsonPath("$.messagesNonLus").value(2));

        verify(messageService).obtenirStatistiquesMessages(1);
    }

    @Test
    void testObtenirStatistiquesMessages_ServiceException() throws Exception {
        // Arrange
        when(messageService.obtenirStatistiquesMessages(1))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        // Act & Assert
        mockMvc.perform(get("/api/messages/statistiques/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé"));
    }

    @Test
    void testCorsConfiguration() throws Exception {
        // Test que les requêtes CORS sont bien gérées
        mockMvc.perform(options("/api/messages")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk());
    }

    // Tests de validation des données
    @Test
    void testEnvoyerMessage_ContenuVide() throws Exception {
        // Arrange
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("expediteurId", 1);
        messageData.put("destinataireId", 2);
        messageData.put("contenu", "");

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEnvoyerMessage_ContenuNull() throws Exception {
        // Arrange
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("expediteurId", 1);
        messageData.put("destinataireId", 2);
        messageData.put("contenu", null);

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Les champs expediteurId, destinataireId et contenu sont obligatoires"));
    }

    // Test de performance - vérifier que les endpoints répondent rapidement
    @Test
    void testPerformanceObtenirConversation() throws Exception {
        // Arrange
        List<Message> conversation = Arrays.asList(message1, message2);
        when(messageService.obtenirConversation(1, 2)).thenReturn(conversation);

        long startTime = System.currentTimeMillis();

        // Act
        mockMvc.perform(get("/api/messages/conversation/1/2"))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert - Vérifier que la réponse est rapide (moins de 100ms)
        assert(duration < 100);
    }
}