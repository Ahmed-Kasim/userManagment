package com.codeQuest.userManagment.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "accounts")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_id")
    private Long id;

    @Column(name = "First_name")
    private String firstName;

    @Column(name = "Last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_num", unique = true)
    private String phoneNum;

    @Column(name = "pass")
    private String password;
    
    @Column(name = "gender")
    private String gender;

    @Column(name = "B_date")
    private String birthDate;

    // Getters and setter
}