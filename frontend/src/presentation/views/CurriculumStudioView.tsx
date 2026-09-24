"use client";

import React, { useState } from "react";
import Link from "next/link";
import { AppShell } from "@/presentation/organisms/AppShell";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatusPill } from "@/presentation/atoms/StatusPill";

interface FootholdItem {
  readonly id: string;
  readonly number: string;
  readonly title: string;
  readonly trackId: string;
  readonly altitude: string;
  readonly status: "DRAFT" | "PUBLISHED";
}

interface TestCaseItem {
  readonly id: string;
  name: string;
  type: "VISIBLE" | "HIDDEN";
  input: string;
  expected: string;
  constraintNotes?: string;
}

const INITIAL_FOOTHOLDS: FootholdItem[] = [
  {
    id: "f21",
    number: "2.1",
    title: "Arrays: Mailbox Grid",
    trackId: "track-2",
    altitude: "1,100m",
    status: "PUBLISHED",
  },
  {
    id: "f22",
    number: "2.2",
    title: "Linked Nodes: Scavenger",
    trackId: "track-2",
    altitude: "1,250m",
    status: "PUBLISHED",
  },
  {
    id: "f23",
    number: "2.3",
    title: "Circular Queue: Carousel",
    trackId: "track-2",
    altitude: "1,420m",
    status: "PUBLISHED",
  },
  {
    id: "f24",
    number: "2.4",
    title: "Priority Queue: Min-Heap",
    trackId: "track-2",
    altitude: "1,600m",
    status: "DRAFT", // Edge Case 4: In-progress draft
  },
];

const STARTER_TEMPLATE = `public class MinHeap {
    private final int[] heap;
    private int size = 0;

    public MinHeap(int capacity) {
        this.heap = new int[capacity];
    }

    public void insert(int priority, String task) {
        // Student implements siftUp
    }

    public String pollMin() {
        // Student implements siftDown
        return null;
    }

    public int size() {
        return size;
    }
}`;

export const CurriculumStudioView: React.FC = () => {
  const [footholds, setFootholds] = useState<FootholdItem[]>(INITIAL_FOOTHOLDS);
  const [selectedFootholdId, setSelectedFootholdId] = useState<string>("f24");
  const [selectedTrack, setSelectedTrack] = useState<string>("Track 2: Data Structures");
  const [title, setTitle] = useState<string>("Priority Queue: Min-Heap Implementation");
  const [altitude, setAltitude] = useState<string>("1,600m");
  const [storyTitle, setStoryTitle] = useState<string>(
    "The Hospital Emergency Room Triage Desk"
  );
  const [storyBody, setStoryBody] = useState<string>(
    "In an ER, patients don't get treated strictly by arrival order (FIFO). A patient with cardiac arrest has higher priority (lower number = 1) than a sprained wrist (priority = 10). The triage desk maintains a Min-Heap."
  );
  const [starterCode, setStarterCode] = useState<string>(STARTER_TEMPLATE);
  const [testCases, setTestCases] = useState<TestCaseItem[]>([
    {
      id: "tc1",
      name: "Visible Test 1: Basic Insert & Poll",
      type: "VISIBLE",
      input: "insert(3, 'Low'), insert(1, 'Crit'), pollMin()",
      expected: "'Crit'",
    },
    {
      id: "tc2",
      name: "Hidden Test 2: 100k Heap Elements Stress",
      type: "HIDDEN",
      input: "Checks O(log N) runtime & memory boundary",
      expected: "Max 450ms execution limit",
      constraintNotes: "Enforces strict heap invariant without ArrayList autoboxing overhead",
    },
  ]);

  const [notification, setNotification] = useState<string | null>(null);
  const [isSandboxRunning, setIsSandboxRunning] = useState<boolean>(false);

  const selectedFoothold =
    footholds.find((f) => f.id === selectedFootholdId) || footholds[3];

  // John's Edge Case 4: Foothold Draft vs. Published State
  const handlePublish = () => {
    setFootholds((prev) =>
      prev.map((f) =>
        f.id === selectedFoothold.id ? { ...f, status: "PUBLISHED" } : f
      )
    );
    setNotification(
      `Foothold "${title}" is now PUBLISHED and live on the student mountain track!`
    );
    setTimeout(() => setNotification(null), 4000);
  };

  const handleSaveDraft = () => {
    setFootholds((prev) =>
      prev.map((f) =>
        f.id === selectedFoothold.id ? { ...f, title, altitude } : f
      )
    );
    setNotification("Draft saved to cloud curriculum database.");
    setTimeout(() => setNotification(null), 3000);
  };

  const handlePreflightTest = () => {
    setIsSandboxRunning(true);
    setTimeout(() => {
      setIsSandboxRunning(false);
      setNotification("Pre-flight sandbox execution passed! All 2 test cases verified.");
      setTimeout(() => setNotification(null), 4000);
    }, 1200);
  };

  const handleCreateNew = () => {
    const newId = `f${Date.now()}`;
    const newFoothold: FootholdItem = {
      id: newId,
      number: "2.5",
      title: "New Foothold Draft",
      trackId: "track-2",
      altitude: "1,750m",
      status: "DRAFT",
    };
    setFootholds([...footholds, newFoothold]);
    setSelectedFootholdId(newId);
    setTitle("New Foothold Draft");
    setAltitude("1,750m");
    setStoryTitle("Physical Analogy Mechanism");
    setStoryBody("Describe the real-world physical mental model here...");
  };

  return (
    <AppShell
      trackTitle="Curriculum Authoring"
      trackHref="/curriculum/studio"
      footholdTitle="Authoring Studio (Story 2.3)"
      explicitRole="ROLE_MENTOR"
    >
      {/* Global Notification Banner */}
      {notification && (
        <div className="bg-indigo-600 text-white px-6 py-2 text-xs font-medium flex items-center justify-between animate-fadeIn">
          <div className="flex items-center space-x-2">
            <VectorIcon name="check-circle" size={16} />
            <span>{notification}</span>
          </div>
          <button
            onClick={() => setNotification(null)}
            className="text-white hover:text-indigo-100 font-bold"
          >
            ✕
          </button>
        </div>
      )}

      {/* Main Studio Viewport */}
      <main className="flex-1 p-6 max-w-7xl mx-auto w-full grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* ========================================================================= */}
        {/* LEFT COLUMN: Curriculum Hierarchy & Drafts (4 cols / 320px)               */}
        {/* ========================================================================= */}
        <div className="lg:col-span-4 flex flex-col space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">
            {/* Header */}
            <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Curriculum Tracks
              </h2>
              <button
                type="button"
                onClick={handleCreateNew}
                className="px-2.5 py-1 rounded-md bg-blue-50 hover:bg-blue-100 text-blue-700 text-[11px] font-bold border border-blue-200 transition-colors"
              >
                + New Foothold
              </button>
            </div>

            <div className="p-3 space-y-4 max-h-[500px] overflow-y-auto">
              {/* Track 1 */}
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1.5 px-1">
                  Track 1: Java 21 Models (500m)
                </div>
                <div className="p-2.5 rounded-lg bg-slate-50 border border-slate-200 text-xs text-slate-600 flex items-center space-x-2">
                  <VectorIcon name="checkmark" size={14} className="text-emerald-600" />
                  <span className="font-medium">5 Footholds Published</span>
                </div>
              </div>

              {/* Track 2 (Active) */}
              <div>
                <div className="text-[10px] font-bold text-indigo-600 uppercase tracking-wider mb-1.5 px-1">
                  Track 2: Data Structures (1,500m)
                </div>
                <div className="space-y-1.5">
                  {footholds.map((fh) => {
                    const isSelected = fh.id === selectedFootholdId;
                    return (
                      <div
                        key={fh.id}
                        onClick={() => {
                          setSelectedFootholdId(fh.id);
                          setTitle(fh.title);
                          setAltitude(fh.altitude);
                        }}
                        className={`p-2.5 rounded-lg border text-xs cursor-pointer transition-all flex items-center justify-between ${
                          isSelected
                            ? "bg-indigo-50/70 border-indigo-600 text-indigo-950 font-bold shadow-2xs"
                            : "bg-white border-slate-200 hover:bg-slate-50 text-slate-700"
                        }`}
                      >
                        <span className="truncate pr-2">
                          {fh.number} {fh.title}
                        </span>
                        <span
                          className={`text-[9px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider shrink-0 ${
                            fh.status === "PUBLISHED"
                              ? "bg-emerald-100 text-emerald-800"
                              : "bg-amber-100 text-amber-800 border border-amber-200"
                          }`}
                        >
                          {fh.status}
                        </span>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Track 3 */}
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1.5 px-1">
                  Track 3: Concurrency (2,800m)
                </div>
                <div className="p-2.5 rounded-lg bg-slate-50 border border-slate-200 text-xs text-slate-600">
                  1 Published • 2 Drafts
                </div>
              </div>
            </div>

            {/* Bulk Uploader Box */}
            <div className="p-4 bg-purple-50/60 border-t border-purple-100 m-3 rounded-lg border">
              <div className="text-xs font-bold text-purple-900">
                Bulk Curriculum Uploader
              </div>
              <p className="text-[11px] text-purple-700 mt-0.5">
                Drag and drop Markdown or YAML files containing complete foothold modules.
              </p>
              <div className="mt-3 p-4 border-2 border-dashed border-purple-300 rounded-lg bg-white/80 text-center cursor-pointer hover:bg-white transition-colors">
                <div className="text-xs font-semibold text-indigo-600">
                  Drop .yaml or .md here
                </div>
                <div className="text-[10px] text-purple-600 mt-0.5">
                  or click to browse local files
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* ========================================================================= */}
        {/* RIGHT COLUMN: Foothold Authoring & Test Suite Builder (8 cols)            */}
        {/* ========================================================================= */}
        <div className="lg:col-span-8 flex flex-col space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">
            {/* Canvas Header */}
            <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <div>
                <h2 className="text-sm font-bold text-slate-900">
                  Authoring: {title}
                </h2>
                <span className="text-[11px] text-slate-500">
                  Target Track: {selectedTrack}
                </span>
              </div>

              {/* Draft vs Published Edge Case 4 Pill */}
              <div className="flex items-center space-x-2">
                <span
                  className={`text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wider ${
                    selectedFoothold.status === "PUBLISHED"
                      ? "bg-emerald-100 text-emerald-800 border border-emerald-300"
                      : "bg-amber-100 text-amber-800 border border-amber-300"
                  }`}
                >
                  STATUS: {selectedFoothold.status}
                </span>
              </div>
            </div>

            <div className="p-5 space-y-5">
              {/* Form Section 1: Metadata Fields */}
              <div className="grid grid-cols-1 sm:grid-cols-12 gap-3">
                <div className="sm:col-span-4">
                  <label className="block text-xs font-bold text-slate-700 mb-1">
                    Assign To Track
                  </label>
                  <select
                    value={selectedTrack}
                    onChange={(e) => setSelectedTrack(e.target.value)}
                    className="w-full text-xs bg-white border border-slate-300 rounded-lg p-2.5 text-slate-900 focus:ring-2 focus:ring-indigo-600 focus:outline-hidden"
                  >
                    <option value="Track 1: Java 21 Models">Track 1: Java 21 Models (500m)</option>
                    <option value="Track 2: Data Structures">Track 2: Data Structures (1,500m)</option>
                    <option value="Track 3: Concurrency">Track 3: Concurrency (2,800m)</option>
                    <option value="Track 4: Distributed">Track 4: Distributed Systems (4,000m)</option>
                  </select>
                </div>

                <div className="sm:col-span-5">
                  <label className="block text-xs font-bold text-slate-700 mb-1">
                    Foothold Title
                  </label>
                  <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="w-full text-xs bg-white border border-slate-300 rounded-lg p-2 text-slate-900 focus:ring-2 focus:ring-indigo-600 focus:outline-hidden font-medium"
                  />
                </div>

                <div className="sm:col-span-3">
                  <label className="block text-xs font-bold text-slate-700 mb-1">
                    Altitude (Meters)
                  </label>
                  <input
                    type="text"
                    value={altitude}
                    onChange={(e) => setAltitude(e.target.value)}
                    className="w-full text-xs bg-white border border-slate-300 rounded-lg p-2 text-slate-900 focus:ring-2 focus:ring-indigo-600 focus:outline-hidden font-medium"
                  />
                </div>
              </div>

              {/* Form Section 2: Real-World Conceptual Story & Mental Model */}
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <label className="text-xs font-bold text-slate-900">
                    Real-World Conceptual Story &amp; Mental Model
                  </label>
                  <span className="text-[10px] text-slate-400">
                    (Mandatory: Anchor to a real physical mechanism)
                  </span>
                </div>

                <input
                  type="text"
                  value={storyTitle}
                  onChange={(e) => setStoryTitle(e.target.value)}
                  className="w-full text-xs bg-white border border-slate-300 rounded-lg p-2 text-slate-900 focus:ring-2 focus:ring-indigo-600 focus:outline-hidden font-semibold"
                  placeholder="Story Analogy Title..."
                />

                <textarea
                  rows={3}
                  value={storyBody}
                  onChange={(e) => setStoryBody(e.target.value)}
                  className="w-full text-xs bg-slate-50 border border-slate-300 rounded-lg p-2.5 text-slate-800 focus:ring-2 focus:ring-indigo-600 focus:outline-hidden leading-relaxed"
                  placeholder="Explain the real-world mechanism..."
                />

                <div className="flex items-center space-x-2 text-[11px] text-blue-700 bg-blue-50 px-3 py-1.5 rounded border border-blue-200">
                  <VectorIcon name="checkmark" size={13} strokeWidth={2} />
                  <span>Attached Diagram: <code className="font-mono font-bold">er_triage_heap.svg</code></span>
                </div>
              </div>

              {/* Form Section 3: Java 21 Method Contract & Starter Template */}
              <div>
                <div className="flex items-center justify-between mb-1.5">
                  <label className="text-xs font-bold text-slate-900">
                    Java 21 Method Contract &amp; Starter Template
                  </label>
                  <span className="text-[10px] font-mono text-slate-400">
                    MinHeap.java (Java 21)
                  </span>
                </div>
                <div className="rounded-lg bg-[#0f172a] text-slate-100 overflow-hidden font-mono text-xs border border-slate-800">
                  <textarea
                    rows={7}
                    value={starterCode}
                    onChange={(e) => setStarterCode(e.target.value)}
                    className="w-full bg-transparent p-3 text-slate-200 focus:outline-hidden resize-none font-mono text-xs leading-relaxed"
                  />
                </div>
              </div>

              {/* Form Section 4: Automated Test Case Suite */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-xs font-bold text-slate-900">
                    Automated Test Case Suite (Visible &amp; Hidden)
                  </label>
                  <span className="text-[10px] text-slate-500">
                    {testCases.length} Test Cases Configured
                  </span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {testCases.map((tc) => (
                    <div
                      key={tc.id}
                      className={`p-3 rounded-lg border text-xs flex flex-col justify-between ${
                        tc.type === "VISIBLE"
                          ? "bg-emerald-50/70 border-emerald-200"
                          : "bg-slate-50 border-slate-200"
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <span className="font-bold text-slate-900">{tc.name}</span>
                        <span
                          className={`text-[9px] font-bold px-2 py-0.5 rounded uppercase ${
                            tc.type === "VISIBLE"
                              ? "bg-emerald-100 text-emerald-800"
                              : "bg-slate-200 text-slate-700"
                          }`}
                        >
                          {tc.type}
                        </span>
                      </div>
                      <div className="mt-2 space-y-0.5 font-mono text-[10px] text-slate-700">
                        <div>Input: {tc.input}</div>
                        <div>Expected: {tc.expected}</div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Bottom Actions Toolbar */}
              <div className="pt-3 border-t border-slate-200 flex flex-wrap items-center justify-between gap-3">
                <button
                  type="button"
                  onClick={handlePreflightTest}
                  disabled={isSandboxRunning}
                  className="px-4 py-2 rounded-lg bg-white border border-slate-300 hover:bg-slate-50 text-slate-700 text-xs font-semibold flex items-center space-x-1.5 shadow-2xs transition-colors"
                >
                  <VectorIcon name="play" size={13} />
                  <span>
                    {isSandboxRunning ? "Testing Sandbox..." : "Pre-flight Test in Sandbox"}
                  </span>
                </button>

                <div className="flex items-center space-x-2.5">
                  <button
                    type="button"
                    onClick={handleSaveDraft}
                    className="px-4 py-2 rounded-lg bg-white border border-slate-300 hover:bg-slate-50 text-slate-700 text-xs font-semibold transition-colors"
                  >
                    Save Draft
                  </button>

                  <button
                    type="button"
                    onClick={handlePublish}
                    className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold transition-colors shadow-sm flex items-center space-x-1.5"
                  >
                    <span>Publish Foothold</span>
                    <VectorIcon name="arrow-right" size={14} />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </AppShell>
  );
};
