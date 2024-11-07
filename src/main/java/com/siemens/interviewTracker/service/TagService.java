package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.TagDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.Tag;
import com.siemens.interviewTracker.mapper.TagMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final CandidateRepository candidateRepository;
    private final TagMapper tagMapper;

    @Autowired
    public TagService(TagRepository tagRepository, CandidateRepository candidateRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.candidateRepository = candidateRepository;
        this.tagMapper = tagMapper;
    }

    public TagDTO createTag(TagDTO tagDTO) {
        log.info("Creating a new tag with name: {}", tagDTO.getName());
        Tag tag = tagMapper.TagDTOtoTag(tagDTO);
        Tag savedTag = tagRepository.save(tag);
        log.debug("Tag created with ID: {}", savedTag.getId());
        return tagMapper.TagtoTagDTO(savedTag);
    }

    public List<TagDTO> getAllTags() {
        log.info("Fetching all tags");
        List<TagDTO> tags = tagRepository.findAll().stream()
                .map(tagMapper::TagtoTagDTO)
                .collect(Collectors.toList());
        log.debug("Total tags fetched: {}", tags.size());
        return tags;
    }

    public TagDTO getTagById(UUID id) {
        log.info("Fetching tag with ID: {}", id);
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            log.debug("Tag found: {}", tagOptional.get().getName());
            return tagMapper.TagtoTagDTO(tagOptional.get());
        } else {
            log.warn("Tag with ID {} not found", id);
            return null;
        }
    }

    public TagDTO updateTag(UUID id, TagDTO tagDTO) {
        log.info("Updating tag with ID: {}", id);
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            Tag tagToUpdate = tagOptional.get();
            tagToUpdate.setName(tagDTO.getName());
            Tag updatedTag = tagRepository.save(tagToUpdate);
            log.debug("Tag updated with new name: {}", updatedTag.getName());
            return tagMapper.TagtoTagDTO(updatedTag);
        } else {
            log.warn("Tag with ID {} not found for update", id);
            return null;
        }
    }

    public boolean deleteTag(UUID id) {
        log.info("Deleting tag with ID: {}", id);
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            log.debug("Tag with ID {} deleted", id);
            return true;
        } else {
            log.warn("Tag with ID {} not found for deletion", id);
            return false;
        }
    }

    public TagDTO assignExistingTagToCandidate(UUID candidateId, UUID tagId) {
        log.info("Assigning tag with ID: {} to candidate with ID: {}", tagId, candidateId);

        Optional<Candidate> candidateOptional = candidateRepository.findById(candidateId);
        Optional<Tag> tagOptional = tagRepository.findById(tagId);

        if (candidateOptional.isPresent() && tagOptional.isPresent()) {
            Candidate candidate = candidateOptional.get();
            Tag tag = tagOptional.get();

            candidate.getTags().add(tag);
            candidateRepository.save(candidate);

            log.debug("Tag {} assigned to candidate {}", tag.getName(), candidate.getName());
            return tagMapper.TagtoTagDTO(tag);
        } else {
            if (candidateOptional.isEmpty()) {
                log.warn("Candidate with ID {} not found", candidateId);
            }
            if (tagOptional.isEmpty()) {
                log.warn("Tag with ID {} not found", tagId);
            }
            return null;
        }
    }


}
