package io.cx.ml.cli.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum ModelType {
    // -----------------------------------------------------------------------------------------------------------------
    // |                                         TRITON BACKEND | DEFAULT FILENAME  | RECURSIVE COPY |
    // -----------------------------------------------------------------------------------------------------------------
    ONNX("onnxruntime", "model.onnx", true),            // Единый файл
    TENSORFLOW("tensorflow", null, true),                // Папка (SavedModel)
    PYTORCH("pytorch", "model.pt", true),               // Единый файл (TorchScript)
    TENSORRT("tensorrt", "model.plan", true),           // Единый файл (Plan)
    OPEN_VINO("openvino", null, true),                   // Папка (model.xml + model.bin)
    SKLEARN("fil", "model.joblib", true),               // Единый файл (FIL/Joblib)
    XGBOOST("fil", "model.bin", true),                  // Единый файл (FIL/Binary)
    // Keras не является прямым бэкендом Triton, он работает через TF.
    // Если вы используете Keras H5, он должен быть конвертирован в TF SavedModel
    // или использован с TF backend (но это не идиоматично).
    KERAS_H5("tensorflow", "model.h5", true),          // Часто обрабатывается как одиночный файл TF.
    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    // |                                         TRITON BACKEND | DEFAULT FILENAME  | RECURSIVE COPY |
    // -----------------------------------------------------------------------------------------------------------------
    // Другие типы моделей, которые не поддерживаются Triton но созданы для demo версии сервиса
    // -----------------------------------------------------------------------------------------------------------------
    UNKNOWN("unknown", null, false);

    private final String tritonBackend;
    private final String defaultFilename;
    private final boolean recursiveCopy;

    /**
     * Статический метод для получения ModelType по имени его бэкенда.
     * Это может быть полезно для обратного маппинга или привязки.
     *
     * @param tritonBackendName Имя бэкенда Triton (например, "onnxruntime").
     * @return Соответствующий ModelType.
     * @throws IllegalArgumentException Если тип не найден.
     */
    @SneakyThrows
    public static ModelType fromTritonBackend(String tritonBackendName) {
        return Stream.of(ModelType.values())
                .filter(type -> type.tritonBackend.equalsIgnoreCase(tritonBackendName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Triton backend: " + tritonBackendName));
    }
}
