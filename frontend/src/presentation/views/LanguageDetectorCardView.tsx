import React from "react";
import { Sparkles, MessageSquare } from "lucide-react";
import { Button } from "@/presentation/atoms/Button";
import { LanguageDetectorControllerResult } from "@/controller/useLanguageDetectorController";

export interface LanguageDetectorCardViewProps {
  readonly controller: LanguageDetectorControllerResult;
}

/**
 * Pure presentation card for conversational language heuristic testing.
 */
export const LanguageDetectorCardView: React.FC<LanguageDetectorCardViewProps> = ({ controller }) => {
  const {
    testText,
    isDetecting,
    detectionResult,
    errorMessage,
    setTestText,
    handleDetect,
    setPreset,
  } = controller;

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col gap-4">
      <div className="flex items-center gap-2 text-indigo-600">
        <Sparkles className="w-5 h-5" />
        <h3 className="text-sm font-bold text-slate-900">Conversational Language Detector</h3>
      </div>
      <p className="text-xs text-slate-500 leading-relaxed">
        Story 1.4 automatically tags user sessions with{" "}
        <code className="px-1 py-0.5 rounded bg-slate-100 font-semibold text-indigo-600">
          languagePreference = HINGLISH
        </code>{" "}
        when conversational markers (&ldquo;bhai&rdquo;, &ldquo;samajh nahi aaya&rdquo;, &ldquo;kaise kare&rdquo;) are detected.
      </p>

      <div className="flex flex-col gap-2">
        <label htmlFor="test-text" className="text-xs font-semibold text-slate-700">
          Sample Message / Question
        </label>
        <textarea
          id="test-text"
          rows={3}
          value={testText}
          onChange={(e) => setTestText(e.target.value)}
          placeholder="Type Hindi/Hinglish or English text..."
          className="w-full p-3 rounded-xl border border-slate-200 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 leading-relaxed"
        />
      </div>

      <div className="flex flex-wrap gap-1.5">
        <span className="text-[10px] text-slate-400 font-semibold mr-1">Presets:</span>
        <button
          type="button"
          onClick={() => setPreset("Bhai recursion samajh nahi aaya, kaise kare?")}
          className="px-2 py-0.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-[10px] text-slate-700 transition-colors cursor-pointer"
        >
          Hinglish 1
        </button>
        <button
          type="button"
          onClick={() => setPreset("Yaar Spring Boot microservices me issue ho raha hai batao")}
          className="px-2 py-0.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-[10px] text-slate-700 transition-colors cursor-pointer"
        >
          Hinglish 2
        </button>
        <button
          type="button"
          onClick={() => setPreset("Please explain how Redis session management works")}
          className="px-2 py-0.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-[10px] text-slate-700 transition-colors cursor-pointer"
        >
          English
        </button>
      </div>

      <Button
        type="button"
        onClick={handleDetect}
        disabled={isDetecting || !testText.trim()}
        isLoading={isDetecting}
        loadingText="Classifying..."
        leftIcon={<MessageSquare className="w-3.5 h-3.5" />}
        variant="primary"
        size="sm"
        className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-700 shadow-indigo-500/20"
      >
        Detect Language &amp; Update Session
      </Button>

      {errorMessage && (
        <p className="text-xs text-red-600 font-medium">{errorMessage}</p>
      )}

      {detectionResult && (
        <div className="mt-2 p-3.5 rounded-xl bg-slate-50 border border-slate-200/80 flex flex-col gap-1.5">
          <div className="flex items-center justify-between text-xs">
            <span className="text-slate-500 font-medium">Result:</span>
            <span
              className={`px-2 py-0.5 rounded font-bold text-xs ${
                detectionResult.languagePreference === "HINGLISH"
                  ? "bg-purple-100 text-purple-800"
                  : "bg-blue-100 text-blue-800"
              }`}
            >
              {detectionResult.languagePreference}
            </span>
          </div>
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span>Confidence:</span>
            <span className="font-semibold text-slate-700">
              {Math.round(detectionResult.confidence * 100)}%
            </span>
          </div>
        </div>
      )}
    </div>
  );
};
