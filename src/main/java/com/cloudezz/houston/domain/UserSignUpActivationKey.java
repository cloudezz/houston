package com.cloudezz.houston.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Sign up activation key
 * 
 */
@Entity
@Table(name = "T_USER_SIGNUP_ACTIVATION_KEY")
public class UserSignUpActivationKey extends BaseEntity {

  private static final long serialVersionUID = 3495564875689724995L;


  @Id
  @Column(name = "email_id")
  private String emailId;

  @JsonIgnore
  @NotNull
  @Column(name = "activation_key")
  private String activationKey;
  
  
  @JsonIgnore
  @Column(name = "signup_date")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
  private LocalDate signUpDate;
  


  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  @Override
  public String getId() {
    return emailId;
  }

  @Override
  public void setId(String id) {
    emailId = id;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserSignUpActivationKey that = (UserSignUpActivationKey) o;

    if (!activationKey.equals(that.activationKey)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return activationKey.hashCode();
  }

  @Override
  public String toString() {
    return "UserSignUpActivationKey{" + "emailId='" + emailId + '\'' + ", activationKey='"
        + activationKey + '\'' + "}";
  }

  public LocalDate getSignUpDate() {
    return signUpDate;
  }

  public void setSignUpDate(LocalDate signUpDate) {
    this.signUpDate = signUpDate;
  }

  
}
