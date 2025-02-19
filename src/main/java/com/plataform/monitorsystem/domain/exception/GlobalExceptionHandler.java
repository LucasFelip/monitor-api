package com.plataform.monitorsystem.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public static class ErrorResponse {

        private int status;
        private String message;

        public ErrorResponse() {}

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public static Builder builder() {
            return new Builder();
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public static class Builder {
            private int status;
            private String message;

            public Builder status(int status) {
                this.status = status;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public ErrorResponse build() {
                return new ErrorResponse(status, message);
            }
        }
    }
}
