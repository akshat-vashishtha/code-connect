"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { authService } from "@/service/AuthService";

/**
 * Enterprise mentor holding-state screen for CodeConnect.
 * Displayed when a Mentor signs in or registers while pending admin verification.
 *
 * UX Improvements (Sally's Epic 1 pass):
 * - No AppShell (no sidebar — mentor has not been activated yet)
 * - 3-step progress indicator: Applied ✓ → Under Review (pulsing) → Approved
 * - Large amber clock-alert icon (visual hierarchy anchor)
 * - Personalized subtext with mentor's email from session
 * - Amber-themed design language (distinct from blue student / rose admin)
 * - Live check status action retained from original design
 */
export default function PendingApprovalPage() {
  const router = useRouter();
  const [isChecking, setIsChecking] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const handleCheckStatus = async () => {
    setIsChecking(true);
    setStatusMessage(null);
    try {
      const user = await authService.getCurrentUser();
      if (user && user.role === "ROLE_MENTOR" && user.status === "ACTIVE") {
        setStatusMessage("Approved! Your mentor account has been activated.");
        setTimeout(() => {
          router.push("/mentor/desk");
        }, 1200);
      } else {
        setStatusMessage("Application is still in review. Check back shortly!");
      }
    } catch {
      setStatusMessage("Unable to reach verification service. Please try again.");
    } finally {
      setIsChecking(false);
    }
  };

  const handleLogout = async () => {
    try {
      await authService.logout();
    } finally {
      window.location.href = "/";
    }
  };

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 font-sans flex flex-col">
      {/* Minimal header — no sidebar since mentor isn't activated */}
      <header className="h-16 bg-white border-b border-slate-200 px-6 flex items-center justify-between">
        <Link href="/" className="inline-flex items-center gap-3 group">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-600 flex items-center justify-center text-white font-extrabold text-sm shadow-md shadow-amber-500/20 group-hover:scale-105 transition-transform">
            CC
          </div>
          <div>
            <span className="text-base font-bold tracking-tight text-slate-900">
              Code<span className="text-amber-600">Connect</span>
            </span>
            <span className="block text-[10px] font-medium text-slate-400">
              Mentor Verification
            </span>
          </div>
        </Link>

        <button
          type="button"
          onClick={handleLogout}
          className="text-xs font-semibold text-red-600 hover:text-red-700 inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 hover:border-red-200 hover:bg-red-50 transition-colors"
        >
          <VectorIcon name="log-out" size={13} />
          <span>Sign Out</span>
        </button>
      </header>

      {/* Main centered content */}
      <main className="flex-1 flex items-center justify-center py-12 px-4 sm:px-6">
        <div className="w-full max-w-lg space-y-6">

          {/* Large amber icon anchor */}
          <div className="flex flex-col items-center text-center space-y-4">
            <div className="relative">
              <div className="w-20 h-20 rounded-2xl bg-amber-100 border-2 border-amber-200 flex items-center justify-center shadow-lg shadow-amber-100">
                <VectorIcon name="clock" size={40} className="text-amber-600" />
              </div>
              {/* Pulsing amber ring */}
              <div className="absolute inset-0 rounded-2xl border-2 border-amber-400 animate-pulse opacity-40" />
            </div>

            {/* Status pill */}
            <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-bold shadow-xs">
              <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse" />
              <span>Application Under Staff Review</span>
            </div>
          </div>

          {/* Main card */}
          <div className="bg-white rounded-2xl border border-slate-200 shadow-[0_8px_30px_rgba(15,23,42,0.06)] overflow-hidden">
            {/* Amber top accent bar */}
            <div className="h-1 bg-gradient-to-r from-amber-500 to-orange-500" />

            <div className="p-6 sm:p-8 space-y-6">
              {/* Heading */}
              <div className="text-center space-y-1.5">
                <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
                  Application Under Review
                </h1>
                <p className="text-xs sm:text-sm text-slate-600 leading-relaxed max-w-sm mx-auto">
                  Your application and professional background have been received and
                  queued for review in the Admin Governance Studio.
                </p>
              </div>

              {/* 3-Step Progress Timeline */}
              <div className="relative">
                <div className="flex items-start justify-between">
                  {/* Step 1: Applied */}
                  <div className="flex flex-col items-center flex-1">
                    <div className="w-9 h-9 rounded-full bg-emerald-100 border-2 border-emerald-400 text-emerald-700 flex items-center justify-center shrink-0 shadow-sm">
                      <VectorIcon name="check-circle" size={18} strokeWidth={2.5} />
                    </div>
                    <div className="mt-2 text-center">
                      <p className="text-[11px] font-bold text-emerald-700">Applied</p>
                      <p className="text-[10px] text-slate-400">Received</p>
                    </div>
                  </div>

                  {/* Connector line 1 */}
                  <div className="flex-1 h-[2px] mt-[18px] mx-1 bg-gradient-to-r from-emerald-300 to-amber-300" />

                  {/* Step 2: Under Review (active / pulsing) */}
                  <div className="flex flex-col items-center flex-1">
                    <div className="w-9 h-9 rounded-full bg-amber-100 border-2 border-amber-400 text-amber-700 flex items-center justify-center shrink-0 shadow-sm animate-pulse">
                      <VectorIcon name="clock" size={16} strokeWidth={2} />
                    </div>
                    <div className="mt-2 text-center">
                      <p className="text-[11px] font-bold text-amber-700">Under Review</p>
                      <p className="text-[10px] text-amber-500 font-medium">In Progress</p>
                    </div>
                  </div>

                  {/* Connector line 2 */}
                  <div className="flex-1 h-[2px] mt-[18px] mx-1 bg-slate-200" />

                  {/* Step 3: Approved (locked) */}
                  <div className="flex flex-col items-center flex-1 opacity-40">
                    <div className="w-9 h-9 rounded-full bg-slate-100 border-2 border-slate-300 text-slate-400 flex items-center justify-center shrink-0">
                      <VectorIcon name="lock" size={15} strokeWidth={2} />
                    </div>
                    <div className="mt-2 text-center">
                      <p className="text-[11px] font-bold text-slate-500">Approved</p>
                      <p className="text-[10px] text-slate-400">Pending</p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Verification Pipeline Detail */}
              <div className="border border-slate-200/80 rounded-xl bg-slate-50/60 divide-y divide-slate-100">
                {/* Step A: Registered */}
                <div className="flex items-start gap-3 p-4">
                  <div className="w-7 h-7 rounded-lg bg-emerald-100 border border-emerald-200 text-emerald-700 flex items-center justify-center shrink-0 mt-0.5">
                    <VectorIcon name="checkmark" size={13} strokeWidth={2.5} />
                  </div>
                  <div>
                    <p className="text-xs font-semibold text-slate-900">Application Registered</p>
                    <p className="text-[11px] text-slate-500 mt-0.5 leading-relaxed">
                      Your credentials and technical profile have been safely recorded in MongoDB.
                    </p>
                  </div>
                </div>

                {/* Step B: Credential Verification (current) */}
                <div className="flex items-start gap-3 p-4">
                  <div className="w-7 h-7 rounded-lg bg-amber-100 border border-amber-200 text-amber-700 flex items-center justify-center shrink-0 mt-0.5 animate-pulse">
                    <VectorIcon name="clock" size={13} strokeWidth={2} />
                  </div>
                  <div>
                    <p className="text-xs font-semibold text-slate-900">Staff Credential Verification</p>
                    <p className="text-[11px] text-slate-500 mt-0.5 leading-relaxed">
                      Platform leads are verifying your industry engineering experience and public code contributions.
                    </p>
                  </div>
                </div>

                {/* Step C: Mentor Desk Activation (locked) */}
                <div className="flex items-start gap-3 p-4 opacity-50">
                  <div className="w-7 h-7 rounded-lg bg-slate-100 border border-slate-200 text-slate-400 flex items-center justify-center shrink-0 mt-0.5">
                    <VectorIcon name="lock" size={12} strokeWidth={2} />
                  </div>
                  <div>
                    <p className="text-xs font-semibold text-slate-600">Mentor Desk Activation</p>
                    <p className="text-[11px] text-slate-500 mt-0.5 leading-relaxed">
                      Upon approval, your account unlocks the Mentor Escalation Desk and Curriculum Authoring Studio.
                    </p>
                  </div>
                </div>
              </div>

              {/* Status feedback message */}
              {statusMessage && (
                <div
                  className={`p-3 rounded-xl border text-xs font-medium text-center animate-fade-in ${
                    statusMessage.includes("Approved")
                      ? "bg-emerald-50 border-emerald-200 text-emerald-800"
                      : "bg-blue-50 border-blue-200 text-blue-800"
                  }`}
                >
                  {statusMessage}
                </div>
              )}

              {/* Action Buttons */}
              <div className="space-y-2.5">
                <button
                  type="button"
                  onClick={handleCheckStatus}
                  disabled={isChecking}
                  id="check-status-btn"
                  className="w-full h-11 inline-flex justify-center items-center gap-2 rounded-xl text-xs font-bold text-white bg-amber-600 hover:bg-amber-700 active:bg-amber-800 shadow-md shadow-amber-500/20 transition-all disabled:opacity-50"
                >
                  <VectorIcon
                    name="refresh"
                    size={14}
                    className={isChecking ? "animate-spin text-white" : "text-white"}
                  />
                  <span>{isChecking ? "Checking Verification..." : "Check Approval Status"}</span>
                </button>

                <Link
                  href="/"
                  className="w-full h-10 inline-flex justify-center items-center rounded-xl text-xs font-semibold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 transition-colors"
                >
                  Return to Homepage
                </Link>
              </div>
            </div>
          </div>

          {/* Footer note */}
          <p className="text-center text-xs text-slate-400">
            Questions?{" "}
            <a
              href="mailto:support@codeconnect.dev"
              className="text-amber-600 hover:underline font-medium"
            >
              support@codeconnect.dev
            </a>
          </p>
        </div>
      </main>
    </div>
  );
}
