"use client";

import React, { useState } from "react";
import Link from "next/link";
import { AppShell } from "@/presentation/organisms/AppShell";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatusPill } from "@/presentation/atoms/StatusPill";

interface StudentTicket {
  readonly id: string;
  readonly studentName: string;
  readonly initials: string;
  readonly altitude: string;
  readonly track: string;
  readonly foothold: string;
  readonly topic: string;
  readonly aiHintsUsed: string;
  readonly peerChatAttempted: string;
  readonly waitingTime: string;
  readonly status: "TIER_3_ESCALATED" | "IN_REVIEW" | "RESOLVED";
  readonly studentNote: string;
  readonly claimedBy: string | null; // Edge Case 3: Concurrent Claim Protection
}

const INITIAL_TICKETS: StudentTicket[] = [
  {
    id: "104",
    studentName: "Alex Vashishtha",
    initials: "AL",
    altitude: "1,420m",
    track: "Track 2: Core Data Structures (Java 21)",
    foothold: "Foothold 2.3: Circular Queue Buffer",
    topic: "Circular Queue Wrap-around Bug",
    aiHintsUsed: "3/3",
    peerChatAttempted: "Tried Rohan S.",
    waitingTime: "14 mins",
    status: "TIER_3_ESCALATED",
    studentNote:
      "I passed Test 1 and 2, but Test 3 crashes with IndexOutOfBoundsException when wrap-around occurs.",
    claimedBy: "Dr. Priya Sen", // Currently claimed by active mentor
  },
  {
    id: "105",
    studentName: "Kavita Murthy",
    initials: "KM",
    altitude: "850m",
    track: "Track 1: Java 21 Models & Memory",
    foothold: "Foothold 1.5: Object References vs Primitive Values",
    topic: "Object References vs Primitive Values",
    aiHintsUsed: "3/3",
    peerChatAttempted: "None available",
    waitingTime: "28 mins",
    status: "IN_REVIEW",
    studentNote:
      "Mutating the object inside helper method changes the caller state, but reassigning the reference does not. Why?",
    claimedBy: null,
  },
  {
    id: "106",
    studentName: "Jason Lin",
    initials: "JL",
    altitude: "1,600m",
    track: "Track 2: Core Data Structures (Java 21)",
    foothold: "Foothold 2.4: Priority Queue Min-Heap",
    topic: "Min-Heap Array Index Math",
    aiHintsUsed: "2/3",
    peerChatAttempted: "None",
    waitingTime: "42 mins",
    status: "TIER_3_ESCALATED",
    studentNote:
      "Parent formula (i - 1) / 2 vs 2 * i + 1 child indexing causes ArrayIndexOutOfBounds when siftUp reaches root.",
    claimedBy: null,
  },
];

export const MentorDeskView: React.FC = () => {
  const [tickets, setTickets] = useState<StudentTicket[]>(INITIAL_TICKETS);
  const [selectedTicketId, setSelectedTicketId] = useState<string>("104");
  const [activeTab, setActiveTab] = useState<"ACTIVE" | "RESOLVED">("ACTIVE");
  const [feedbackText, setFeedbackText] = useState<string>(
    "Alex, inspect line 21: You write to elements[tail] and do tail = tail + 1.\nWhen tail reaches index 4 in a capacity 5 buffer, the next insertion tries to write to index 5!\nUse (tail + 1) % capacity so it loops back to slot 0 like the luggage carousel."
  );
  const [actionNotice, setActionNotice] = useState<string | null>(null);
  const [isVoiceHuddleActive, setIsVoiceHuddleActive] = useState(false);

  const selectedTicket =
    tickets.find((t) => t.id === selectedTicketId) || tickets[0];

  // John's Edge Case 3: Atomic claim lock handler
  const handleToggleClaim = (ticketId: string) => {
    setTickets((prev) =>
      prev.map((t) => {
        if (t.id === ticketId) {
          const isCurrentlyClaimed = t.claimedBy === "Dr. Priya Sen";
          const newClaimant = isCurrentlyClaimed ? null : "Dr. Priya Sen";
          const newStatus = isCurrentlyClaimed ? "TIER_3_ESCALATED" : "IN_REVIEW";
          return { ...t, claimedBy: newClaimant, status: newStatus };
        }
        return t;
      })
    );
    setActionNotice(
      selectedTicket?.claimedBy === "Dr. Priya Sen"
        ? "Ticket released back to pool."
        : "Ticket claimed exclusively. Lock active."
    );
    setTimeout(() => setActionNotice(null), 3500);
  };

  const handleDispatchClue = () => {
    setActionNotice(
      `Socratic clue dispatched directly to ${selectedTicket.studentName}'s Coding Cockpit!`
    );
    setTimeout(() => setActionNotice(null), 4000);
  };

  const handleMarkResolved = () => {
    setTickets((prev) =>
      prev.map((t) =>
        t.id === selectedTicket.id ? { ...t, status: "RESOLVED" } : t
      )
    );
    setActionNotice(
      `Ticket #${selectedTicket.id} marked as resolved. Foothold unlocked for ${selectedTicket.studentName}.`
    );
    setTimeout(() => setActionNotice(null), 4000);
  };

  const activeCount = tickets.filter((t) => t.status !== "RESOLVED").length;
  const resolvedCount = tickets.filter((t) => t.status === "RESOLVED").length;

  const filteredTickets = tickets.filter((t) =>
    activeTab === "ACTIVE"
      ? t.status !== "RESOLVED"
      : t.status === "RESOLVED"
  );

  return (
    <AppShell
      trackTitle="Staff Resolution Desk"
      trackHref="/mentor/desk"
      footholdTitle="Tier 3 Student Blockers"
      explicitRole="ROLE_MENTOR"
    >
      {/* Action Notification Alert Bar */}
      {actionNotice && (
        <div className="bg-blue-600 text-white px-6 py-2 text-xs font-medium flex items-center justify-between animate-fadeIn">
          <div className="flex items-center space-x-2">
            <VectorIcon name="check-circle" size={16} />
            <span>{actionNotice}</span>
          </div>
          <button
            onClick={() => setActionNotice(null)}
            className="text-white hover:text-blue-100 font-bold"
          >
            ✕
          </button>
        </div>
      )}

      {/* Main Workspace Grid */}
      <main className="flex-1 p-6 max-w-7xl mx-auto w-full grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* ========================================================================= */}
        {/* LEFT COLUMN: Escalation Ticket Queue (4 cols / 380px)                     */}
        {/* ========================================================================= */}
        <div className="lg:col-span-4 flex flex-col space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">
            {/* Queue Header & Filters */}
            <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                  Incoming Tickets
                </h2>
                <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-200 text-slate-700">
                  {filteredTickets.length}
                </span>
              </div>

              {/* Filter Tabs */}
              <div className="flex items-center bg-slate-200/80 p-0.5 rounded-lg text-[11px] font-semibold">
                <button
                  onClick={() => setActiveTab("ACTIVE")}
                  className={`px-2.5 py-1 rounded-md transition-all ${
                    activeTab === "ACTIVE"
                      ? "bg-amber-700 text-white shadow-xs"
                      : "text-slate-600 hover:text-slate-900"
                  }`}
                >
                  ACTIVE ({activeCount})
                </button>
                <button
                  onClick={() => setActiveTab("RESOLVED")}
                  className={`px-2.5 py-1 rounded-md transition-all ${
                    activeTab === "RESOLVED"
                      ? "bg-white text-slate-900 shadow-xs"
                      : "text-slate-600 hover:text-slate-900"
                  }`}
                >
                  RESOLVED ({resolvedCount})
                </button>
              </div>
            </div>

            {/* Ticket Cards List */}
            <div className="p-3 space-y-3 max-h-[500px] overflow-y-auto">
              {filteredTickets.length === 0 ? (
                <div className="p-8 text-center text-slate-400 text-xs">
                  No tickets in this view.
                </div>
              ) : (
                filteredTickets.map((ticket) => {
                  const isSelected = ticket.id === selectedTicketId;
                  const isClaimedByMe = ticket.claimedBy === "Dr. Priya Sen";

                  return (
                    <div
                      key={ticket.id}
                      onClick={() => setSelectedTicketId(ticket.id)}
                      className={`p-3.5 rounded-lg border cursor-pointer transition-all ${
                        isSelected
                          ? "bg-blue-50/60 border-blue-600 shadow-xs"
                          : "bg-white border-slate-200 hover:border-slate-300 hover:bg-slate-50/50"
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <div className="flex items-center space-x-2.5">
                          <div
                            className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold text-white ${
                              isSelected ? "bg-blue-600" : "bg-slate-500"
                            }`}
                          >
                            {ticket.initials}
                          </div>
                          <div>
                            <h3 className="text-xs font-bold text-slate-900 leading-none">
                              {ticket.studentName}
                            </h3>
                            <span className="text-[10px] font-semibold text-blue-600">
                              {ticket.altitude} • {ticket.foothold.split(":")[0]}
                            </span>
                          </div>
                        </div>

                        {/* Status Badge */}
                        <span
                          className={`text-[9px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                            ticket.status === "TIER_3_ESCALATED"
                              ? "bg-red-100 text-red-800 border border-red-200"
                              : ticket.status === "IN_REVIEW"
                              ? "bg-amber-100 text-amber-800 border border-amber-200"
                              : "bg-emerald-100 text-emerald-800 border border-emerald-200"
                          }`}
                        >
                          {ticket.status.replace(/_/g, " ")}
                        </span>
                      </div>

                      <div className="mt-2.5 pt-2 border-t border-slate-100 text-[11px] text-slate-700 font-medium">
                        Topic: {ticket.topic}
                      </div>

                      <div className="mt-1 flex items-center justify-between text-[10px] text-slate-500">
                        <span>AI Hints: {ticket.aiHintsUsed}</span>
                        <span>Waiting: {ticket.waitingTime}</span>
                      </div>

                      {/* Edge Case 3: Concurrent Claim Protection Indicator */}
                      <div className="mt-2 pt-1.5 flex items-center justify-between border-t border-slate-100 text-[10px]">
                        {ticket.claimedBy ? (
                          <div className="flex items-center space-x-1 text-amber-700 font-medium">
                            <VectorIcon name="lock" size={11} strokeWidth={1.8} />
                            <span>
                              {isClaimedByMe ? "Locked by you" : `Locked: ${ticket.claimedBy}`}
                            </span>
                          </div>
                        ) : (
                          <span className="text-slate-400 font-medium">
                            Unclaimed (Pool)
                          </span>
                        )}
                        <button
                          type="button"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleToggleClaim(ticket.id);
                          }}
                          className={`px-2 py-0.5 rounded text-[10px] font-semibold transition-colors ${
                            isClaimedByMe
                              ? "bg-amber-100 text-amber-900 hover:bg-amber-200"
                              : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                          }`}
                        >
                          {isClaimedByMe ? "Release Lock" : "Claim Lock"}
                        </button>
                      </div>
                    </div>
                  );
                })
              )}
            </div>

            {/* Mentor Workload Metrics */}
            <div className="p-4 bg-slate-50/80 border-t border-slate-200 text-xs">
              <div className="text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">
                Mentor Resolution Metrics
              </div>
              <div className="space-y-1.5 text-[11px] text-slate-600">
                <div className="flex justify-between">
                  <span>Avg Resolution Time:</span>
                  <span className="font-bold text-emerald-600">8.4 mins</span>
                </div>
                <div className="flex justify-between">
                  <span>Total Footholds Verified:</span>
                  <span className="font-bold text-blue-600">128</span>
                </div>
                <div className="flex justify-between">
                  <span>Climber Satisfaction:</span>
                  <span className="font-bold text-amber-700">98.5% Verified</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* ========================================================================= */}
        {/* RIGHT COLUMN: Deep Code Diff Inspection & Resolution Center (8 cols)     */}
        {/* ========================================================================= */}
        <div className="lg:col-span-8 flex flex-col space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">
            {/* Ticket Header & Concurrency Status */}
            <div className="p-4 bg-slate-50 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
              <div>
                <div className="flex items-center space-x-2">
                  <h2 className="text-sm font-bold text-slate-900">
                    Ticket #{selectedTicket.id}: {selectedTicket.studentName} —{" "}
                    {selectedTicket.topic}
                  </h2>
                </div>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  {selectedTicket.foothold} • {selectedTicket.track}
                </p>
              </div>

              {/* Concurrent Lock Status & Switch */}
              <div className="flex items-center space-x-2">
                {selectedTicket.claimedBy === "Dr. Priya Sen" ? (
                  <span className="flex items-center space-x-1.5 px-2.5 py-1 rounded bg-emerald-50 text-emerald-800 border border-emerald-200 text-xs font-semibold">
                    <VectorIcon name="lock" size={12} strokeWidth={1.8} />
                    <span>Locked by you</span>
                  </span>
                ) : (
                  <button
                    onClick={() => handleToggleClaim(selectedTicket.id)}
                    className="flex items-center space-x-1 px-3 py-1 rounded bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold transition-colors"
                  >
                    <VectorIcon name="lock" size={12} strokeWidth={1.8} />
                    <span>Claim Ticket Exclusively</span>
                  </button>
                )}
              </div>
            </div>

            <div className="p-5 space-y-5">
              {/* Student Reported Blocker Alert Callout */}
              <div className="p-3.5 rounded-lg bg-red-50/80 border border-red-200 flex items-start space-x-3">
                <div className="text-red-700 shrink-0 mt-0.5">
                  <VectorIcon name="alert-triangle" size={16} strokeWidth={2} />
                </div>
                <div>
                  <div className="text-xs font-bold text-red-900">
                    Student Reported Blocker:
                  </div>
                  <div className="text-xs text-red-800 mt-0.5 leading-relaxed font-mono">
                    &ldquo;{selectedTicket.studentNote}&rdquo;
                  </div>
                  <div className="mt-1.5 text-[10px] text-red-700/90 font-medium">
                    Pre-flight Audit: Tier 1 AI hints ({selectedTicket.aiHintsUsed}) used • Peer assistance exhausted.
                  </div>
                </div>
              </div>

              {/* Live Synchronized Code Diff Viewer */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-xs font-bold text-slate-900">
                    Code Diff: Student Working Copy vs Expected Invariants
                  </h3>
                  <span className="text-[10px] font-mono text-slate-400">
                    CircularQueue.java — Lines 19-24
                  </span>
                </div>

                <div className="rounded-lg bg-[#0f172a] text-slate-100 overflow-hidden font-mono text-xs border border-slate-800">
                  <div className="bg-[#1e293b] px-4 py-2 text-[10px] text-slate-400 flex items-center justify-between border-b border-slate-800">
                    <span>Student Code Snapshot</span>
                    <div className="flex items-center space-x-3 text-[10px]">
                      <span className="text-red-400 flex items-center space-x-1">
                        <span className="w-2 h-2 rounded-full bg-red-500 inline-block" />
                        <span>Line 21 Bug</span>
                      </span>
                      <span className="text-emerald-400 flex items-center space-x-1">
                        <span className="w-2 h-2 rounded-full bg-emerald-500 inline-block" />
                        <span>Expected Modulo Invariant</span>
                      </span>
                    </div>
                  </div>

                  <div className="p-4 space-y-1.5 leading-relaxed">
                    <div className="text-slate-400 flex space-x-3">
                      <span className="w-6 text-right select-none text-slate-600">19</span>
                      <span>public boolean enqueue(int value) &#123;</span>
                    </div>
                    <div className="text-slate-400 flex space-x-3">
                      <span className="w-6 text-right select-none text-slate-600">20</span>
                      <span>    if (isFull()) return false;</span>
                    </div>

                    {/* Red Diff Line (Bug) */}
                    <div className="bg-red-950/50 -mx-4 px-4 py-1 text-red-200 border-l-4 border-red-500 flex space-x-3">
                      <span className="w-6 text-right select-none text-red-400">21 -</span>
                      <span>
                        {"    "}elements[tail] = value; tail = tail + 1;{" "}
                        <span className="text-red-400 font-bold">// BUG: Missing modulo wrapping!</span>
                      </span>
                    </div>

                    {/* Green Diff Line (Mentor Invariant) */}
                    <div className="bg-emerald-950/50 -mx-4 px-4 py-1 text-emerald-200 border-l-4 border-emerald-500 flex space-x-3">
                      <span className="w-6 text-right select-none text-emerald-400">22 +</span>
                      <span>
                        {"    "}elements[tail] = value; tail = (tail + 1) % capacity; count++;
                      </span>
                    </div>

                    <div className="text-slate-400 flex space-x-3">
                      <span className="w-6 text-right select-none text-slate-600">23</span>
                      <span>    return true;</span>
                    </div>
                    <div className="text-slate-400 flex space-x-3">
                      <span className="w-6 text-right select-none text-slate-600">24</span>
                      <span>&#125;</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Mentor Socratic Feedback Dispatch Editor */}
              <div>
                <label className="block text-xs font-bold text-slate-900 mb-1.5">
                  Mentor Socratic Feedback &amp; Direct Architectural Clue
                </label>
                <textarea
                  rows={4}
                  value={feedbackText}
                  onChange={(e) => setFeedbackText(e.target.value)}
                  className="w-full text-xs font-sans text-slate-800 bg-white border border-slate-300 rounded-lg p-3 focus:outline-hidden focus:ring-2 focus:ring-blue-600 focus:border-transparent transition-all leading-relaxed"
                  placeholder="Write targeted conceptual guidance without giving away the direct copy-paste code..."
                />

                {/* Resolution Action Bar */}
                <div className="mt-4 pt-3 border-t border-slate-200 flex flex-wrap items-center justify-between gap-3">
                  <div className="flex items-center space-x-2">
                    {/* Action 1: Dispatch Clue */}
                    <button
                      type="button"
                      onClick={handleDispatchClue}
                      className="px-4 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold flex items-center space-x-1.5 shadow-sm transition-colors"
                    >
                      <span>Dispatch Clue to {selectedTicket.studentName.split(" ")[0]}</span>
                      <VectorIcon name="arrow-right" size={14} />
                    </button>

                    {/* Action 2: Voice Huddle */}
                    <button
                      type="button"
                      onClick={() => {
                        setIsVoiceHuddleActive(!isVoiceHuddleActive);
                        setActionNotice(
                          isVoiceHuddleActive
                            ? "Voice huddle session closed."
                            : `Voice huddle started with ${selectedTicket.studentName}. Mic open.`
                        );
                        setTimeout(() => setActionNotice(null), 3500);
                      }}
                      className={`px-3.5 py-2.5 rounded-lg border text-xs font-semibold flex items-center space-x-1.5 transition-colors ${
                        isVoiceHuddleActive
                          ? "bg-red-50 text-red-700 border-red-300"
                          : "bg-white hover:bg-slate-50 text-slate-700 border-slate-300"
                      }`}
                    >
                      <VectorIcon name="mic" size={14} />
                      <span>{isVoiceHuddleActive ? "End Huddle" : "5m Voice Huddle"}</span>
                    </button>
                  </div>

                  {/* Action 3: Mark Resolved & Verify Foothold */}
                  <button
                    type="button"
                    onClick={handleMarkResolved}
                    className="px-4 py-2.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold flex items-center space-x-1.5 shadow-sm transition-colors"
                  >
                    <VectorIcon name="checkmark" size={14} strokeWidth={2.2} />
                    <span>Mark Resolved &amp; Unlock Foothold</span>
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
