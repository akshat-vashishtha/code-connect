"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { signup, ApiError } from "@/lib/api/auth";
import { UserRole } from "@/types/api";

export default function SignupPage() {
  const router = useRouter();

  const [role, setRole] = useState<UserRole>("ROLE_STUDENT");
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [linkedInUrl, setLinkedInUrl] = useState("");
  const [bio, setBio] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isDuplicateEmail, setIsDuplicateEmail] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const validateForm = (): boolean => {
    const errors: Record<string, string> = {};

    if (!displayName.trim()) {
      errors.displayName = "Full name is required";
    } else if (displayName.trim().length < 2) {
      errors.displayName = "Name must be at least 2 characters";
    }

    if (!email.trim()) {
      errors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      errors.email = "Please enter a valid email address";
    }

    if (!password) {
      errors.password = "Password is required";
    } else if (password.length < 8) {
      errors.password = "Password must be at least 8 characters";
    }

    if (role === "ROLE_MENTOR") {
      if (!linkedInUrl.trim()) {
        errors.linkedInUrl = "LinkedIn profile URL is required for mentor verification";
      } else if (!linkedInUrl.trim().startsWith("http")) {
        errors.linkedInUrl = "Must be a valid URL starting with http:// or https://";
      }

      if (!bio.trim()) {
        errors.bio = "Professional bio / background is required";
      } else if (bio.trim().length < 20) {
        errors.bio = "Please provide at least 20 characters describing your experience";
      }
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setIsDuplicateEmail(false);

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      const response = await signup({
        displayName: displayName.trim(),
        email: email.trim(),
        password,
        role,
        linkedInUrl: role === "ROLE_MENTOR" ? linkedInUrl.trim() : undefined,
        bio: role === "ROLE_MENTOR" ? bio.trim() : undefined,
      });

      if (response.success) {
        if (role === "ROLE_MENTOR") {
          router.push("/pending-approval");
        } else {
          router.push("/?registered=true");
        }
      }
    } catch (err: unknown) {
      if (err instanceof ApiError) {
        if (err.status === 409) {
          setIsDuplicateEmail(true);
          setErrorMessage(err.message);
        } else if (err.problemDetail?.errors) {
          setFieldErrors(err.problemDetail.errors);
          setErrorMessage("Please review and correct the errors below.");
        } else {
          setErrorMessage(err.message);
        }
      } else {
        setErrorMessage("An unexpected network error occurred. Please try again.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const calculatePasswordStrength = (pwd: string) => {
    if (!pwd) return { label: "", color: "" };
    if (pwd.length < 8) return { label: "Too short", color: "text-red-500 bg-red-100" };
    const hasLetters = /[a-zA-Z]/.test(pwd);
    const hasNumbers = /\d/.test(pwd);
    const hasSpecial = /[^a-zA-Z0-9]/.test(pwd);
    const score = (hasLetters ? 1 : 0) + (hasNumbers ? 1 : 0) + (hasSpecial ? 1 : 0);

    if (score === 3 && pwd.length >= 10) return { label: "Strong", color: "text-emerald-700 bg-emerald-100" };
    if (score >= 2) return { label: "Fair", color: "text-amber-700 bg-amber-100" };
    return { label: "Weak", color: "text-orange-700 bg-orange-100" };
  };

  const strength = calculatePasswordStrength(password);

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center items-center gap-2">
          <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center text-white font-bold text-xl shadow-md shadow-blue-500/20">
            CC
          </div>
          <span className="text-2xl font-extrabold tracking-tight text-slate-900">
            Code<span className="text-blue-600">Connect</span>
          </span>
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold tracking-tight text-slate-900">
          Create your account
        </h2>
        <p className="mt-2 text-center text-sm text-slate-600">
          Already have an account?{" "}
          <Link href="/login" className="font-medium text-blue-600 hover:text-blue-500 transition-colors">
            Sign in
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-lg">
        <div className="bg-white py-8 px-4 shadow-xl shadow-slate-200/50 sm:rounded-2xl sm:px-10 border border-slate-200/80">
          {/* Role Selection Tabs */}
          <div className="mb-6">
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-500 mb-2">
              Select Your Learning Role
            </label>
            <div className="grid grid-cols-2 gap-2 p-1 bg-slate-100 rounded-xl border border-slate-200">
              <button
                type="button"
                id="role-student-btn"
                onClick={() => {
                  setRole("ROLE_STUDENT");
                  setErrorMessage(null);
                }}
                className={`flex items-center justify-center gap-2 py-2.5 px-3 rounded-lg text-sm font-semibold transition-all ${
                  role === "ROLE_STUDENT"
                    ? "bg-white text-blue-600 shadow-sm border border-slate-200"
                    : "text-slate-600 hover:text-slate-900"
                }`}
              >
                <span>🎓</span>
                <span>Student</span>
              </button>
              <button
                type="button"
                id="role-mentor-btn"
                onClick={() => {
                  setRole("ROLE_MENTOR");
                  setErrorMessage(null);
                }}
                className={`flex items-center justify-center gap-2 py-2.5 px-3 rounded-lg text-sm font-semibold transition-all ${
                  role === "ROLE_MENTOR"
                    ? "bg-white text-blue-600 shadow-sm border border-slate-200"
                    : "text-slate-600 hover:text-slate-900"
                }`}
              >
                <span>🛡️</span>
                <span>Mentor</span>
              </button>
            </div>
          </div>

          {/* Mentor info note */}
          {role === "ROLE_MENTOR" && (
            <div className="mb-6 rounded-xl bg-blue-50 border border-blue-200 p-4 text-xs text-blue-800 flex items-start gap-2.5">
              <span className="text-base leading-none">ℹ️</span>
              <div>
                <p className="font-semibold text-blue-900">Mentor Verification Protocol</p>
                <p className="mt-0.5 text-blue-700 leading-relaxed">
                  Mentor applications undergo administrative verification. Once submitted, your account will enter
                  a pending state while your professional credentials are confirmed.
                </p>
              </div>
            </div>
          )}

          {/* Error Banner */}
          {errorMessage && (
            <div
              className={`mb-6 rounded-xl p-4 text-sm flex items-start gap-3 border ${
                isDuplicateEmail
                  ? "bg-amber-50 border-amber-300 text-amber-900"
                  : "bg-red-50 border-red-300 text-red-900"
              }`}
            >
              <span className="text-base leading-none">{isDuplicateEmail ? "⚠️" : "❌"}</span>
              <div className="flex-1">
                <p className="font-semibold">{isDuplicateEmail ? "Email Already Registered" : "Registration Failed"}</p>
                <p className="mt-0.5 text-xs opacity-90">{errorMessage}</p>
                {isDuplicateEmail && (
                  <Link
                    href="/login"
                    className="inline-block mt-2 text-xs font-semibold text-amber-900 underline hover:no-underline"
                  >
                    Go to Login page &rarr;
                  </Link>
                )}
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Display Name */}
            <div>
              <label htmlFor="displayName" className="block text-xs font-semibold text-slate-700">
                Full Name
              </label>
              <div className="mt-1">
                <input
                  id="displayName"
                  name="displayName"
                  type="text"
                  autoComplete="name"
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  placeholder="e.g. Aarav Sharma"
                  className={`block w-full px-3.5 py-2.5 rounded-xl border text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition-all ${
                    fieldErrors.displayName ? "border-red-500 bg-red-50/20" : "border-slate-300 bg-white"
                  }`}
                />
                {fieldErrors.displayName && (
                  <p className="mt-1 text-xs text-red-600 font-medium">{fieldErrors.displayName}</p>
                )}
              </div>
            </div>

            {/* Email Address */}
            <div>
              <label htmlFor="email" className="block text-xs font-semibold text-slate-700">
                Email Address
              </label>
              <div className="mt-1">
                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@example.com"
                  className={`block w-full px-3.5 py-2.5 rounded-xl border text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition-all ${
                    fieldErrors.email ? "border-red-500 bg-red-50/20" : "border-slate-300 bg-white"
                  }`}
                />
                {fieldErrors.email && (
                  <p className="mt-1 text-xs text-red-600 font-medium">{fieldErrors.email}</p>
                )}
              </div>
            </div>

            {/* Password */}
            <div>
              <div className="flex justify-between items-center">
                <label htmlFor="password" className="block text-xs font-semibold text-slate-700">
                  Password
                </label>
                {strength.label && (
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${strength.color}`}>
                    {strength.label}
                  </span>
                )}
              </div>
              <div className="mt-1 relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="new-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="At least 8 characters"
                  className={`block w-full px-3.5 py-2.5 pr-10 rounded-xl border text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition-all ${
                    fieldErrors.password ? "border-red-500 bg-red-50/20" : "border-slate-300 bg-white"
                  }`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center text-xs text-slate-400 hover:text-slate-600 font-medium"
                >
                  {showPassword ? "Hide" : "Show"}
                </button>
              </div>
              {fieldErrors.password && (
                <p className="mt-1 text-xs text-red-600 font-medium">{fieldErrors.password}</p>
              )}
            </div>

            {/* Mentor-Specific Fields */}
            {role === "ROLE_MENTOR" && (
              <>
                <div>
                  <label htmlFor="linkedInUrl" className="block text-xs font-semibold text-slate-700">
                    LinkedIn Profile URL <span className="text-red-500">*</span>
                  </label>
                  <div className="mt-1">
                    <input
                      id="linkedInUrl"
                      name="linkedInUrl"
                      type="url"
                      value={linkedInUrl}
                      onChange={(e) => setLinkedInUrl(e.target.value)}
                      placeholder="https://linkedin.com/in/yourprofile"
                      className={`block w-full px-3.5 py-2.5 rounded-xl border text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition-all ${
                        fieldErrors.linkedInUrl ? "border-red-500 bg-red-50/20" : "border-slate-300 bg-white"
                      }`}
                    />
                    {fieldErrors.linkedInUrl && (
                      <p className="mt-1 text-xs text-red-600 font-medium">{fieldErrors.linkedInUrl}</p>
                    )}
                  </div>
                </div>

                <div>
                  <label htmlFor="bio" className="block text-xs font-semibold text-slate-700">
                    Professional Experience & Bio <span className="text-red-500">*</span>
                  </label>
                  <div className="mt-1">
                    <textarea
                      id="bio"
                      name="bio"
                      rows={3}
                      value={bio}
                      onChange={(e) => setBio(e.target.value)}
                      placeholder="Summarize your engineering background, key technical domains, and mentorship interest..."
                      className={`block w-full px-3.5 py-2.5 rounded-xl border text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition-all ${
                        fieldErrors.bio ? "border-red-500 bg-red-50/20" : "border-slate-300 bg-white"
                      }`}
                    />
                    {fieldErrors.bio && (
                      <p className="mt-1 text-xs text-red-600 font-medium">{fieldErrors.bio}</p>
                    )}
                  </div>
                </div>
              </>
            )}

            <div className="pt-2">
              <button
                type="submit"
                id="signup-submit-btn"
                disabled={isLoading}
                className="w-full flex justify-center items-center py-3 px-4 border border-transparent rounded-xl shadow-md shadow-blue-500/25 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              >
                {isLoading ? (
                  <span className="flex items-center gap-2">
                    <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                    </svg>
                    Creating Account...
                  </span>
                ) : role === "ROLE_MENTOR" ? (
                  "Submit Mentor Application"
                ) : (
                  "Create Student Account"
                )}
              </button>
            </div>
          </form>

          <div className="mt-6 text-center text-xs text-slate-500">
            By signing up, you agree to CodeConnect&apos;s Terms of Service and Code of Conduct.
          </div>
        </div>
      </div>
    </div>
  );
}
