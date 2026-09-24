import React from "react";
import Link from "next/link";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

/**
 * Enterprise status screen displayed when a Mentor signs in or registers
 * while their application is awaiting administrative verification.
 * Adheres strictly to SaaS Light Workbench aesthetic.
 */
export default function PendingApprovalPage() {
  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 flex flex-col justify-center items-center py-12 px-4 sm:px-6 font-sans">
      <div className="w-full max-w-lg flex flex-col items-center">
        {/* Top Back Navigation */}
        <div className="w-full mb-6 flex items-center justify-between">
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-slate-900 bg-white hover:bg-slate-50 px-3.5 py-1.5 rounded-xl border border-slate-200 shadow-sm transition-colors"
          >
            <span>&larr; Return to Home</span>
          </Link>

          <Link
            href="/login"
            className="text-xs font-semibold text-blue-600 hover:text-blue-700 hover:underline"
          >
            Sign In with Another Account &rarr;
          </Link>
        </div>

        {/* Elevated Pristine White Card */}
        <div className="w-full bg-white rounded-2xl shadow-[0_8px_30px_rgba(15,23,42,0.06)] border border-slate-200/90 p-6 sm:p-9 text-center">
          {/* Status Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-bold mb-5 shadow-xs">
            <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse" />
            <span>Status: Application Under Staff Review</span>
          </div>

          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            Thank you for applying to mentor!
          </h1>
          <p className="mt-2 text-xs sm:text-sm text-slate-600 leading-relaxed max-w-md mx-auto">
            Your application and professional background have been received and queued for review by our engineering team.
          </p>

          {/* Verification Pipeline Steps */}
          <div className="my-6 text-left border border-slate-200/80 rounded-xl bg-slate-50/60 p-4 space-y-3.5">
            <div className="text-[11px] font-bold uppercase tracking-wider text-slate-500 flex items-center justify-between pb-2 border-b border-slate-200/80">
              <span>Verification Pipeline</span>
              <span className="text-blue-700 font-mono text-[10px] bg-blue-50 px-2 py-0.5 rounded border border-blue-200 font-bold">
                Admin Studio Desk
              </span>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-lg bg-emerald-100 border border-emerald-200 text-emerald-700 flex items-center justify-center shrink-0 mt-0.5">
                <VectorIcon name="checkmark" size={12} strokeWidth={2} />
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-900">Application Registered</p>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Your credentials and technical profile have been safely recorded in MongoDB.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-lg bg-blue-100 border border-blue-200 text-blue-700 flex items-center justify-center shrink-0 mt-0.5 animate-pulse">
                <VectorIcon name="clock" size={12} strokeWidth={2} />
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-900">Staff Credential Verification</p>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Platform leads verify your industry engineering experience and public code contributions.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 opacity-60">
              <div className="w-6 h-6 rounded-lg bg-slate-200 border border-slate-300 text-slate-500 flex items-center justify-center shrink-0 mt-0.5">
                <VectorIcon name="lock" size={11} strokeWidth={2} />
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-700">Mentor Desk Activation</p>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Upon approval, your account unlocks the Mentor Escalation Desk and Curriculum Authoring Studio.
                </p>
              </div>
            </div>
          </div>

          {/* Turnaround Estimate */}
          <div className="bg-blue-50/70 rounded-xl p-3.5 text-xs text-blue-900 border border-blue-200/80 text-left mb-6 flex items-start gap-2.5">
            <VectorIcon name="lightbulb" size={15} className="text-blue-600 shrink-0 mt-0.5" />
            <div>
              <p className="font-bold text-blue-950">Estimated turnaround: 24 to 48 hours</p>
              <p className="text-[11px] text-blue-700 mt-0.5 leading-relaxed">
                Reviews are completed promptly. You will gain immediate access as soon as your profile is verified in the Admin Studio.
              </p>
            </div>
          </div>

          {/* Action Button */}
          <Link
            href="/"
            className="w-full inline-flex justify-center items-center gap-2 py-2.5 px-5 rounded-xl text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 active:scale-[0.99] transition-all"
          >
            <span>Return to CodeConnect Home</span>
            <VectorIcon name="arrow-right" size={13} className="text-white" />
          </Link>
        </div>

        {/* Security Footer */}
        <div className="mt-6 flex items-center gap-2 text-xs text-slate-400">
          <VectorIcon name="shield" size={13} className="text-slate-400" />
          <span>CodeConnect Educator Network &bull; Spring Security &bull; k3d Sandbox</span>
        </div>
      </div>
    </div>
  );
}
