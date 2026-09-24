import { UserRole } from "@/domain/enums/UserRole";

/**
 * Registration request payload matching com.codeconnect.gateway.application.dto.request.SignupRequest
 */
export interface SignupRequest {
  readonly email: string;
  readonly password: string;
  readonly displayName: string;
  readonly role: UserRole;
  readonly linkedInUrl?: string;
  readonly bio?: string;
}
