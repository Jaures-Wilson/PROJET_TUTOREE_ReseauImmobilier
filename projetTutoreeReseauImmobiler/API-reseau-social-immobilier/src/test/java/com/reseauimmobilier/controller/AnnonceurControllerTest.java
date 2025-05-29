package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.Annonceur;
import com.reseauimmobilier.service.AnnonceurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AnnonceurController.class)
@DisplayName("Tests pour AnnonceurController")
class AnnonceurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnonceurService annonceurService;

    @Autowired
    private ObjectMapper objectMapper;

    private Annonceur annonceurTest;
    private Map<String, Object> demandeConversion;

    @BeforeEach
    void setUp() {
        // Création d'un annonceur de test
        annonceurTest = new Annonceur();
        annonceurTest.setIdUtilisateur(1);
        annonceurTest.setNom("Bruno Nji");
        annonceurTest.setEmail("bruno@example.com");
        annonceurTest.setMotDePasse("pass456");
        annonceurTest.setRole(Annonceur.Role.ANNONCEUR);
        annonceurTest.setNumPhone(677445566);
        annonceurTest.setFormatNumPays(237);
        annonceurTest.setNumeroMTN_MoMo(675000001);
        annonceurTest.setNumeroOrangeMoney(690000002);
        annonceurTest.setNumeroUBA(123456789);

        // Demande de conversion type
        demandeConversion = new HashMap<>();
        demandeConversion.put("utilisateurId", 1);
        demandeConversion.put("numeroMTN_MoMo", 675000001);
        demandeConversion.put("numeroOrangeMoney", 690000002);
        demandeConversion.put("numeroUBA", 123456789);
    }

    @Test
    @DisplayName("POST /api/annonceurs - Conversion réussie d'un utilisateur en annonceur")
    void testConvertirEnAnnonceur_Succes() throws Exception {
        // Given
        when(annonceurService.convertirUtilisateurEnAnnonceur(1, 675000001, 690000002, 123456789))
                .thenReturn(annonceurTest);

        // When & Then
        mockMvc.perform(post("/api/annonceurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandeConversion)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.nom").value("Bruno Nji"))
                .andExpect(jsonPath("$.email").value("bruno@example.com"))
                .andExpect(jsonPath("$.role").value("ANNONCEUR"))
                .andExpect(jsonPath("$.numeroMTN_MoMo").value(675000001))
                .andExpect(jsonPath("$.numeroOrangeMoney").value(690000002))
                .andExpect(jsonPath("$.numeroUBA").value(123456789));

        verify(annonceurService, times(1))
                .convertirUtilisateurEnAnnonceur(1, 675000001, 690000002, 123456789);
    }

    @Test
    @DisplayName("POST /api/annonceurs - Échec de conversion avec utilisateur inexistant")
    void testConvertirEnAnnonceur_UtilisateurInexistant() throws Exception {
        // Given
        when(annonceurService.convertirUtilisateurEnAnnonceur(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        // When & Then
        mockMvc.perform(post("/api/annonceurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandeConversion)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(annonceurService, times(1))
                .convertirUtilisateurEnAnnonceur(1, 675000001, 690000002, 123456789);
    }

    @Test
    @DisplayName("POST /api/annonceurs - Données invalides dans la demande")
    void testConvertirEnAnnonceur_DonneesInvalides() throws Exception {
        // Given - demande avec données manquantes
        Map<String, Object> demandeInvalide = new HashMap<>();
        demandeInvalide.put("utilisateurId", null);

        // When & Then
        mockMvc.perform(post("/api/annonceurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandeInvalide)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/annonceurs/{id} - Récupération d'un annonceur existant")
    void testObtenirAnnonceur_Succes() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParId(1)).thenReturn(Optional.of(annonceurTest));

        // When & Then
        mockMvc.perform(get("/api/annonceurs/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.nom").value("Bruno Nji"))
                .andExpect(jsonPath("$.email").value("bruno@example.com"));

        verify(annonceurService, times(1)).obtenirAnnonceurParId(1);
    }

    @Test
    @DisplayName("GET /api/annonceurs/{id} - Annonceur non trouvé")
    void testObtenirAnnonceur_NonTrouve() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParId(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/annonceurs/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(annonceurService, times(1)).obtenirAnnonceurParId(999);
    }

    @Test
    @DisplayName("GET /api/annonceurs/utilisateur/{utilisateurId} - Récupération par ID utilisateur")
    void testObtenirAnnonceurParUtilisateurId_Succes() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParUtilisateurId(1)).thenReturn(Optional.of(annonceurTest));

        // When & Then
        mockMvc.perform(get("/api/annonceurs/utilisateur/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.nom").value("Bruno Nji"));

        verify(annonceurService, times(1)).obtenirAnnonceurParUtilisateurId(1);
    }

    @Test
    @DisplayName("GET /api/annonceurs/utilisateur/{utilisateurId} - Utilisateur non annonceur")
    void testObtenirAnnonceurParUtilisateurId_NonTrouve() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParUtilisateurId(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/annonceurs/utilisateur/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(annonceurService, times(1)).obtenirAnnonceurParUtilisateurId(999);
    }

    @Test
    @DisplayName("GET /api/annonceurs - Récupération de tous les annonceurs")
    void testObtenirTousLesAnnonceurs_Succes() throws Exception {
        // Given
        Annonceur annonceur2 = new Annonceur();
        annonceur2.setIdUtilisateur(2);
        annonceur2.setNom("Alice Mbarga");
        annonceur2.setEmail("alice@example.com");
        annonceur2.setRole(Annonceur.Role.ANNONCEUR);

        List<Annonceur> annonceurs = Arrays.asList(annonceurTest, annonceur2);
        when(annonceurService.obtenirTousLesAnnonceurs()).thenReturn(annonceurs);

        // When & Then
        mockMvc.perform(get("/api/annonceurs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Bruno Nji"))
                .andExpect(jsonPath("$[1].nom").value("Alice Mbarga"));

        verify(annonceurService, times(1)).obtenirTousLesAnnonceurs();
    }

    @Test
    @DisplayName("GET /api/annonceurs - Liste vide d'annonceurs")
    void testObtenirTousLesAnnonceurs_ListeVide() throws Exception {
        // Given
        when(annonceurService.obtenirTousLesAnnonceurs()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/annonceurs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(annonceurService, times(1)).obtenirTousLesAnnonceurs();
    }

    @Test
    @DisplayName("PUT /api/annonceurs/{id} - Modification réussie d'un annonceur")
    void testModifierAnnonceur_Succes() throws Exception {
        // Given
        Annonceur annonceurModifie = new Annonceur();
        annonceurModifie.setIdUtilisateur(1);
        annonceurModifie.setNom("Bruno Nji Modifié");
        annonceurModifie.setEmail("bruno.modifie@example.com");
        annonceurModifie.setRole(Annonceur.Role.ANNONCEUR);
        annonceurModifie.setNumeroMTN_MoMo(675000999);

        when(annonceurService.modifierAnnonceur(eq(1), any(Annonceur.class)))
                .thenReturn(annonceurModifie);

        // When & Then
        mockMvc.perform(put("/api/annonceurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceurModifie)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nom").value("Bruno Nji Modifié"))
                .andExpect(jsonPath("$.email").value("bruno.modifie@example.com"))
                .andExpect(jsonPath("$.numeroMTN_MoMo").value(675000999));

        verify(annonceurService, times(1)).modifierAnnonceur(eq(1), any(Annonceur.class));
    }

    @Test
    @DisplayName("PUT /api/annonceurs/{id} - Modification d'un annonceur inexistant")
    void testModifierAnnonceur_NonTrouve() throws Exception {
        // Given
        when(annonceurService.modifierAnnonceur(eq(999), any(Annonceur.class)))
                .thenThrow(new RuntimeException("Annonceur non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/annonceurs/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceurTest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(annonceurService, times(1)).modifierAnnonceur(eq(999), any(Annonceur.class));
    }

    @Test
    @DisplayName("DELETE /api/annonceurs/{id} - Suppression réussie d'un annonceur")
    void testSupprimerAnnonceur_Succes() throws Exception {
        // Given
        doNothing().when(annonceurService).supprimerAnnonceur(1);

        // When & Then
        mockMvc.perform(delete("/api/annonceurs/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(annonceurService, times(1)).supprimerAnnonceur(1);
    }

    @Test
    @DisplayName("DELETE /api/annonceurs/{id} - Suppression d'un annonceur inexistant")
    void testSupprimerAnnonceur_NonTrouve() throws Exception {
        // Given
        doThrow(new RuntimeException("Annonceur non trouvé"))
                .when(annonceurService).supprimerAnnonceur(999);

        // When & Then
        mockMvc.perform(delete("/api/annonceurs/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(annonceurService, times(1)).supprimerAnnonceur(999);
    }

    @Test
    @DisplayName("GET /api/annonceurs/{id}/statistiques - Récupération des statistiques")
    void testObtenirStatistiquesAnnonceur_Succes() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParId(1)).thenReturn(Optional.of(annonceurTest));
        when(annonceurService.compterAnnoncesParAnnonceur(1)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/annonceurs/1/statistiques"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.annonceur.nom").value("Bruno Nji"))
                .andExpect(jsonPath("$.nombreAnnonces").value(5));

        verify(annonceurService, times(1)).obtenirAnnonceurParId(1);
        verify(annonceurService, times(1)).compterAnnoncesParAnnonceur(1);
    }

    @Test
    @DisplayName("GET /api/annonceurs/{id}/statistiques - Annonceur inexistant")
    void testObtenirStatistiquesAnnonceur_NonTrouve() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParId(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/annonceurs/999/statistiques"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(annonceurService, times(1)).obtenirAnnonceurParId(999);
        verify(annonceurService, never()).compterAnnoncesParAnnonceur(anyInt());
    }

    @Test
    @DisplayName("GET /api/annonceurs/{id}/statistiques - Erreur interne du serveur")
    void testObtenirStatistiquesAnnonceur_ErreurInterne() throws Exception {
        // Given
        when(annonceurService.obtenirAnnonceurParId(1))
                .thenThrow(new RuntimeException("Erreur base de données"));

        // When & Then
        mockMvc.perform(get("/api/annonceurs/1/statistiques"))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(annonceurService, times(1)).obtenirAnnonceurParId(1);
    }

    @Test
    @DisplayName("Vérification de la configuration CORS")
    void testCorsConfiguration() throws Exception {
        // When & Then
        mockMvc.perform(options("/api/annonceurs")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @DisplayName("Test avec Content-Type invalide")
    void testAvecContentTypeInvalide() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/annonceurs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("données invalides"))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Test avec JSON malformé")
    void testAvecJsonMalforme() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/annonceurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{nom: 'Bruno', 'email':}")) // JSON malformé
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}