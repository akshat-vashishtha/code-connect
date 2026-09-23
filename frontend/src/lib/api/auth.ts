import { ApiResponse } from "@/types/api";
import { ProblemDetail, SignupRequest, UserResponse } from "@/types/auth";

export class ApiError extends Error {
  public readonly status: number;
  public readonly problemDetail?: ProblemDetail;

  constructor(message: string, status: number, problemDetail?: ProblemDetail) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.problemDetail = problemDetail;
  }
}

/**
 * Dispatches user registration command to Gateway service.
 * Includes credentials to receive and persist the APP_SESSION cookie.
 */
export async function signup(request: SignupRequest): Promise<ApiResponse<UserResponse>> {
  const response = await fetch("/api/v1/auth/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    let problemDetail: ProblemDetail | undefined;
    try {
      problemDetail = (await response.json()) as ProblemDetail;
    } catch {
      // Non-JSON error body fallback
    }

    const errorMessage =
      problemDetail?.detail ||
      problemDetail?.title ||
      `Registration failed with status ${response.status}`;

    throw new ApiError(errorMessage, response.status, problemDetail);
  }

  return (await response.json()) as ApiResponse<UserResponse>;
}
