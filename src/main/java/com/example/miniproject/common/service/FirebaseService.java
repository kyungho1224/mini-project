package com.example.miniproject.common.service;

import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseService implements ImageService {

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    @Value("${app.firebase-image-url}")
    private String imageUrl;

    public String upload(MultipartFile file, String filename) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());
        bucket.create(filename, content, file.getContentType());
        return getImageUrl(filename);
    }

    @Override
    public String getImageUrl(String name) {
        return String.format(imageUrl, firebaseBucket, name);
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        String name = generateFilename(file.getOriginalFilename());
        bucket.create(name, file.getBytes(), file.getContentType());
        return name;
    }

    @Override
    public String save(BufferedImage bufferedImage, String originFilename) throws IOException {
        byte[] bytes = getByteArrays(bufferedImage, getExtension(originFilename));
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        String name = generateFilename(originFilename);
        bucket.create(name, bytes);
        return name;
    }

    @Override
    public void delete(String name) {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        if (StringUtils.isEmpty(name)) {
            throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION);
        }
        Blob blob = bucket.get(name);
        if (blob == null) {
            throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION);
        }
        blob.delete();
    }

}
