import { HttpClient } from "@/client/HttpClient";
import { ApiResponse } from "@/dto/response/ApiResponse";
import { MentorApprovalResponse } from "@/dto/response/MentorApprovalResponse";

/**
 * Dedicated API Client for Administrative Mentor Review endpoints.
 * Interacts with Gateway-protected routes at /api/v1/admin/mentors/**
 */
export class AdminMentorClient {
  public async getPendingMentors(): Promise<ApiResponse<MentorApprovalResponse[]>> {
    return HttpClient.execute<MentorApprovalResponse[]>(
      "/api/v1/admin/mentors/pending",
      {
        method: "GET",
      },
      "Failed to retrieve pending mentor applications"
    );
  }

  public async approveMentor(applicationId: string): Promise<ApiResponse<MentorApprovalResponse>> {
    return HttpClient.execute<MentorApprovalResponse>(
      `/api/v1/admin/mentors/${applicationId}/approve`,
      {
        method: "POST",
      },
      "Failed to approve mentor application"
    );
  }

  public async rejectMentor(applicationId: string): Promise<ApiResponse<MentorApprovalResponse>> {
    return HttpClient.execute<MentorApprovalResponse>(
      `/api/v1/admin/mentors/${applicationId}/reject`,
      {
        method: "POST",
      },
      "Failed to reject mentor application"
    );
  }
}

export const adminMentorClient = new AdminMentorClient();
