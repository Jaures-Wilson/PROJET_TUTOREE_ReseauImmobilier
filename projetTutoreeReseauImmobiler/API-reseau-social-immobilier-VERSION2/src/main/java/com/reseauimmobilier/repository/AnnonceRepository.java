package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Annonce;
import com.reseauimmobilier.model.Annonceur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Integer > {
    
    // Rechercher par statut
    List<Annonce> findByStatus(String status);
    
    // Rechercher par annonceur
    List<Annonce> findByAnnonceur(Annonceur annonceur);
    
    List<Annonce> findByAnnonceurIdUtilisateur(Integer  idAnnonceur);
    
    // Rechercher par localisation
    List<Annonce> findByLocalisationContainingIgnoreCase(String localisation);
    
    // Rechercher par prix
    List<Annonce> findByPrixMensuelBetween(Integer prixMin, Integer prixMax);
    
    List<Annonce> findByPrixMensuelLessThanEqual(Integer prixMax);
    
    List<Annonce> findByPrixMensuelGreaterThanEqual(Integer prixMin);
    
    // Rechercher par titre ou description
    @Query("SELECT a FROM Annonce a WHERE LOWER(a.titre) LIKE LOWER(CONCAT('%', :mot, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :mot, '%'))")
    List<Annonce> rechercherParMotCle(@Param("mot") String motCle);
    
    // Recherche combinée
    @Query("SELECT a FROM Annonce a WHERE " +
           "(:localisation IS NULL OR LOWER(a.localisation) LIKE LOWER(CONCAT('%', :localisation, '%'))) AND " +
           "(:prixMin IS NULL OR a.prixMensuel >= :prixMin) AND " +
           "(:prixMax IS NULL OR a.prixMensuel <= :prixMax) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:motCle IS NULL OR LOWER(a.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :motCle, '%')))")
    List<Annonce> rechercherAvecFiltres(
            @Param("localisation") String localisation,
            @Param("prixMin") Integer prixMin,
            @Param("prixMax") Integer prixMax,
            @Param("status") Annonce.Status status, 
            @Param("motCle") String motCle
    );
    
    // Annonces les plus populaires
    List<Annonce> findTop10ByOrderByNombreVueDesc();
    
    List<Annonce> findTop10ByOrderByNombreFavoriDesc();
    
    // Annonces récentes
    List<Annonce> findTop10ByOrderByDatePublicationDesc();
    
    // Compter par statut
    long countByStatus(String status);
    
    // Compter par annonceur
    long countByAnnonceurIdUtilisateur(Integer  idAnnonceur);
    
    // Statistiques pour un annonceur
    @Query("SELECT SUM(a.nombreVue) FROM Annonce a WHERE a.annonceur.idUtilisateur = :idAnnonceur")
    Long sommeVuesParAnnonceur(@Param("idAnnonceur") Integer idAnnonceur);

    @Query("SELECT SUM(a.nombreFavori) FROM Annonce a WHERE a.annonceur.idUtilisateur = :idAnnonceur")
    Long sommeFavorisParAnnonceur(@Param("idAnnonceur") Integer idAnnonceur);

    @Query("SELECT SUM(a.nombreVisite) FROM Annonce a WHERE a.annonceur.idUtilisateur = :idAnnonceur")
    Long sommeVisitesParAnnonceur(@Param("idAnnonceur") Integer idAnnonceur);


    
    // Annonces par prix croissant/décroissant
    List<Annonce> findByStatusOrderByPrixMensuelAsc(String status);
    
    List<Annonce> findByStatusOrderByPrixMensuelDesc(String status);
    
    // Vérifier si une annonce existe pour un annonceur
    boolean existsByIdAnnonceAndAnnonceurIdUtilisateur(Integer  idAnnonce, Integer  idAnnonceur);
}