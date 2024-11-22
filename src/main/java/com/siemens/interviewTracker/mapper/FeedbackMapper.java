package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.FeedbackDTO;
import com.siemens.interviewTracker.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "candidateId", source = "candidate.id")
    @Mapping(target = "stageId", source = "stage.id")
    FeedbackDTO toDTO(Feedback feedback);
    @Mapping(target = "candidate.id", source = "candidateId")
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "stage.id", source = "stageId")
    Feedback toEntity(FeedbackDTO feedbackDTO);

}
