"use client";

import React, { useState } from "react";
import { LessonResponse, LanguageMode, StoryContent } from "@/types/curriculum";

export interface StoryReaderViewProps {
  readonly lesson: LessonResponse;
  readonly languageMode?: LanguageMode;
  readonly onLanguageModeChange?: (mode: LanguageMode) => void;
  readonly onOpenCockpit?: (lessonId: string) => void;
}

/**
 * Distraction-Free Text Story Reader Component.
 * Displays story-driven concept narratives, real-world analogies, code snippets with copy controls,
 * bilingual language mode switching (ENGLISH | HINGLISH), and non-blocking prerequisite recommendations.
 * Strictly guarantees ZERO <video>, <iframe>, or <audio> media elements.
 */
export const StoryReaderView: React.FC<StoryReaderViewProps> = ({
  lesson,
  languageMode = "ENGLISH",
  onLanguageModeChange,
  onOpenCockpit,
}) => {
  const [copied, setCopied] = useState<boolean>(false);
  const [activeLanguage, setActiveLanguage] = useState<LanguageMode>(languageMode);

  const currentMode: LanguageMode = activeLanguage;
  const rawAnalogies = lesson.storyAnalogies || (lesson as unknown as { storyAnalogy?: Record<string, StoryContent> }).storyAnalogy || {};
  const currentStory: StoryContent | undefined =
    rawAnalogies[currentMode] || rawAnalogies["ENGLISH"] || rawAnalogies["HINGLISH"] || Object.values(rawAnalogies)[0];

  const handleLanguageToggle = (mode: LanguageMode) => {
    setActiveLanguage(mode);
    if (onLanguageModeChange) {
      onLanguageModeChange(mode);
    }
  };

  const handleCopyCode = async () => {
    if (!lesson.starterCode) return;
    try {
      await navigator.clipboard.writeText(lesson.starterCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Fallback if clipboard API is restricted
    }
  };

  const prereq = lesson.prerequisiteRecommendation;

  return (
    <article className="flex flex-col h-full bg-slate-900 text-slate-100 rounded-2xl border border-slate-800 shadow-2xl overflow-hidden font-sans">
      {/* Top Bar / Header */}
      <header className="flex items-center justify-between px-6 py-4 bg-slate-950/80 border-b border-slate-800 backdrop-blur-md">
        <div className="flex items-center space-x-3">
          <span className="px-2.5 py-1 text-xs font-semibold uppercase tracking-wider text-cyan-400 bg-cyan-950/60 border border-cyan-800/60 rounded-md">
            Lesson {lesson.sequence}
          </span>
          <h2 className="text-lg font-bold text-white tracking-tight">{lesson.title}</h2>
        </div>

        {/* Bilingual Language Mode Selector */}
        <div className="flex items-center bg-slate-900 p-1 rounded-xl border border-slate-800">
          <button
            type="button"
            onClick={() => handleLanguageToggle("ENGLISH")}
            className={`px-3 py-1 text-xs font-medium rounded-lg transition-all duration-200 ${
              currentMode === "ENGLISH"
                ? "bg-cyan-500 text-slate-950 font-semibold shadow-md"
                : "text-slate-400 hover:text-slate-200"
            }`}
          >
            EN
          </button>
          <button
            type="button"
            onClick={() => handleLanguageToggle("HINGLISH")}
            className={`px-3 py-1 text-xs font-medium rounded-lg transition-all duration-200 ${
              currentMode === "HINGLISH"
                ? "bg-amber-500 text-slate-950 font-semibold shadow-md"
                : "text-slate-400 hover:text-slate-200"
            }`}
          >
            HINGLISH
          </button>
        </div>
      </header>

      {/* Non-Blocking Prerequisite Banner */}
      {prereq && prereq.isRecommended && (
        <aside className="mx-6 mt-4 p-3 bg-amber-950/40 border border-amber-800/60 rounded-xl flex items-center justify-between text-xs text-amber-200">
          <div className="flex items-center space-x-2">
            <span className="text-base">💡</span>
            <span className="font-medium">
              {prereq.recommendationBadgeText || `Prerequisite "${prereq.prerequisiteLessonTitle}" Recommended`}
            </span>
          </div>
          <span className="text-[10px] text-amber-400/80 uppercase tracking-wider font-bold">
            Non-Blocking
          </span>
        </aside>
      )}

      {/* Story Narrative Content Area */}
      <div className="flex-1 overflow-y-auto px-6 py-6 space-y-6 text-slate-300 leading-relaxed text-sm">
        {currentStory ? (
          <>
            {/* Story Title & Narrative */}
            <section className="space-y-3">
              <h3 className="text-xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-400">
                {currentStory.title}
              </h3>
              <p className="whitespace-pre-line text-slate-200 leading-relaxed">
                {currentStory.narrative}
              </p>
            </section>

            {/* Real-World Visual Analogy Container */}
            {currentStory.realWorldAnalogy && (
              <section className="p-4 bg-slate-950/60 rounded-xl border border-slate-800 space-y-2">
                <h4 className="text-xs font-bold uppercase tracking-wider text-cyan-400 flex items-center space-x-1.5">
                  <span>🏛️ Real-World Analogy</span>
                </h4>
                <p className="text-xs text-slate-300 leading-normal">
                  {currentStory.realWorldAnalogy}
                </p>
              </section>
            )}

            {/* Socratic Hint Prompts */}
            {currentStory.socraticPrompts && currentStory.socraticPrompts.length > 0 && (
              <section className="space-y-2 pt-2">
                <h4 className="text-xs font-bold uppercase tracking-wider text-indigo-400">
                  🤔 Socratic Reflections
                </h4>
                <ul className="space-y-2 pl-4 list-disc text-slate-300 text-xs">
                  {currentStory.socraticPrompts.map((prompt, idx) => (
                    <li key={idx} className="leading-normal">
                      {prompt}
                    </li>
                  ))}
                </ul>
              </section>
            )}
          </>
        ) : (
          <p className="text-slate-500 italic">No story content available for selected language.</p>
        )}

        {/* Starter Code Block Section */}
        {lesson.starterCode && (
          <section className="space-y-2 pt-4 border-t border-slate-800/80">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Starter Code Snippet
              </span>
              <button
                type="button"
                onClick={handleCopyCode}
                className="px-2.5 py-1 text-xs font-medium text-cyan-400 hover:text-cyan-300 bg-slate-850 hover:bg-slate-800 rounded-lg border border-slate-700/80 transition-colors"
              >
                {copied ? "✓ Copied!" : "Copy Code"}
              </button>
            </div>
            <pre className="p-4 bg-slate-950 rounded-xl border border-slate-800/80 text-xs font-mono text-cyan-300 overflow-x-auto leading-relaxed">
              <code>{lesson.starterCode}</code>
            </pre>
          </section>
        )}
      </div>

      {/* Action Footer */}
      {onOpenCockpit && (
        <footer className="p-4 bg-slate-950/90 border-t border-slate-800 flex justify-end">
          <button
            type="button"
            onClick={() => onOpenCockpit(lesson.id)}
            className="px-5 py-2.5 text-xs font-bold text-slate-950 bg-gradient-to-r from-cyan-400 to-blue-400 hover:from-cyan-300 hover:to-blue-300 rounded-xl shadow-lg transition-all transform active:scale-95"
          >
            Open Coding Cockpit →
          </button>
        </footer>
      )}
    </article>
  );
};
