import { AuthClient, authClient } from "@/client/AuthClient";
import { LoginRequest } from "@/dto/request/LoginRequest";
import { SignupRequest } from "@/dto/request/SignupRequest";
import { UserResponse } from "@/dto/response/UserResponse";

/**
 * Service facade orchestrating authentication operations.
 * Coordinates network client calls and response unwrapping.
 */
export class AuthService {
  constructor(private readonly client: AuthClient = authClient) {}

  public async login(request: LoginRequest): Promise<UserResponse> {
    const response = await this.client.login(request);
    return response.data;
  }

  public async signup(request: SignupRequest): Promise<UserResponse> {
    const response = await this.client.signup(request);
    return response.data;
  }

  public async getCurrentUser(): Promise<UserResponse | null> {
    try {
      const response = await this.client.getCurrentUser();
      return response.data;
    } catch {
      return null;
    }
  }

  public async logout(): Promise<void> {
    await this.client.logout();
  }
}

export const authService = new AuthService();
