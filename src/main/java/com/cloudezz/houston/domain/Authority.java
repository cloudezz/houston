package com.cloudezz.houston.domain;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "T_AUTHORITY")
public class Authority extends BaseEntity {

  private static final long serialVersionUID = 859782860278000178L;

  @NotNull
  @Size(min = 0, max = 50)
  @Id
  @Column(name = "authority_name", unique = true)
  private String authorityName;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Authority authority = (Authority) o;

    if (authorityName != null ? !authorityName.equals(authority.authorityName)
        : authority.authorityName != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return authorityName != null ? authorityName.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Authority{" + "name='" + authorityName + '\'' + "}";
  }

  @Override
  public String getId() {
    return authorityName;
  }

  @Override
  public void setId(String id) {
    authorityName = id;
  }

  /**
   * @return the authorityName
   */
  public String getAuthorityName() {
    return authorityName;
  }

  /**
   * @param authorityName the authorityName to set
   */
  public void setAuthorityName(String authorityName) {
    this.authorityName = authorityName;
  }
}
