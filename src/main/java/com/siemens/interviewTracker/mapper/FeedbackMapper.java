package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.FeedbackDTO;
import com.siemens.interviewTracker.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackDTO toDTO(Feedback feedback);
    Feedback toEntity(FeedbackDTO feedbackDTO);
}
