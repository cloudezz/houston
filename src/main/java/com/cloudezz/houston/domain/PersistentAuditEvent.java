package com.cloudezz.houston.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Persist AuditEvent managed by the Spring Boot actuator
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */

@Entity
@Table(name = "T_PERSISTENT_AUDIT_EVENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PersistentAuditEvent extends BaseEntity {

  private static final long serialVersionUID = 55138762249008026L;

    @Id
    @Column(name = "event_id")
    private String id;

    @NotNull
    @Column(name = "principal")
    private String principal;

    @Column(name = "event_date")
    private Date auditEventDate;

    @Column(name = "event_type")
    private String auditEventType;


    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="T_PERSISTENT_AUDIT_EVENT_DATA", joinColumns=@JoinColumn(name="event_id"))
    private Map<String, String> data = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Date getAuditEventDate() {
        return auditEventDate;
    }

    public void setAuditEventDate(Date auditEventDate) {
        this.auditEventDate = auditEventDate;
    }

    public String getAuditEventType() {
        return auditEventType;
    }

    public void setAuditEventType(String auditEventType) {
        this.auditEventType = auditEventType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
