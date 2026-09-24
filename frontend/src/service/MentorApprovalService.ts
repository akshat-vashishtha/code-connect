import { AdminMentorClient, adminMentorClient } from "@/client/AdminMentorClient";
import { MentorApprovalResponse } from "@/dto/response/MentorApprovalResponse";

/**
 * Service orchestrating administrative mentor reviews and state updates.
 */
export class MentorApprovalService {
  constructor(private readonly client: AdminMentorClient = adminMentorClient) {}

  public async getPendingApplications(): Promise<MentorApprovalResponse[]> {
    const response = await this.client.getPendingMentors();
    return response.data || [];
  }

  public async approveApplication(applicationId: string): Promise<MentorApprovalResponse> {
    const response = await this.client.approveMentor(applicationId);
    return response.data;
  }

  public async rejectApplication(applicationId: string): Promise<MentorApprovalResponse> {
    const response = await this.client.rejectMentor(applicationId);
    return response.data;
  }
}

export const mentorApprovalService = new MentorApprovalService();
