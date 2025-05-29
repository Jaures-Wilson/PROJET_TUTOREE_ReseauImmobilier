package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private UtilisateurController utilisateurController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Utilisateur utilisateurTest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();
        objectMapper = new ObjectMapper();
        
        // Créer un utilisateur de test
        utilisateurTest = new Utilisateur();
        utilisateurTest.setIdUtilisateur(1);
        utilisateurTest.setNom("Alice Mbarga");
        utilisateurTest.setEmail("alice@example.com");
        utilisateurTest.setMotDePasse("pass123");
        utilisateurTest.setRole(Utilisateur.Role.ACQUEREUR);
        utilisateurTest.setNumPhone(690112233);
        utilisateurTest.setFormatNumPays(237);
    }

    @Test
    void testCreerUtilisateur_Succes() throws Exception {
        // Given
        when(utilisateurService.creerUtilisateur(any(Utilisateur.class))).thenReturn(utilisateurTest);

        // When & Then
        mockMvc.perform(post("/api/utilisateurs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilisateurTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.nom").value("Alice Mbarga"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("ACQUEREUR"));

        verify(utilisateurService, times(1)).creerUtilisateur(any(Utilisateur.class));
    }

    @Test
    void testCreerUtilisateur_Echec() throws Exception {
        // Given
        when(utilisateurService.creerUtilisateur(any(Utilisateur.class)))
                .thenThrow(new RuntimeException("Email déjà existant"));

        // When & Then
        mockMvc.perform(post("/api/utilisateurs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilisateurTest)))
                .andExpect(status().isBadRequest());

        verify(utilisateurService, times(1)).creerUtilisateur(any(Utilisateur.class));
    }

    @Test
    void testObtenirUtilisateur_Trouve() throws Exception {
        // Given
        when(utilisateurService.obtenirUtilisateurParId(1)).thenReturn(Optional.of(utilisateurTest));

        // When & Then
        mockMvc.perform(get("/api/utilisateurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.nom").value("Alice Mbarga"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(utilisateurService, times(1)).obtenirUtilisateurParId(1);
    }

    @Test
    void testObtenirUtilisateur_NonTrouve() throws Exception {
        // Given
        when(utilisateurService.obtenirUtilisateurParId(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/utilisateurs/999"))
                .andExpect(status().isNotFound());

        verify(utilisateurService, times(1)).obtenirUtilisateurParId(999);
    }

    @Test
    void testObtenirTousLesUtilisateurs() throws Exception {
        // Given
        Utilisateur utilisateur2 = new Utilisateur();
        utilisateur2.setIdUtilisateur(2);
        utilisateur2.setNom("Bruno Nji");
        utilisateur2.setEmail("bruno@example.com");
        utilisateur2.setRole(Utilisateur.Role.ANNONCEUR);

        List<Utilisateur> utilisateurs = Arrays.asList(utilisateurTest, utilisateur2);
        when(utilisateurService.obtenirTousLesUtilisateurs()).thenReturn(utilisateurs);

        // When & Then
        mockMvc.perform(get("/api/utilisateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Alice Mbarga"))
                .andExpect(jsonPath("$[1].nom").value("Bruno Nji"));

        verify(utilisateurService, times(1)).obtenirTousLesUtilisateurs();
    }

    @Test
    void testModifierUtilisateur_Succes() throws Exception {
        // Given
        Utilisateur utilisateurModifie = new Utilisateur();
        utilisateurModifie.setIdUtilisateur(1);
        utilisateurModifie.setNom("Alice Mbarga Modifiée");
        utilisateurModifie.setEmail("alice.modifiee@example.com");
        utilisateurModifie.setRole(Utilisateur.Role.ACQUEREUR);

        when(utilisateurService.modifierUtilisateur(eq(1), any(Utilisateur.class)))
                .thenReturn(utilisateurModifie);

        // When & Then
        mockMvc.perform(put("/api/utilisateurs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilisateurModifie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Alice Mbarga Modifiée"))
                .andExpect(jsonPath("$.email").value("alice.modifiee@example.com"));

        verify(utilisateurService, times(1)).modifierUtilisateur(eq(1), any(Utilisateur.class));
    }

    @Test
    void testModifierUtilisateur_NonTrouve() throws Exception {
        // Given
        when(utilisateurService.modifierUtilisateur(eq(999), any(Utilisateur.class)))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/utilisateurs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilisateurTest)))
                .andExpect(status().isNotFound());

        verify(utilisateurService, times(1)).modifierUtilisateur(eq(999), any(Utilisateur.class));
    }

    @Test
    void testSupprimerUtilisateur_Succes() throws Exception {
        // Given
        doNothing().when(utilisateurService).supprimerUtilisateur(1);

        // When & Then
        mockMvc.perform(delete("/api/utilisateurs/1"))
                .andExpect(status().isNoContent());

        verify(utilisateurService, times(1)).supprimerUtilisateur(1);
    }

    @Test
    void testSupprimerUtilisateur_NonTrouve() throws Exception {
        // Given
        doThrow(new RuntimeException("Utilisateur non trouvé"))
                .when(utilisateurService).supprimerUtilisateur(999);

        // When & Then
        mockMvc.perform(delete("/api/utilisateurs/999"))
                .andExpect(status().isNotFound());

        verify(utilisateurService, times(1)).supprimerUtilisateur(999);
    }

    @Test
    void testConnecterUtilisateur_Succes() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "alice@example.com");
        credentials.put("motDePasse", "pass123");

        when(utilisateurService.connecterUtilisateur("alice@example.com", "pass123"))
                .thenReturn(Optional.of(utilisateurTest));

        // When & Then
        mockMvc.perform(post("/api/utilisateurs/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.nom").value("Alice Mbarga"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(utilisateurService, times(1)).connecterUtilisateur("alice@example.com", "pass123");
    }

    @Test
    void testConnecterUtilisateur_Echec() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "alice@example.com");
        credentials.put("motDePasse", "mauvais_password");

        when(utilisateurService.connecterUtilisateur("alice@example.com", "mauvais_password"))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/utilisateurs/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());

        verify(utilisateurService, times(1)).connecterUtilisateur("alice@example.com", "mauvais_password");
    }

    @Test
    void testConnecterUtilisateur_CredentialsManquants() throws Exception {
        // Given
        Map<String, String> credentialsIncompletes = new HashMap<>();
        credentialsIncompletes.put("email", "alice@example.com");
        // Pas de mot de passe

        when(utilisateurService.connecterUtilisateur("alice@example.com", null))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/utilisateurs/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialsIncompletes)))
                .andExpect(status().isUnauthorized());

        verify(utilisateurService, times(1)).connecterUtilisateur("alice@example.com", null);
    }

    @Test
    void testCreerUtilisateur_AvecDonneesCompletes() throws Exception {
        // Given
        Utilisateur utilisateurComplet = new Utilisateur();
        utilisateurComplet.setNom("Test User");
        utilisateurComplet.setEmail("test@example.com");
        utilisateurComplet.setMotDePasse("password123");
        utilisateurComplet.setRole(Utilisateur.Role.ACQUEREUR);
        utilisateurComplet.setNumPhone(699123456);
        utilisateurComplet.setFormatNumPays(237);

        Utilisateur utilisateurCree = new Utilisateur();
        utilisateurCree.setIdUtilisateur(3);
        utilisateurCree.setNom("Test User");
        utilisateurCree.setEmail("test@example.com");
        utilisateurCree.setMotDePasse("password123");
        utilisateurCree.setRole(Utilisateur.Role.ACQUEREUR);
        utilisateurCree.setNumPhone(699123456);
        utilisateurCree.setFormatNumPays(237);

        when(utilisateurService.creerUtilisateur(any(Utilisateur.class))).thenReturn(utilisateurCree);

        // When & Then
        mockMvc.perform(post("/api/utilisateurs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilisateurComplet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUtilisateur").value(3))
                .andExpect(jsonPath("$.nom").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ACQUEREUR"))
                .andExpect(jsonPath("$.numPhone").value(699123456))
                .andExpect(jsonPath("$.formatNumPays").value(237));
    }

    @Test
    void testGestionErreurs_DonneesInvalides() throws Exception {
        // Given
        String jsonInvalide = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/api/utilisateurs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalide))
                .andExpect(status().isBadRequest());
    }
}