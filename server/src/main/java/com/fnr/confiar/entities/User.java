package com.fnr.confiar.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User extends BaseEntity {

  public User(Long id, String name, String lastName, String userName, String mail, UserProfile profile) {
  }
  
  public User(Long id, String name, String lastName, String userName, String mail) {
  }
  

  @Column(length = 30)
  private String name;
  @Column(length = 30)
  private String lastName;
  @Column(nullable = false, unique = true)
  private String userName;
  @Column(nullable = false, unique = true)
  private String mail;
  @Column(nullable = false)
  private String password;
  @ManyToOne
  @JoinColumn(name="PROFILE_ID")
  private UserProfile profile;

}
