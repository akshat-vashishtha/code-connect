import { ApiResponse } from "@/types/api";
import { LoginRequest, ProblemDetail, SignupRequest, UserResponse } from "@/types/auth";

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

async function handleResponse<T>(response: Response, defaultErrorMessage: string): Promise<ApiResponse<T>> {
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
      `${defaultErrorMessage} with status ${response.status}`;

    throw new ApiError(errorMessage, response.status, problemDetail);
  }

  return (await response.json()) as ApiResponse<T>;
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

  return handleResponse<UserResponse>(response, "Registration failed");
}

/**
 * Dispatches direct credential login command to Gateway service.
 * Issues Redis session and sets HttpOnly APP_SESSION cookie.
 */
export async function login(request: LoginRequest): Promise<ApiResponse<UserResponse>> {
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });

  return handleResponse<UserResponse>(response, "Login failed");
}

/**
 * Retrieves authenticated user identity and profile from Gateway service using APP_SESSION cookie.
 */
export async function getCurrentUser(): Promise<ApiResponse<UserResponse>> {
  const response = await fetch("/api/v1/auth/me", {
    method: "GET",
    headers: {
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
  });

  return handleResponse<UserResponse>(response, "Failed to retrieve user profile");
}

/**
 * Convenient alias for getCurrentUser
 */
export const getMe = getCurrentUser;

/**
 * Invalidates user session in Redis and clears APP_SESSION cookie.
 */
export async function logout(): Promise<ApiResponse<null>> {
  const response = await fetch("/api/v1/auth/logout", {
    method: "POST",
    headers: {
      Accept: "application/json, application/problem+json",
    },
    credentials: "include",
  });

  return handleResponse<null>(response, "Logout failed");
}
