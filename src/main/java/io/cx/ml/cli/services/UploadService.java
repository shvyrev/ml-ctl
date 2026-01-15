package io.cx.ml.cli.services;

import io.cx.ml.cli.clients.FileClient;
import io.cx.ml.cli.clients.FolderClient;
import io.cx.ml.cli.clients.SessionClient;
import io.cx.ml.cli.dto.*;
import io.cx.ml.cli.utils.HashUtils;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.cx.ml.cli.dto.ResponseType.EXISTING_FILE;
import static java.util.Optional.ofNullable;

@Slf4j
@ApplicationScoped
public class UploadService {

    @Inject
    @RestClient
    SessionClient sessionClient;

    @Inject
    @RestClient
    FolderClient folderClient;

    private final Map<String, Long> folderCache = new ConcurrentHashMap<>();

    private static final int CHUNK_SIZE = 5 * 1024 * 1024;

    @Inject
    @RestClient
    FileClient fileClient;

    public Uni<Void> deleteFile(UUID fileId) {
        System.out.println("Удаление файла " + fileId + "...");
        return fileClient.deleteFile(fileId);
    }

    public void uploadFile(Path path, Long targetFolderId) throws Exception {
        long totalSize = Files.size(path);
        System.out.println("Вычисление контрольной суммы...");

        // 2. Считаем хэш
        System.out.println("Вычисление контрольной суммы...");
        String etag = HashUtils.calculateETag(path);

        // 3. Создаем сессию со ВСЕМИ полями
        UploadSessionRequest initReq = new UploadSessionRequest();
        initReq.setFileName(path.getFileName().toString());
        initReq.setUploadLength(totalSize);
        initReq.setExpectedEtag(etag);
        initReq.setFolderId(targetFolderId);

        System.out.println("Инициализация сессии на сервере...");
        UploadSessionResponse session = sessionClient.createSession(initReq);

        if (session.getType() != null && session.getType().equals(EXISTING_FILE)) {
            System.out.println("  [v] Дедупликация: " + path.getFileName());
            return;
        }

        long currentOffset = 0;

        try (RandomAccessFile raf = new RandomAccessFile(path.toAbsolutePath().toString(), "r")) {
            while (currentOffset < totalSize) {
                int bytesToRead = (int) Math.min(CHUNK_SIZE, totalSize - currentOffset);
                byte[] buffer = new byte[bytesToRead];
                raf.seek(currentOffset);
                raf.readFully(buffer);

                // УДАЛИ ПРИНТ ОТСЮДА

                try (Response response = sessionClient.uploadChunk(session.getSessionId(), currentOffset, buffer)) {
                    String nextOffsetStr = response.getHeaderString("Upload-Offset");
                    if (nextOffsetStr != null) {
                        currentOffset = Long.parseLong(nextOffsetStr);
                    } else {
                        currentOffset += bytesToRead;
                    }
                }

                // ПЕРЕНЕСИ СЮДА: теперь мы печатаем прогресс ПОСЛЕ успешной отправки
                System.out.printf("\rЗагрузка: %d%% (%d/%d байт)",
                        (currentOffset * 100 / totalSize), currentOffset, totalSize);

                Thread.sleep(100);
            }
        }
    }

    // Основной метод загрузки (теперь один)
    public void uploadFile(Path path, Long targetFolderId, String cloudPath) throws Exception {
        Long folderId = targetFolderId;

        // 1. Резолвим путь, если он передан
        if (cloudPath != null && !cloudPath.isBlank() && !cloudPath.equals("/")) {
            // Используем кэш, чтобы не дергать сервер постоянно
            folderId = folderCache.computeIfAbsent(cloudPath, this::resolveFolderId);
        } else if (folderId == null || folderId == 0) {
            folderId = folderClient.getRootFolder().getId();
        }

        long totalSize = Files.size(path);
        String etag = HashUtils.calculateETag(path);

        UploadSessionRequest initReq = new UploadSessionRequest();
        initReq.setFileName(path.getFileName().toString());
        initReq.setUploadLength(totalSize);
        initReq.setExpectedEtag(etag);
        initReq.setFolderId(folderId);

        System.out.println("\nИнициализация: " + path.getFileName() + " (Folder: " + folderId + ")");
        UploadSessionResponse session = sessionClient.createSession(initReq);

        // Проверка дедупликации
        if (session.getType() != null && session.getType().equals(EXISTING_FILE)) {
            System.out.println("  [v] Дедупликация: " + path.getFileName());
            return;
        }

        // 2. Логика отправки чанков (вынеси в приватный метод для чистоты)
        performChunkedUpload(path, session, totalSize);
    }

    private void performChunkedUpload(Path path, UploadSessionResponse session, long totalSize) throws Exception {

        long currentOffset = ofNullable(session)
                .map(UploadSessionResponse::getUploadOffset)
                .orElse(0L);

        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            while (currentOffset < totalSize) {
                int bytesToRead = (int) Math.min(CHUNK_SIZE, totalSize - currentOffset);
                byte[] buffer = new byte[bytesToRead];
                raf.seek(currentOffset);
                raf.readFully(buffer);

                try (Response response = sessionClient.uploadChunk(session.getSessionId(), currentOffset, buffer)) {
                    String nextOffsetStr = response.getHeaderString("Upload-Offset");
                    currentOffset = (nextOffsetStr != null) ? Long.parseLong(nextOffsetStr) : currentOffset + bytesToRead;
                }
                System.out.printf("\r  --> %s: %d%%", path.getFileName(), (currentOffset * 100 / totalSize));
            }
            System.out.println(" [OK]");
        }
    }

    private Long resolveFolderId(String cloudPath) {
        // Нормализуем путь: убираем двойные слэши и заменяем \ на /
        String cleanPath = cloudPath.replace("\\", "/").replaceAll("/+", "/");

        FolderInfoResponse currentFolder = folderClient.getRootFolder();
        Long currentId = currentFolder.getId();

        String[] parts = cleanPath.split("/");
        for (String part : parts) {
            if (part.isBlank()) continue;

            var contents = folderClient.getFolderContents(currentId, 0, 1000);
            Optional<FileInfoResponse> found = contents.getData().stream()
                    .filter(f -> Boolean.TRUE.equals(f.getIsFolder()) && part.equalsIgnoreCase(f.getOriginalName()))
                    .findFirst();

            if (found.isPresent()) {
                currentId = Long.parseLong(found.get().getId());
            } else {
                FolderCreateRequest createReq = new FolderCreateRequest();
                createReq.setName(part);
                createReq.setParentId(currentId);
                FileInfoResponse created = folderClient.createFolder(createReq);
                currentId = Long.parseLong(created.getId());
            }
        }
        return currentId;
    }

    private Long resolveFolderId(String cloudPath, Long folderId) {
        // 1. Начинаем с корня.
        // Если на сервере ID корня всегда 0 или какой-то фиксированный, можно сэкономить запрос.
        // Но лучше получить его честно:
        FolderInfoResponse currentFolder = folderClient.getRootFolder();
        Long currentId = currentFolder.getId();

        if (cloudPath == null || cloudPath.isBlank()) {
            // Если указан -f, используем его, иначе корень
            return (folderId != null && folderId != 0) ? folderId : currentId;
        }

        // 2. Разбираем путь, например "Music/Rock/2024"
        String[] parts = cloudPath.split("/");

        for (String part : parts) {
            if (part.isBlank()) continue;

            System.out.println("Проверка папки '" + part + "' в директории ID: " + currentId);

            // Ищем папку с таким именем среди содержимого текущей папки
            var contents = folderClient.getFolderContents(currentId, 0, 1000);

            Optional<FileInfoResponse> found = contents.getData().stream()
                    .filter(f -> Boolean.TRUE.equals(f.getIsFolder()) && part.equalsIgnoreCase(f.getOriginalName()))
                    .findFirst();

            if (found.isPresent()) {
                currentId = Long.parseLong(found.get().getId());
            } else {
                // 3. Если не нашли — создаем
                System.out.println("Папка не найдена. Создаю '" + part + "'...");
                FolderCreateRequest createReq = new FolderCreateRequest();
                createReq.setName(part);
                createReq.setParentId(currentId);

                FileInfoResponse created = folderClient.createFolder(createReq);
                currentId = Long.parseLong(created.getId());
            }
        }

        return currentId;
    }
}