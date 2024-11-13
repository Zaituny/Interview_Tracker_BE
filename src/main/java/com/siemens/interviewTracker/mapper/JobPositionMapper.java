package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.JobPositionDTO;
import com.siemens.interviewTracker.entity.JobPosition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobPositionMapper {

    JobPositionDTO toJobPositionDTO(JobPosition jobPosition);

    JobPosition toJobPosition(JobPositionDTO jobPositionDTO);
}
