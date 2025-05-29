package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Message;
import com.reseauimmobilier.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Trouver les messages reçus par un utilisateur
    List<Message> findByDestinataire_IdUtilisateurOrderByDateEnvoiDesc(Integer destinataireId);

    // Trouver les messages envoyés par un utilisateur
    List<Message> findByExpediteur_IdUtilisateurOrderByDateEnvoiDesc(Integer expediteurId);

    // Trouver les messages non lus d'un utilisateur
    List<Message> findByDestinataire_IdUtilisateurAndLuFalseOrderByDateEnvoiDesc(Integer destinataireId);

    // Compter les messages non lus d'un utilisateur
    long countByDestinataire_IdUtilisateurAndLuFalse(Integer destinataireId);

    // Conversation entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE " +
           "(m.expediteur.idUtilisateur = :user1 AND m.destinataire.idUtilisateur = :user2) OR " +
           "(m.expediteur.idUtilisateur = :user2 AND m.destinataire.idUtilisateur = :user1) " +
           "ORDER BY m.dateEnvoi ASC")
    List<Message> findConversation(@Param("user1") Integer user1, @Param("user2") Integer user2);

    // Tous les messages d'un utilisateur (envoyés et reçus)
    @Query("SELECT m FROM Message m WHERE " +
           "m.expediteur.idUtilisateur = :userId OR m.destinataire.idUtilisateur = :userId " +
           "ORDER BY m.dateEnvoi DESC")
    List<Message> findAllMessagesForUser(@Param("userId") Integer userId);

    // Messages entre deux utilisateurs dans une période
    @Query("SELECT m FROM Message m WHERE " +
    	       "((m.expediteur.idUtilisateur = :user1 AND m.destinataire.idUtilisateur = :user2) OR " +
    	       "(m.expediteur.idUtilisateur = :user2 AND m.destinataire.idUtilisateur = :user1)) " +
    	       "AND m.dateEnvoi BETWEEN :dateDebut AND :dateFin " +
    	       "ORDER BY m.dateEnvoi ASC")
    	List<Message> findConversationBetweenDates(@Param("user1") Integer user1, 
    	                                           @Param("user2") Integer user2,
    	                                           @Param("dateDebut") LocalDateTime dateDebut,
    	                                           @Param("dateFin") LocalDateTime dateFin);


    // Messages récents (dernières 24h par exemple)
    @Query("SELECT m FROM Message m WHERE m.dateEnvoi >= :dateDebut ORDER BY m.dateEnvoi DESC")
    List<Message> findMessagesRecents(@Param("dateDebut") LocalDateTime dateDebut);

    // Utilisateurs avec qui un utilisateur a échangé des messages
    @Query("SELECT DISTINCT CASE " +
           "WHEN m.expediteur.idUtilisateur = :userId THEN m.destinataire " +
           "ELSE m.expediteur END " +
           "FROM Message m WHERE m.expediteur.idUtilisateur = :userId OR m.destinataire.idUtilisateur = :userId")
    List<Utilisateur> findConversationPartners(@Param("userId") Integer userId);
}