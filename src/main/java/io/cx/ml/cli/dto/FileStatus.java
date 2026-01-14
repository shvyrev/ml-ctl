package io.cx.ml.cli.dto;

public enum FileStatus {
    PENDING,     // Файл загружен, ожидает проверки
    VERIFIED,    // Прошел валидацию и антивирус
    INFECTED,    // Обнаружены угрозы
    DELETED      // Помечен на удаление
}
