"use client";

import React, { useState } from "react";
import Link from "next/link";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatusPill } from "@/presentation/atoms/StatusPill";
import { AppNavRail } from "@/presentation/organisms/AppNavRail";
import { logout } from "@/lib/api/auth";

const STARTER_CODE = `public class CircularQueue {
    private final int[] buffer;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    public CircularQueue(int k) {
        this.capacity = k;
        this.buffer = new int[k];
    }

    public boolean enqueue(int value) {
        if (isFull()) return false;
        buffer[tail] = value;
        tail = (tail + 1) % capacity;
        count++;
        return true;
    }

    public boolean dequeue() {
        if (isEmpty()) return false;
        head = (head + 1) % capacity;
        count--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : buffer[head];
    }

    public int Rear() {
        if (isEmpty()) return -1;
        int prev = (tail - 1 + capacity) % capacity;
        return buffer[prev];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == capacity;
    }
}`;

export const CodingCockpitView: React.FC = () => {
  const [code, setCode] = useState(STARTER_CODE);
  const [isExpanded, setIsExpanded] = useState(false);
  const [isResetConfirmOpen, setIsResetConfirmOpen] = useState(false);
  const [isSocraticOpen, setIsSocraticOpen] = useState(false);
  const [isPeerDrawerOpen, setIsPeerDrawerOpen] = useState(false);
  const [activeTestCase, setActiveTestCase] = useState<1 | 2 | 3>(3);
  const [selectedLanguage, setSelectedLanguage] = useState<"EN" | "HINGLISH">("EN");
  const [unlockedTier3, setUnlockedTier3] = useState(false);
  const [isRunning, setIsRunning] = useState(false);
  const [executionState, setExecutionState] = useState<"PASSED" | "FAILED" | "TIMED_OUT">("FAILED");
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  const handleReset = () => {
    setCode(STARTER_CODE);
    setIsResetConfirmOpen(false);
  };

  const handleRunTests = () => {
    setIsRunning(true);
    setTimeout(() => {
      setIsRunning(false);
      setExecutionState("PASSED");
    }, 600);
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // allow graceful redirect
    }
    window.location.href = "/login";
  };

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 font-sans flex flex-col justify-between overflow-x-hidden">
      {/* ========================================================================= */}
      {/* TOP NAVIGATION COCKPIT STRIP (Wireframe 03)                                */}
      {/* ========================================================================= */}
      <header className="h-14 bg-white border-b border-slate-200 px-4 sm:px-5 flex items-center justify-between sticky top-0 z-40 shadow-xs">
        <div className="flex items-center gap-2.5 sm:gap-3">
          <Link href="/" className="flex items-center gap-2 group mr-1" title="CodeConnect Home">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-extrabold text-xs shadow-xs group-hover:scale-105 transition-transform">
              CC
            </div>
          </Link>

          <div className="h-5 w-[1px] bg-slate-200 hidden sm:block" />

          <Link href="/curriculum" className="text-xs font-semibold text-slate-500 hover:text-blue-600 transition-colors">
            Track 2: Data Structures
          </Link>
          <span className="text-slate-300 font-light text-xs">/</span>

          <span className="text-xs font-semibold text-blue-600 truncate max-w-[200px] sm:max-w-none">
            Foothold 2.3 (Circular Queue)
          </span>
          <StatusPill status="SOLVING" size="sm" />
        </div>

        {/* Right Drawer Launchers (AI Coach & Peer Solvers) & User Menu */}
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => {
              setIsSocraticOpen(!isSocraticOpen);
              setIsPeerDrawerOpen(false);
            }}
            className={`h-8 px-3 rounded-lg border text-xs font-semibold inline-flex items-center gap-1.5 transition-all ${
              isSocraticOpen
                ? "bg-blue-50 border-blue-300 text-blue-700"
                : "bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100"
            }`}
          >
            <VectorIcon name="lightbulb" size={13} className="text-blue-600" />
            <span className="hidden sm:inline">Socratic Coach (Clues)</span>
          </button>

          <button
            type="button"
            onClick={() => {
              setIsPeerDrawerOpen(!isPeerDrawerOpen);
              setIsSocraticOpen(false);
            }}
            className={`h-8 px-3 rounded-lg border text-xs font-semibold inline-flex items-center gap-1.5 transition-all ${
              isPeerDrawerOpen
                ? "bg-emerald-50 border-emerald-300 text-emerald-800"
                : "bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100"
            }`}
          >
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="hidden sm:inline">15 Classmates Online</span>
          </button>

          {/* User Profile & Logout Dropdown */}
          <div className="relative pl-2 border-l border-slate-200">
            <button
              type="button"
              onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
              className="flex items-center gap-1.5 focus:outline-hidden hover:opacity-90 transition-opacity"
              title="User Account Menu"
              id="cockpit-user-menu-btn"
            >
              <div className="w-7 h-7 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-xs ring-1 ring-blue-200">
                AV
              </div>
              <VectorIcon name="chevron-down" size={12} className="text-slate-400" />
            </button>

            {isUserMenuOpen && (
              <div className="absolute right-0 mt-2 w-56 rounded-xl bg-white border border-slate-200 shadow-xl py-2 z-50 animate-fadeIn text-xs">
                <div className="px-4 py-2 border-b border-slate-100">
                  <div className="font-bold text-slate-900">Alex Vashishtha</div>
                  <div className="text-[10px] text-slate-500 font-mono">alex@codeconnect.dev</div>
                </div>
                <div className="py-1 text-slate-700 font-medium">
                  <Link href="/" className="block px-4 py-1.5 hover:bg-slate-50">
                    Mountain Tracks Overview
                  </Link>
                  <Link href="/dashboard" className="block px-4 py-1.5 hover:bg-slate-50">
                    Mountain Ascent Dashboard
                  </Link>
                  <Link href="/curriculum" className="block px-4 py-1.5 hover:bg-slate-50">
                    Curriculum Story Reader
                  </Link>
                </div>
                <div className="pt-1 mt-1 border-t border-slate-100 px-2">
                  <button
                    type="button"
                    onClick={handleLogout}
                    className="w-full flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold text-red-600 hover:bg-red-50 text-left"
                    id="cockpit-logout-btn"
                  >
                    <VectorIcon name="log-out" size={13} />
                    <span>Log Out</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* ========================================================================= */}
      {/* BODY WITH COMMON LEFT RAIL + 50/50 SPLIT WORKBENCH CONTAINER (Wireframe 03) */}
      {/* ========================================================================= */}
      <div className="flex-1 flex overflow-hidden">
        <AppNavRail />

        <main className="flex-1 p-4 grid grid-cols-1 lg:grid-cols-12 gap-4 items-stretch overflow-y-auto relative">
        {/* LEFT PANE: Problem Specification & Constraints (5 cols / or collapsed) */}
        {!isExpanded && (
          <section className="lg:col-span-5 bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">
            <div className="p-3.5 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-xs font-bold text-slate-900 tracking-tight">
                Problem Specification &amp; Story Constraints
              </h2>
              <button
                type="button"
                onClick={() => setIsExpanded(true)}
                className="text-[11px] font-semibold text-slate-500 hover:text-slate-800 inline-flex items-center gap-1"
                title="Expand Editor (Cmd+B)"
              >
                <span>Expand</span>
                <VectorIcon name="expand" size={11} />
              </button>
            </div>

            <div className="p-5 overflow-y-auto space-y-4 text-xs leading-relaxed text-slate-700 flex-1">
              <div>
                <h3 className="text-sm font-bold text-slate-900 mb-1">
                  Design Circular Queue (FIFO with Wrap-Around)
                </h3>
                <p className="text-slate-600">
                  Implement a circular buffer adhering strictly to FIFO semantics.
                  The circular queue connects the end to the beginning to prevent wasted slots.
                </p>
              </div>

              <div className="p-3 bg-blue-50/60 rounded-lg border border-blue-200 text-blue-950 space-y-1">
                <span className="font-bold block text-[11px] text-blue-900">Mechanical Model:</span>
                <p className="text-[11px] text-blue-800 leading-relaxed">
                  Baggage carousel conveyor: indices wrap via <code className="font-mono font-bold">(tail + 1) % capacity</code>.
                </p>
              </div>

              <div className="space-y-2">
                <span className="font-bold text-slate-900 block">Method Contract:</span>
                <ul className="space-y-1 font-mono text-[11px] text-slate-700 bg-slate-50 p-2.5 rounded-lg border border-slate-200">
                  <li>&bull; <strong className="text-blue-700">boolean enqueue(int value)</strong>: Inserts item into buffer.</li>
                  <li>&bull; <strong className="text-blue-700">boolean dequeue()</strong>: Deletes front element.</li>
                  <li>&bull; <strong className="text-blue-700">int Front()</strong>: Gets front item (-1 if empty).</li>
                  <li>&bull; <strong className="text-blue-700">int Rear()</strong>: Gets rear item (-1 if empty).</li>
                  <li>&bull; <strong className="text-blue-700">boolean isEmpty()</strong>: Checks if buffer is empty.</li>
                  <li>&bull; <strong className="text-blue-700">boolean isFull()</strong>: Checks if buffer is full.</li>
                </ul>
              </div>

              <div className="space-y-1 text-slate-500 text-[11px]">
                <span className="font-bold text-slate-700 block">Constraints:</span>
                <p>&bull; 1 &le; k &le; 1000 (capacity)</p>
                <p>&bull; 0 &le; value &le; 1000</p>
                <p>&bull; At most 3000 calls will be made to enqueue / dequeue.</p>
              </div>
            </div>
          </section>
        )}

        {/* RIGHT PANE: Monaco Code Editor + Test Console Tray (7 cols or 12 cols if expanded) */}
        <section
          className={`${
            isExpanded ? "lg:col-span-12" : "lg:col-span-7"
          } bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden`}
        >
          {/* Editor Header Bar */}
          <div className="h-10 bg-[#1e293b] border-b border-slate-700 px-4 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="px-2 py-0.5 rounded bg-slate-800 text-slate-200 font-mono text-[11px] font-medium border border-slate-700">
                CircularQueue.java
              </span>
              <span className="text-[10px] font-semibold text-slate-400">Java 21 LTS</span>
            </div>

            <div className="flex items-center gap-2">
              {/* Reset Button with Confirmation Popover (Edge Case 2) */}
              <div className="relative">
                <button
                  type="button"
                  onClick={() => setIsResetConfirmOpen(!isResetConfirmOpen)}
                  className="px-2 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-[10px] font-medium transition-colors"
                >
                  Reset
                </button>

                {isResetConfirmOpen && (
                  <div className="absolute right-0 top-8 w-56 p-3 bg-white border border-slate-200 rounded-xl shadow-xl z-50 text-slate-800 text-xs space-y-2">
                    <p className="font-bold text-slate-900">Reset Starter Code?</p>
                    <p className="text-[11px] text-slate-500 leading-tight">
                      Your current edits will be cleared and replaced with the original template.
                    </p>
                    <div className="flex items-center justify-end gap-1.5 pt-1">
                      <button
                        type="button"
                        onClick={() => setIsResetConfirmOpen(false)}
                        className="px-2 py-1 rounded bg-slate-100 text-slate-600 text-[10px] font-semibold hover:bg-slate-200"
                      >
                        Cancel
                      </button>
                      <button
                        type="button"
                        onClick={handleReset}
                        className="px-2 py-1 rounded bg-rose-600 text-white text-[10px] font-bold hover:bg-rose-700"
                      >
                        Confirm Reset
                      </button>
                    </div>
                  </div>
                )}
              </div>

              {/* Expand Toggle */}
              <button
                type="button"
                onClick={() => setIsExpanded(!isExpanded)}
                className="px-2 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-[10px] font-medium transition-colors"
              >
                {isExpanded ? "Split 50/50" : "Expand (⤢)"}
              </button>
            </div>
          </div>

          {/* Code Editor Body */}
          <div className="flex-1 bg-[#0f172a] text-slate-100 font-mono text-xs p-3 overflow-auto min-h-[300px]">
            <textarea
              value={code}
              onChange={(e) => setCode(e.target.value)}
              spellCheck={false}
              className="w-full h-full min-h-[300px] bg-transparent text-emerald-400 font-mono text-xs leading-relaxed focus:outline-none resize-none selection:bg-blue-600 selection:text-white"
            />
          </div>

          {/* Action Bar */}
          <div className="h-11 bg-slate-100 border-t border-slate-200 px-4 flex items-center justify-between">
            <span className="text-[11px] text-slate-500 font-mono">
              Press <strong className="text-slate-800">Ctrl+Enter</strong> to run
            </span>

            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={handleRunTests}
                disabled={isRunning}
                className="h-8 px-3 rounded-lg border border-slate-300 bg-white hover:bg-slate-50 text-slate-700 text-xs font-semibold inline-flex items-center gap-1.5 transition-colors shadow-xs"
              >
                <VectorIcon name="play" size={10} className="text-slate-600" />
                <span>Run Visible Tests</span>
              </button>

              <button
                type="button"
                onClick={handleRunTests}
                disabled={isRunning}
                className="h-8 px-4 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold inline-flex items-center gap-1.5 shadow-sm transition-all"
              >
                <span>Submit Solution</span>
                <VectorIcon name="arrow-right" size={12} className="text-white" />
              </button>
            </div>
          </div>

          {/* ========================================================================= */}
          {/* TEST EVALUATION CONSOLE TRAY (Wireframe 03)                                */}
          {/* ========================================================================= */}
          <div className="border-t border-slate-200 bg-slate-50/80 p-3 space-y-2.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <span className="text-xs font-bold text-slate-900">Test Sandbox Output:</span>
                {executionState === "PASSED" ? (
                  <StatusPill status="PASS" label="3 of 3 Passed (100%)" size="sm" />
                ) : executionState === "TIMED_OUT" ? (
                  <StatusPill status="TIMED_OUT" label="Execution Ceiling (3.0s)" size="sm" />
                ) : (
                  <StatusPill status="FAIL" label="2 of 3 Passed (66%)" size="sm" />
                )}
                <span className="text-[10px] text-slate-400 font-mono">
                  Duration: 18ms &bull; RAM: 24MB
                </span>
              </div>

              {/* Test Case Selectors */}
              <div className="flex items-center gap-1.5">
                <button
                  type="button"
                  onClick={() => setActiveTestCase(1)}
                  className={`px-2 py-0.5 rounded text-[10px] font-bold border transition-colors ${
                    activeTestCase === 1
                      ? "bg-emerald-100 border-emerald-300 text-emerald-800"
                      : "bg-white border-slate-200 text-slate-600"
                  }`}
                >
                  Case 1 [PASS]
                </button>
                <button
                  type="button"
                  onClick={() => setActiveTestCase(2)}
                  className={`px-2 py-0.5 rounded text-[10px] font-bold border transition-colors ${
                    activeTestCase === 2
                      ? "bg-emerald-100 border-emerald-300 text-emerald-800"
                      : "bg-white border-slate-200 text-slate-600"
                  }`}
                >
                  Case 2 [PASS]
                </button>
                <button
                  type="button"
                  onClick={() => setActiveTestCase(3)}
                  className={`px-2 py-0.5 rounded text-[10px] font-bold border transition-colors ${
                    activeTestCase === 3
                      ? "bg-rose-100 border-rose-300 text-rose-800"
                      : "bg-white border-slate-200 text-slate-600"
                  }`}
                >
                  Case 3 [FAIL]
                </button>
              </div>
            </div>

            {/* Test Diff Diagnostic Card */}
            <div className="p-3 bg-white rounded-lg border border-slate-200 text-xs font-mono space-y-1.5">
              <div className="flex items-center justify-between text-[11px] text-slate-500">
                <span>INPUT: [&quot;CircularQueue(5)&quot;, &quot;enqueue(10)&quot;, &quot;enqueue(60)&quot;]</span>
                <span className="text-emerald-700 font-bold">EXPECTED: true (wraps to index 0)</span>
              </div>
              <div className="text-[11px] text-rose-600 font-semibold">
                ACTUAL: java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds
              </div>

              {/* Socratic Recommendation Bar */}
              <div className="pt-1.5 flex items-center justify-between bg-blue-50/70 p-2 rounded border border-blue-200/80 text-[11px] text-blue-900">
                <span className="flex items-center gap-1.5">
                  <VectorIcon name="lightbulb" size={13} className="text-blue-600" />
                  <span>Socratic Clue: Remember modulo operator (%) from baggage carousel analogy!</span>
                </span>
                <button
                  type="button"
                  onClick={() => setIsSocraticOpen(true)}
                  className="px-2 py-0.5 bg-blue-600 text-white rounded text-[10px] font-bold hover:bg-blue-700"
                >
                  Ask AI Clue &rarr;
                </button>
              </div>
            </div>
          </div>
        </section>

        {/* ========================================================================= */}
        {/* SLIDE-OUT SOCRATIC AI DRAWER (Wireframe 04)                                */}
        {/* ========================================================================= */}
        {isSocraticOpen && (
          <aside className="fixed right-0 top-13 bottom-0 w-full sm:w-[430px] bg-white border-l border-slate-200 shadow-2xl z-50 flex flex-col justify-between p-5 animate-in slide-in-from-right duration-200">
            <div className="space-y-4">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                <div className="flex items-center gap-2">
                  <VectorIcon name="lightbulb" size={16} className="text-blue-600" />
                  <h3 className="text-xs font-bold text-slate-900">Socratic AI Coach</h3>
                </div>

                <div className="flex items-center gap-2">
                  {/* Hinglish Toggle */}
                  <button
                    type="button"
                    onClick={() =>
                      setSelectedLanguage(selectedLanguage === "EN" ? "HINGLISH" : "EN")
                    }
                    className="px-2 py-0.5 rounded-full border border-slate-200 text-[10px] font-bold bg-slate-50 text-slate-700"
                  >
                    {selectedLanguage === "HINGLISH" ? "Hinglish Mode" : "English Mode"}
                  </button>

                  <button
                    type="button"
                    onClick={() => setIsSocraticOpen(false)}
                    className="p-1 text-slate-400 hover:text-slate-700"
                  >
                    <VectorIcon name="error-x" size={14} />
                  </button>
                </div>
              </div>

              {/* Anti-Spoiler Guardrail Banner */}
              <div className="p-2.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-900 text-[11px] flex items-center gap-2">
                <VectorIcon name="shield" size={13} className="text-emerald-700 shrink-0" />
                <span>Strict Socratic Mode: Guiding with mental models &amp; clues.</span>
              </div>

              {/* 3-Tier Progressive Hint Ladder */}
              <div className="space-y-2 pt-1">
                <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
                  Progressive Hint Tiers (3 Available)
                </span>

                {/* Tier 1 */}
                <div className="p-3 rounded-lg border border-slate-200 bg-slate-50 space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-emerald-700">Tier 1 Hint</span>
                    <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-slate-200 text-slate-600">
                      VIEWED
                    </span>
                  </div>
                  <p className="text-[11px] text-slate-600">
                    Baggage Carousel Analogy: Think about what happens when your index passes capacity.
                  </p>
                </div>

                {/* Tier 2 */}
                <div className="p-3 rounded-lg border border-amber-300 bg-amber-50/50 space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-amber-800">Tier 2 Hint</span>
                    <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-amber-200 text-amber-900">
                      UNLOCKED
                    </span>
                  </div>
                  <p className="text-[11px] text-amber-900 font-mono">
                    Use (tail + 1) % capacity before assigning value.
                  </p>
                </div>

                {/* Tier 3: Guarded behind unlock confirmation */}
                <div className="p-3 rounded-lg border border-slate-200 bg-slate-50/60 space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-slate-600">Tier 3: Structural Model</span>
                    {!unlockedTier3 && (
                      <button
                        type="button"
                        onClick={() => setUnlockedTier3(true)}
                        className="px-2 py-0.5 rounded bg-blue-600 text-white text-[10px] font-bold hover:bg-blue-700"
                      >
                        Unlock Hint 3
                      </button>
                    )}
                  </div>
                  {unlockedTier3 ? (
                    <p className="text-[11px] text-slate-700 font-mono leading-relaxed bg-white p-2 rounded border border-slate-200">
                      count++; if (count == capacity) isFull = true;
                    </p>
                  ) : (
                    <p className="text-[11px] text-slate-400">
                      Locked to prevent accidental code spoilers.
                    </p>
                  )}
                </div>
              </div>
            </div>

            {/* Quick Prompt Chips */}
            <div className="space-y-2 pt-4 border-t border-slate-100">
              <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
                Quick Prompts
              </span>
              <div className="flex flex-wrap gap-1.5">
                <button
                  type="button"
                  className="px-2.5 py-1 rounded-full border border-slate-200 bg-white hover:bg-slate-50 text-[11px] text-slate-700"
                >
                  Explain in Hinglish
                </button>
                <button
                  type="button"
                  className="px-2.5 py-1 rounded-full border border-slate-200 bg-white hover:bg-slate-50 text-[11px] text-slate-700"
                >
                  Why modulo 5 failed?
                </button>
              </div>
            </div>
          </aside>
        )}

        {/* ========================================================================= */}
        {/* SLIDE-OUT PEER SOLVERS NETWORK DRAWER (Wireframe 05)                       */}
        {/* ========================================================================= */}
        {isPeerDrawerOpen && (
          <aside className="fixed right-0 top-13 bottom-0 w-full sm:w-[430px] bg-white border-l border-slate-200 shadow-2xl z-50 flex flex-col justify-between p-5 animate-in slide-in-from-right duration-200">
            <div className="space-y-4">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                <div className="flex items-center gap-2">
                  <VectorIcon name="users" size={16} className="text-emerald-600" />
                  <h3 className="text-xs font-bold text-slate-900">Peer Solvers Network</h3>
                </div>

                <button
                  type="button"
                  onClick={() => setIsPeerDrawerOpen(false)}
                  className="p-1 text-slate-400 hover:text-slate-700"
                >
                  <VectorIcon name="error-x" size={14} />
                </button>
              </div>

              {/* Chat Thread with Rohan */}
              <div className="space-y-2 text-xs">
                <div className="p-2.5 rounded-lg bg-slate-50 text-slate-600 text-center text-[10px]">
                  Connected with Rohan Sharma for Foothold 2.3
                </div>

                <div className="p-3 rounded-lg bg-slate-100 text-slate-800 space-y-1">
                  <span className="font-bold block text-slate-900">Rohan:</span>
                  <p className="text-[11px]">
                    Hey Alex! Are you getting stuck with empty vs full condition when head and tail meet?
                  </p>
                </div>

                <div className="p-3 rounded-lg bg-blue-600 text-white ml-6 space-y-1">
                  <span className="font-bold block text-blue-100">You (Alex):</span>
                  <p className="text-[11px]">
                    Exactly! When tail == head, my code can&apos;t tell if buffer has 0 items or is totally full.
                  </p>
                </div>

                <div className="p-3 rounded-lg bg-slate-100 text-slate-800 space-y-1">
                  <span className="font-bold block text-slate-900">Rohan:</span>
                  <p className="text-[11px]">
                    Classic trap! Maintain an explicit <code className="font-mono font-bold">count</code> variable incremented on enqueue and decremented on dequeue!
                  </p>
                </div>
              </div>
            </div>

            {/* Request Mentor Assistance */}
            <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
              <span className="text-[11px] text-slate-500">Still stuck after peer chat?</span>
              <button
                type="button"
                onClick={() => alert("Assistance request sent to staff mentor queue. You will receive feedback directly in your cockpit.")}
                className="font-bold text-amber-700 hover:text-amber-800 hover:underline inline-flex items-center gap-1 cursor-pointer"
              >
                <span>Request Staff Mentor Help &rarr;</span>
              </button>
            </div>
          </aside>
        )}
      </main>
      </div>
    </div>
  );
};
