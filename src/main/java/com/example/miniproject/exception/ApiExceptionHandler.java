package com.example.miniproject.exception;

import com.example.miniproject.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

    private final SlackApi slackApi;
    private final SlackAttachment slackAttachment;
    private final SlackMessage slackMessage;
    @Qualifier("applicationTaskExecutor")
    private final TaskExecutor taskExecutor;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> apiExceptionHandler(HttpServletRequest request, ApiException ex) {
        slackNotification(request, ex.getStackTrace(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          ApiResponse.error(ex.getErrorDescription())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> allExceptionHandler(HttpServletRequest request, RuntimeException ex) {
        slackNotification(request, ex.getStackTrace(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        slackNotification(request, ex.getStackTrace(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    private void slackNotification(HttpServletRequest request, StackTraceElement[] stackTrace, Throwable ex) {
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setText(Arrays.toString(stackTrace));
        slackAttachment.setFields(List.of(
          new SlackField().setTitle("Request URL").setValue(String.valueOf(request.getRequestURI())),
          new SlackField().setTitle("Request Method").setValue(request.getMethod()),
          new SlackField().setTitle("Request Time").setValue(new Date().toString()),
          new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()),
          new SlackField().setTitle("Request User-Agent").setValue(request.getHeader("User-Agent")),
          new SlackField().setTitle("Exception Message").setValue(ex.getMessage())
        ));
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        taskExecutor.execute(() -> slackApi.call(slackMessage));
    }

}
