package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.Paiement;
import com.reseauimmobilier.service.PaiementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaiementController.class)
class PaiementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaiementService paiementService;

    @Autowired
    private ObjectMapper objectMapper;

    private Paiement paiementTest;
    private List<Paiement> listePaiements;

    @BeforeEach
    void setUp() {
        // Création d'un paiement de test
        paiementTest = new Paiement();
        paiementTest.setIdPaiement(1);
        paiementTest.setMontant(100000);
        paiementTest.setMoyenPaiement(Paiement.MoyenPaiement.MoMo);
        paiementTest.setStatut(Paiement.StatutPaiement.EN_ATTENTE);
        paiementTest.setTypeContrat(Paiement.TypeContrat.VENTE);
        paiementTest.setDatePaiement(LocalDateTime.now());
        paiementTest.setLu(false);
        paiementTest.setCapturePhotoPreuvePaiement(new byte[]{1, 2, 3});

        // Liste de paiements pour les tests
        listePaiements = Arrays.asList(paiementTest);
    }

    @Test
    void testEffectuerPaiement_Success() throws Exception {
        // Mock du service
        when(paiementService.effectuerPaiement(any(Paiement.class), any())).thenReturn(paiementTest);

        // Création du fichier multipart
        MockMultipartFile photoPreuve = new MockMultipartFile(
                "photoPreuve", 
                "test.jpg", 
                "image/jpeg", 
                "test image content".getBytes()
        );

        // Test de la requête
        mockMvc.perform(multipart("/api/paiements/acquereur/1")
                .file(photoPreuve)
                .param("montant", "100000")
                .param("moyenPaiement", "MoMo")
                .param("typeContrat", "VENTE")
                .param("annonceId", "1")
                .param("annonceurId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPaiement").value(1))
                .andExpect(jsonPath("$.montant").value(100000))
                .andExpect(jsonPath("$.moyenPaiement").value("MoMo"))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));

        // Vérification que le service a été appelé
        verify(paiementService, times(1)).effectuerPaiement(any(Paiement.class), any());
    }

    @Test
    void testEffectuerPaiement_Error() throws Exception {
        // Mock du service pour lever une exception
        when(paiementService.effectuerPaiement(any(Paiement.class), any()))
                .thenThrow(new RuntimeException("Erreur de paiement"));

        MockMultipartFile photoPreuve = new MockMultipartFile(
                "photoPreuve", 
                "test.jpg", 
                "image/jpeg", 
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/paiements/acquereur/1")
                .file(photoPreuve)
                .param("montant", "100000")
                .param("moyenPaiement", "MoMo")
                .param("typeContrat", "VENTE")
                .param("annonceId", "1")
                .param("annonceurId", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Erreur de paiement"));
    }

    @Test
    void testObtenirPaiement_Found() throws Exception {
        when(paiementService.obtenirPaiementParId(1)).thenReturn(Optional.of(paiementTest));

        mockMvc.perform(get("/api/paiements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaiement").value(1))
                .andExpect(jsonPath("$.montant").value(100000));

        verify(paiementService, times(1)).obtenirPaiementParId(1);
    }

    @Test
    void testObtenirPaiement_NotFound() throws Exception {
        when(paiementService.obtenirPaiementParId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/paiements/999"))
                .andExpect(status().isNotFound());

        verify(paiementService, times(1)).obtenirPaiementParId(999);
    }

    @Test
    void testObtenirTousLesPaiements() throws Exception {
        when(paiementService.obtenirTousLesPaiements()).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idPaiement").value(1));

        verify(paiementService, times(1)).obtenirTousLesPaiements();
    }

    @Test
    void testObtenirPaiementsAcquereur() throws Exception {
        when(paiementService.obtenirPaiementsParAcquereur(1)).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/acquereur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].montant").value(100000));

        verify(paiementService, times(1)).obtenirPaiementsParAcquereur(1);
    }

    @Test
    void testObtenirPaiementsAnnonceur() throws Exception {
        when(paiementService.obtenirPaiementsParAnnonceur(2)).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/annonceur/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsParAnnonceur(2);
    }

    @Test
    void testObtenirPaiementsEnAttente() throws Exception {
        when(paiementService.obtenirPaiementsEnAttente()).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/en-attente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsEnAttente();
    }

    @Test
    void testObtenirPaiementsConfirmes() throws Exception {
        when(paiementService.obtenirPaiementsConfirmes()).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/confirmes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsConfirmes();
    }

    @Test
    void testObtenirPaiementsParStatut_Valid() throws Exception {
        when(paiementService.obtenirPaiementsParStatut(Paiement.StatutPaiement.EN_ATTENTE))
                .thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/statut/EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsParStatut(Paiement.StatutPaiement.EN_ATTENTE);
    }

    @Test
    void testObtenirPaiementsParStatut_Invalid() throws Exception {
        mockMvc.perform(get("/api/paiements/statut/INVALID_STATUT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Statut de paiement invalide"));

        verify(paiementService, never()).obtenirPaiementsParStatut(any());
    }

    @Test
    void testValiderPaiement_Success() throws Exception {
        Paiement paiementValide = new Paiement();
        paiementValide.setIdPaiement(1);
        paiementValide.setStatut(Paiement.StatutPaiement.CONFIRMEE);

        when(paiementService.validerPaiement(1)).thenReturn(paiementValide);

        mockMvc.perform(put("/api/paiements/1/valider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaiement").value(1))
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"));

        verify(paiementService, times(1)).validerPaiement(1);
    }

    @Test
    void testValiderPaiement_Error() throws Exception {
        when(paiementService.validerPaiement(999))
                .thenThrow(new RuntimeException("Paiement introuvable"));

        mockMvc.perform(put("/api/paiements/999/valider"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Paiement introuvable"));
    }

    @Test
    void testRefuserPaiement() throws Exception {
        Paiement paiementRefuse = new Paiement();
        paiementRefuse.setIdPaiement(1);
        paiementRefuse.setStatut(Paiement.StatutPaiement.REFUSE);

        when(paiementService.refuserPaiement(1, "Motif de refus")).thenReturn(paiementRefuse);

        String requestBody = objectMapper.writeValueAsString(Map.of("motif", "Motif de refus"));

        mockMvc.perform(put("/api/paiements/1/refuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSE"));

        verify(paiementService, times(1)).refuserPaiement(1, "Motif de refus");
    }

    @Test
    void testMarquerCommeLu() throws Exception {
        Paiement paiementLu = new Paiement();
        paiementLu.setIdPaiement(1);
        paiementLu.setLu(true);

        when(paiementService.marquerCommeLu(1)).thenReturn(paiementLu);

        mockMvc.perform(put("/api/paiements/1/lu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lu").value(true));

        verify(paiementService, times(1)).marquerCommeLu(1);
    }

    @Test
    void testSimulerVirement() throws Exception {
        doNothing().when(paiementService).simulerVirementVersAnnonceur(1);

        mockMvc.perform(post("/api/paiements/1/virement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Virement effectué avec succès"));

        verify(paiementService, times(1)).simulerVirementVersAnnonceur(1);
    }

    @Test
    void testSupprimerPaiement() throws Exception {
        doNothing().when(paiementService).supprimerPaiement(1);

        mockMvc.perform(delete("/api/paiements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paiement supprimé avec succès"));

        verify(paiementService, times(1)).supprimerPaiement(1);
    }

    @Test
    void testObtenirStatistiques() throws Exception {
        Map<String, Object> stats = Map.of(
                "totalPaiements", 10,
                "montantTotal", 1000000,
                "paiementsConfirmes", 8
        );

        when(paiementService.obtenirStatistiquesPaiements()).thenReturn(stats);

        mockMvc.perform(get("/api/paiements/statistiques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPaiements").value(10))
                .andExpect(jsonPath("$.montantTotal").value(1000000));

        verify(paiementService, times(1)).obtenirStatistiquesPaiements();
    }

    @Test
    void testObtenirRevenusAnnonceur() throws Exception {
        when(paiementService.obtenirRevenusTotalAnnonceur(2)).thenReturn(500000L);

        mockMvc.perform(get("/api/paiements/revenus/annonceur/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annonceurId").value(2))
                .andExpect(jsonPath("$.revenus").value(500000));

        verify(paiementService, times(1)).obtenirRevenusTotalAnnonceur(2);
    }

    @Test
    void testObtenirDepensesAcquereur() throws Exception {
        when(paiementService.obtenirDepensesTotalAcquereur(1)).thenReturn(200000L);

        mockMvc.perform(get("/api/paiements/depenses/acquereur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acquereurId").value(1))
                .andExpect(jsonPath("$.depenses").value(200000));

        verify(paiementService, times(1)).obtenirDepensesTotalAcquereur(1);
    }

    @Test
    void testObtenirPaiementsRecents() throws Exception {
        when(paiementService.obtenirPaiementsRecents(30)).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/recents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsRecents(30);
    }

    @Test
    void testObtenirPaiementsRecents_CustomDays() throws Exception {
        when(paiementService.obtenirPaiementsRecents(7)).thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/recents?jours=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsRecents(7);
    }

    @Test
    void testObtenirPaiementsParPeriode() throws Exception {
        when(paiementService.obtenirPaiementsParPeriode(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(listePaiements);

        mockMvc.perform(get("/api/paiements/periode")
                .param("dateDebut", "2024-01-01T00:00:00")
                .param("dateFin", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(paiementService, times(1)).obtenirPaiementsParPeriode(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testVerifierExistencePaiement_True() throws Exception {
        when(paiementService.paiementExistePourAnnonce(1, 1)).thenReturn(true);

        mockMvc.perform(get("/api/paiements/existe/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true));

        verify(paiementService, times(1)).paiementExistePourAnnonce(1, 1);
    }

    @Test
    void testVerifierExistencePaiement_False() throws Exception {
        when(paiementService.paiementExistePourAnnonce(1, 1)).thenReturn(false);

        mockMvc.perform(get("/api/paiements/existe/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(false));

        verify(paiementService, times(1)).paiementExistePourAnnonce(1, 1);
    }

    @Test
    void testCreerPaiementSimple() throws Exception {
        when(paiementService.effectuerPaiement(any(Paiement.class), isNull())).thenReturn(paiementTest);

        Paiement paiementSimple = new Paiement();
        paiementSimple.setMontant(50000);
        paiementSimple.setMoyenPaiement(Paiement.MoyenPaiement.OM);
        paiementSimple.setTypeContrat(Paiement.TypeContrat.PROMESSE_VENTE);

        String requestBody = objectMapper.writeValueAsString(paiementSimple);

        mockMvc.perform(post("/api/paiements/simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPaiement").value(1));

        verify(paiementService, times(1)).effectuerPaiement(any(Paiement.class), isNull());
    }

    @Test
    void testCreerPaiementSimple_Error() throws Exception {
        when(paiementService.effectuerPaiement(any(Paiement.class), isNull()))
                .thenThrow(new RuntimeException("Erreur création paiement"));

        Paiement paiementSimple = new Paiement();
        paiementSimple.setMontant(50000);

        String requestBody = objectMapper.writeValueAsString(paiementSimple);

        mockMvc.perform(post("/api/paiements/simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Erreur création paiement"));
    }

    @Test
    void testGestionErreurService() throws Exception {
        when(paiementService.obtenirTousLesPaiements())
                .thenThrow(new RuntimeException("Erreur base de données"));

        mockMvc.perform(get("/api/paiements"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Erreur base de données"));
    }
}