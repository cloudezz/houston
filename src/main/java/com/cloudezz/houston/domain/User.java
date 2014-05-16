package com.cloudezz.houston.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A user.
 */
@Entity
@Table(name = "T_USER")
public class User extends BaseEntity {

  private static final long serialVersionUID = 3028913419749325011L;

  @NotNull
  @Size(min = 0, max = 50)
  private String login;

  @JsonIgnore
  @Size(min = 0, max = 100)
  private String password;

  @Size(min = 0, max = 50)
  @Column(name = "first_name")
  private String firstName;

  @Size(min = 0, max = 50)
  @Column(name = "last_name")
  private String lastName;

  @Size(min = 0, max = 50)
  @Column(name = "account_id")
  private String accountId;

  @Column(name = "active", nullable = false, columnDefinition = "TINYINT")
  private boolean active;

  @Email
  @Size(min = 0, max = 100)
  @Id
  @Column(name = "email", unique = true)
  private String email;

  @JsonIgnore
  @ManyToMany
  @JoinTable(name = "T_USER_AUTHORITY", joinColumns = {@JoinColumn(name = "email",
      referencedColumnName = "email")}, inverseJoinColumns = {@JoinColumn(name = "authority_name",
      referencedColumnName = "authority_name")})
  private Set<Authority> authorities;

  @JsonIgnore
  @OneToMany(mappedBy = "user")
  private Set<PersistentToken> persistentTokens;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "owner")
  private List<Application> applications;

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  public Set<PersistentToken> getPersistentTokens() {
    return persistentTokens;
  }

  public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
    this.persistentTokens = persistentTokens;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    User user = (User) o;

    if (!email.equals(user.email)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return email.hashCode();
  }

  @Override
  public String toString() {
    return "User{" + "login='" + login + '\'' + ", password='" + password + '\'' + ", firstName='"
        + firstName + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\'' + "}";
  }

  @Override
  public String getId() {
    return email;
  }

  @Override
  public void setId(String id) {
    email = id;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public List<Application> getApplications() {
    return applications;
  }

  public void setApplications(List<Application> applications) {
    this.applications = applications;
  }
}
