import { HttpClient } from "@/client/HttpClient";
import { LanguageDetectionRequest } from "@/dto/request/LanguageDetectionRequest";
import { ApiResponse } from "@/dto/response/ApiResponse";
import { LanguageDetectionResponse } from "@/dto/response/LanguageDetectionResponse";

/**
 * Dedicated API Client for User Service Language Heuristic endpoints.
 */
export class LanguageClient {
  public async detectLanguage(text: string): Promise<ApiResponse<LanguageDetectionResponse>> {
    const request: LanguageDetectionRequest = { text };
    return HttpClient.execute<LanguageDetectionResponse>(
      "/api/v1/users/detect-language",
      {
        method: "POST",
        body: JSON.stringify(request),
      },
      "Failed to detect language preference"
    );
  }
}

export const languageClient = new LanguageClient();
