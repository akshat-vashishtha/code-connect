/**
 * Representation of pending mentor application record matching:
 * com.codeconnect.user.application.dto.MentorApprovalResponse
 */
export interface MentorApprovalResponse {
  id: string;
  userId: string;
  email: string;
  linkedInUrl: string | null;
  bio: string | null;
  status: "PENDING" | "APPROVED" | "REJECTED";
  submittedAt: string;
  reviewedAt: string | null;
  reviewedBy: string | null;
}

/**
 * Request payload for conversational language detection
 */
export interface LanguageDetectionRequest {
  text: string;
}

/**
 * Response payload for conversational language detection
 */
export interface LanguageDetectionResponse {
  languagePreference: "ENGLISH" | "HINGLISH";
  confidence: number;
}
