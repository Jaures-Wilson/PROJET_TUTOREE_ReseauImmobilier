package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.*;
import com.reseauimmobilier.service.AdminService;
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
@WebMvcTest(AdminController.class)
class AdministrateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private Utilisateur utilisateurTest;
    private Administrateur adminTest;
    private Annonce annonceTest;
    private Paiement paiementTest;
    private Plainte plainteTest;

    @BeforeEach
    void setUp() {
        // Initialisation des objets de test
        utilisateurTest = new Utilisateur();
        utilisateurTest.setIdUtilisateur(1);
        utilisateurTest.setNom("Test User");
        utilisateurTest.setEmail("test@example.com");
        utilisateurTest.setMotDePasse("password");
        utilisateurTest.setRole(Utilisateur.Role.ACQUEREUR);
        utilisateurTest.setNumPhone(690112233);
        utilisateurTest.setFormatNumPays(237);

        adminTest = new Administrateur();
        adminTest.setIdUtilisateur(2);
        adminTest.setNom("Admin Test");
        adminTest.setEmail("admin@example.com");
        adminTest.setMotDePasse("adminpass");
        adminTest.setRole(Utilisateur.Role.ADMIN);
        adminTest.setNumPhone(699998877);
        adminTest.setFormatNumPays(237);
        adminTest.setNumeroMTN_MoMo(675000001);
        adminTest.setNumeroOrangeMoney(690000002);
        adminTest.setNumeroUBA(123456789);

        annonceTest = new Annonce();
        annonceTest.setIdAnnonce(1);
        annonceTest.setTitre("Studio Test");
        annonceTest.setDescription("Description test");
        annonceTest.setPrixMensuel(100000);
        annonceTest.setLocalisation("Douala");
        annonceTest.setStatus(Annonce.Status.DISPONIBLE);
        annonceTest.setDatePublication(LocalDateTime.now());
        annonceTest.setTarifReservation(10000);

        paiementTest = new Paiement();
        paiementTest.setIdPaiement(1);
        paiementTest.setMontant(100000);
        paiementTest.setDatePaiement(LocalDateTime.now());
        paiementTest.setMoyenPaiement(Paiement.MoyenPaiement.MoMo);
        paiementTest.setStatut(Paiement.StatutPaiement.EN_ATTENTE);
        paiementTest.setTypeContrat(Paiement.TypeContrat.VENTE);
        paiementTest.setLu(false);

        plainteTest = new Plainte();
        plainteTest.setIdPlainte(1);
        plainteTest.setContenu("Test plainte");
        plainteTest.setDatePlainte(LocalDateTime.now());
        plainteTest.setStatut("NOUVELLE");
    }

    // Tests pour la gestion des utilisateurs
    @Test
    void testObtenirTousLesUtilisateurs() throws Exception {
        List<Utilisateur> utilisateurs = Arrays.asList(utilisateurTest, adminTest);
        when(adminService.obtenirTousLesUtilisateurs()).thenReturn(utilisateurs);

        mockMvc.perform(get("/api/admin/utilisateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Test User"))
                .andExpect(jsonPath("$[1].nom").value("Admin Test"));

        verify(adminService, times(1)).obtenirTousLesUtilisateurs();
    }

    @Test
    void testSupprimerUtilisateur_Success() throws Exception {
        doNothing().when(adminService).supprimerUtilisateur(1);

        mockMvc.perform(delete("/api/admin/utilisateurs/1"))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).supprimerUtilisateur(1);
    }

    @Test
    void testSupprimerUtilisateur_NotFound() throws Exception {
        doThrow(new RuntimeException("Utilisateur non trouvé")).when(adminService).supprimerUtilisateur(999);

        mockMvc.perform(delete("/api/admin/utilisateurs/999"))
                .andExpect(status().isNotFound());

        verify(adminService, times(1)).supprimerUtilisateur(999);
    }

    @Test
    void testSuspendreUtilisateur_Success() throws Exception {
        when(adminService.suspendreUtilisateur(1)).thenReturn(utilisateurTest);

        mockMvc.perform(put("/api/admin/utilisateurs/1/suspendre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Test User"));

        verify(adminService, times(1)).suspendreUtilisateur(1);
    }

    @Test
    void testSuspendreUtilisateur_NotFound() throws Exception {
        when(adminService.suspendreUtilisateur(999)).thenThrow(new RuntimeException("Utilisateur non trouvé"));

        mockMvc.perform(put("/api/admin/utilisateurs/999/suspendre"))
                .andExpect(status().isNotFound());

        verify(adminService, times(1)).suspendreUtilisateur(999);
    }

    // Tests pour la gestion des annonces
    @Test
    void testObtenirToutesLesAnnonces() throws Exception {
        List<Annonce> annonces = Arrays.asList(annonceTest);
        when(adminService.obtenirToutesLesAnnonces()).thenReturn(annonces);

        mockMvc.perform(get("/api/admin/annonces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").value("Studio Test"));

        verify(adminService, times(1)).obtenirToutesLesAnnonces();
    }

    @Test
    void testObtenirAnnoncesEnAttenteValidation() throws Exception {
        List<Annonce> annonces = Arrays.asList(annonceTest);
        when(adminService.obtenirAnnoncesEnAttenteValidation()).thenReturn(annonces);

        mockMvc.perform(get("/api/admin/annonces/validation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").value("Studio Test"));

        verify(adminService, times(1)).obtenirAnnoncesEnAttenteValidation();
    }

    @Test
    void testValiderAnnonce_Accepter() throws Exception {
        Map<String, Object> decision = new HashMap<>();
        decision.put("accepter", true);
        decision.put("motif", "");

        when(adminService.validerAnnonce(1, true, "")).thenReturn(annonceTest);

        mockMvc.perform(put("/api/admin/annonces/1/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Studio Test"));

        verify(adminService, times(1)).validerAnnonce(1, true, "");
    }

    @Test
    void testValiderAnnonce_Refuser() throws Exception {
        Map<String, Object> decision = new HashMap<>();
        decision.put("accepter", false);
        decision.put("motif", "Informations insuffisantes");

        when(adminService.validerAnnonce(1, false, "Informations insuffisantes")).thenReturn(annonceTest);

        mockMvc.perform(put("/api/admin/annonces/1/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Studio Test"));

        verify(adminService, times(1)).validerAnnonce(1, false, "Informations insuffisantes");
    }

    @Test
    void testValiderAnnonce_NotFound() throws Exception {
        Map<String, Object> decision = new HashMap<>();
        decision.put("accepter", true);

        when(adminService.validerAnnonce(eq(999), anyBoolean(), anyString()))
                .thenThrow(new RuntimeException("Annonce non trouvée"));

        mockMvc.perform(put("/api/admin/annonces/999/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSupprimerAnnonce_Success() throws Exception {
        doNothing().when(adminService).supprimerAnnonce(1);

        mockMvc.perform(delete("/api/admin/annonces/1"))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).supprimerAnnonce(1);
    }

    @Test
    void testSupprimerAnnonce_NotFound() throws Exception {
        doThrow(new RuntimeException("Annonce non trouvée")).when(adminService).supprimerAnnonce(999);

        mockMvc.perform(delete("/api/admin/annonces/999"))
                .andExpect(status().isNotFound());

        verify(adminService, times(1)).supprimerAnnonce(999);
    }

    // Tests pour la gestion des paiements
    @Test
    void testObtenirTousLesPaiements() throws Exception {
        List<Paiement> paiements = Arrays.asList(paiementTest);
        when(adminService.obtenirTousLesPaiements()).thenReturn(paiements);

        mockMvc.perform(get("/api/admin/paiements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].montant").value(100000));

        verify(adminService, times(1)).obtenirTousLesPaiements();
    }

    @Test
    void testObtenirPaiementsEnAttente() throws Exception {
        List<Paiement> paiements = Arrays.asList(paiementTest);
        when(adminService.obtenirPaiementsEnAttente()).thenReturn(paiements);

        mockMvc.perform(get("/api/admin/paiements/attente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"));

        verify(adminService, times(1)).obtenirPaiementsEnAttente();
    }

    @Test
    void testValiderPaiement_Success() throws Exception {
        paiementTest.setStatut(Paiement.StatutPaiement.CONFIRMEE);
        when(adminService.validerPaiement(1)).thenReturn(paiementTest);

        mockMvc.perform(put("/api/admin/paiements/1/valider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"));

        verify(adminService, times(1)).validerPaiement(1);
    }

    @Test
    void testValiderPaiement_NotFound() throws Exception {
        when(adminService.validerPaiement(999)).thenThrow(new RuntimeException("Paiement non trouvé"));

        mockMvc.perform(put("/api/admin/paiements/999/valider"))
                .andExpect(status().isNotFound());

        verify(adminService, times(1)).validerPaiement(999);
    }

    @Test
    void testRefuserPaiement_Success() throws Exception {
        Map<String, String> motifMap = new HashMap<>();
        motifMap.put("motif", "Preuve de paiement invalide");

        paiementTest.setStatut(Paiement.StatutPaiement.REFUSE);
        when(adminService.refuserPaiement(1, "Preuve de paiement invalide")).thenReturn(paiementTest);

        mockMvc.perform(put("/api/admin/paiements/1/refuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motifMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSE"));

        verify(adminService, times(1)).refuserPaiement(1, "Preuve de paiement invalide");
    }

    @Test
    void testRefuserPaiement_NotFound() throws Exception {
        Map<String, String> motifMap = new HashMap<>();
        motifMap.put("motif", "Motif test");

        when(adminService.refuserPaiement(eq(999), anyString()))
                .thenThrow(new RuntimeException("Paiement non trouvé"));

        mockMvc.perform(put("/api/admin/paiements/999/refuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motifMap)))
                .andExpect(status().isNotFound());
    }

    // Tests pour la gestion des plaintes
    @Test
    void testObtenirToutesLesPlaintes() throws Exception {
        List<Plainte> plaintes = Arrays.asList(plainteTest);
        when(adminService.obtenirToutesLesPlaintes()).thenReturn(plaintes);

        mockMvc.perform(get("/api/admin/plaintes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contenu").value("Test plainte"));

        verify(adminService, times(1)).obtenirToutesLesPlaintes();
    }

    @Test
    void testObtenirPlaintesNonLues() throws Exception {
        List<Plainte> plaintes = Arrays.asList(plainteTest);
        when(adminService.obtenirPlaintesNonLues()).thenReturn(plaintes);

        mockMvc.perform(get("/api/admin/plaintes/non-lues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].statut").value("NOUVELLE"));

        verify(adminService, times(1)).obtenirPlaintesNonLues();
    }

    @Test
    void testMarquerPlainteCommeLue_Success() throws Exception {
        plainteTest.setStatut("LU");
        when(adminService.marquerPlainteCommeLue(1)).thenReturn(plainteTest);

        mockMvc.perform(put("/api/admin/plaintes/1/marquer-lu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("LU"));

        verify(adminService, times(1)).marquerPlainteCommeLue(1);
    }

    @Test
    void testMarquerPlainteCommeLue_NotFound() throws Exception {
        when(adminService.marquerPlainteCommeLue(999))
                .thenThrow(new RuntimeException("Plainte non trouvée"));

        mockMvc.perform(put("/api/admin/plaintes/999/marquer-lu"))
                .andExpect(status().isNotFound());

        verify(adminService, times(1)).marquerPlainteCommeLue(999);
    }

    // Tests pour les statistiques
    @Test
    void testObtenirStatistiquesGlobales() throws Exception {
        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("totalUtilisateurs", 100);
        statistiques.put("totalAnnonces", 50);
        statistiques.put("totalPaiements", 25);
        statistiques.put("totalPlaintes", 5);

        when(adminService.obtenirStatistiquesGlobales()).thenReturn(statistiques);

        mockMvc.perform(get("/api/admin/statistiques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUtilisateurs").value(100))
                .andExpect(jsonPath("$.totalAnnonces").value(50))
                .andExpect(jsonPath("$.totalPaiements").value(25))
                .andExpect(jsonPath("$.totalPlaintes").value(5));

        verify(adminService, times(1)).obtenirStatistiquesGlobales();
    }

    // Tests pour la communication
    @Test
    void testEnvoyerEmail_Success() throws Exception {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("utilisateurId", 1);
        emailData.put("message", "Message de test");

        doNothing().when(adminService).envoyerEmail(1, "Message de test");

        mockMvc.perform(post("/api/admin/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Email envoyé avec succès"));

        verify(adminService, times(1)).envoyerEmail(1, "Message de test");
    }

    @Test
    void testEnvoyerEmail_BadRequest() throws Exception {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("utilisateurId", 999);
        emailData.put("message", "Message de test");

        doThrow(new RuntimeException("Utilisateur non trouvé"))
                .when(adminService).envoyerEmail(999, "Message de test");

        mockMvc.perform(post("/api/admin/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé"));
    }

    // Tests pour la création d'administrateur
    @Test
    void testCreerAdministrateur_Success() throws Exception {
        when(adminService.creerAdministrateur(any(Administrateur.class))).thenReturn(adminTest);

        mockMvc.perform(post("/api/admin/administrateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Admin Test"))
                .andExpect(jsonPath("$.email").value("admin@example.com"));

        verify(adminService, times(1)).creerAdministrateur(any(Administrateur.class));
    }

    @Test
    void testCreerAdministrateur_BadRequest() throws Exception {
        when(adminService.creerAdministrateur(any(Administrateur.class)))
                .thenThrow(new RuntimeException("Erreur lors de la création"));

        mockMvc.perform(post("/api/admin/administrateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminTest)))
                .andExpect(status().isBadRequest());

        verify(adminService, times(1)).creerAdministrateur(any(Administrateur.class));
    }

    @Test
    void testObtenirTousLesAdministrateurs() throws Exception {
        List<Administrateur> administrateurs = Arrays.asList(adminTest);
        when(adminService.obtenirTousLesAdministrateurs()).thenReturn(administrateurs);

        mockMvc.perform(get("/api/admin/administrateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Admin Test"));

        verify(adminService, times(1)).obtenirTousLesAdministrateurs();
    }

    // Tests pour les cas limites et validation
    @Test
    void testValiderAnnonce_SansMotif() throws Exception {
        Map<String, Object> decision = new HashMap<>();
        decision.put("accepter", false);
        // Pas de motif spécifié

        when(adminService.validerAnnonce(1, false, "")).thenReturn(annonceTest);

        mockMvc.perform(put("/api/admin/annonces/1/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk());

        verify(adminService, times(1)).validerAnnonce(1, false, "");
    }

    @Test
    void testRefuserPaiement_SansMotif() throws Exception {
        Map<String, String> motifMap = new HashMap<>();
        // Pas de motif spécifié

        when(adminService.refuserPaiement(1, "Aucun motif spécifié")).thenReturn(paiementTest);

        mockMvc.perform(put("/api/admin/paiements/1/refuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motifMap)))
                .andExpect(status().isOk());

        verify(adminService, times(1)).refuserPaiement(1, "Aucun motif spécifié");
    }

    @Test
    void testEnvoyerEmail_DonnéesIncomplètes() throws Exception {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("utilisateurId", 1);
        // Pas de message

        doThrow(new RuntimeException("Message manquant"))
                .when(adminService).envoyerEmail(eq(1), isNull());

        mockMvc.perform(post("/api/admin/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Message manquant"));
    }
}