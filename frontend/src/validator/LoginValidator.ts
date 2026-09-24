import { LoginRequest } from "@/dto/request/LoginRequest";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Validates login credentials prior to network dispatch.
 * Mirrors Spring Boot's Validator / @Valid constraint checks.
 */
export class LoginValidator {
  public static validate(request: LoginRequest): Record<string, string> {
    const errors: Record<string, string> = {};

    const trimmedEmail = request.email.trim();
    if (!trimmedEmail) {
      errors.email = "Email address is required";
    } else if (!EMAIL_REGEX.test(trimmedEmail)) {
      errors.email = "Please enter a valid email address";
    }

    if (!request.password) {
      errors.password = "Password is required";
    }

    return errors;
  }
}
