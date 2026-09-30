"use client";

import React, { useState } from "react";
import { LessonResponse, LanguageMode, StoryContent } from "@/types/curriculum";
import { BookOpen, HelpCircle, CheckCircle2, Sparkles, Terminal } from "lucide-react";

export interface CockpitStoryPaneProps {
  readonly lesson: LessonResponse | null;
  readonly languageMode: LanguageMode;
  readonly onLanguageModeChange: (mode: LanguageMode) => void;
}

export const CockpitStoryPane: React.FC<CockpitStoryPaneProps> = ({
  lesson,
  languageMode,
  onLanguageModeChange,
}) => {
  const [activeTab, setActiveTab] = useState<"narrative" | "testcases">("narrative");

  if (!lesson) {
    return (
      <div className="flex flex-col items-center justify-center h-full bg-slate-900 border border-slate-800 rounded-2xl p-8 text-center text-slate-400">
        <BookOpen className="w-10 h-10 text-slate-600 mb-3" />
        <h3 className="text-base font-semibold text-slate-200">No Lesson Selected</h3>
        <p className="text-xs text-slate-500 mt-1">Select a foothold from the curriculum to view problem context.</p>
      </div>
    );
  }

  const rawAnalogies = lesson.storyAnalogies || (lesson as unknown as { storyAnalogy?: Record<string, StoryContent> }).storyAnalogy || {};
  const currentStory: StoryContent | undefined =
    rawAnalogies[languageMode] || rawAnalogies["ENGLISH"] || rawAnalogies["HINGLISH"] || Object.values(rawAnalogies)[0];

  return (
    <article className="flex flex-col h-full bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden font-sans">
      {/* Story Pane Header */}
      <header className="flex items-center justify-between px-5 py-3.5 bg-slate-950/80 border-b border-slate-800 backdrop-blur-md">
        <div className="flex items-center space-x-2.5">
          <span className="px-2.5 py-0.5 text-xs font-semibold uppercase tracking-wider text-cyan-400 bg-cyan-950/60 border border-cyan-800/60 rounded-md">
            Lesson {lesson.sequence}
          </span>
          <h2 className="text-sm font-semibold text-slate-100 truncate max-w-[240px] md:max-w-[320px]">
            {lesson.title}
          </h2>
        </div>

        {/* Bilingual Language Selector */}
        <div className="flex items-center bg-slate-900 p-0.5 rounded-lg border border-slate-800">
          <button
            type="button"
            onClick={() => onLanguageModeChange("ENGLISH")}
            className={`px-2.5 py-1 text-xs font-medium rounded-md transition-all ${
              languageMode === "ENGLISH"
                ? "bg-cyan-600 text-white shadow-xs"
                : "text-slate-400 hover:text-slate-200"
            }`}
          >
            EN
          </button>
          <button
            type="button"
            onClick={() => onLanguageModeChange("HINGLISH")}
            className={`px-2.5 py-1 text-xs font-medium rounded-md transition-all ${
              languageMode === "HINGLISH"
                ? "bg-cyan-600 text-white shadow-xs"
                : "text-slate-400 hover:text-slate-200"
            }`}
          >
            HINGLISH
          </button>
        </div>
      </header>

      {/* Internal Navigation Tabs (Story Narrative vs. Test Cases) */}
      <nav className="flex items-center px-4 bg-slate-950/40 border-b border-slate-800/60 text-xs">
        <button
          type="button"
          onClick={() => setActiveTab("narrative")}
          className={`flex items-center space-x-1.5 py-2.5 px-3 border-b-2 font-medium transition-colors ${
            activeTab === "narrative"
              ? "border-cyan-400 text-cyan-300"
              : "border-transparent text-slate-400 hover:text-slate-200"
          }`}
        >
          <BookOpen className="w-3.5 h-3.5" />
          <span>Story & Narrative</span>
        </button>
        <button
          type="button"
          onClick={() => setActiveTab("testcases")}
          className={`flex items-center space-x-1.5 py-2.5 px-3 border-b-2 font-medium transition-colors ${
            activeTab === "testcases"
              ? "border-cyan-400 text-cyan-300"
              : "border-transparent text-slate-400 hover:text-slate-200"
          }`}
        >
          <Terminal className="w-3.5 h-3.5" />
          <span>Test Cases ({lesson.testCases?.filter((tc) => !tc.isHidden).length || 0})</span>
        </button>
      </nav>

      {/* Content Scroll Area */}
      <div className="flex-1 overflow-y-auto p-6 space-y-6 text-slate-300 custom-scrollbar">
        {activeTab === "narrative" ? (
          <>
            {/* Story Title */}
            {currentStory?.title && (
              <div>
                <h3 className="text-lg font-bold text-white tracking-tight">{currentStory.title}</h3>
              </div>
            )}

            {/* Narrative Content */}
            <div className="prose prose-invert prose-sm max-w-none leading-relaxed text-slate-300 space-y-4">
              {currentStory?.narrative ? (
                <p className="whitespace-pre-line text-sm leading-6">{currentStory.narrative}</p>
              ) : (
                <p className="text-sm text-slate-400 italic">No narrative available for this lesson.</p>
              )}
            </div>

            {/* Real World Analogy Box */}
            {currentStory?.realWorldAnalogy && (
              <section className="bg-slate-950/80 border border-slate-800 rounded-xl p-4.5 space-y-2 relative overflow-hidden">
                <div className="absolute top-0 right-0 w-24 h-24 bg-cyan-500/5 rounded-full blur-2xl" />
                <div className="flex items-center space-x-2 text-cyan-400">
                  <Sparkles className="w-4 h-4 text-cyan-400" />
                  <h4 className="text-xs font-semibold uppercase tracking-wider">Real-World Analogy</h4>
                </div>
                <p className="text-xs text-slate-300 leading-relaxed italic">
                  &ldquo;{currentStory.realWorldAnalogy}&rdquo;
                </p>
              </section>
            )}

            {/* Socratic Prompts / Guidance */}
            {currentStory?.socraticPrompts && currentStory.socraticPrompts.length > 0 && (
              <section className="space-y-2.5 pt-2">
                <div className="flex items-center space-x-2 text-amber-400">
                  <HelpCircle className="w-4 h-4 text-amber-400" />
                  <h4 className="text-xs font-semibold uppercase tracking-wider">Think Like An Engineer</h4>
                </div>
                <ul className="space-y-2">
                  {currentStory.socraticPrompts.map((prompt, index) => (
                    <li
                      key={index}
                      className="text-xs text-slate-300 bg-slate-950/40 border border-slate-800/80 rounded-lg p-3 flex items-start space-x-2.5"
                    >
                      <span className="text-amber-400 font-bold">•</span>
                      <span>{prompt}</span>
                    </li>
                  ))}
                </ul>
              </section>
            )}
          </>
        ) : (
          /* Test Cases Tab */
          <div className="space-y-4">
            <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
              Visible Test Cases ({lesson.testCases?.filter((tc) => !tc.isHidden).length || 0})
            </h4>
            {lesson.testCases && lesson.testCases.filter((tc) => !tc.isHidden).length > 0 ? (
              <div className="space-y-3">
                {lesson.testCases
                  .filter((tc) => !tc.isHidden)
                  .map((tc, index) => (
                    <div
                      key={tc.id || index}
                      className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-3"
                    >
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-semibold text-slate-200">{tc.name || `Test Case #${index + 1}`}</span>
                        <span className="flex items-center space-x-1 text-[11px] text-emerald-400">
                          <CheckCircle2 className="w-3 h-3" />
                          <span>Visible</span>
                        </span>
                      </div>
                      <div className="space-y-2 font-mono text-xs">
                        <div>
                          <span className="text-slate-500 text-[10px] uppercase">Input:</span>
                          <pre className="bg-slate-900 border border-slate-800 rounded p-2 text-cyan-300 overflow-x-auto mt-0.5">
                            {tc.input || "(empty)"}
                          </pre>
                        </div>
                        <div>
                          <span className="text-slate-500 text-[10px] uppercase">Expected Output:</span>
                          <pre className="bg-slate-900 border border-slate-800 rounded p-2 text-emerald-300 overflow-x-auto mt-0.5">
                            {tc.expectedOutput || "(empty)"}
                          </pre>
                        </div>
                      </div>
                    </div>
                  ))}
              </div>
            ) : (
              <p className="text-xs text-slate-500 italic">No visible test cases defined for this foothold.</p>
            )}
          </div>
        )}
      </div>
    </article>
  );
};
