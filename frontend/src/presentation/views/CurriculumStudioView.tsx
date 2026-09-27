"use client";

import React, { useState } from "react";
import { AppShell } from "@/presentation/organisms/AppShell";
import { curriculumClient } from "@/client/CurriculumClient";
import {
  TrackStatus,
  LanguageMode,
  TestCase,
  CreateTrackRequest,
  CreateModuleRequest,
  CreateLessonRequest,
} from "@/types/curriculum";

export const CurriculumStudioView: React.FC = () => {
  const [activeTab, setActiveTab] = useState<"track" | "module" | "lesson">("lesson");
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Track Form State
  const [trackForm, setTrackForm] = useState<CreateTrackRequest>({
    title: "",
    slug: "",
    description: "",
    estimatedHours: 10,
    status: "PUBLISHED" as TrackStatus,
  });

  // Module Form State
  const [moduleForm, setModuleForm] = useState<CreateModuleRequest>({
    trackId: "",
    title: "",
    slug: "",
    sequence: 1,
    description: "",
  });

  // Lesson Form State
  const [lessonForm, setLessonForm] = useState<{
    moduleId: string;
    trackId: string;
    title: string;
    slug: string;
    sequence: number;
    enTitle: string;
    enNarrative: string;
    enAnalogy: string;
    hinglishTitle: string;
    hinglishNarrative: string;
    hinglishAnalogy: string;
    starterCode: string;
    solutionTemplate: string;
    prerequisiteLessonId: string;
  }>({
    moduleId: "",
    trackId: "",
    title: "",
    slug: "",
    sequence: 1,
    enTitle: "",
    enNarrative: "",
    enAnalogy: "",
    hinglishTitle: "",
    hinglishNarrative: "",
    hinglishAnalogy: "",
    starterCode: "public class Solution {\n    // Write your code here\n}",
    solutionTemplate: "",
    prerequisiteLessonId: "",
  });

  // Test Cases State
  const [testCases, setTestCases] = useState<TestCase[]>([
    { id: "tc-1", name: "Sample Case 1", input: "5", expectedOutput: "10", isHidden: false },
  ]);

  const handleAddTestCase = () => {
    const newId = `tc-${testCases.length + 1}`;
    setTestCases([
      ...testCases,
      { id: newId, name: `Test Case ${testCases.length + 1}`, input: "", expectedOutput: "", isHidden: false },
    ]);
  };

  const handleRemoveTestCase = (index: number) => {
    setTestCases(testCases.filter((_, i) => i !== index));
  };

  const handleTestCaseChange = (index: number, field: keyof TestCase, value: string | boolean) => {
    const updated = [...testCases];
    updated[index] = { ...updated[index], [field]: value };
    setTestCases(updated);
  };

  const handleCreateTrack = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setSuccessMessage(null);
    setErrorMessage(null);
    try {
      const res = await curriculumClient.createTrack(trackForm);
      setSuccessMessage(`Track "${res.data.title}" created successfully!`);
      setTrackForm({ title: "", slug: "", description: "", estimatedHours: 10, status: "PUBLISHED" });
    } catch (err: unknown) {
      setErrorMessage(err instanceof Error ? err.message : "Failed to create track");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCreateModule = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setSuccessMessage(null);
    setErrorMessage(null);
    try {
      const res = await curriculumClient.createModule(moduleForm);
      setSuccessMessage(`Module "${res.data.title}" created successfully!`);
      setModuleForm({ trackId: "", title: "", slug: "", sequence: 1, description: "" });
    } catch (err: unknown) {
      setErrorMessage(err instanceof Error ? err.message : "Failed to create module");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCreateLesson = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setSuccessMessage(null);
    setErrorMessage(null);

    const request: CreateLessonRequest = {
      moduleId: lessonForm.moduleId.trim(),
      trackId: lessonForm.trackId.trim(),
      title: lessonForm.title.trim(),
      slug: lessonForm.slug.trim(),
      sequence: Number(lessonForm.sequence),
      storyAnalogies: {
        ENGLISH: {
          title: lessonForm.enTitle || lessonForm.title,
          narrative: lessonForm.enNarrative,
          realWorldAnalogy: lessonForm.enAnalogy,
          socraticPrompts: [],
        },
        HINGLISH: {
          title: lessonForm.hinglishTitle || lessonForm.title,
          narrative: lessonForm.hinglishNarrative,
          realWorldAnalogy: lessonForm.hinglishAnalogy,
          socraticPrompts: [],
        },
      } as Record<LanguageMode, { title: string; narrative: string; realWorldAnalogy: string; socraticPrompts: string[] }>,
      starterCode: lessonForm.starterCode,
      solutionTemplate: lessonForm.solutionTemplate || undefined,
      testCases: testCases,
      prerequisiteLessonId: lessonForm.prerequisiteLessonId.trim() || undefined,
    };

    try {
      const res = await curriculumClient.createLesson(request);
      setSuccessMessage(`Lesson "${res.data.title}" published successfully!`);
    } catch (err: unknown) {
      setErrorMessage(err instanceof Error ? err.message : "Failed to create lesson");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell trackTitle="Curriculum Studio" trackHref="/curriculum/studio" lessonTitle="Authoring">
      <main className="flex-1 p-8 max-w-5xl mx-auto w-full font-sans">
        {/* Studio Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Curriculum Authoring Studio</h1>
          <p className="text-sm text-slate-600 mt-1">Author and publish curriculum tracks, modules, lessons, and test suites.</p>
        </div>

        {/* Tab Navigation */}
        <div className="flex border-b border-slate-200 mb-6">
          <button
            type="button"
            onClick={() => setActiveTab("lesson")}
            className={`py-3 px-6 text-sm font-semibold border-b-2 transition-all ${
              activeTab === "lesson" ? "border-cyan-600 text-cyan-600" : "border-transparent text-slate-500 hover:text-slate-800"
            }`}
          >
            Author Lesson
          </button>
          <button
            type="button"
            onClick={() => setActiveTab("module")}
            className={`py-3 px-6 text-sm font-semibold border-b-2 transition-all ${
              activeTab === "module" ? "border-cyan-600 text-cyan-600" : "border-transparent text-slate-500 hover:text-slate-800"
            }`}
          >
            Create Module
          </button>
          <button
            type="button"
            onClick={() => setActiveTab("track")}
            className={`py-3 px-6 text-sm font-semibold border-b-2 transition-all ${
              activeTab === "track" ? "border-cyan-600 text-cyan-600" : "border-transparent text-slate-500 hover:text-slate-800"
            }`}
          >
            Create Track
          </button>
        </div>

        {/* Status Banners */}
        {successMessage && (
          <div className="mb-6 p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-xl text-sm font-medium">
            ✓ {successMessage}
          </div>
        )}
        {errorMessage && (
          <div className="mb-6 p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-xl text-sm font-medium">
            ⚠️ {errorMessage}
          </div>
        )}

        {/* Tab 1: Create Track */}
        {activeTab === "track" && (
          <form onSubmit={handleCreateTrack} className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs space-y-4">
            <h2 className="text-lg font-bold text-slate-900 border-b pb-2">New Track</h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Track Title</label>
                <input
                  type="text"
                  required
                  value={trackForm.title}
                  onChange={(e) => setTrackForm({ ...trackForm, title: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="e.g. Data Structures & Algorithms"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Slug</label>
                <input
                  type="text"
                  required
                  value={trackForm.slug}
                  onChange={(e) => setTrackForm({ ...trackForm, slug: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="e.g. dsa-foundations"
                />
              </div>
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Description</label>
              <textarea
                required
                rows={3}
                value={trackForm.description}
                onChange={(e) => setTrackForm({ ...trackForm, description: e.target.value })}
                className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                placeholder="Brief track description..."
              />
            </div>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2.5 bg-cyan-600 hover:bg-cyan-700 text-white font-bold text-xs rounded-xl shadow-md transition-all"
            >
              {isSubmitting ? "Creating..." : "Save Track"}
            </button>
          </form>
        )}

        {/* Tab 2: Create Module */}
        {activeTab === "module" && (
          <form onSubmit={handleCreateModule} className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs space-y-4">
            <h2 className="text-lg font-bold text-slate-900 border-b pb-2">New Module</h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Track ID</label>
                <input
                  type="text"
                  required
                  value={moduleForm.trackId}
                  onChange={(e) => setModuleForm({ ...moduleForm, trackId: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="Target Track ID"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Module Title</label>
                <input
                  type="text"
                  required
                  value={moduleForm.title}
                  onChange={(e) => setModuleForm({ ...moduleForm, title: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="e.g. Arrays & Memory Allocation"
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Slug</label>
                <input
                  type="text"
                  required
                  value={moduleForm.slug}
                  onChange={(e) => setModuleForm({ ...moduleForm, slug: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="e.g. arrays-memory"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Sequence Index</label>
                <input
                  type="number"
                  required
                  min={1}
                  value={moduleForm.sequence}
                  onChange={(e) => setModuleForm({ ...moduleForm, sequence: Number(e.target.value) })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                />
              </div>
            </div>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2.5 bg-cyan-600 hover:bg-cyan-700 text-white font-bold text-xs rounded-xl shadow-md transition-all"
            >
              {isSubmitting ? "Creating..." : "Save Module"}
            </button>
          </form>
        )}

        {/* Tab 3: Author Lesson */}
        {activeTab === "lesson" && (
          <form onSubmit={handleCreateLesson} className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs space-y-6">
            <h2 className="text-lg font-bold text-slate-900 border-b pb-2">Author & Publish Lesson</h2>

            {/* General Metadata */}
            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Track ID</label>
                <input
                  type="text"
                  required
                  value={lessonForm.trackId}
                  onChange={(e) => setLessonForm({ ...lessonForm, trackId: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="Track ID"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Module ID</label>
                <input
                  type="text"
                  required
                  value={lessonForm.moduleId}
                  onChange={(e) => setLessonForm({ ...lessonForm, moduleId: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="Module ID"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Sequence</label>
                <input
                  type="number"
                  required
                  min={1}
                  value={lessonForm.sequence}
                  onChange={(e) => setLessonForm({ ...lessonForm, sequence: Number(e.target.value) })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Lesson Title</label>
                <input
                  type="text"
                  required
                  value={lessonForm.title}
                  onChange={(e) => setLessonForm({ ...lessonForm, title: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="e.g. Dynamic Array Resizing"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Slug</label>
                <input
                  type="text"
                  required
                  value={lessonForm.slug}
                  onChange={(e) => setLessonForm({ ...lessonForm, slug: e.target.value })}
                  className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500"
                  placeholder="e.g. dynamic-array-resizing"
                />
              </div>
            </div>

            {/* Bilingual Narrative Authoring */}
            <div className="border border-slate-200 rounded-xl p-4 bg-slate-50 space-y-4">
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700">Bilingual Story Analogies</h3>
              <div className="grid grid-cols-2 gap-4">
                {/* English Narrative */}
                <div className="space-y-2">
                  <span className="text-xs font-bold text-cyan-600">English Narrative</span>
                  <input
                    type="text"
                    placeholder="Story Title (EN)"
                    value={lessonForm.enTitle}
                    onChange={(e) => setLessonForm({ ...lessonForm, enTitle: e.target.value })}
                    className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-lg"
                  />
                  <textarea
                    rows={4}
                    placeholder="Narrative content (EN)..."
                    value={lessonForm.enNarrative}
                    onChange={(e) => setLessonForm({ ...lessonForm, enNarrative: e.target.value })}
                    className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-lg"
                  />
                  <input
                    type="text"
                    placeholder="Real-World Analogy (EN)"
                    value={lessonForm.enAnalogy}
                    onChange={(e) => setLessonForm({ ...lessonForm, enAnalogy: e.target.value })}
                    className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-lg"
                  />
                </div>

                {/* Hinglish Narrative */}
                <div className="space-y-2">
                  <span className="text-xs font-bold text-amber-600">Hinglish Narrative</span>
                  <input
                    type="text"
                    placeholder="Story Title (Hinglish)"
                    value={lessonForm.hinglishTitle}
                    onChange={(e) => setLessonForm({ ...lessonForm, hinglishTitle: e.target.value })}
                    className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-lg"
                  />
                  <textarea
                    rows={4}
                    placeholder="Narrative content (Hinglish)..."
                    value={lessonForm.hinglishNarrative}
                    onChange={(e) => setLessonForm({ ...lessonForm, hinglishNarrative: e.target.value })}
                    className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-lg"
                  />
                  <input
                    type="text"
                    placeholder="Real-World Analogy (Hinglish)"
                    value={lessonForm.hinglishAnalogy}
                    onChange={(e) => setLessonForm({ ...lessonForm, hinglishAnalogy: e.target.value })}
                    className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-lg"
                  />
                </div>
              </div>
            </div>

            {/* Code Templates */}
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Starter Code (Java)</label>
              <textarea
                rows={4}
                required
                value={lessonForm.starterCode}
                onChange={(e) => setLessonForm({ ...lessonForm, starterCode: e.target.value })}
                className="w-full px-3 py-2 text-xs font-mono border border-slate-300 rounded-lg bg-slate-950 text-cyan-300"
              />
            </div>

            {/* Test Case Builder */}
            <div className="border border-slate-200 rounded-xl p-4 space-y-3">
              <div className="flex justify-between items-center">
                <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700">Test Cases Suite</h3>
                <button
                  type="button"
                  onClick={handleAddTestCase}
                  className="px-3 py-1 bg-slate-800 text-white rounded-lg text-xs font-semibold hover:bg-slate-700"
                >
                  + Add Test Case
                </button>
              </div>
              {testCases.map((tc, index) => (
                <div key={tc.id || index} className="grid grid-cols-4 gap-2 items-center bg-slate-50 p-3 rounded-lg border border-slate-200">
                  <input
                    type="text"
                    placeholder="Name"
                    value={tc.name}
                    onChange={(e) => handleTestCaseChange(index, "name", e.target.value)}
                    className="px-2 py-1 text-xs border rounded"
                  />
                  <input
                    type="text"
                    placeholder="Input"
                    value={tc.input}
                    onChange={(e) => handleTestCaseChange(index, "input", e.target.value)}
                    className="px-2 py-1 text-xs border rounded font-mono"
                  />
                  <input
                    type="text"
                    placeholder="Expected Output"
                    value={tc.expectedOutput}
                    onChange={(e) => handleTestCaseChange(index, "expectedOutput", e.target.value)}
                    className="px-2 py-1 text-xs border rounded font-mono"
                  />
                  <div className="flex items-center space-x-2 justify-end">
                    <label className="flex items-center space-x-1 text-xs">
                      <input
                        type="checkbox"
                        checked={tc.isHidden}
                        onChange={(e) => handleTestCaseChange(index, "isHidden", e.target.checked)}
                      />
                      <span>Hidden</span>
                    </label>
                    <button
                      type="button"
                      onClick={() => handleRemoveTestCase(index)}
                      className="text-rose-600 text-xs hover:text-rose-800 font-bold"
                    >
                      ✕
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-3 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white font-extrabold text-sm rounded-xl shadow-lg transition-all"
            >
              {isSubmitting ? "Publishing..." : "Publish Lesson to Curriculum"}
            </button>
          </form>
        )}
      </main>
    </AppShell>
  );
};
