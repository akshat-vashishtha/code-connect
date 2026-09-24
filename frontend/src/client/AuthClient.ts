import { HttpClient } from "@/client/HttpClient";
import { LoginRequest } from "@/dto/request/LoginRequest";
import { SignupRequest } from "@/dto/request/SignupRequest";
import { ApiResponse } from "@/dto/response/ApiResponse";
import { UserResponse } from "@/dto/response/UserResponse";

/**
 * Dedicated API Client for Authentication & Identity endpoints.
 * Interacts with Spring Cloud Gateway at /api/v1/auth/**
 */
export class AuthClient {
  public async signup(request: SignupRequest): Promise<ApiResponse<UserResponse>> {
    return HttpClient.execute<UserResponse>(
      "/api/v1/auth/signup",
      {
        method: "POST",
        body: JSON.stringify(request),
      },
      "Registration failed"
    );
  }

  public async login(request: LoginRequest): Promise<ApiResponse<UserResponse>> {
    return HttpClient.execute<UserResponse>(
      "/api/v1/auth/login",
      {
        method: "POST",
        body: JSON.stringify(request),
      },
      "Login failed"
    );
  }

  public async getCurrentUser(): Promise<ApiResponse<UserResponse>> {
    return HttpClient.execute<UserResponse>(
      "/api/v1/auth/me",
      {
        method: "GET",
      },
      "Failed to retrieve user profile"
    );
  }

  public async logout(): Promise<ApiResponse<null>> {
    return HttpClient.execute<null>(
      "/api/v1/auth/logout",
      {
        method: "POST",
      },
      "Logout failed"
    );
  }
}

export const authClient = new AuthClient();
