package com.codeconnect.submission.application.service;

import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;

import java.util.List;

public interface SubmissionService {

    SubmissionResponse createSubmission(CreateSubmissionRequest request, String studentId, String studentEmail);

    SubmissionResponse getSubmissionById(String submissionId);

    List<SubmissionResponse> getSubmissionsByStudentId(String studentId);

    List<SubmissionResponse> getSubmissionsByFootholdId(String footholdId);
}
