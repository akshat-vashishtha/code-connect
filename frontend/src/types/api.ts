/**
 * Standard API Response envelope with 1:1 parity with Java backend record
 * com.codeconnect.user.application.dto.response.ApiResponse<T>
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  traceId: string | null;
  timestamp: string;
}

/**
 * Platform User Role hierarchy (AD-9)
 */
export type UserRole = "ROLE_STUDENT" | "ROLE_MENTOR" | "ROLE_ADMIN";

/**
 * User Account status lifecycle
 */
export type UserStatus = "ACTIVE" | "PENDING_APPROVAL" | "BANNED";

/**
 * User Profile view representation
 */
export interface UserProfile {
  id: string;
  email: string;
  displayName: string;
  role: UserRole;
  status: UserStatus;
  avatarUrl?: string;
  bio?: string;
}

/**
 * Service Health status probe response
 */
export interface ServiceHealth {
  status: "UP" | "DOWN" | "UNKNOWN";
  service: string;
  port: number;
}
