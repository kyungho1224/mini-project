package com.example.miniproject.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        FirebaseOptions.Builder optionBuilder = FirebaseOptions.builder();
        if (serviceAccount != null) {
            FirebaseOptions options;
            try {
                options = optionBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FirebaseApp.initializeApp(options);
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }

}
