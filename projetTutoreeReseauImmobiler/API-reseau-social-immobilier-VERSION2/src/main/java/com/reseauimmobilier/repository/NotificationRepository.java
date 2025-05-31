package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Notification;
import com.reseauimmobilier.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByDestinataireOrderByDateCreationDesc(Utilisateur destinataire);

    List<Notification> findByDestinataireAndLuOrderByDateCreationDesc(Utilisateur destinataire, Boolean lu);

    List<Notification> findByDestinataireIdUtilisateurOrderByDateCreationDesc(Integer destinataireId);

    List<Notification> findByDestinataireIdUtilisateurAndLuOrderByDateCreationDesc(Integer destinataireId, Boolean lu);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.destinataire.idUtilisateur = :utilisateurId AND n.lu = false")
    Long countNotificationsNonLues(@Param("utilisateurId") Integer utilisateurId);

    List<Notification> findByExpediteurOrderByDateCreationDesc(Utilisateur expediteur);

    List<Notification> findByTypeOrderByDateCreationDesc(Notification.Type type);

    @Query("SELECT n FROM Notification n WHERE n.destinataire.idUtilisateur = :utilisateurId AND n.type = :type ORDER BY n.dateCreation DESC")
    List<Notification> findByDestinataireAndType(@Param("utilisateurId") Integer utilisateurId, @Param("type") Notification.Type type);

    // Pour l'administrateur - récupérer toutes les notifications
    @Query("SELECT n FROM Notification n ORDER BY n.dateCreation DESC")
    List<Notification> findAllOrderByDateCreationDesc();

    // Compter toutes les notifications non lues pour l'admin
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.lu = false")
    Long countTotalNotificationsNonLues();
}