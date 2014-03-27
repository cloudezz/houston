package com.cloudezz.houston.repository;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cloudezz.houston.domain.PersistentAuditEvent;

/**
 * Spring Data JPA repository for the PersistentAuditEvent entity.
 */
public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, String> {

    List<PersistentAuditEvent> findByPrincipal(String principal);

    List<PersistentAuditEvent> findByPrincipalAndAuditEventDateGreaterThan(String principal, LocalDateTime after);

    @Query("select p from PersistentAuditEvent p where p.auditEventDate >= ?1 and p.auditEventDate <= ?2")
    List<PersistentAuditEvent> findByDates(Date fromDate, Date toDate);
}
