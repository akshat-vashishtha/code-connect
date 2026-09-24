import React from "react";
import Link from "next/link";
import { Clock, ShieldCheck, CheckCircle2, UserCheck, ArrowRight, ArrowLeft, Sparkles, Shield } from "lucide-react";

/**
 * Enterprise status screen displayed when a Mentor signs in or registers
 * while their application is awaiting administrative verification.
 */
export default function PendingApprovalPage() {
  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center items-center py-12 px-4 sm:px-6 lg:px-8 font-sans relative overflow-hidden selection:bg-blue-500 selection:text-white">
      {/* Subtle Background Pattern */}
      <div className="absolute inset-0 bg-grid-pattern opacity-60 pointer-events-none" />

      <div className="w-full max-w-lg relative z-10 flex flex-col items-center">
        {/* Top Back Navigation & Brand Header */}
        <div className="w-full mb-6 flex items-center justify-between">
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-700 hover:text-slate-900 bg-white hover:bg-slate-100 px-3.5 py-1.5 rounded-xl border border-slate-200 shadow-sm transition-all hover:-translate-x-0.5"
          >
            <ArrowLeft className="w-3.5 h-3.5 text-slate-500" />
            <span>Back to Home</span>
          </Link>

          <Link href="/login" className="text-xs font-semibold text-blue-600 hover:text-blue-700 hover:underline">
            Sign In with Another Account &rarr;
          </Link>
        </div>

        {/* Elevated Pristine White Card */}
        <div className="w-full bg-white/95 backdrop-blur-md rounded-2xl shadow-xl shadow-slate-200/60 border border-slate-200/90 p-6 sm:p-10 text-center">
          {/* Status Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-semibold mb-6 shadow-xs">
            <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse" />
            <span>Status: Application Under Mentor Review</span>
          </div>

          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
            Thank you for applying to mentor!
          </h1>
          <p className="mt-3 text-sm text-slate-600 leading-relaxed max-w-md mx-auto">
            Your application and professional background have been received and queued for review by our engineering team.
          </p>

          {/* Verification Pipeline Steps */}
          <div className="my-8 text-left border border-slate-200 rounded-xl bg-slate-50/70 p-5 space-y-4">
            <div className="text-[11px] font-bold uppercase tracking-wider text-slate-500 flex items-center justify-between pb-2 border-b border-slate-200">
              <span>Verification Pipeline</span>
              <span className="text-blue-600 font-mono text-[10px] bg-blue-50 px-2 py-0.5 rounded border border-blue-100">
                Senior Staff Desk
              </span>
            </div>

            <div className="flex items-start gap-3.5">
              <div className="w-7 h-7 rounded-xl bg-emerald-100 border border-emerald-200 text-emerald-600 flex items-center justify-center shrink-0 mt-0.5">
                <CheckCircle2 className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs sm:text-sm font-semibold text-slate-900">Application Registered</p>
                <p className="text-xs text-slate-500 mt-0.5">
                  Your profile and background details have been recorded safely in our mentor registry.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3.5">
              <div className="w-7 h-7 rounded-xl bg-blue-100 border border-blue-200 text-blue-600 flex items-center justify-center shrink-0 mt-0.5 animate-pulse">
                <Clock className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs sm:text-sm font-semibold text-slate-900">Staff Credential Verification</p>
                <p className="text-xs text-slate-500 mt-0.5">
                  Platform leads verify your professional experience and industry mentoring expertise.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3.5 opacity-60">
              <div className="w-7 h-7 rounded-xl bg-slate-200 border border-slate-300 text-slate-500 flex items-center justify-center shrink-0 mt-0.5">
                <UserCheck className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs sm:text-sm font-semibold text-slate-600">Mentor Desk Activation</p>
                <p className="text-xs text-slate-500 mt-0.5">
                  Upon approval, your account unlocks 1-on-1 student escalation chats and code review desks.
                </p>
              </div>
            </div>
          </div>

          {/* Turnaround Estimate */}
          <div className="bg-blue-50/80 rounded-xl p-4 text-xs text-blue-900 border border-blue-200/80 text-left mb-6 flex items-start gap-3">
            <Sparkles className="w-4 h-4 text-blue-600 shrink-0 mt-0.5" />
            <div>
              <p className="font-semibold text-blue-950">Estimated turnaround: 24 to 48 hours</p>
              <p className="text-blue-700 mt-0.5 leading-relaxed">
                Reviews are completed promptly. You will gain immediate access as soon as your profile is verified.
              </p>
            </div>
          </div>

          {/* Action Button */}
          <Link
            href="/"
            className="w-full inline-flex justify-center items-center gap-2 py-3 px-6 rounded-xl text-sm font-bold text-white bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 hover:shadow-lg hover:shadow-blue-500/30 hover:-translate-y-0.5 active:scale-[0.99] transition-all"
          >
            <span>Return to CodeConnect Home</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {/* Security Footer */}
        <div className="mt-6 flex items-center gap-2 text-xs text-slate-400">
          <Shield className="w-4 h-4 text-slate-400" />
          <span>CodeConnect Educator Network &bull; Reviewed by Staff Engineers</span>
        </div>
      </div>
    </div>
  );
}
