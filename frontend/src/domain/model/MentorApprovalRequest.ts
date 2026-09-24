import { MentorApprovalStatus } from "@/domain/enums/MentorApprovalStatus";

/**
 * Domain entity representing a mentor onboarding application.
 */
export interface MentorApprovalRequest {
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
