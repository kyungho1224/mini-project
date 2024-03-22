package com.example.miniproject.common.service;


import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public interface ImageService {

    String getImageUrl(String name);

    String save(MultipartFile file) throws IOException;

    String upload(MultipartFile file, String filename) throws IOException;

    String save(BufferedImage bufferedImage, String originFilename) throws IOException;

    void delete(String name) throws IOException;

    default String getExtension(String originFilename) {
        return StringUtils.getFilenameExtension(originFilename);
    }

    default String generateFilename(String originFilename) {
        return UUID.randomUUID() + getExtension(originFilename);
    }

    default byte[] getByteArrays(BufferedImage bufferedImage, String format) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, format, bos);
            bos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR.getDescription());
        }
    }

}
