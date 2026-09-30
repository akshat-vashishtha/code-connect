package com.codeconnect.submission.application.service.impl;

import com.codeconnect.submission.application.command.DomainCommand;
import com.codeconnect.submission.application.command.factory.SubmissionCommandFactory;
import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.mapper.SubmissionMapper;
import com.codeconnect.submission.domain.enums.SubmissionStatus;
import com.codeconnect.submission.domain.exception.ResourceNotFoundException;
import com.codeconnect.submission.domain.model.SubmissionDocument;
import com.codeconnect.submission.domain.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private SubmissionCommandFactory commandFactory;

    @Mock
    private SubmissionRepository repository;

    private SubmissionMapper mapper;
    private SubmissionServiceImpl submissionService;

    @BeforeEach
    void setUp() {
        mapper = new SubmissionMapper();
        submissionService = new SubmissionServiceImpl(commandFactory, repository, mapper);
    }

    @Test
    @DisplayName("Should delegate submission creation to CommandFactory and execute command")
    @SuppressWarnings("unchecked")
    void shouldCreateSubmission() {
        CreateSubmissionRequest request = new CreateSubmissionRequest("foothold-1", "class Sol {}", "JAVA");
        SubmissionResponse expectedResponse = new SubmissionResponse(
            "sub-1", "student-1", "s@codeconnect.dev", "foothold-1", SubmissionStatus.PENDING, "class Sol {}", "JAVA", null, Instant.now(), null
        );

        DomainCommand<SubmissionResponse> mockCommand = mock(DomainCommand.class);
        when(mockCommand.execute()).thenReturn(expectedResponse);
        when(commandFactory.createSubmissionCommand(request, "student-1", "s@codeconnect.dev")).thenReturn(mockCommand);

        SubmissionResponse actualResponse = submissionService.createSubmission(request, "student-1", "s@codeconnect.dev");

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandFactory).createSubmissionCommand(request, "student-1", "s@codeconnect.dev");
        verify(mockCommand).execute();
    }

    @Test
    @DisplayName("Should retrieve submission by ID")
    void shouldGetSubmissionById() {
        SubmissionDocument doc = SubmissionDocument.builder()
            .id("sub-1")
            .studentId("student-1")
            .footholdId("foothold-1")
            .status(SubmissionStatus.PENDING)
            .build();

        when(repository.findById("sub-1")).thenReturn(Optional.of(doc));

        SubmissionResponse response = submissionService.getSubmissionById("sub-1");

        assertThat(response.submissionId()).isEqualTo("sub-1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when submission not found")
    void shouldThrowWhenNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.getSubmissionById("missing"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Submission not found");
    }

    @Test
    @DisplayName("Should retrieve submissions by student ID")
    void shouldGetSubmissionsByStudentId() {
        SubmissionDocument doc = SubmissionDocument.builder()
            .id("sub-1")
            .studentId("student-1")
            .footholdId("foothold-1")
            .status(SubmissionStatus.PASSED)
            .build();

        when(repository.findByStudentIdOrderByCreatedAtDesc("student-1")).thenReturn(List.of(doc));

        List<SubmissionResponse> responses = submissionService.getSubmissionsByStudentId("student-1");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).submissionId()).isEqualTo("sub-1");
    }
}
