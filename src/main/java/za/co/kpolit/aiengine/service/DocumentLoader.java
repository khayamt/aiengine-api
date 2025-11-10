package za.co.kpolit.aiengine.service;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentLoader {

    private final Tika tika = new Tika();
    private final List<String> documentPaths;

   // public DocumentLoader(@Value("${hrbot.docs}") List<String> documentPaths) {
   public DocumentLoader(List<String> documentPaths) {
        //this.documentPaths = documentPaths;
        this.documentPaths = List.of("/app/secure/hr/hr_policy.pdf", "/app/secure/hr/leave_policy.pdf");
    }

    public String loadAllDocuments() {
        return documentPaths.stream()
                .map(this::parseDocumentSafe)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining("\n\n"));
    }

    private String parseDocumentSafe(String path) {
        try {
            String text;
            if (path.startsWith("http")) {
                try (InputStream is = new URL(path).openStream()) {
                    text = tika.parseToString(is);
                }
            } else {
                File file = new File(path);
                if (!file.exists()) throw new IllegalArgumentException("File not found: " + path);
                text = tika.parseToString(file);
            }
            return "=== " + path.substring(path.lastIndexOf('/') + 1) + " ===\n" + text;
        } catch (Exception e) {
            return "Error reading: " + path + " -> " + e.getMessage();
        }
    }
}