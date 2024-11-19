package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.FeedbackItemDTO;
import com.siemens.interviewTracker.entity.FeedbackItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackItemMapper {
    FeedbackItemDTO toDTO(FeedbackItem feedbackItem);
    FeedbackItem toEntity(FeedbackItemDTO feedbackItemDTO);
}

