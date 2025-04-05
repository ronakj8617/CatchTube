package com.example.catchtubebackend;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/catchtube")
@CrossOrigin(origins = "*")
public class DownloadService {
    private String DOWNLOAD_DIR = "downloads/";

    @GetMapping("/download")
    public ResponseEntity<String> hello(@RequestParam String url) {
        try {
            Files.createDirectories(Path.of(DOWNLOAD_DIR));
            String command = String.format("yt-dlp -o %s%%(title)s.%%(ext)s %s", DOWNLOAD_DIR, url);

            Process process = Runtime.getRuntime().exec(command);
//            System.out.println(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMessage = new String(process.getErrorStream().readAllBytes());
                return ResponseEntity.status(500).body("Error: " + errorMessage);
            }
            File[] files = new File(DOWNLOAD_DIR).listFiles();
            if (files != null && files.length > 0) {
                File file = files[0];
                String originalFileName = file.getName();
                String modifiedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
                File modifiedFile = new File(DOWNLOAD_DIR, modifiedFileName);
                if (!originalFileName.equals(modifiedFileName) && modifiedFile.exists()) {
                    modifiedFileName = System.currentTimeMillis() + "_" + modifiedFileName;
                    modifiedFile = new File(DOWNLOAD_DIR, modifiedFileName);
                }
                file.renameTo(modifiedFile);
                String retFileLink = String.format("http://localhost:8080/api/catchtube/file/%s", modifiedFileName);
                return ResponseEntity.ok(retFileLink);
//                return ResponseEntity.ok("Click here to download the file: <a href=" + retFileLink + ">" + originalFileName + "</a>");
//                return ResponseEntity.ok(retFileLink + originalFileName);
            }
            return ResponseEntity.status(404).body("Error: video is not downloaded");
        } catch (IOException | InterruptedException interruptedException) {
//            System.out.println(interruptedException.getMessage());
            return ResponseEntity.status(404).body("Error occured: " + interruptedException.getMessage());
        }
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity serveFile(@PathVariable String fileName) {
        try {
            File file = new File(DOWNLOAD_DIR + fileName);
            String currentDirectory =  file.getAbsolutePath();
            if (!fileName.matches("[a-zA-Z0-9._-]+")) {
                return ResponseEntity.status(404).body("Invalid file name");
            }
            if (file.exists()) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(resource);
//                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(resource);
            }
        } catch (Exception exception) {
            return ResponseEntity.status(500).body("Error occured " + exception);
        }
        return ResponseEntity.ok("Okay");
    }
}
