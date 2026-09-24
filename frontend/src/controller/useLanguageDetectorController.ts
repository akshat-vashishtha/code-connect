"use client";

import { useState } from "react";
import { languageDetectionService } from "@/service/LanguageDetectionService";
import { LanguageDetectionResponse } from "@/dto/response/LanguageDetectionResponse";

export interface LanguageDetectorControllerResult {
  readonly testText: string;
  readonly isDetecting: boolean;
  readonly detectionResult: LanguageDetectionResponse | null;
  readonly errorMessage: string | null;
  readonly setTestText: (text: string) => void;
  readonly handleDetect: () => Promise<void>;
  readonly setPreset: (presetText: string) => void;
}

/**
 * Controller orchestrating state and events for the conversational language test bench.
 */
export function useLanguageDetectorController(): LanguageDetectorControllerResult {
  const [testText, setTestText] = useState("bhai kaise kare samajh nahi aaya code me issue hai");
  const [detectionResult, setDetectionResult] = useState<LanguageDetectionResponse | null>(null);
  const [isDetecting, setIsDetecting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleDetect = async (): Promise<void> => {
    if (!testText.trim()) return;
    setIsDetecting(true);
    setErrorMessage(null);
    try {
      const result = await languageDetectionService.evaluateMessage(testText.trim());
      setDetectionResult(result);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Language detection failed";
      setErrorMessage(msg);
    } finally {
      setIsDetecting(false);
    }
  };

  const setPreset = (presetText: string): void => {
    setTestText(presetText);
    setErrorMessage(null);
  };

  return {
    testText,
    isDetecting,
    detectionResult,
    errorMessage,
    setTestText,
    handleDetect,
    setPreset,
  };
}
