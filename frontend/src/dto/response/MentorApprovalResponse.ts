import { MentorApprovalStatus } from "@/domain/enums/MentorApprovalStatus";

/**
 * Representation of pending mentor application record matching:
 * com.codeconnect.user.application.dto.MentorApprovalResponse
 */
export interface MentorApprovalResponse {
  readonly id: string;
  readonly userId: string;
  readonly email: string;
  readonly linkedInUrl: string | null;
  readonly bio: string | null;
  readonly status: MentorApprovalStatus;
  readonly submittedAt: string;
  readonly reviewedAt: string | null;
  readonly reviewedBy: string | null;
}
