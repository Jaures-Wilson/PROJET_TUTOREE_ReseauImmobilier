package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Photo;
import com.reseauimmobilier.repository.PhotoRepository;
import com.reseauimmobilier.repository.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "http://localhost:3000")
public class PhotoController {

    @Autowired
    private PhotoRepository photoRepository;
    
    @Autowired
    private AnnonceRepository annonceRepository;

    // POST /api/photos - Ajouter une photo à une annonce
    @PostMapping
    public ResponseEntity<?> ajouterPhoto(@RequestParam("file") MultipartFile file,
                                        @RequestParam("idAnnonce") Integer  idAnnonce,
                                        @RequestParam(value = "estVideo", defaultValue = "false") Boolean estVideo) {
        try {
            // Vérifier que l'annonce existe
            if (!annonceRepository.existsById(idAnnonce)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Annonce non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Vérifier le nombre de photos existantes (max 5)
            long nombrePhotos = photoRepository.countByIdAnnonce(idAnnonce);
            if (nombrePhotos >= 5) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Maximum 5 photos/vidéos par annonce");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Vérifier que le fichier n'est pas vide
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Fichier vide");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Créer et sauvegarder la photo
            Photo photo = new Photo();
            photo.setIdAnnonce(idAnnonce);
            photo.setPhoto(file.getBytes());
            photo.setEstVideo(estVideo);

            Photo photoSauvegardee = photoRepository.save(photo);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Photo ajoutée avec succès");
            response.put("idPhoto", photoSauvegardee.getIdPhoto());
            response.put("idAnnonce", photoSauvegardee.getIdAnnonce());
            response.put("estVideo", photoSauvegardee.getEstVideo());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la lecture du fichier");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/photos/annonce/{idAnnonce} - Récupérer toutes les photos d'une annonce
    @GetMapping("/annonce/{idAnnonce}")
    public ResponseEntity<?> obtenirPhotosParAnnonce(@PathVariable Integer  idAnnonce) {
        try {
            List<Photo> photos = photoRepository.findByIdAnnonce(idAnnonce);
            
            Map<String, Object> response = new HashMap<>();
            response.put("photos", photos);
            response.put("total", photos.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des photos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/photos/{id} - Récupérer une photo spécifique
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> obtenirPhoto(@PathVariable Integer id) {
        try {
            Optional<Photo> photoOpt = photoRepository.findById(id);
            
            if (!photoOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Photo photo = photoOpt.get();
            
            HttpHeaders headers = new HttpHeaders();
            if (photo.getEstVideo()) {
                headers.setContentType(MediaType.parseMediaType("video/mp4"));
            } else {
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(photo.getPhoto());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/photos/{id} - Supprimer une photo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerPhoto(@PathVariable Integer id) {
        try {
            if (!photoRepository.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Photo non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            photoRepository.deleteById(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Photo supprimée avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la suppression");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // DELETE /api/photos/annonce/{idAnnonce} - Supprimer toutes les photos d'une annonce
    @DeleteMapping("/annonce/{idAnnonce}")
    public ResponseEntity<?> supprimerToutesPhotosAnnonce(@PathVariable Integer  idAnnonce) {
        try {
            List<Photo> photos = photoRepository.findByIdAnnonce(idAnnonce);
            
            if (photos.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Aucune photo trouvée pour cette annonce");
                return ResponseEntity.ok(response);
            }

            photoRepository.deleteByIdAnnonce(idAnnonce);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Toutes les photos de l'annonce ont été supprimées");
            response.put("nombrePhotosSupprimes", photos.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la suppression des photos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/photos/annonce/{idAnnonce}/count - Compter les photos d'une annonce
    @GetMapping("/annonce/{idAnnonce}/count")
    public ResponseEntity<?> compterPhotosAnnonce(@PathVariable Integer  idAnnonce) {
        try {
            long totalPhotos = photoRepository.countByIdAnnonce(idAnnonce);
            long nombrePhotos = photoRepository.countByIdAnnonceAndEstVideo(idAnnonce, false);
            long nombreVideos = photoRepository.countByIdAnnonceAndEstVideo(idAnnonce, true);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalPhotos", totalPhotos);
            response.put("nombrePhotos", nombrePhotos);
            response.put("nombreVideos", nombreVideos);
            response.put("peutAjouterPlus", totalPhotos < 5);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors du comptage des photos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}