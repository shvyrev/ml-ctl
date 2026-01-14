package io.cx.ml.cli.utils;

import com.dynatrace.hash4j.file.FileHashing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.ToLongFunction;

public class HashUtils {

    /**
     * Вычисляет MD5 хеш файла и возвращает его в виде Hex-строки (ETag).
     */
    public static String calculateETag(Path path) throws IOException {
        return FileHashing.imohash1_0_2().hashFileTo128Bits(path).toString();
    }

//    public static String calculateETag(Path path) throws IOException {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("MD5");
//            try (InputStream is = Files.newInputStream(path)) {
//                byte[] buffer = new byte[8192];
//                int read;
//                while ((read = is.read(buffer)) > 0) {
//                    digest.update(buffer, 0, read);
//                }
//            }
//            return bytesToHex(digest.digest());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("MD5 algorithm not found", e);
//        }
//    }
//
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }
}