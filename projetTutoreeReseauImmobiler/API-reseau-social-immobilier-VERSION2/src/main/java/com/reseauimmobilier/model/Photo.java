package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_photo") 
    private Integer  idPhoto;
    
    @Column(name="id_annonce" ,nullable = false)
    private Integer  idAnnonce;
    
    @Lob
    @Column(name="photo", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] photo;
    
    @Column(name="est_video", nullable = true)
    private Boolean estVideo = false;  // FALSE pour photo, TRUE pour vidéo
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonce",referencedColumnName = "id_annonce", insertable = false, updatable = false)
    private Annonce annonce;
}