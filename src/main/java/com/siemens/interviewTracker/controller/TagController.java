package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.TagDTO;
import com.siemens.interviewTracker.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDTO) {
        log.info("Request to create a new tag");
        TagDTO createdTag = tagService.createTag(tagDTO);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        log.info("Request to fetch all tags");
        List<TagDTO> tags = tagService.getAllTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable UUID id) {
        log.info("Request to fetch tag with ID: {}", id);
        TagDTO tag = tagService.getTagById(id);
        return tag != null ? new ResponseEntity<>(tag, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(@PathVariable UUID id, @RequestBody TagDTO tagDTO) {
        log.info("Request to update tag with ID: {}", id);
        TagDTO updatedTag = tagService.updateTag(id, tagDTO);
        return updatedTag != null ? new ResponseEntity<>(updatedTag, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        log.info("Request to delete tag with ID: {}", id);
        boolean deleted = tagService.deleteTag(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/assign")
    public ResponseEntity<TagDTO> assignExistingTagToCandidate(
            @RequestParam UUID candidateId, @RequestParam UUID tagId) {
        log.info("Request to assign existing tag with ID: {} to candidate with ID: {}", tagId, candidateId);
        TagDTO assignedTag = tagService.assignExistingTagToCandidate(candidateId, tagId);
        return assignedTag != null ? new ResponseEntity<>(assignedTag, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
