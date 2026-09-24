import { ApiResponse } from "@/dto/response/ApiResponse";
import { ProblemDetail } from "@/dto/response/ProblemDetail";

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
 * Enterprise HTTP Client wrapper mirroring Spring's WebClient.
 * Encapsulates credentials, headers, and RFC 7807 ProblemDetail error mapping.
 */
export class HttpClient {
  public static async execute<T>(
    endpoint: string,
    options: RequestInit,
    defaultErrorMessage: string
  ): Promise<ApiResponse<T>> {
    const defaultHeaders: HeadersInit = {
      Accept: "application/json, application/problem+json",
      ...(options.body ? { "Content-Type": "application/json" } : {}),
    };

    const response = await fetch(endpoint, {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
      credentials: "include",
    });

    if (!response.ok) {
      let problemDetail: ProblemDetail | undefined;
      try {
        problemDetail = (await response.json()) as ProblemDetail;
      } catch {
        // Fallback for non-JSON response bodies
      }

      const errorMessage =
        problemDetail?.detail ||
        problemDetail?.title ||
        `${defaultErrorMessage} (HTTP ${response.status})`;

      throw new ApiError(errorMessage, response.status, problemDetail);
    }

    return (await response.json()) as ApiResponse<T>;
  }
}
