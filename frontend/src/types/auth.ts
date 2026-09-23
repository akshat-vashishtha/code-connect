import { UserRole, UserStatus } from "./api";

/**
 * Registration request payload matching com.codeconnect.gateway.application.dto.request.SignupRequest
 */
export interface SignupRequest {
  email: string;
  password: string;
  displayName: string;
  role: UserRole;
  linkedInUrl?: string;
  bio?: string;
}

/**
 * Direct credential login request payload matching com.codeconnect.gateway.application.dto.request.LoginRequest
 */
export interface LoginRequest {
  email: string;
  password: string;
}

/**
 * Sanitized user response payload matching com.codeconnect.gateway.application.dto.response.UserResponse
 */
export interface UserResponse {
  id: string;
  email: string;
  displayName: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
}

/**
 * RFC 7807 Problem Details representation
 */
export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance?: string;
  timestamp?: string;
  errors?: Record<string, string>;
}
