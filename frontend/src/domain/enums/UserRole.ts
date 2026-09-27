/**
 * Discrete user role assignments matching com.codeconnect.user.domain.enums.UserRole
 */
export enum UserRole {
  STUDENT = "ROLE_STUDENT",
  MENTOR = "ROLE_MENTOR",
  ADMIN = "ROLE_ADMIN",
}

export type UserRoleType = "ROLE_STUDENT" | "ROLE_MENTOR" | "ROLE_ADMIN";
