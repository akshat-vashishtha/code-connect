import { UserRole } from "@/domain/enums/UserRole";
import { UserStatus } from "@/domain/enums/UserStatus";

/**
 * Sanitized user response payload matching com.codeconnect.gateway.application.dto.response.UserResponse
 */
export interface UserResponse {
  readonly id: string;
  readonly email: string;
  readonly displayName: string;
  readonly role: UserRole;
  readonly status: UserStatus;
  readonly createdAt: string;
}
