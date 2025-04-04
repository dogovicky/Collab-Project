package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.ConnectionDTO;
import com.capricon.Collab_Project.dto.ConnectionRequest;
import com.capricon.Collab_Project.service.ConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConnectionController {
    private final ConnectionService connectionService;

    @PostMapping("/send-connection-request")
    public ResponseEntity<ApiResponse<ConnectionDTO>> sendConnectionRequest(@Valid @RequestBody ConnectionRequest request) {
        try {
            ApiResponse<ConnectionDTO> response = connectionService.sendConnectionRequest(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleConnectionControllerException(ex);
        }
    }

    @PostMapping("/accept-connection-request")
    public ResponseEntity<ApiResponse<ConnectionDTO>> acceptConnectionRequest(@Valid @RequestBody ConnectionRequest request) {
        try {
            ApiResponse<ConnectionDTO> response = connectionService.acceptConnectionRequest(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleConnectionControllerException(ex);
        }
    }




    private <T> ResponseEntity<ApiResponse<T>> handleConnectionControllerException(Throwable ex) {
        Throwable cause = (ex instanceof RuntimeException && ex.getCause() != null) ? ex.getCause() : ex;
        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));
    }
}
