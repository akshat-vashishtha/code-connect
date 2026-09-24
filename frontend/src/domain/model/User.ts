import { UserRole } from "@/domain/enums/UserRole";
import { UserStatus } from "@/domain/enums/UserStatus";

/**
 * Domain entity representing an authenticated platform user.
 */
export interface User {
  readonly id: string;
  readonly email: string;
  readonly displayName: string;
  readonly role: UserRole;
  readonly status: UserStatus;
  readonly createdAt: string;
}
