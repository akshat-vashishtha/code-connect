import { SignupRequest } from "@/dto/request/SignupRequest";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Validates registration payloads and enforces role-based invariants.
 * Adheres to SLAP by delegating mentor field checks to a dedicated helper method.
 */
export class SignupValidator {
  public static validate(request: SignupRequest): Record<string, string> {
    const errors: Record<string, string> = {};

    SignupValidator.validateCommonFields(request, errors);

    if (request.role === "ROLE_MENTOR") {
      SignupValidator.validateMentorFields(request, errors);
    }

    return errors;
  }

  private static validateCommonFields(request: SignupRequest, errors: Record<string, string>): void {
    const trimmedName = request.displayName.trim();
    if (!trimmedName || trimmedName.length < 2) {
      errors.displayName = "Name must be at least 2 characters";
    }

    const trimmedEmail = request.email.trim();
    if (!trimmedEmail || !EMAIL_REGEX.test(trimmedEmail)) {
      errors.email = "Please enter a valid email address";
    }

    if (!request.password || request.password.length < 8) {
      errors.password = "Password must be at least 8 characters";
    }
  }

  private static validateMentorFields(request: SignupRequest, errors: Record<string, string>): void {
    const trimmedUrl = request.linkedInUrl ? request.linkedInUrl.trim() : "";
    if (!trimmedUrl || (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://"))) {
      errors.linkedInUrl = "Must be a valid LinkedIn URL starting with http:// or https://";
    }

    const trimmedBio = request.bio ? request.bio.trim() : "";
    if (!trimmedBio || trimmedBio.length < 20) {
      errors.bio = "Please provide at least 20 characters describing your experience";
    }
  }
}
