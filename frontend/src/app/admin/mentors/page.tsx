"use client";

import React, { useEffect, useState, useTransition } from "react";
import Link from "next/link";
import { getCurrentUser } from "@/lib/api/auth";
import { getPendingMentors, approveMentor, rejectMentor, detectLanguage } from "@/lib/api/admin";
import { UserResponse } from "@/types/auth";
import { MentorApprovalResponse, LanguageDetectionResponse } from "@/types/admin";
import {
  ShieldAlert,
  ShieldCheck,
  CheckCircle2,
  XCircle,
  ExternalLink,
  RefreshCw,
  Clock,
  Sparkles,
  Search,
  MessageSquare,
  ArrowLeft,
  Award,
} from "lucide-react";

export default function AdminMentorsPage() {
  const [currentUser, setCurrentUser] = useState<UserResponse | null>(null);
  const [isAuthLoading, setIsAuthLoading] = useState(true);
  const [unauthorized, setUnauthorized] = useState(false);

  const [applications, setApplications] = useState<MentorApprovalResponse[]>([]);
  const [isDataLoading, setIsDataLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [actionInProgress, setActionInProgress] = useState<string | null>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<{
    type: "success" | "error";
    text: string;
  } | null>(null);

  // Conversational Language Detector Widget State
  const [testText, setTestText] = useState("bhai kaise kare samajh nahi aaya code me issue hai");
  const [detectionResult, setDetectionResult] = useState<LanguageDetectionResponse | null>(null);
  const [isDetecting, setIsDetecting] = useState(false);

  const [, startTransition] = useTransition();

  // Load session & check permissions
  useEffect(() => {
    let isMounted = true;

    async function checkAuthAndLoad() {
      try {
        const userRes = await getCurrentUser();
        if (!isMounted) return;

        if (userRes.success && userRes.data) {
          setCurrentUser(userRes.data);
          if (userRes.data.role === "ROLE_ADMIN") {
            loadPendingApplications();
          } else {
            setUnauthorized(true);
          }
        } else {
          setUnauthorized(true);
        }
      } catch {
        if (isMounted) setUnauthorized(true);
      } finally {
        if (isMounted) setIsAuthLoading(false);
      }
    }

    checkAuthAndLoad();

    return () => {
      isMounted = false;
    };
  }, []);

  async function loadPendingApplications() {
    setIsDataLoading(true);
    setFeedbackMessage(null);
    try {
      const res = await getPendingMentors();
      if (res.success && res.data) {
        startTransition(() => {
          setApplications(res.data || []);
        });
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to load pending mentor applications";
      setFeedbackMessage({ type: "error", text: msg });
    } finally {
      setIsDataLoading(false);
    }
  }

  async function handleApprove(appId: string, email: string) {
    setActionInProgress(appId);
    setFeedbackMessage(null);
    try {
      const res = await approveMentor(appId);
      if (res.success) {
        setApplications((prev) => prev.filter((a) => a.id !== appId));
        setFeedbackMessage({
          type: "success",
          text: `Application for ${email} approved! Elevated user to ROLE_MENTOR and active Redis session updated in O(1) time.`,
        });
      } else {
        setFeedbackMessage({ type: "error", text: res.message || "Approval failed" });
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Approval failed";
      setFeedbackMessage({ type: "error", text: msg });
    } finally {
      setActionInProgress(null);
    }
  }

  async function handleReject(appId: string, email: string) {
    setActionInProgress(appId);
    setFeedbackMessage(null);
    try {
      const res = await rejectMentor(appId);
      if (res.success) {
        setApplications((prev) => prev.filter((a) => a.id !== appId));
        setFeedbackMessage({
          type: "success",
          text: `Application for ${email} has been rejected.`,
        });
      } else {
        setFeedbackMessage({ type: "error", text: res.message || "Rejection failed" });
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Rejection failed";
      setFeedbackMessage({ type: "error", text: msg });
    } finally {
      setActionInProgress(null);
    }
  }

  async function handleTestLanguage() {
    if (!testText.trim()) return;
    setIsDetecting(true);
    try {
      const res = await detectLanguage(testText.trim());
      if (res.success && res.data) {
        setDetectionResult(res.data);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Language detection failed";
      setFeedbackMessage({ type: "error", text: msg });
    } finally {
      setIsDetecting(false);
    }
  }

  const filteredApplications = applications.filter((app) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      app.email.toLowerCase().includes(q) ||
      (app.bio && app.bio.toLowerCase().includes(q)) ||
      (app.linkedInUrl && app.linkedInUrl.toLowerCase().includes(q))
    );
  });

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
      <div className="min-h-screen bg-slate-50 flex flex-col justify-center items-center p-6">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-xl shadow-slate-200/60 p-8 border border-slate-200 text-center flex flex-col items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-red-100 text-red-600 flex items-center justify-center">
            <ShieldAlert className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-extrabold text-slate-900">Access Restricted</h2>
          <p className="text-sm text-slate-600 leading-relaxed">
            The Mentor Verification Gateway requires <code className="px-1.5 py-0.5 rounded bg-slate-100 text-red-600 font-semibold">ROLE_ADMIN</code> privilege.
            {currentUser
              ? ` Your current authenticated role is ${currentUser.role}.`
              : " No active administrative session was found."}
          </p>
          <div className="w-full flex flex-col gap-2 pt-2">
            <Link
              href="/login"
              className="w-full py-2.5 px-4 rounded-xl text-sm font-semibold bg-blue-600 hover:bg-blue-700 text-white shadow-sm transition-colors text-center"
            >
              Sign In with Admin Account
            </Link>
            <Link
              href="/"
              className="w-full py-2.5 px-4 rounded-xl text-sm font-semibold text-slate-700 hover:bg-slate-100 border border-slate-200 transition-colors text-center"
            >
              Back to Home
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
          <div>
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
                  Admin Mentor Verification
                </h1>
                <p className="text-xs text-slate-500">
                  Dual-Layer RBAC Gatekeeper &bull; Real-time Redis Session Mutation
                </p>
              </div>
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
              className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-semibold bg-white border border-slate-200 hover:bg-slate-100 text-slate-700 shadow-sm transition-all disabled:opacity-50"
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
            {isDataLoading && applications.length === 0 ? (
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
                              <span className="font-mono text-[11px] text-slate-400">ID: {app.id.substring(0, 8)}...</span>
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

                      {/* Bio / Motivation */}
                      {app.bio && (
                        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80 text-xs text-slate-700 leading-relaxed">
                          <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1">
                            Applicant Bio &amp; Expertise Statement
                          </div>
                          &ldquo;{app.bio}&rdquo;
                        </div>
                      )}

                      {/* Actions */}
                      <div className="flex items-center justify-end gap-3 pt-2 border-t border-slate-100">
                        <button
                          type="button"
                          onClick={() => handleReject(app.id, app.email)}
                          disabled={isOperating}
                          className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-semibold text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 border border-red-200 transition-colors disabled:opacity-50"
                        >
                          <XCircle className="w-3.5 h-3.5" />
                          Reject
                        </button>

                        <button
                          type="button"
                          onClick={() => handleApprove(app.id, app.email)}
                          disabled={isOperating}
                          className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 shadow-sm shadow-emerald-500/20 transition-all disabled:opacity-50"
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

          {/* Right Column: Conversational Language Detector Sandbox (1 Col on lg) */}
          <div className="flex flex-col gap-6">
            {/* Story 1.4 Heuristic Hinglish Detector Widget */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col gap-4">
              <div className="flex items-center gap-2 text-indigo-600">
                <Sparkles className="w-5 h-5" />
                <h3 className="text-sm font-bold text-slate-900">Conversational Language Detector</h3>
              </div>
              <p className="text-xs text-slate-500 leading-relaxed">
                Story 1.4 automatically tags user sessions with <code className="px-1 py-0.5 rounded bg-slate-100 font-semibold text-indigo-600">languagePreference = HINGLISH</code> when conversational markers (&ldquo;bhai&rdquo;, &ldquo;samajh nahi aaya&rdquo;, &ldquo;kaise kare&rdquo;) are detected.
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
                  onClick={() => setTestText("Bhai recursion samajh nahi aaya, kaise kare?")}
                  className="px-2 py-0.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-[10px] text-slate-700 transition-colors"
                >
                  Hinglish 1
                </button>
                <button
                  type="button"
                  onClick={() => setTestText("Yaar Spring Boot microservices me issue ho raha hai batao")}
                  className="px-2 py-0.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-[10px] text-slate-700 transition-colors"
                >
                  Hinglish 2
                </button>
                <button
                  type="button"
                  onClick={() => setTestText("Please explain how Redis session management works")}
                  className="px-2 py-0.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-[10px] text-slate-700 transition-colors"
                >
                  English
                </button>
              </div>

              <button
                type="button"
                onClick={handleTestLanguage}
                disabled={isDetecting || !testText.trim()}
                className="w-full py-2.5 px-4 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-sm shadow-indigo-500/20 transition-all disabled:opacity-50 flex items-center justify-center gap-1.5"
              >
                <MessageSquare className="w-3.5 h-3.5" />
                {isDetecting ? "Classifying..." : "Detect Language & Update Session"}
              </button>

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
                    <span className="font-semibold text-slate-700">{detectionResult.confidence * 100}%</span>
                  </div>
                </div>
              )}
            </div>

            {/* Architecture Card */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col gap-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Security Architecture Highlights
              </h4>
              <ul className="text-xs text-slate-600 space-y-2 leading-relaxed">
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-blue-600 mt-0.5 shrink-0" />
                  <span>
                    <strong>Gateway Perimeter:</strong> Enforces <code className="font-semibold text-slate-800">ROLE_ADMIN</code> on <code className="font-semibold text-slate-800">/api/v1/admin/**</code> returning RFC 7807 403 Forbidden.
                  </span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-blue-600 mt-0.5 shrink-0" />
                  <span>
                    <strong>O(1) Session Elevation:</strong> Directly updates active Redis session hash attributes without forcing mentor re-login.
                  </span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-blue-600 mt-0.5 shrink-0" />
                  <span>
                    <strong>Identity Propagation:</strong> Injects <code className="font-semibold text-slate-800">X-User-Id</code>, <code className="font-semibold text-slate-800">X-User-Role</code> headers downstream to microservices.
                  </span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
