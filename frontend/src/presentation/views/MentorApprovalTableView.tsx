import React from "react";
import Link from "next/link";
import {
  ShieldAlert,
  ShieldCheck,
  CheckCircle2,
  XCircle,
  ExternalLink,
  RefreshCw,
  Clock,
  Search,
  ArrowLeft,
  Award,
} from "lucide-react";
import { AdminMentorControllerResult } from "@/controller/useAdminMentorController";

export interface MentorApprovalTableViewProps {
  readonly controller: AdminMentorControllerResult;
  readonly detectorSlot?: React.ReactNode;
}

/**
 * Pure presentation view for the Admin Mentor Verification dashboard.
 * Renders applications queue and dispatches actions to AdminMentorController.
 */
export const MentorApprovalTableView: React.FC<MentorApprovalTableViewProps> = ({
  controller,
  detectorSlot,
}) => {
  const {
    currentUser,
    isAuthLoading,
    unauthorized,
    filteredApplications,
    isDataLoading,
    searchQuery,
    actionInProgress,
    feedbackMessage,
    setSearchQuery,
    loadPendingApplications,
    handleApprove,
    handleReject,
  } = controller;

  if (isAuthLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center p-6 bg-slate-50">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin" />
          <p className="text-sm font-medium text-slate-600">Verifying administrative credentials...</p>
        </div>
      </div>
    );
  }

  if (unauthorized) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center p-6 relative overflow-hidden font-sans">
        {/* Subtle Ambient Glow */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-red-600/10 rounded-full blur-3xl pointer-events-none" />

        <div className="max-w-md w-full bg-slate-900/90 backdrop-blur-xl rounded-2xl shadow-2xl p-8 border border-slate-800 text-center flex flex-col items-center gap-5 relative z-10">
          <div className="w-16 h-16 rounded-2xl bg-red-500/10 border border-red-500/20 text-red-400 flex items-center justify-center shadow-lg shadow-red-500/10">
            <ShieldAlert className="w-8 h-8" />
          </div>
          <div>
            <h2 className="text-2xl font-extrabold text-white tracking-tight">Access Restricted</h2>
            <p className="mt-2 text-xs sm:text-sm text-slate-400 leading-relaxed">
              The Mentor Verification Desk requires elevated{" "}
              <code className="px-1.5 py-0.5 rounded bg-slate-800 text-red-400 font-mono font-semibold border border-slate-700">
                ROLE_ADMIN
              </code>{" "}
              privilege.
              {currentUser
                ? ` Your current session role is ${currentUser.role}.`
                : " No active administrative session was found."}
            </p>
          </div>
          <div className="w-full flex flex-col gap-2.5 pt-2">
            <Link
              href="/login"
              className="w-full py-3 px-4 rounded-xl text-sm font-bold bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white shadow-lg shadow-blue-500/25 transition-all text-center hover:-translate-y-0.5"
            >
              Sign In with Admin Account &rarr;
            </Link>
            <Link
              href="/"
              className="w-full py-2.5 px-4 rounded-xl text-xs font-semibold text-slate-400 hover:text-slate-200 hover:bg-slate-800/80 border border-slate-800 transition-colors text-center"
            >
              Back to CodeConnect Home
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto flex flex-col gap-8">
        {/* Navigation & Header */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-slate-200">
          <div className="flex items-center gap-3">
            <Link
              href="/"
              className="p-2 rounded-xl bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-colors"
              title="Back to Platform Home"
            >
              <ArrowLeft className="w-4 h-4" />
            </Link>
            <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold text-lg shadow-md shadow-blue-500/20">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <h1 className="text-2xl font-bold tracking-tight text-slate-900">
                Mentor Verification Desk
              </h1>
              <p className="text-xs text-slate-500">
                Review and approve industry mentors &bull; Maintain platform educational standards
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-purple-50 border border-purple-200 text-purple-700 text-xs font-semibold">
              <Award className="w-3.5 h-3.5" />
              <span>Admin: {currentUser?.displayName || currentUser?.email}</span>
            </div>
            <button
              type="button"
              onClick={loadPendingApplications}
              disabled={isDataLoading}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-semibold bg-white border border-slate-200 hover:bg-slate-100 text-slate-700 shadow-sm transition-all disabled:opacity-50 cursor-pointer"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isDataLoading ? "animate-spin" : ""}`} />
              Refresh Queue
            </button>
          </div>
        </div>

        {/* Feedback Alert */}
        {feedbackMessage && (
          <div
            className={`p-4 rounded-xl text-sm flex items-start gap-3 border ${
              feedbackMessage.type === "success"
                ? "bg-emerald-50 border-emerald-300 text-emerald-900"
                : "bg-red-50 border-red-300 text-red-900"
            }`}
          >
            {feedbackMessage.type === "success" ? (
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
            ) : (
              <XCircle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
            )}
            <div className="flex-1 font-medium">{feedbackMessage.text}</div>
          </div>
        )}

        {/* Main Content Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column: Applications List (2 Cols on lg) */}
          <div className="lg:col-span-2 flex flex-col gap-6">
            {/* Search & Counter Bar */}
            <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row items-center justify-between gap-3">
              <div className="relative w-full sm:w-80">
                <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                <input
                  type="text"
                  placeholder="Filter by email or bio keywords..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                />
              </div>
              <div className="flex items-center gap-2 text-xs font-semibold text-slate-600 w-full sm:w-auto justify-end">
                <span className="px-2.5 py-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
                  {filteredApplications.length} Pending {filteredApplications.length === 1 ? "Review" : "Reviews"}
                </span>
              </div>
            </div>

            {/* List / Cards */}
            {isDataLoading && filteredApplications.length === 0 ? (
              <div className="p-12 text-center bg-white rounded-2xl border border-slate-200 shadow-sm">
                <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
                <p className="text-xs text-slate-500">Querying mentor_approval_requests collection...</p>
              </div>
            ) : filteredApplications.length === 0 ? (
              <div className="p-12 text-center bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center">
                  <CheckCircle2 className="w-6 h-6" />
                </div>
                <h3 className="text-base font-bold text-slate-800">Verification Queue Clear</h3>
                <p className="text-xs text-slate-500 max-w-sm">
                  {searchQuery
                    ? "No pending mentor applications match your search filter."
                    : "All pending mentor applications have been reviewed. Elevated mentors have immediate access to mentor routes."}
                </p>
              </div>
            ) : (
              <div className="flex flex-col gap-4">
                {filteredApplications.map((app) => {
                  const isOperating = actionInProgress === app.id;
                  const dateStr = app.submittedAt
                    ? new Date(app.submittedAt).toLocaleDateString("en-US", {
                        month: "short",
                        day: "numeric",
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    : "Recently";

                  return (
                    <div
                      key={app.id}
                      className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col gap-4 hover:border-slate-300 transition-colors"
                    >
                      {/* Top Header of Card */}
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-xl bg-indigo-50 border border-indigo-200 text-indigo-700 flex items-center justify-center font-bold text-sm">
                            {app.email.charAt(0).toUpperCase()}
                          </div>
                          <div>
                            <div className="flex items-center gap-2">
                              <h4 className="text-sm font-bold text-slate-900">{app.email}</h4>
                              <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-50 text-amber-700 border border-amber-200">
                                {app.status}
                              </span>
                            </div>
                            <div className="flex items-center gap-2 text-xs text-slate-500 mt-0.5">
                              <Clock className="w-3 h-3" />
                              <span>Submitted: {dateStr}</span>
                              <span>&bull;</span>
                              <span className="font-mono text-[11px] text-slate-400">
                                ID: {app.id.substring(0, 8)}...
                              </span>
                            </div>
                          </div>
                        </div>

                        {/* LinkedIn Link */}
                        {app.linkedInUrl && (
                          <a
                            href={app.linkedInUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-blue-600 hover:text-blue-700 bg-blue-50/70 hover:bg-blue-100 border border-blue-200/80 transition-colors self-start sm:self-auto"
                          >
                            <span>LinkedIn Profile</span>
                            <ExternalLink className="w-3 h-3" />
                          </a>
                        )}
                      </div>

                      {/* Bio Statement */}
                      {app.bio && (
                        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80 text-xs text-slate-700 leading-relaxed">
                          <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1">
                            Applicant Bio &amp; Expertise Statement
                          </div>
                          &ldquo;{app.bio}&rdquo;
                        </div>
                      )}

                      {/* Action Buttons */}
                      <div className="flex items-center justify-end gap-3 pt-2 border-t border-slate-100">
                        <button
                          type="button"
                          onClick={() => handleReject(app.id, app.email)}
                          disabled={isOperating}
                          className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-semibold text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 border border-red-200 transition-colors disabled:opacity-50 cursor-pointer"
                        >
                          <XCircle className="w-3.5 h-3.5" />
                          Reject
                        </button>

                        <button
                          type="button"
                          onClick={() => handleApprove(app.id, app.email)}
                          disabled={isOperating}
                          className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 shadow-sm shadow-emerald-500/20 transition-all disabled:opacity-50 cursor-pointer"
                        >
                          {isOperating ? (
                            <span className="flex items-center gap-1.5">
                              <span className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                              Elevating...
                            </span>
                          ) : (
                            <>
                              <CheckCircle2 className="w-3.5 h-3.5" />
                              Approve &amp; Elevate
                            </>
                          )}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Right Column: Injected Slot (Language Detector Card) & Security Highlights */}
          <div className="flex flex-col gap-6">
            {detectorSlot}

            {/* Architecture Card */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col gap-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Mentor Verification Standards
              </h4>
              <ul className="text-xs text-slate-600 space-y-2 leading-relaxed">
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-blue-600 mt-0.5 shrink-0" />
                  <span>
                    <strong>Professional Tenure:</strong> Confirm applicant holds relevant Java/distributed systems engineering experience.
                  </span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-blue-600 mt-0.5 shrink-0" />
                  <span>
                    <strong>Real-time Privilege Upgrade:</strong> Approved mentors immediately unlock 1-on-1 student escalation desks.
                  </span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-blue-600 mt-0.5 shrink-0" />
                  <span>
                    <strong>Educational Mission:</strong> Mentors guide learners using Socratic probing rather than dumping raw solutions.
                  </span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
