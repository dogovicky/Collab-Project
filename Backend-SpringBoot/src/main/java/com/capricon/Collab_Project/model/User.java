package com.capricon.Collab_Project.model;

import com.capricon.Collab_Project.model.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "UUID")
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "fullName", nullable = false, unique = true)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "gender", columnDefinition = "genderEnum")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String institution;
    private String bio;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "profilePictureUrl")
    private String profilePictureUrl;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "fieldOfInterest", columnDefinition = "TEXT[]")
    private List<String> fieldOfInterest;

    @Column(name = "isEnabled")
    private Boolean isEnabled;

    @Column(name = "verificationCode")
    private String verificationCode;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<CommunityMembership> communityMemberships;

    @OneToMany(mappedBy = "userId") //Connections initiated by this user
    private List<Connection> connectionsInitiated;

    @OneToMany(mappedBy = "connectedUserId") //Connections received by this user
    private List<Connection> connectionsReceived;

    @OneToMany(mappedBy = "authorId", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "userId")
    private List<Comment> comments;

    @OneToMany(mappedBy = "userId")
    private List<Like> likes;

    @OneToMany(mappedBy = "userId")
    private List<Repost> reposts;

    @OneToMany(mappedBy = "senderId") //Messages sent by this user
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiverId") //Messages this user has received
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "senderId")
    private List<Notification> triggeredNotifications; //Notifications triggered by the user

    @OneToMany(mappedBy = "receiverId")
    private List<Notification> receivedNotifications; //Notifications the user has received

    @OneToMany(mappedBy = "userId")
    private List<Attachment> attachments;

    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }



}
