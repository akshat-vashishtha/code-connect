"use client";

import React from "react";
import Link from "next/link";
import { AppShell } from "@/presentation/organisms/AppShell";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatusPill } from "@/presentation/atoms/StatusPill";

/**
 * Mountain Curriculum Explorer & Reader View matching Wireframe 02-epic2-curriculum-reader.svg.
 * Replaces text walls with embedded physical vector mechanisms (airport baggage carousel).
 */
export const CurriculumReaderView: React.FC = () => {
  return (
    <AppShell
      trackTitle="Track 2: Data Structures"
      trackHref="/curriculum"
      footholdTitle="Foothold 2.3: Circular Queue"
    >
      <main className="max-w-[1152px] mx-auto w-full px-4 sm:px-6 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* ========================================================================= */}
          {/* LEFT COLUMN: Mountain Track & Foothold Ascent Tree (Width: 340px / 4 cols) */}
          {/* ========================================================================= */}
          <aside className="lg:col-span-4 bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-4 bg-slate-50/80 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-xs font-bold text-slate-900 tracking-tight">
                Ascent Route &amp; Tracks
              </h2>
              <span className="text-[10px] font-extrabold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
                TRACK 2/4
              </span>
            </div>

            <div className="p-3 space-y-2">
              {/* Track 1: Completed */}
              <div className="p-3 rounded-xl border border-slate-200 bg-slate-50/60 flex items-center gap-3">
                <div className="w-6 h-6 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0">
                  <VectorIcon name="checkmark" size={12} strokeWidth={2} />
                </div>
                <div>
                  <h3 className="text-xs font-semibold text-slate-900">
                    Track 1: Java 21 Mental Models
                  </h3>
                  <p className="text-[11px] text-slate-500">5/5 Footholds Conquered &bull; 500m</p>
                </div>
              </div>

              {/* Track 2: Active */}
              <div className="p-3 rounded-xl border-2 border-blue-600 bg-blue-50/30">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-bold text-blue-900">
                    Track 2: Data Structures
                  </span>
                  <StatusPill status="ACTIVE" size="sm" />
                </div>

                {/* Sub-Footholds List */}
                <div className="space-y-1.5 pl-2 border-l-2 border-blue-200 ml-1">
                  <div className="flex items-center justify-between text-xs py-1 text-slate-600">
                    <span className="flex items-center gap-1.5">
                      <VectorIcon name="checkmark" size={11} className="text-emerald-600" />
                      <span>2.1: Dynamic Arrays</span>
                    </span>
                    <span className="text-[10px] font-mono text-emerald-600 font-bold">100%</span>
                  </div>

                  <div className="flex items-center justify-between text-xs py-1 text-slate-600">
                    <span className="flex items-center gap-1.5">
                      <VectorIcon name="checkmark" size={11} className="text-emerald-600" />
                      <span>2.2: Singly Linked List</span>
                    </span>
                    <span className="text-[10px] font-mono text-emerald-600 font-bold">100%</span>
                  </div>

                  {/* Active Foothold */}
                  <div className="flex items-center justify-between text-xs py-1.5 px-2 bg-blue-100/70 text-blue-900 rounded-lg font-bold">
                    <span className="flex items-center gap-1.5">
                      <span className="w-1.5 h-1.5 rounded-full bg-blue-600" />
                      <span>2.3: Circular Queue</span>
                    </span>
                    <span className="text-[10px] font-mono text-blue-700 font-extrabold">+150m</span>
                  </div>

                  <div className="flex items-center justify-between text-xs py-1 text-slate-400">
                    <span className="flex items-center gap-1.5">
                      <VectorIcon name="lock" size={10} className="text-slate-400" />
                      <span>2.4: Doubly Linked Deque</span>
                    </span>
                    <span className="text-[10px] font-mono">Locked</span>
                  </div>
                </div>
              </div>

              {/* Track 3: Locked */}
              <div className="p-3 rounded-xl border border-slate-200/70 bg-slate-50/30 flex items-center gap-3 opacity-60">
                <div className="w-6 h-6 rounded-full bg-slate-200 text-slate-400 flex items-center justify-center shrink-0">
                  <VectorIcon name="lock" size={11} strokeWidth={2} />
                </div>
                <div>
                  <h3 className="text-xs font-semibold text-slate-600">
                    Track 3: High-Performance Trees &amp; Heaps
                  </h3>
                  <p className="text-[11px] text-slate-400">Locked &bull; Reach Altitude 2,200m</p>
                </div>
              </div>
            </div>
          </aside>

          {/* ========================================================================= */}
          {/* RIGHT COLUMN: Conceptual Story & Foothold Challenge Specification (8 cols) */}
          {/* ========================================================================= */}
          <section className="lg:col-span-8 space-y-5">
            {/* Foothold Header Card */}
            <div className="p-6 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-4">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-extrabold bg-blue-50 text-blue-700 border border-blue-200">
                    FOOTHOLD 2.3
                  </span>
                  <StatusPill status="ACTIVE" size="sm" />
                </div>

                <div className="flex items-center gap-4 text-xs font-semibold text-slate-500">
                  <span className="flex items-center gap-1.5">
                    <VectorIcon name="mountain" size={13} className="text-blue-600" />
                    <span>Altitude Gain: +150m</span>
                  </span>
                  <span className="flex items-center gap-1.5">
                    <VectorIcon name="clock" size={13} className="text-slate-400" />
                    <span>Est. Time: 25 mins</span>
                  </span>
                </div>
              </div>

              <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
                Design Circular Queue (FIFO with Wrap-Around)
              </h1>

              {/* Paragraph 1: Real-World Systems Analogy */}
              <div className="space-y-3 text-xs sm:text-sm text-slate-700 leading-relaxed">
                <p>
                  Imagine the terminal baggage conveyor carousel at an international airport.
                  Bags arrive from the aircraft cargo loader and are placed onto consecutive conveyor
                  slots. Passengers take their luggage from the front, vacating those slots.
                </p>
                <p>
                  If we represented this carousel as a standard linear array with fixed start and end boundaries,
                  traditional implementations falsely claim the buffer is full—even if the front slots have already been vacated!
                </p>
              </div>

              {/* Embedded Physical Mechanism Diagram Card (Wireframe 02) */}
              <div className="p-5 rounded-xl bg-slate-50 border border-slate-200/90 space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="text-xs font-bold text-slate-900">
                    Visual Model: Circular Wrap-Around Indexing
                  </h3>
                  <span className="text-[11px] font-mono text-slate-500 font-semibold">
                    Formula: (rear + 1) % Capacity
                  </span>
                </div>

                {/* Conveyor Array Cells SVG */}
                <div className="overflow-x-auto py-2">
                  <div className="flex items-center justify-center gap-2 min-w-[500px]">
                    {/* Slot 0 (Empty) */}
                    <div className="w-16 h-14 rounded-lg bg-slate-200/80 border border-slate-300 flex flex-col items-center justify-center text-slate-500 font-mono text-xs">
                      <span className="text-[10px] text-slate-400">Idx 0</span>
                      <span className="font-semibold text-slate-600">Empty</span>
                    </div>

                    {/* Slot 1 (FRONT) */}
                    <div className="w-16 h-14 rounded-lg bg-blue-100 border-2 border-blue-500 flex flex-col items-center justify-center text-blue-900 font-mono text-xs shadow-xs">
                      <span className="text-[10px] text-blue-600 font-bold">FRONT (1)</span>
                      <span className="font-bold">Bag #41</span>
                    </div>

                    {/* Slot 2 */}
                    <div className="w-16 h-14 rounded-lg bg-blue-50 border border-blue-300 flex flex-col items-center justify-center text-blue-900 font-mono text-xs">
                      <span className="text-[10px] text-slate-400">Idx 2</span>
                      <span className="font-semibold">Bag #42</span>
                    </div>

                    {/* Slot 3 */}
                    <div className="w-16 h-14 rounded-lg bg-blue-50 border border-blue-300 flex flex-col items-center justify-center text-blue-900 font-mono text-xs">
                      <span className="text-[10px] text-slate-400">Idx 3</span>
                      <span className="font-semibold">Bag #43</span>
                    </div>

                    {/* Slot 4 (REAR) */}
                    <div className="w-16 h-14 rounded-lg bg-amber-100 border-2 border-amber-500 flex flex-col items-center justify-center text-amber-950 font-mono text-xs shadow-xs">
                      <span className="text-[10px] text-amber-700 font-bold">REAR (4)</span>
                      <span className="font-bold">Bag #44</span>
                    </div>
                  </div>
                </div>

                {/* Key Mental Model Banner */}
                <div className="p-3 bg-white rounded-lg border border-slate-200 flex items-center gap-2 text-xs">
                  <span className="font-bold text-slate-900 shrink-0">Key Mental Model:</span>
                  <span className="text-slate-600">
                    Connects end-to-beginning, converting an O(N) shift into an O(1) modulo increment.
                  </span>
                </div>
              </div>

              {/* Foothold Challenge Specifications */}
              <div className="space-y-2 pt-2">
                <h3 className="text-sm font-bold text-slate-900">Your Foothold Challenge</h3>
                <p className="text-xs text-slate-600 leading-relaxed">
                  Implement a fixed-capacity circular queue in Java 21 supporting{" "}
                  <code className="px-1.5 py-0.5 rounded bg-slate-100 font-mono font-semibold text-slate-800">
                    enqueue()
                  </code>
                  ,{" "}
                  <code className="px-1.5 py-0.5 rounded bg-slate-100 font-mono font-semibold text-slate-800">
                    dequeue()
                  </code>
                  ,{" "}
                  <code className="px-1.5 py-0.5 rounded bg-slate-100 font-mono font-semibold text-slate-800">
                    peek()
                  </code>
                  , and{" "}
                  <code className="px-1.5 py-0.5 rounded bg-slate-100 font-mono font-semibold text-slate-800">
                    isFull()
                  </code>
                  . Ensure your modulo math wraps around cleanly without throwing an{" "}
                  <code className="px-1.5 py-0.5 rounded bg-rose-50 text-rose-700 font-mono font-semibold border border-rose-200">
                    ArrayIndexOutOfBoundsException
                  </code>
                  .
                </p>
              </div>

              {/* 3-Tier Safety Net Badges (Wireframe 02) */}
              <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 flex flex-wrap items-center justify-between gap-3">
                <span className="text-[10px] font-extrabold uppercase tracking-wider text-slate-500">
                  3-Tier Resolution Ecosystem Ready:
                </span>

                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200 text-xs font-semibold inline-flex items-center gap-1.5">
                    <VectorIcon name="lightbulb" size={12} className="text-blue-600" />
                    <span>Tier 1: Socratic AI</span>
                  </span>

                  <span className="px-2.5 py-1 rounded-full bg-indigo-50 text-indigo-700 border border-indigo-200 text-xs font-semibold inline-flex items-center gap-1.5">
                    <VectorIcon name="users" size={12} className="text-indigo-600" />
                    <span>Tier 2: 8 Solvers Online</span>
                  </span>

                  <span className="px-2.5 py-1 rounded-full bg-amber-50 text-amber-800 border border-amber-200 text-xs font-semibold inline-flex items-center gap-1.5">
                    <VectorIcon name="shield" size={12} className="text-amber-700" />
                    <span>Tier 3: Staff Mentor</span>
                  </span>
                </div>
              </div>

              {/* Action Bar */}
              <div className="pt-2 flex items-center justify-end">
                <Link
                  href="/cockpit/2.3"
                  className="h-11 px-6 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-bold inline-flex items-center gap-2 shadow-md shadow-blue-500/20 transition-all"
                >
                  <span>Step Into Code Cockpit (Java 21)</span>
                  <VectorIcon name="arrow-right" size={14} className="text-white" />
                </Link>
              </div>
            </div>
          </section>
        </div>
      </main>
    </AppShell>
  );
};
