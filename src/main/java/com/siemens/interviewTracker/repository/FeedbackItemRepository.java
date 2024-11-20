package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.entity.FeedbackItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackItemRepository extends JpaRepository<FeedbackItem, UUID> {
}
