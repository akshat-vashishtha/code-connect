/**
 * Direct credential login request payload matching com.codeconnect.gateway.application.dto.request.LoginRequest
 */
export interface LoginRequest {
  readonly email: string;
  readonly password: string;
}
