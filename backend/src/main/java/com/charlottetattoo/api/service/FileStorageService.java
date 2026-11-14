package com.charlottetattoo.api.service;

import com.charlottetattoo.api.exception.ApiException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Salva e cancella le immagini nella cartella "uploads". */
@Service
public class FileStorageService {

    // Tipi di file accettati e relativa estensione
    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
    }

    /** Salva l'immagine con un nome casuale e restituisce il nome del file. */
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Seleziona un'immagine");
        }
        String extension = ALLOWED_TYPES.get(file.getContentType());
        if (extension == null) {
            throw ApiException.badRequest("Formato non valido: usa JPG, PNG o WEBP");
        }

        // Il nome e' generato da noi (UUID), cosi' nessuno puo' scrivere in altre cartelle
        String filename = UUID.randomUUID() + extension;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Non sono riuscita a salvare l'immagine");
        }
        return filename;
    }

    /** Copia un file gia' presente nel progetto (usato solo per i dati di esempio). */
    public String saveFromStream(InputStream in, String filename) throws IOException {
        Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public void delete(String filename) {
        if (filename == null) {
            return;
        }
        try {
            Files.deleteIfExists(uploadPath.resolve(filename).normalize());
        } catch (IOException e) {
            // Se non riusciamo a cancellare il file non e' un problema grave
        }
    }
}
