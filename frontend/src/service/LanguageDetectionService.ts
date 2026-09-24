import { LanguageClient, languageClient } from "@/client/LanguageClient";
import { LanguageDetectionResponse } from "@/dto/response/LanguageDetectionResponse";

/**
 * Service orchestrating conversational text analysis.
 */
export class LanguageDetectionService {
  constructor(private readonly client: LanguageClient = languageClient) {}

  public async evaluateMessage(message: string): Promise<LanguageDetectionResponse> {
    const response = await this.client.detectLanguage(message);
    return response.data;
  }
}

export const languageDetectionService = new LanguageDetectionService();
