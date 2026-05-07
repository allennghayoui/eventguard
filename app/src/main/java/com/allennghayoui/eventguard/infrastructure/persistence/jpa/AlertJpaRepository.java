package com.allennghayoui.eventguard.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allennghayoui.eventguard.infrastructure.persistence.entity.AlertEntity;

public interface AlertJpaRepository extends JpaRepository<AlertEntity, UUID> {
    List<AlertEntity> findByRuleName(String ruleName);
}
