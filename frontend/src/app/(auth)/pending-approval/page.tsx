import React from "react";
import Link from "next/link";

export default function PendingApprovalPage() {
  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-lg">
        {/* Brand header */}
        <div className="flex justify-center items-center gap-2 mb-8">
          <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center text-white font-bold text-xl shadow-md shadow-blue-500/20">
            CC
          </div>
          <span className="text-2xl font-extrabold tracking-tight text-slate-900">
            Code<span className="text-blue-600">Connect</span>
          </span>
        </div>

        {/* Status Card */}
        <div className="bg-white py-8 px-6 shadow-xl shadow-slate-200/50 sm:rounded-2xl sm:px-10 border border-slate-200/80 text-center">
          {/* Status Badge */}
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-semibold mb-4">
            <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse"></span>
            Status: Application Under Admin Review
          </div>

          <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            Thank you for applying to mentor!
          </h2>
          <p className="mt-2 text-sm text-slate-600 leading-relaxed">
            Your application and professional credentials have been received and recorded in our verification registry.
          </p>

          {/* Verification Timeline */}
          <div className="my-8 text-left border border-slate-100 rounded-xl bg-slate-50/50 p-5 space-y-4">
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500">
              Verification Pipeline
            </h3>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center text-xs font-bold shrink-0 mt-0.5">
                ✓
              </div>
              <div>
                <p className="text-sm font-semibold text-slate-800">Application Submitted</p>
                <p className="text-xs text-slate-500">Account created and credentials encrypted with BCrypt.</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-xs font-bold shrink-0 mt-0.5 animate-pulse">
                2
              </div>
              <div>
                <p className="text-sm font-semibold text-slate-800">Admin Credential Review</p>
                <p className="text-xs text-slate-500">
                  Platform administrators verify your LinkedIn profile and professional domain expertise.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 opacity-60">
              <div className="w-6 h-6 rounded-full bg-slate-200 text-slate-500 flex items-center justify-center text-xs font-bold shrink-0 mt-0.5">
                3
              </div>
              <div>
                <p className="text-sm font-semibold text-slate-800">Mentor Privilege Activation</p>
                <p className="text-xs text-slate-500">
                  Upon approval, your role will be upgraded in real-time, unlocking mentor studios and code review desks.
                </p>
              </div>
            </div>
          </div>

          <div className="bg-blue-50/60 rounded-xl p-4 text-xs text-blue-800 border border-blue-100 text-left mb-6">
            <p className="font-semibold text-blue-900">Estimated turnaround</p>
            <p className="mt-0.5 leading-relaxed">
              Reviews are typically completed within 24 to 48 business hours. You can safely return to the home screen.
            </p>
          </div>

          <div className="space-y-3">
            <Link
              href="/"
              className="w-full inline-flex justify-center items-center py-2.5 px-4 rounded-xl shadow-sm text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 transition-colors"
            >
              Return to CodeConnect Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
