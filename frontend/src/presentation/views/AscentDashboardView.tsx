"use client";

import React, { useState } from "react";
import Link from "next/link";
import { AppShell } from "@/presentation/organisms/AppShell";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

export const AscentDashboardView: React.FC = () => {
  const [selectedOption, setSelectedOption] = useState<string>("B");
  const [isAnswerVerified, setIsAnswerVerified] = useState<boolean>(false);
  const [verificationFeedback, setVerificationFeedback] = useState<string | null>(null);

  const handleVerifyRetention = () => {
    if (selectedOption === "B") {
      setIsAnswerVerified(true);
      setVerificationFeedback(
        "Checkpoint Verified! Retention contract satisfied. Track 3 (Concurrency & Virtual Threads) is now unlocked."
      );
    } else {
      setIsAnswerVerified(false);
      setVerificationFeedback(
        "Retention check failed. Re-read the conveyor belt mental model before attempting again."
      );
    }
  };

  return (
    <AppShell
      trackTitle="Mountain Ascent Dashboard"
      trackHref="/dashboard"
      footholdTitle="Topographic Progression & Ridgeline"
    >
      {/* Main Container */}
      <main className="flex-1 p-6 max-w-7xl mx-auto w-full space-y-6">
        {/* ========================================================================= */}
        {/* TOP ROW: KEY ASCENT METRICS CARDS (4 cards)                               */}
        {/* ========================================================================= */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Metric 1: Current Altitude */}
          <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-xs flex items-center space-x-4">
            <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center shrink-0">
              <VectorIcon name="mountain" size={24} strokeWidth={1.8} />
            </div>
            <div>
              <div className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                Current Altitude
              </div>
              <div className="text-xl font-extrabold text-slate-900 leading-tight">
                1,420m <span className="text-xs font-normal text-slate-500">/ 4,000m</span>
              </div>
              <div className="text-[11px] font-semibold text-blue-600">
                35.5% to the Summit
              </div>
            </div>
          </div>

          {/* Metric 2: Footholds Conquered */}
          <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-xs flex items-center space-x-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center shrink-0">
              <VectorIcon name="target" size={24} strokeWidth={1.8} />
            </div>
            <div>
              <div className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                Footholds Conquered
              </div>
              <div className="text-xl font-extrabold text-slate-900 leading-tight">
                9 <span className="text-xs font-normal text-slate-500">/ 20 Total</span>
              </div>
              <div className="text-[11px] font-semibold text-emerald-600">
                Track 2: 4/5 Conquered
              </div>
            </div>
          </div>

          {/* Metric 3: Active Streak */}
          <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-xs flex items-center space-x-4">
            <div className="w-12 h-12 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center shrink-0">
              <VectorIcon name="flame" size={24} strokeWidth={1.8} />
            </div>
            <div>
              <div className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                Ascent Streak
              </div>
              <div className="text-xl font-extrabold text-slate-900 leading-tight">
                5 Days
              </div>
              <div className="text-[11px] font-semibold text-amber-700">
                Top 10% Consistency
              </div>
            </div>
          </div>

          {/* Metric 4: Retention Index */}
          <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-xs flex items-center space-x-4">
            <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center shrink-0">
              <VectorIcon name="shield" size={24} strokeWidth={1.8} />
            </div>
            <div>
              <div className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                Load-Bearing Retention
              </div>
              <div className="text-xl font-extrabold text-slate-900 leading-tight">
                {isAnswerVerified ? "100%" : "Pending"}
              </div>
              <div className="text-[11px] font-semibold text-purple-700">
                Verified at Transitions
              </div>
            </div>
          </div>
        </div>

        {/* ========================================================================= */}
        {/* MAIN WORKSPACE: MAP (Left) + LOAD-BEARING CHECKPOINT (Right)              */}
        {/* ========================================================================= */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          {/* Topographic Ascent Elevation Map (7 cols) */}
          <div className="lg:col-span-7 bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">
            <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <div>
                <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                  Topographic Ascent Elevation Map
                </h2>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Follow the 4 load-bearing camps from basecamp to distributed summit
                </p>
              </div>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-blue-100 text-blue-800">
                LIVE TELEMETRY
              </span>
            </div>

            {/* Stylized SVG Ridgeline Progression Graphic */}
            <div className="p-6 flex-1 flex flex-col justify-center items-center">
              <div className="w-full max-w-xl aspect-[16/10] relative">
                <svg
                  viewBox="0 0 640 440"
                  className="w-full h-full drop-shadow-xs select-none"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  {/* Mountain Silhouette Background */}
                  <path
                    d="M 20 410 L 160 310 L 280 210 L 440 120 L 580 40 L 620 410 Z"
                    fill="#f8fafc"
                    stroke="#e2e8f0"
                    strokeWidth="2"
                  />

                  {/* Conquered Ridge Path (Emerald) */}
                  <path
                    d="M 50 390 L 160 300 L 290 200"
                    stroke="#059669"
                    strokeWidth="4"
                    strokeLinecap="round"
                  />
                  {/* Active Segment (Blue Pulse Dash) */}
                  <path
                    d="M 290 200 L 340 160"
                    stroke="#2563eb"
                    strokeWidth="4"
                    strokeDasharray="6 4"
                    strokeLinecap="round"
                  />
                  {/* Locked Future Segments (Slate Dash) */}
                  <path
                    d="M 340 160 L 440 110 L 570 40"
                    stroke="#cbd5e1"
                    strokeWidth="3"
                    strokeDasharray="4 4"
                    strokeLinecap="round"
                  />

                  {/* Waypoint 0: Base Camp (0m) */}
                  <g transform="translate(50, 390)">
                    <circle cx="0" cy="0" r="14" fill="#059669" />
                    <polyline
                      points="-4 0 -1 3 4 -3"
                      fill="none"
                      stroke="#ffffff"
                      strokeWidth="2"
                      strokeLinecap="round"
                    />
                    <rect
                      x="-55"
                      y="18"
                      width="110"
                      height="24"
                      rx="6"
                      fill="#f1f5f9"
                      stroke="#cbd5e1"
                      strokeWidth="1"
                    />
                    <text
                      x="0"
                      y="34"
                      fill="#334155"
                      fontSize="10"
                      fontWeight="600"
                      textAnchor="middle"
                    >
                      Base Camp (0m)
                    </text>
                  </g>

                  {/* Waypoint 1: Camp 1 (Java Models, 500m) - Conquered */}
                  <g transform="translate(160, 300)">
                    <circle cx="0" cy="0" r="16" fill="#059669" />
                    <polyline
                      points="-5 0 -2 3 5 -4"
                      fill="none"
                      stroke="#ffffff"
                      strokeWidth="2.2"
                      strokeLinecap="round"
                    />
                    <rect
                      x="-65"
                      y="-42"
                      width="130"
                      height="32"
                      rx="6"
                      fill="#ecfdf5"
                      stroke="#a7f3d0"
                      strokeWidth="1"
                    />
                    <text
                      x="0"
                      y="-28"
                      fill="#065f46"
                      fontSize="10.5"
                      fontWeight="700"
                      textAnchor="middle"
                    >
                      Camp 1: Java Models
                    </text>
                    <text
                      x="0"
                      y="-15"
                      fill="#059669"
                      fontSize="8.5"
                      textAnchor="middle"
                    >
                      500m • 5/5 Conquered
                    </text>
                  </g>

                  {/* Waypoint 2: Camp 2 (Data Structures, 1,500m) - ACTIVE POSITION */}
                  <g transform="translate(300, 190)">
                    {/* Pulsing ring */}
                    <circle
                      cx="0"
                      cy="0"
                      r="26"
                      fill="#eff6ff"
                      stroke="#bfdbfe"
                      strokeWidth="2"
                    />
                    <circle cx="0" cy="0" r="18" fill="#2563eb" />
                    <circle
                      cx="0"
                      cy="0"
                      r="6"
                      fill="none"
                      stroke="#ffffff"
                      strokeWidth="1.5"
                    />
                    <polygon points="2 -2 -1 2 1 2" fill="#ffffff" />

                    {/* Active Climber Badge */}
                    <rect
                      x="-80"
                      y="-52"
                      width="160"
                      height="40"
                      rx="8"
                      fill="#1e293b"
                    />
                    <text
                      x="0"
                      y="-36"
                      fill="#ffffff"
                      fontSize="10"
                      fontWeight="700"
                      textAnchor="middle"
                    >
                      YOU ARE HERE (1,420m)
                    </text>
                    <text
                      x="0"
                      y="-22"
                      fill="#93c5fd"
                      fontSize="9"
                      textAnchor="middle"
                    >
                      Foothold 2.3: Circular Queue
                    </text>
                  </g>

                  {/* Waypoint 3: High Camp 3 (Concurrency, 2,800m) - LOCKED */}
                  <g transform="translate(440, 110)">
                    <circle
                      cx="0"
                      cy="0"
                      r="16"
                      fill="#f1f5f9"
                      stroke="#94a3b8"
                      strokeWidth="2"
                    />
                    <rect
                      x="-4"
                      y="-2"
                      width="8"
                      height="6"
                      rx="1"
                      fill="#64748b"
                    />
                    <path
                      d="M -3 -2 V -5 C -3 -6.5 3 -6.5 3 -5 V -2"
                      fill="none"
                      stroke="#64748b"
                      strokeWidth="1.2"
                    />
                    <rect
                      x="-65"
                      y="-42"
                      width="130"
                      height="32"
                      rx="6"
                      fill="#ffffff"
                      stroke="#cbd5e1"
                      strokeWidth="1"
                    />
                    <text
                      x="0"
                      y="-28"
                      fill="#475569"
                      fontSize="10.5"
                      fontWeight="700"
                      textAnchor="middle"
                    >
                      Camp 3: Concurrency
                    </text>
                    <text
                      x="0"
                      y="-15"
                      fill="#94a3b8"
                      fontSize="8.5"
                      textAnchor="middle"
                    >
                      2,800m • Requires Checkpoint
                    </text>
                  </g>

                  {/* Waypoint 4: The Summit (4,000m) - LOCKED */}
                  <g transform="translate(570, 40)">
                    <circle
                      cx="0"
                      cy="0"
                      r="18"
                      fill="#f1f5f9"
                      stroke="#94a3b8"
                      strokeWidth="2"
                    />
                    <line
                      x1="-3"
                      y1="8"
                      x2="-3"
                      y2="-8"
                      stroke="#0f172a"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                    />
                    <polygon points="-3 -8 6 -4 -3 0" fill="#2563eb" />
                    <rect
                      x="-70"
                      y="16"
                      width="140"
                      height="32"
                      rx="6"
                      fill="#ffffff"
                      stroke="#cbd5e1"
                      strokeWidth="1"
                    />
                    <text
                      x="0"
                      y="30"
                      fill="#0f172a"
                      fontSize="10"
                      fontWeight="800"
                      textAnchor="middle"
                    >
                      THE SUMMIT (4,000m)
                    </text>
                    <text
                      x="0"
                      y="42"
                      fill="#64748b"
                      fontSize="8"
                      textAnchor="middle"
                    >
                      Distributed Microservices
                    </text>
                  </g>
                </svg>
              </div>
            </div>

            {/* Bottom Cockpit Action Banner */}
            <div className="p-4 bg-blue-50/70 border-t border-blue-100 flex flex-col sm:flex-row items-center justify-between gap-3">
              <div className="text-xs text-blue-900 font-medium">
                Next Objective: Master Foothold 2.4 (Priority Queue) to reach Camp 2.
              </div>
              <Link
                href="/cockpit/2.3"
                className="px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shrink-0 transition-colors shadow-xs flex items-center space-x-1.5"
              >
                <span>Resume Cockpit</span>
                <VectorIcon name="arrow-right" size={14} />
              </Link>
            </div>
          </div>

          {/* Load-Bearing Verification Modal / Card (5 cols) */}
          <div className="lg:col-span-5 bg-white rounded-xl border border-slate-200 shadow-md flex flex-col overflow-hidden">
            {/* Header */}
            <div className="p-4 bg-slate-900 text-white flex items-center space-x-3">
              <div className="w-8 h-8 rounded-lg bg-slate-800 text-sky-400 flex items-center justify-center shrink-0">
                <VectorIcon name="scale" size={18} strokeWidth={1.8} />
              </div>
              <div>
                <h3 className="text-xs font-bold uppercase tracking-wider text-white">
                  Load-Bearing Checkpoint
                </h3>
                <p className="text-[10px] font-bold text-sky-400 uppercase tracking-tight">
                  Prerequisite Verification • Track 2 to Track 3
                </p>
              </div>
            </div>

            <div className="p-5 flex-1 flex flex-col space-y-4">
              {/* Callout */}
              <div className="p-3 rounded-lg bg-amber-50 border border-amber-200 text-amber-900 text-xs">
                <div className="font-bold flex items-center space-x-1.5 text-amber-950">
                  <VectorIcon name="alert-triangle" size={14} />
                  <span>Foundational Verification Required:</span>
                </div>
                <p className="mt-1 text-[11px] text-amber-800 leading-relaxed">
                  Before ascending to Concurrency &amp; Virtual Threads, verify deep conceptual retention with this rapid challenge.
                </p>
              </div>

              {/* Challenge Question */}
              <div>
                <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">
                  Challenge 1/1: Memory &amp; Pipeline Safety
                </div>
                <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 text-xs font-semibold text-slate-800 leading-relaxed">
                  In a high-throughput Java payment service, why does an{" "}
                  <span className="text-blue-600 font-bold">unbounded queue</span>{" "}
                  risk sudden{" "}
                  <span className="text-red-600 font-bold">OutOfMemoryError</span>{" "}
                  under backpressure?
                </div>
              </div>

              {/* Options */}
              <div className="space-y-2">
                {[
                  {
                    id: "A",
                    text: "A. The garbage collector cannot collect active thread handles.",
                  },
                  {
                    id: "B",
                    text: "B. Producers outpace consumers, accumulating millions of heap references indefinitely.",
                  },
                  {
                    id: "C",
                    text: "C. Stack frames exceed the 1MB default thread memory limit.",
                  },
                ].map((opt) => {
                  const isSelected = selectedOption === opt.id;
                  return (
                    <div
                      key={opt.id}
                      onClick={() => setSelectedOption(opt.id)}
                      className={`p-3 rounded-lg border text-xs cursor-pointer transition-all flex items-start space-x-2.5 ${
                        isSelected
                          ? "bg-blue-50/80 border-blue-600 text-blue-950 font-semibold shadow-xs"
                          : "bg-white border-slate-200 hover:bg-slate-50 text-slate-700"
                      }`}
                    >
                      <div
                        className={`w-4 h-4 rounded-full border mt-0.5 flex items-center justify-center shrink-0 ${
                          isSelected
                            ? "border-blue-600 bg-blue-600"
                            : "border-slate-300 bg-white"
                        }`}
                      >
                        {isSelected && (
                          <div className="w-1.5 h-1.5 rounded-full bg-white" />
                        )}
                      </div>
                      <span className="leading-snug">{opt.text}</span>
                    </div>
                  );
                })}
              </div>

              {/* Story Connection Box */}
              <div className="p-3 rounded-lg bg-emerald-50/70 border border-emerald-200 text-emerald-900 text-[11px]">
                <div className="font-bold flex items-center space-x-1.5 text-emerald-950">
                  <VectorIcon name="checkmark" size={13} strokeWidth={2} />
                  <span>Story Connection:</span>
                </div>
                <p className="mt-0.5 text-emerald-800">
                  Remember the luggage conveyor belt: Without a bound, suitcases pile to the ceiling!
                </p>
              </div>

              {/* Verification Feedback Banner */}
              {verificationFeedback && (
                <div
                  className={`p-3 rounded-lg text-xs font-semibold ${
                    isAnswerVerified
                      ? "bg-emerald-100 text-emerald-900 border border-emerald-300"
                      : "bg-red-100 text-red-900 border border-red-300"
                  }`}
                >
                  {verificationFeedback}
                </div>
              )}

              {/* Action Button */}
              <button
                type="button"
                onClick={handleVerifyRetention}
                className="w-full py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-sm transition-colors flex items-center justify-center space-x-2"
              >
                <span>Verify Retention &amp; Unlock Track 3</span>
                <VectorIcon name="arrow-right" size={14} />
              </button>
            </div>
          </div>
        </div>
      </main>
    </AppShell>
  );
};
