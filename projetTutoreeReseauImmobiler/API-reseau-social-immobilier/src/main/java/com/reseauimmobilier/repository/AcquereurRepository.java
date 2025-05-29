package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Acquereur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcquereurRepository extends JpaRepository<Acquereur, Integer> {
    
}