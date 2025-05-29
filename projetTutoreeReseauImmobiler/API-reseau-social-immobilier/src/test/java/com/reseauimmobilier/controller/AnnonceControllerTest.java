package com.reseauimmobilier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reseauimmobilier.model.Annonce;
import com.reseauimmobilier.service.AnnonceService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnonceController.class)
class AnnonceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnonceService annonceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Annonce annonceTest;
    private List<Annonce> listAnnonces;

    @BeforeEach
    void setUp() {
        annonceTest = new Annonce();
        annonceTest.setIdAnnonce(1);
        annonceTest.setTitre("Studio à Bonamoussadi");
        annonceTest.setDescription("Studio moderne et bien équipé");
        annonceTest.setPrixMensuel(100000);
        annonceTest.setLocalisation("Douala");
        annonceTest.setStatus(Annonce.Status.DISPONIBLE);
        annonceTest.setDatePublication(LocalDateTime.now());
        annonceTest.setTarifReservation(10000);
        annonceTest.setNombreVue(5);
        annonceTest.setNombreFavori(3);
        annonceTest.setNombreVisite(2);

        Annonce annonce2 = new Annonce();
        annonce2.setIdAnnonce(2);
        annonce2.setTitre("Appartement à Bastos");
        annonce2.setDescription("Grand appartement 3 chambres");
        annonce2.setPrixMensuel(250000);
        annonce2.setLocalisation("Yaoundé");
        annonce2.setStatus(Annonce.Status.RÉSERVÉ);

        listAnnonces = Arrays.asList(annonceTest, annonce2);
    }

    @Test
    void testCreerAnnonce_Success() throws Exception {
        when(annonceService.creerAnnonce(any(Annonce.class), eq(1)))
                .thenReturn(annonceTest);

        mockMvc.perform(post("/api/annonces")
                        .param("idAnnonceur", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAnnonce").value(1))
                .andExpect(jsonPath("$.titre").value("Studio à Bonamoussadi"))
                .andExpect(jsonPath("$.prixMensuel").value(100000))
                .andExpect(jsonPath("$.localisation").value("Douala"))
                .andExpect(jsonPath("$.status").value("DISPONIBLE"));
    }

    @Test
    void testCreerAnnonce_Error() throws Exception {
        when(annonceService.creerAnnonce(any(Annonce.class), eq(1)))
                .thenThrow(new RuntimeException("Annonceur non trouvé"));

        mockMvc.perform(post("/api/annonces")
                        .param("idAnnonceur", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceTest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Annonceur non trouvé"));
    }

    @Test
    void testObtenirToutesLesAnnonces() throws Exception {
        when(annonceService.obtenirToutesLesAnnonces()).thenReturn(listAnnonces);

        mockMvc.perform(get("/api/annonces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titre").value("Studio à Bonamoussadi"))
                .andExpect(jsonPath("$[1].titre").value("Appartement à Bastos"));
    }

    @Test
    void testObtenirAnnonceParId_Found() throws Exception {
        when(annonceService.obtenirAnnonceParId(1)).thenReturn(Optional.of(annonceTest));

        mockMvc.perform(get("/api/annonces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAnnonce").value(1))
                .andExpect(jsonPath("$.titre").value("Studio à Bonamoussadi"))
                .andExpect(jsonPath("$.nombreVue").value(5));
    }

    @Test
    void testObtenirAnnonceParId_NotFound() throws Exception {
        when(annonceService.obtenirAnnonceParId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/annonces/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenirAnnonceParId_AvecIncrementVue() throws Exception {
        when(annonceService.obtenirAnnonceParIdAvecVue(1)).thenReturn(Optional.of(annonceTest));

        mockMvc.perform(get("/api/annonces/1")
                        .param("incrementerVue", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAnnonce").value(1));

        verify(annonceService).obtenirAnnonceParIdAvecVue(1);
        verify(annonceService, never()).obtenirAnnonceParId(anyInt());
    }

    @Test
    void testModifierAnnonce_Success() throws Exception {
        Annonce annonceModifiee = new Annonce();
        annonceModifiee.setTitre("Studio Modifié");
        annonceModifiee.setPrixMensuel(120000);

        when(annonceService.modifierAnnonce(eq(1), any(Annonce.class), eq(1)))
                .thenReturn(annonceTest);

        mockMvc.perform(put("/api/annonces/1")
                        .param("idAnnonceur", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceModifiee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAnnonce").value(1));
    }

    @Test
    void testModifierAnnonce_Error() throws Exception {
        when(annonceService.modifierAnnonce(eq(1), any(Annonce.class), eq(2)))
                .thenThrow(new RuntimeException("Vous n'êtes pas autorisé à modifier cette annonce"));

        mockMvc.perform(put("/api/annonces/1")
                        .param("idAnnonceur", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceTest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Vous n'êtes pas autorisé à modifier cette annonce"));
    }

    @Test
    void testSupprimerAnnonce_Success() throws Exception {
        doNothing().when(annonceService).supprimerAnnonce(1, 1);

        mockMvc.perform(delete("/api/annonces/1")
                        .param("idAnnonceur", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Annonce supprimée avec succès"));
    }

    @Test
    void testSupprimerAnnonce_Error() throws Exception {
        doThrow(new RuntimeException("Annonce non trouvée"))
                .when(annonceService).supprimerAnnonce(99, 1);

        mockMvc.perform(delete("/api/annonces/99")
                        .param("idAnnonceur", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Annonce non trouvée"));
    }

    @Test
    void testChangerStatutAnnonce_Success() throws Exception {
        annonceTest.setStatus(Annonce.Status.RÉSERVÉ);
        when(annonceService.changerStatutAnnonce(1, Annonce.Status.RÉSERVÉ, 1))
                .thenReturn(annonceTest);

        mockMvc.perform(put("/api/annonces/1/statut")
                        .param("status", "RÉSERVÉ")  
                        .param("idAnnonceur", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RÉSERVÉ"));
    }


    @Test
    void testChangerStatutAnnonce_StatutInvalideSpring() throws Exception {
        mockMvc.perform(put("/api/annonces/1/statut")
                        .param("status", "STATUT_INVALIDE") 
                        .param("idAnnonceur", "1"))
                .andExpect(status().isBadRequest());
    }




    @Test
    void testRechercherAnnonces_AvecFiltres() throws Exception {
        when(annonceService.rechercherAnnonces("Douala", 50000, 150000, Annonce.Status.DISPONIBLE, "studio"))
                .thenReturn(Arrays.asList(annonceTest));

        mockMvc.perform(get("/api/annonces/recherche")
                        .param("localisation", "Douala")
                        .param("prixMin", "50000")
                        .param("prixMax", "150000")
                        .param("status", "DISPONIBLE")
                        .param("motCle", "studio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].localisation").value("Douala"));
    }

    @Test
    void testRechercherAnnonces_SansFiltres() throws Exception {
        when(annonceService.rechercherAnnonces(null, null, null, null, null))
                .thenReturn(listAnnonces);

        mockMvc.perform(get("/api/annonces/recherche"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testObtenirAnnoncesDisponibles() throws Exception {
        when(annonceService.obtenirAnnoncesDisponibles())
                .thenReturn(Arrays.asList(annonceTest));

        mockMvc.perform(get("/api/annonces/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("DISPONIBLE"));
    }

    @Test
    void testObtenirAnnoncesParPrixCroissant() throws Exception {
        when(annonceService.obtenirAnnoncesParPrixCroissant()).thenReturn(listAnnonces);

        mockMvc.perform(get("/api/annonces/prix-croissant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testObtenirAnnoncesParPrixDecroissant() throws Exception {
        when(annonceService.obtenirAnnoncesParPrixDecroissant()).thenReturn(listAnnonces);

        mockMvc.perform(get("/api/annonces/prix-decroissant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testObtenirAnnoncesPopulaires() throws Exception {
        when(annonceService.obtenirAnnoncesPopulaires()).thenReturn(Arrays.asList(annonceTest));

        mockMvc.perform(get("/api/annonces/populaires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testObtenirAnnoncesRecentes() throws Exception {
        when(annonceService.obtenirAnnoncesRecentes()).thenReturn(Arrays.asList(annonceTest));

        mockMvc.perform(get("/api/annonces/recentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testRechercherParMotCle() throws Exception {
        when(annonceService.rechercherParMotCle("studio")).thenReturn(Arrays.asList(annonceTest));

        mockMvc.perform(get("/api/annonces/mot-cle")
                        .param("motCle", "studio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").value("Studio à Bonamoussadi"));
    }

    @Test
    void testObtenirStatistiquesAnnonce_Success() throws Exception {
        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("nombreVue", 5);
        statistiques.put("nombreFavori", 3);
        statistiques.put("nombreVisite", 2);
        statistiques.put("titre", "Studio à Bonamoussadi");

        when(annonceService.obtenirStatistiquesAnnonce(1, 1)).thenReturn(statistiques);

        mockMvc.perform(get("/api/annonces/1/statistiques")
                        .param("idAnnonceur", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreVue").value(5))
                .andExpect(jsonPath("$.nombreFavori").value(3))
                .andExpect(jsonPath("$.nombreVisite").value(2))
                .andExpect(jsonPath("$.titre").value("Studio à Bonamoussadi"));
    }

    @Test
    void testObtenirStatistiquesAnnonce_Error() throws Exception {
        when(annonceService.obtenirStatistiquesAnnonce(1, 2))
                .thenThrow(new RuntimeException("Accès non autorisé"));

        mockMvc.perform(get("/api/annonces/1/statistiques")
                        .param("idAnnonceur", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Accès non autorisé"));
    }

    @Test
    void testIncrementerFavoris_Success() throws Exception {
        doNothing().when(annonceService).incrementerFavoris(1);

        mockMvc.perform(put("/api/annonces/1/favoris/incrementer"))
                .andExpect(status().isOk())
                .andExpect(content().string("Favori ajouté"));
    }

    @Test
    void testIncrementerFavoris_Error() throws Exception {
        doThrow(new RuntimeException("Annonce non trouvée"))
                .when(annonceService).incrementerFavoris(99);

        mockMvc.perform(put("/api/annonces/99/favoris/incrementer"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Annonce non trouvée"));
    }

    @Test
    void testDecrementerFavoris_Success() throws Exception {
        doNothing().when(annonceService).decrementerFavoris(1);

        mockMvc.perform(put("/api/annonces/1/favoris/decrementer"))
                .andExpect(status().isOk())
                .andExpect(content().string("Favori retiré"));
    }

    @Test
    void testDecrementerFavoris_Error() throws Exception {
        doThrow(new RuntimeException("Annonce non trouvée"))
                .when(annonceService).decrementerFavoris(99);

        mockMvc.perform(put("/api/annonces/99/favoris/decrementer"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Annonce non trouvée"));
    }

    @Test
    void testIncrementerVisites_Success() throws Exception {
        doNothing().when(annonceService).incrementerVisites(1);

        mockMvc.perform(put("/api/annonces/1/visites/incrementer"))
                .andExpect(status().isOk())
                .andExpect(content().string("Visite ajoutée"));
    }

    @Test
    void testIncrementerVisites_Error() throws Exception {
        doThrow(new RuntimeException("Annonce non trouvée"))
                .when(annonceService).incrementerVisites(99);

        mockMvc.perform(put("/api/annonces/99/visites/incrementer"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur: Annonce non trouvée"));
    }

    @Test
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(options("/api/annonces")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk());
    }

    @Test
    void testParametresRequis_CreerAnnonce() throws Exception {
        mockMvc.perform(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceTest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParametresRequis_ModifierAnnonce() throws Exception {
        mockMvc.perform(put("/api/annonces/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(annonceTest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParametresRequis_SupprimerAnnonce() throws Exception {
        mockMvc.perform(delete("/api/annonces/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParametresRequis_ChangerStatut() throws Exception {
        mockMvc.perform(put("/api/annonces/1/statut"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParametresRequis_StatistiquesAnnonce() throws Exception {
        mockMvc.perform(get("/api/annonces/1/statistiques"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParametresRequis_RechercheMotCle() throws Exception {
        mockMvc.perform(get("/api/annonces/mot-cle"))
                .andExpect(status().isBadRequest());
    }
}