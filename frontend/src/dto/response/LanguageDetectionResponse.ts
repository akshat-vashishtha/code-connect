/**
 * Response payload for conversational language detection
 */
export interface LanguageDetectionResponse {
  readonly languagePreference: "ENGLISH" | "HINGLISH";
  readonly confidence: number;
}
