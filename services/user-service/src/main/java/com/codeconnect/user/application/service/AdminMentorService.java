package com.codeconnect.user.application.service;

import com.codeconnect.user.application.dto.MentorApprovalResponse;

import java.util.List;

/**
 * Service interface for administrative mentor verification workflows.
 */
public interface AdminMentorService {

    List<MentorApprovalResponse> getPendingMentorApplications();

    MentorApprovalResponse approveMentorApplication(String applicationId, String reviewerAdminEmail);

    MentorApprovalResponse rejectMentorApplication(String applicationId, String reviewerAdminEmail);
}
