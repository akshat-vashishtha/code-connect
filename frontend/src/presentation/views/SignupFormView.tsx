"use client";

import React, { useState } from "react";
import Link from "next/link";
import { SignupControllerResult } from "@/controller/useSignupController";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { AlertBanner } from "@/presentation/atoms/AlertBanner";

export interface SignupFormViewProps {
  readonly controller: SignupControllerResult;
}

/**
 * Presentation view for User Registration and Role Selection.
 * Matches Wireframe 01-epic1-auth-onboarding.svg in SaaS Light Workbench theme.
 */
export const SignupFormView: React.FC<SignupFormViewProps> = ({ controller }) => {
  const {
    formData,
    isLoading,
    errorMessage,
    isDuplicateEmail,
    fieldErrors,
    setFormData,
    handleSubmit,
  } = controller;

  const [termsAccepted, setTermsAccepted] = useState(true);
  const isMentor = formData.role === "ROLE_MENTOR";

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 flex flex-col justify-between p-4 sm:p-6 font-sans">
      {/* Top Header */}
      <header className="max-w-6xl mx-auto w-full flex items-center justify-between py-2">
        <Link href="/" className="inline-flex items-center gap-3 group">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-extrabold text-sm shadow-md shadow-blue-500/20 group-hover:scale-105 transition-transform">
            CC
          </div>
          <div>
            <span className="text-lg font-bold tracking-tight text-slate-900">
              Code<span className="text-blue-600">Connect</span>
            </span>
            <span className="block text-[10px] font-medium text-slate-500">
              The Solo Mountain Climb
            </span>
          </div>
        </Link>

        <div className="flex items-center gap-3">
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-semibold shadow-2xs transition-colors"
          >
            <VectorIcon name="arrow-left" size={13} strokeWidth={2} />
            <span>Back to Platform</span>
          </Link>
          <Link
            href="/login"
            className="text-xs font-semibold text-slate-600 hover:text-blue-600 transition-colors hidden sm:inline"
          >
            Already have an account? <span className="text-blue-600 font-bold">Sign In</span>
          </Link>
        </div>
      </header>

      {/* Main Centered 440px Card (Wireframe 01) */}
      <main className="flex-1 flex items-center justify-center py-6">
        <div className="w-full max-w-[460px] bg-white rounded-2xl border border-slate-200/90 shadow-[0_8px_30px_rgba(15,23,42,0.06)] p-7 sm:p-8">
          {/* Segmented Auth Navigation */}
          <div className="grid grid-cols-2 p-1 bg-slate-100/80 rounded-xl mb-6 text-xs font-bold border border-slate-200/60">
            <Link
              href="/login"
              className="py-2 text-center rounded-lg text-slate-500 hover:text-slate-900 transition-colors"
            >
              Sign In
            </Link>
            <div className="py-2 text-center rounded-lg bg-white text-blue-600 shadow-sm border border-slate-200/80">
              Create Account
            </div>
          </div>

          <div className="mb-5">
            <h1 className="text-xl font-extrabold text-slate-900 tracking-tight">
              Begin Your Ascent
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              Select your platform role to configure your dedicated workspace.
            </p>
          </div>

          {/* Error Banner */}
          {errorMessage && (
            <div className="mb-5">
              <AlertBanner
                variant={isDuplicateEmail ? "warning" : "error"}
                message={errorMessage}
              />
            </div>
          )}

          {/* Role Selection Segmented Cards (Wireframe 01) */}
          <div className="space-y-3 mb-5">
            <div className="text-[11px] font-bold text-slate-700 uppercase tracking-wider">
              Select Role
            </div>

            {/* Student Role Card */}
            <div
              onClick={() => setFormData({ role: "ROLE_STUDENT" })}
              className={`p-3.5 rounded-xl border cursor-pointer transition-all ${
                !isMentor
                  ? "border-blue-600 bg-blue-50/40 ring-1 ring-blue-500/30"
                  : "border-slate-200 hover:border-slate-300 bg-white"
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-slate-900">Student (Climber)</span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                    Instant Access
                  </span>
                </div>
                <div
                  className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                    !isMentor
                      ? "border-blue-600 bg-blue-600"
                      : "border-slate-300 bg-white"
                  }`}
                >
                  {!isMentor && <div className="w-1.5 h-1.5 rounded-full bg-white" />}
                </div>
              </div>
              <p className="text-[11px] text-slate-500 mt-1.5 leading-relaxed">
                Solve progressive tracks, execute code in isolated sandboxes, and receive
                Socratic AI hints.
              </p>
            </div>

            {/* Mentor Role Card */}
            <div
              onClick={() => setFormData({ role: "ROLE_MENTOR" })}
              className={`p-3.5 rounded-xl border cursor-pointer transition-all ${
                isMentor
                  ? "border-blue-600 bg-blue-50/40 ring-1 ring-blue-500/30"
                  : "border-slate-200 hover:border-slate-300 bg-white"
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-slate-900">Mentor (Guide)</span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200">
                    Requires Approval
                  </span>
                </div>
                <div
                  className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                    isMentor
                      ? "border-blue-600 bg-blue-600"
                      : "border-slate-300 bg-white"
                  }`}
                >
                  {isMentor && <div className="w-1.5 h-1.5 rounded-full bg-white" />}
                </div>
              </div>
              <p className="text-[11px] text-slate-500 mt-1.5 leading-relaxed">
                Author real-world tracks, review escalated student diffs, and coach climbers.
              </p>
            </div>
          </div>

          {/* Registration Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label
                htmlFor="signup-displayName"
                className="block text-xs font-semibold text-slate-700 mb-1"
              >
                Full Name
              </label>
              <input
                id="signup-displayName"
                type="text"
                value={formData.displayName}
                onChange={(e) => setFormData({ displayName: e.target.value })}
                placeholder="Alex Vashishtha"
                disabled={isLoading}
                className={`w-full h-10 px-3.5 rounded-xl border bg-slate-50/50 text-slate-900 text-xs placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-600 transition-all ${
                  fieldErrors.displayName
                    ? "border-rose-300 bg-rose-50/30"
                    : "border-slate-200"
                }`}
              />
              {fieldErrors.displayName && (
                <p className="text-[11px] text-rose-600 mt-1 font-medium">
                  {fieldErrors.displayName}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="signup-email"
                className="block text-xs font-semibold text-slate-700 mb-1"
              >
                Email Address
              </label>
              <input
                id="signup-email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ email: e.target.value })}
                placeholder={isMentor ? "priya.sen@swiggy.in" : "alex@codeconnect.dev"}
                disabled={isLoading}
                className={`w-full h-10 px-3.5 rounded-xl border bg-slate-50/50 text-slate-900 text-xs placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-600 transition-all ${
                  fieldErrors.email ? "border-rose-300 bg-rose-50/30" : "border-slate-200"
                }`}
              />
              {fieldErrors.email && (
                <p className="text-[11px] text-rose-600 mt-1 font-medium">
                  {fieldErrors.email}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="signup-password"
                className="block text-xs font-semibold text-slate-700 mb-1"
              >
                Password
              </label>
              <input
                id="signup-password"
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ password: e.target.value })}
                placeholder="At least 8 characters with letters & numbers"
                disabled={isLoading}
                className={`w-full h-10 px-3.5 rounded-xl border bg-slate-50/50 text-slate-900 text-xs placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-600 transition-all ${
                  fieldErrors.password
                    ? "border-rose-300 bg-rose-50/30"
                    : "border-slate-200"
                }`}
              />
              {fieldErrors.password && (
                <p className="text-[11px] text-rose-600 mt-1 font-medium">
                  {fieldErrors.password}
                </p>
              )}
            </div>

            {/* Mentor-Specific Fields */}
            {isMentor && (
              <div className="space-y-4 pt-2 border-t border-slate-100">
                <div>
                  <label
                    htmlFor="signup-linkedInUrl"
                    className="block text-xs font-semibold text-slate-700 mb-1"
                  >
                    LinkedIn / GitHub Profile URL
                  </label>
                  <input
                    id="signup-linkedInUrl"
                    type="url"
                    value={formData.linkedInUrl}
                    onChange={(e) => setFormData({ linkedInUrl: e.target.value })}
                    placeholder="https://github.com/psen-java"
                    disabled={isLoading}
                    className={`w-full h-10 px-3.5 rounded-xl border bg-slate-50/50 text-slate-900 text-xs placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-600 transition-all ${
                      fieldErrors.linkedInUrl
                        ? "border-rose-300 bg-rose-50/30"
                        : "border-slate-200"
                    }`}
                  />
                  {fieldErrors.linkedInUrl && (
                    <p className="text-[11px] text-rose-600 mt-1 font-medium">
                      {fieldErrors.linkedInUrl}
                    </p>
                  )}
                </div>

                <div>
                  <label
                    htmlFor="signup-bio"
                    className="block text-xs font-semibold text-slate-700 mb-1"
                  >
                    Technical Domain &amp; Experience
                  </label>
                  <textarea
                    id="signup-bio"
                    rows={2}
                    value={formData.bio}
                    onChange={(e) => setFormData({ bio: e.target.value })}
                    placeholder="Staff Architect @ Swiggy • 10 yrs Java Enterprise • Distributed Systems"
                    disabled={isLoading}
                    className={`w-full p-3 rounded-xl border bg-slate-50/50 text-slate-900 text-xs placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-600 transition-all ${
                      fieldErrors.bio
                        ? "border-rose-300 bg-rose-50/30"
                        : "border-slate-200"
                    }`}
                  />
                  {fieldErrors.bio && (
                    <p className="text-[11px] text-rose-600 mt-1 font-medium">
                      {fieldErrors.bio}
                    </p>
                  )}
                </div>

                <div className="p-3 rounded-xl bg-amber-50/80 border border-amber-200 text-amber-900 text-[11px] leading-relaxed flex items-start gap-2">
                  <VectorIcon name="shield" size={14} className="text-amber-700 shrink-0 mt-0.5" />
                  <span>
                    Mentor applications are redirected to <code className="font-mono text-amber-950 font-bold">/pending-approval</code> until approved by Admin.
                  </span>
                </div>
              </div>
            )}

            {/* Terms Checkbox */}
            <div className="flex items-start gap-2 pt-1">
              <input
                id="terms"
                type="checkbox"
                checked={termsAccepted}
                onChange={(e) => setTermsAccepted(e.target.checked)}
                className="w-4 h-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500 cursor-pointer mt-0.5"
              />
              <label
                htmlFor="terms"
                className="text-[11px] text-slate-600 leading-tight select-none cursor-pointer"
              >
                I agree to the CodeConnect Honor Code and Sandbox Safety Policies.
              </label>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading || !termsAccepted}
              className="w-full h-11 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all shadow-md shadow-blue-500/20 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? (
                <>
                  <VectorIcon name="refresh" size={14} className="animate-spin text-white" />
                  <span>Creating Account...</span>
                </>
              ) : (
                <>
                  <span>{isMentor ? "Submit Mentor Application" : "Create Student Account"}</span>
                  <VectorIcon name="arrow-right" size={14} className="text-white" />
                </>
              )}
            </button>
          </form>
        </div>
      </main>

      {/* Footer Security Note */}
      <footer className="py-3 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
        <VectorIcon name="shield" size={14} className="text-slate-400" />
        <span>Enterprise Clean Architecture &bull; Java 21 LTS &bull; Spring Boot 3.3</span>
      </footer>
    </div>
  );
};
