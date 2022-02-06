package com.fnr.confiar.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "user")
public class User extends BaseEntity {

  @Builder
  public User(Long id, String name, String lastName, String userName, String mail, String password, UserProfile profile) {
    super(id);
    this.name = name;
    this.lastName = lastName;
    this.userName = userName;
    this.mail = mail;
    this.password = password;
    this.profile = profile;
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
