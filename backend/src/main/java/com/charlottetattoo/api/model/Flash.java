package com.charlottetattoo.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/** Un "flash" e' un disegno pronto da tatuare (una sola volta). */
@Entity
public class Flash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome del file salvato nella cartella uploads
    @Column(nullable = false)
    private String imageFile;

    // Dimensione scritta a mano, es. "8 cm"
    @Column(nullable = false)
    private String size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlashStatus status = FlashStatus.AVAILABLE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageFile() { return imageFile; }
    public void setImageFile(String imageFile) { this.imageFile = imageFile; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public FlashStatus getStatus() { return status; }
    public void setStatus(FlashStatus status) { this.status = status; }
}
