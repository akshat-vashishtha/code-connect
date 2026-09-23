import { ApiResponse } from "@/types/api";
import { MentorApprovalResponse, LanguageDetectionRequest, LanguageDetectionResponse } from "@/types/admin";
import { ApiError } from "@/lib/api/auth";
import { ProblemDetail } from "@/types/auth";

async function handleResponse<T>(response: Response, defaultErrorMessage: string): Promise<ApiResponse<T>> {
  if (!response.ok) {
    let problemDetail: ProblemDetail | undefined;
    try {
      problemDetail = (await response.json()) as ProblemDetail;
    } catch {
      // Non-JSON error body fallback
    }

    const errorMessage =
      problemDetail?.detail ||
      problemDetail?.title ||
      `${defaultErrorMessage} with status ${response.status}`;

    throw new ApiError(errorMessage, response.status, problemDetail);
  }

  return (await response.json()) as ApiResponse<T>;
}

/**
 * Fetches all pending mentor applications from the admin service.
 * Requires ROLE_ADMIN privilege.
 */
export async function getPendingMentors(): Promise<ApiResponse<MentorApprovalResponse[]>> {
  const response = await fetch("/api/v1/admin/mentors/pending", {
    method: "GET",
    headers: {
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
  });

  return handleResponse<MentorApprovalResponse[]>(response, "Failed to retrieve pending mentor applications");
}

/**
 * Approves a pending mentor application.
 * Elevates the user's role to ROLE_MENTOR and updates their active session in real-time.
 */
export async function approveMentor(applicationId: string): Promise<ApiResponse<MentorApprovalResponse>> {
  const response = await fetch(`/api/v1/admin/mentors/${applicationId}/approve`, {
    method: "POST",
    headers: {
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
  });

  return handleResponse<MentorApprovalResponse>(response, "Failed to approve mentor application");
}

/**
 * Rejects a pending mentor application.
 */
export async function rejectMentor(applicationId: string): Promise<ApiResponse<MentorApprovalResponse>> {
  const response = await fetch(`/api/v1/admin/mentors/${applicationId}/reject`, {
    method: "POST",
    headers: {
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
  });

  return handleResponse<MentorApprovalResponse>(response, "Failed to reject mentor application");
}

/**
 * Detects language preference based on conversational text markers.
 */
export async function detectLanguage(text: string): Promise<ApiResponse<LanguageDetectionResponse>> {
  const request: LanguageDetectionRequest = { text };
  const response = await fetch("/api/v1/users/detect-language", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });

  return handleResponse<LanguageDetectionResponse>(response, "Failed to detect language preference");
}
