package _11.asktpk.artisanconnectbackend.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    public byte[] getImageBytes(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }

    public Resource getImageAsResource(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        Resource resource = new UrlResource(path.toUri());

        if(resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Nie można odczytać obrazu: " + imagePath);
        }
    }

    public MediaType getMediaTypeForImage(String imagePath) {
        return MediaTypeFactory
                .getMediaType(imagePath)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }
}