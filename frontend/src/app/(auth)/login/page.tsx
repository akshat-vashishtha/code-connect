"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { login, ApiError } from "@/lib/api/auth";

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isBanned, setIsBanned] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const validateForm = (): boolean => {
    const errors: Record<string, string> = {};

    if (!email.trim()) {
      errors.email = "Email address is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      errors.email = "Please enter a valid email address";
    }

    if (!password) {
      errors.password = "Password is required";
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setIsBanned(false);

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      const response = await login({
        email: email.trim(),
        password,
      });

      if (response.success && response.data) {
        const user = response.data;
        if (user.role === "ROLE_MENTOR" && user.status === "PENDING_APPROVAL") {
          router.push("/pending-approval");
        } else {
          router.push("/?authenticated=true");
        }
      }
    } catch (err: unknown) {
      if (err instanceof ApiError) {
        if (err.status === 403) {
          setIsBanned(true);
          setErrorMessage(
            err.problemDetail?.detail ||
              "Your account has been suspended by an administrator. Please reach out to support."
          );
        } else if (err.status === 401) {
          setErrorMessage("Invalid email or password. Please verify your credentials.");
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
          Sign in to your account
        </h2>
        <p className="mt-2 text-center text-sm text-slate-600">
          Don&apos;t have an account yet?{" "}
          <Link href="/signup" className="font-medium text-blue-600 hover:text-blue-500 transition-colors">
            Create an account
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow-xl shadow-slate-200/50 sm:rounded-2xl sm:px-10 border border-slate-200/80">
          {/* Error Banner */}
          {errorMessage && (
            <div
              className={`mb-6 rounded-xl p-4 text-sm flex items-start gap-3 border ${
                isBanned
                  ? "bg-amber-50 border-amber-300 text-amber-900"
                  : "bg-red-50 border-red-300 text-red-900"
              }`}
            >
              <span className="text-base leading-none">{isBanned ? "⚠️" : "❌"}</span>
              <div className="flex-1">
                <p className="font-semibold">{isBanned ? "Account Suspended" : "Authentication Failed"}</p>
                <p className="mt-0.5 text-xs opacity-90">{errorMessage}</p>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
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
              </div>
              <div className="mt-1 relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
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

            <div className="pt-2">
              <button
                type="submit"
                id="login-submit-btn"
                disabled={isLoading}
                className="w-full flex justify-center items-center py-3 px-4 border border-transparent rounded-xl shadow-md shadow-blue-500/25 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              >
                {isLoading ? (
                  <span className="flex items-center gap-2">
                    <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                    </svg>
                    Signing In...
                  </span>
                ) : (
                  "Sign In"
                )}
              </button>
            </div>
          </form>

          <div className="mt-6 text-center text-xs text-slate-500">
            Protected by reactive Redis session management &amp; anti-enumeration safeguards.
          </div>
        </div>
      </div>
    </div>
  );
}
