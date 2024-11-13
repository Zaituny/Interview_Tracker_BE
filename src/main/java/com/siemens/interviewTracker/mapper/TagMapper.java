package com.siemens.interviewTracker.mapper;


import org.mapstruct.Mapper;
import com.siemens.interviewTracker.dto.TagDTO;
import com.siemens.interviewTracker.entity.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {

    // Mapping Tag entity to TagDTO
    TagDTO TagtoTagDTO(Tag tag);

    // Mapping TagDTO to Tag entity
    Tag TagDTOtoTag(TagDTO tagDTO);
}
