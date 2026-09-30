package com.codeconnect.submission.application.service.impl;

import com.codeconnect.submission.application.command.factory.SubmissionCommandFactory;
import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.mapper.SubmissionMapper;
import com.codeconnect.submission.application.service.SubmissionService;
import com.codeconnect.submission.domain.exception.ResourceNotFoundException;
import com.codeconnect.submission.domain.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service facade orchestrating submission ingestion and queries at a Single Level of Abstraction (SLAP).
 * Delegates transactional creation to Command Pattern via SubmissionCommandFactory.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionCommandFactory commandFactory;
    private final SubmissionRepository repository;
    private final SubmissionMapper mapper;

    @Override
    public SubmissionResponse createSubmission(CreateSubmissionRequest request, String studentId, String studentEmail) {
        log.info("Ingesting submission for studentId={} footholdId={}", studentId, request.footholdId());
        return commandFactory.createSubmissionCommand(request, studentId, studentEmail).execute();
    }

    @Override
    public SubmissionResponse getSubmissionById(String submissionId) {
        return repository.findById(submissionId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByStudentId(String studentId) {
        return repository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByFootholdId(String footholdId) {
        return repository.findByFootholdIdOrderByCreatedAtDesc(footholdId).stream()
            .map(mapper::toResponse)
            .toList();
    }
}
