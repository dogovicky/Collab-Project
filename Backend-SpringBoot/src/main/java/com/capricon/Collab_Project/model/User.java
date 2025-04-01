package com.capricon.Collab_Project.model;


import com.capricon.Collab_Project.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Access(AccessType.FIELD)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid")
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "full_name", nullable = false, unique = true)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String institution;
    private String bio;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "field_of_interest", columnDefinition = "TEXT[]")
    private List<String> fieldOfInterest;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "verification_code")
    private String verificationCode;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CommunityMembership> communityMemberships;

    @OneToMany(mappedBy = "userId") //Connections initiated by this user
    private List<Connection> connectionsInitiated;

    @OneToMany(mappedBy = "connectedUserId") //Connections received by this user
    private List<Connection> connectionsReceived;

    @OneToMany(mappedBy = "authorId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repost> reposts;

    @OneToMany(mappedBy = "senderId") //Messages sent by this user
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiverId") //Messages this user has received
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "senderId")
    private List<Notification> triggeredNotifications; //Notifications triggered by the user

    @OneToMany(mappedBy = "receiverId")
    private List<Notification> receivedNotifications; //Notifications the user has received

    @OneToMany(mappedBy = "user")
    private List<Attachment> attachments;

}
