package io.cx.ml.cli.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

@Log
@ApplicationScoped
public class GlobalExceptionHandler implements IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        ParseResult parseResult) {
        if (ex instanceof NeedAuthException) {
            log.warning("Требуется авторизация");
            cmd.getErr().println("--- ВНИМАНИЕ: ТРЕБУЕТСЯ АВТОРИЗАЦИЯ ---");
            cmd.getErr().println("Пожалуйста, выполните: cx-ml-cli login");
            return 1;
        }

        cmd.getErr().println("Произошла непредвиденная ошибка: " + ex.getMessage());
        return 1;
    }
}