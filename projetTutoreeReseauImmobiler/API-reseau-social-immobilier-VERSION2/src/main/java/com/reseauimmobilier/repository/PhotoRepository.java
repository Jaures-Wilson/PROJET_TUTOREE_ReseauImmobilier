package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    
    // Trouver toutes les photos d'une annonce
    List<Photo> findByIdAnnonce(Integer  idAnnonce);
    
    // Trouver seulement les photos (pas les vidéos) d'une annonce
    List<Photo> findByIdAnnonceAndEstVideo(Integer  idAnnonce, Boolean estVideo);
    
    // Compter le nombre de photos/vidéos d'une annonce
    @Query("SELECT COUNT(p) FROM Photo p WHERE p.idAnnonce = :idAnnonce")
    long countByIdAnnonce(@Param("idAnnonce") Integer  idAnnonce);
    
    // Compter seulement les photos d'une annonce
    long countByIdAnnonceAndEstVideo(Integer  idAnnonce, Boolean estVideo);
    
    // Supprimer toutes les photos d'une annonce
    void deleteByIdAnnonce(Integer  idAnnonce);
    
    // Vérifier si une annonce a des photos
    boolean existsByIdAnnonce(Integer  idAnnonce);
}