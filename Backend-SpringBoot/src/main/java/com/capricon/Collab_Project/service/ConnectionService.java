package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.ConnectionDTO;
import com.capricon.Collab_Project.dto.ConnectionRequest;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Connection;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.enums.Status;
import com.capricon.Collab_Project.repository.ConnectionRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionService {

    private final UserRepo userRepo;
    private final RabbitMQPublisher rabbitMQPublisher;
    private final ConnectionRepo connectionRepo;


    public ApiResponse<List<ConnectionDTO>> getSentPendingConnections(String username) {
        try {
            log.info("Getting pending connection requests for user: {}", username);

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            List<Connection> connections = connectionRepo.findAllPendingRequestsSentByUserId(username);
            List<ConnectionDTO> pendingConnections = connections.stream()
                    .map(this::buildConnectionResponse).toList();

            return ApiResponse.success(pendingConnections);
        } catch (Exception ex) {
            return handleConnectionServiceException(ex);
        }
    }

    public ApiResponse<ConnectionDTO> sendConnectionRequest(ConnectionRequest connectionRequest) {
        try {
            log.info("Sending connection request");

            // Find requesting user
            log.info("Requesting user: {}", connectionRequest.getRequestingUsername());
            User requestingUser = userRepo.findByUsername(connectionRequest.getRequestingUsername())
                    .orElseThrow(() -> new UserException("Requesting user not found", HttpStatus.NOT_FOUND));

            // Find receiving user
            User receivingUser = userRepo.findByUsername(connectionRequest.getReceivingUsername())
                    .orElseThrow(() -> new UserException("Receiving user not found", HttpStatus.NOT_FOUND));

            // Check if connection request exists
            boolean isConnectionExisting = connectionRepo.existsByUserIdAndConnectedUserId(requestingUser, receivingUser);
            if (isConnectionExisting) {
                throw new BusinessException("Connection request already exists", HttpStatus.CONFLICT);
            }

            // If both user exists, build a connection
            Connection connection = Connection.builder()
                    .userId(requestingUser)
                    .connectedUserId(receivingUser)
                    .status(Status.PENDING)
                    .build();

            connectionRepo.save(connection);
            ConnectionDTO response = buildConnectionResponse(connection);
            rabbitMQPublisher.sendMessage("connection", "connection.key", response);
            return ApiResponse.success(response, "Connection request sent successfully");

        } catch (Exception e) {
            return handleConnectionServiceException(e);
        }
    }


    public ApiResponse<ConnectionDTO> acceptConnectionRequest(ConnectionRequest connectionRequest) {
        try {
            log.info("Accepting connection request");

            // Find requesting user
            User requestingUser = userRepo.findByUsername(connectionRequest.getRequestingUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            // Find receiving user
            User receivingUser = userRepo.findByUsername(connectionRequest.getReceivingUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            Connection connection = connectionRepo.findByUserIdAndConnectedUserId(requestingUser, receivingUser)
                    .orElseThrow(() -> new BusinessException("Connection request not found", HttpStatus.NOT_FOUND));
            connection.setStatus(Status.ACCEPTED);

            connectionRepo.save(connection);
            ConnectionDTO response = buildConnectionResponse(connection);
            rabbitMQPublisher.sendMessage("connection", "connection.key", response);
            return ApiResponse.success(response, "Connection request accepted");
        } catch (Exception e) {
            return handleConnectionServiceException(e);
        }
    }

    private ConnectionDTO buildConnectionResponse(Connection connection) {
        return ConnectionDTO.builder()
                .requestingUser(connection.getUserId().getUsername())
                .receivingUser(connection.getConnectedUserId().getUsername())
                .status(connection.getStatus().name())
                .build();
    }

    private <T> ApiResponse<T> handleConnectionServiceException(Throwable ex) {
        Throwable cause = (ex instanceof RuntimeException && ex.getCause() != null) ? ex.getCause() : ex;
        log.error("Error processing request, caused by: {}", cause.getMessage());
        if (cause instanceof UserException userException) {
            return ApiResponse.error(userException.getStatus(), userException.getMessage());
        } else if (cause instanceof BusinessException businessException) {
            return ApiResponse.error(businessException.getStatus(), businessException.getMessage());
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

}
