	package com.reseauimmobilier.repository;
	
	import com.reseauimmobilier.model.Annonceur;
	import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.data.jpa.repository.Query;
	import org.springframework.data.repository.query.Param;
	import org.springframework.stereotype.Repository;
	
	import java.util.Optional;
	
	@Repository
	public interface AnnonceurRepository extends JpaRepository<Annonceur, Integer> {
	    
	    Optional<Annonceur> findByEmail(String email);
	    
	    @Query("SELECT a FROM Annonceur a WHERE a.idUtilisateur = :id")
	    Optional<Annonceur> findByIdUtilisateur(@Param("id") Integer id);
	    
	    boolean existsByEmail(String email);
	    
	    @Query("SELECT COUNT(ann) FROM Annonce ann WHERE ann.annonceur.idUtilisateur = :annonceId")
	    Long countAnnoncesByAnnonceurId(@Param("annonceId") Integer annonceId);
	}