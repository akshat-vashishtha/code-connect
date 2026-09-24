import React from "react";
import Link from "next/link";
import {
  Mail,
  Lock,
  ArrowRight,
  ArrowLeft,
  CheckCircle2,
  Sparkles,
  Compass,
  Layers,
  Shield,
} from "lucide-react";
import { FormInput } from "@/presentation/atoms/FormInput";
import { Button } from "@/presentation/atoms/Button";
import { AlertBanner } from "@/presentation/atoms/AlertBanner";
import { LoginControllerResult } from "@/controller/useLoginController";

export interface LoginFormViewProps {
  readonly controller: LoginControllerResult;
}

/**
 * Presentation view for User Login.
 * Features dual-pane layout: learner milestone progress on left,
 * focused card with explicit Back navigation on right.
 */
export const LoginFormView: React.FC<LoginFormViewProps> = ({ controller }) => {
  const {
    formData,
    isLoading,
    errorMessage,
    fieldErrors,
    setFormData,
    handleSubmit,
  } = controller;

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col lg:grid lg:grid-cols-12 font-sans selection:bg-blue-500 selection:text-white relative">
      {/* ========================================================================= */}
      {/* LEFT COLUMN: Student Progress & Story Mental Models (Desktop Only)        */}
      {/* ========================================================================= */}
      <div className="hidden lg:flex lg:col-span-5 xl:col-span-6 relative flex-col justify-between p-12 overflow-hidden bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950 border-r border-slate-800/80">
        {/* Ambient Glow */}
        <div className="absolute top-0 -left-20 w-96 h-96 bg-blue-600/15 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-10 right-0 w-80 h-80 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

        {/* Top: Brand Header + Back Link */}
        <div className="relative z-10 flex items-center justify-between">
          <Link href="/" className="inline-flex items-center gap-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-500 flex items-center justify-center text-white font-extrabold text-xl shadow-lg shadow-blue-500/30 group-hover:scale-105 transition-transform">
              CC
            </div>
            <div>
              <span className="text-2xl font-bold tracking-tight text-white">
                Code<span className="text-blue-400">Connect</span>
              </span>
              <span className="block text-xs font-medium text-slate-400">The Solo Mountain Climb</span>
            </div>
          </Link>

          <Link
            href="/"
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-400 hover:text-white bg-slate-900/60 hover:bg-slate-800 border border-slate-800 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Back to Home</span>
          </Link>
        </div>

        {/* Center: Mountain Climb Footholds */}
        <div className="relative z-10 my-auto py-8 flex flex-col gap-6 max-w-lg">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-blue-500/10 text-blue-400 border border-blue-500/20 mb-4">
              <Sparkles className="w-3.5 h-3.5" />
              Welcome Back, Climber
            </div>
            <h1 className="text-3xl xl:text-4xl font-extrabold text-white tracking-tight leading-tight">
              Resume Your Mountain Climb to Java Mastery.
            </h1>
            <p className="mt-3 text-sm text-slate-400 leading-relaxed">
              Your active coding challenges, story analogies, and classmate doubt chats are waiting right where you left off.
            </p>
          </div>

          {/* Interactive Progress Footholds Card */}
          <div className="rounded-2xl bg-slate-900/90 border border-slate-800 shadow-2xl p-5 backdrop-blur-xl space-y-3.5">
            <div className="text-xs font-semibold uppercase tracking-wider text-slate-400 flex items-center justify-between pb-2 border-b border-slate-800">
              <span className="flex items-center gap-1.5 text-slate-300">
                <Compass className="w-3.5 h-3.5 text-blue-400" />
                Active Footholds
              </span>
              <span className="text-blue-400 font-mono text-[11px]">Track: Core Data Structures</span>
            </div>

            <div className="space-y-2.5">
              <div className="flex items-center justify-between text-xs p-3 rounded-xl bg-slate-800/60 border border-slate-700/50">
                <div className="flex items-center gap-2.5">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span className="text-slate-200 font-medium">Story 1: The Airport Baggage Carousel (Queues)</span>
                </div>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                  Completed
                </span>
              </div>

              <div className="flex items-center justify-between text-xs p-3 rounded-xl bg-slate-800/60 border border-slate-700/50">
                <div className="flex items-center gap-2.5">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span className="text-slate-200 font-medium">Story 2: The Package Sorting Hub (Arrays)</span>
                </div>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                  Completed
                </span>
              </div>

              <div className="flex items-center justify-between text-xs p-3 rounded-xl bg-blue-500/10 border border-blue-500/30">
                <div className="flex items-center gap-2.5">
                  <div className="w-4 h-4 rounded-full border-2 border-blue-400 border-t-transparent animate-spin shrink-0" />
                  <span className="text-blue-200 font-medium">Story 3: Circular Buffer Synchronization</span>
                </div>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-blue-500/20 text-blue-300 border border-blue-500/30">
                  Active Challenge
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Bottom Info */}
        <div className="relative z-10 flex items-center gap-2 text-xs text-slate-500">
          <Shield className="w-4 h-4 text-blue-400" />
          <span>Fear-Free Solo Learning Environment &bull; Zero Public Ranking Pressure</span>
        </div>
      </div>

      {/* ========================================================================= */}
      {/* RIGHT COLUMN: Sign In Form                                               */}
      {/* ========================================================================= */}
      <div className="lg:col-span-7 xl:col-span-6 bg-slate-50 flex flex-col justify-center items-center py-12 px-4 sm:px-8 lg:px-12 relative overflow-y-auto">
        {/* Subtle Background Pattern */}
        <div className="absolute inset-0 bg-grid-pattern opacity-60 pointer-events-none" />

        {/* Explicit Back to Home & Brand Navigation for Mobile Only */}
        <div className="w-full max-w-[440px] mb-6 flex items-center justify-between relative z-10">
          <div className="lg:hidden">
            <Link
              href="/"
              className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-slate-900 bg-white px-3 py-1.5 rounded-xl border border-slate-200 shadow-sm transition-all hover:-translate-x-0.5"
            >
              <ArrowLeft className="w-3.5 h-3.5 text-slate-500" />
              <span>Back to Home</span>
            </Link>
          </div>

          <Link href="/signup" className="text-xs font-semibold text-blue-600 hover:text-blue-700 hover:underline ml-auto">
            Create an Account &rarr;
          </Link>
        </div>

        {/* Card Container (Strictly bounded to max-w-[440px]) */}
        <div className="w-full max-w-[440px] relative z-10">
          <div className="bg-white/95 backdrop-blur-md rounded-2xl shadow-xl shadow-slate-200/60 border border-slate-200/90 p-6 sm:p-8">
            {/* Header */}
            <div className="mb-6">
              <h2 className="text-2xl font-bold tracking-tight text-slate-900">
                Sign in to your account
              </h2>
              <p className="mt-1 text-xs text-slate-500">
                Don&apos;t have an account yet?{" "}
                <Link
                  href="/signup"
                  className="font-semibold text-blue-600 hover:text-blue-700 hover:underline transition-colors"
                >
                  Create an account free
                </Link>
              </p>
            </div>

            {/* Error Banner */}
            {errorMessage && (
              <div className="mb-5">
                <AlertBanner variant="error" title="Sign In Unsuccessful" message={errorMessage} />
              </div>
            )}

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-4">
              <FormInput
                id="email"
                name="email"
                type="email"
                label="Email Address"
                autoComplete="email"
                icon={<Mail className="w-4 h-4" />}
                value={formData.email}
                onChange={(e) => setFormData({ email: e.target.value })}
                placeholder="name@example.com"
                error={fieldErrors.email}
                disabled={isLoading}
              />

              <FormInput
                id="password"
                name="password"
                label="Password"
                autoComplete="current-password"
                showTogglePassword
                icon={<Lock className="w-4 h-4" />}
                value={formData.password}
                onChange={(e) => setFormData({ password: e.target.value })}
                placeholder="Enter your password"
                error={fieldErrors.password}
                disabled={isLoading}
              />

              <div className="pt-2">
                <Button
                  type="submit"
                  id="login-submit-btn"
                  variant="primary"
                  size="lg"
                  className="w-full py-3.5 font-bold tracking-tight text-sm shadow-lg shadow-blue-500/25"
                  isLoading={isLoading}
                  loadingText="Signing In..."
                  rightIcon={!isLoading ? <ArrowRight className="w-4 h-4" /> : undefined}
                >
                  Sign In
                </Button>
              </div>
            </form>

            <div className="mt-6 pt-5 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span className="text-slate-400">Learn at your own pace</span>
              <Link href="/signup" className="text-blue-600 font-semibold hover:underline">
                Apply as Mentor &rarr;
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
