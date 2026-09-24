import React from "react";
import Link from "next/link";
import {
  User,
  Mail,
  Lock,
  Linkedin,
  FileText,
  Sparkles,
  ArrowRight,
  ArrowLeft,
  CheckCircle2,
  BookOpen,
  GraduationCap,
  Layers,
  Users,
  Compass,
  Shield,
} from "lucide-react";
import { FormInput } from "@/presentation/atoms/FormInput";
import { Button } from "@/presentation/atoms/Button";
import { AlertBanner } from "@/presentation/atoms/AlertBanner";
import { SignupControllerResult } from "@/controller/useSignupController";

export interface SignupFormViewProps {
  readonly controller: SignupControllerResult;
}

/**
 * Presentation view for User Registration.
 * Features a dual-pane layout: learner mission and story preview on left,
 * focused registration card with prominent Back navigation on right.
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

  const isMentor = formData.role === "ROLE_MENTOR";

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col lg:grid lg:grid-cols-12 font-sans selection:bg-blue-500 selection:text-white relative">
      {/* ========================================================================= */}
      {/* LEFT COLUMN: Mission Narrative & Story-First Mental Models (Desktop Only) */}
      {/* ========================================================================= */}
      <div className="hidden lg:flex lg:col-span-5 xl:col-span-6 relative flex-col justify-between p-12 overflow-hidden bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950 border-r border-slate-800/80">
        {/* Ambient Glow Orbs */}
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

        {/* Center: Mission & Story Concept Showcase */}
        <div className="relative z-10 my-auto py-8 flex flex-col gap-6 max-w-lg">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-blue-500/10 text-blue-400 border border-blue-500/20 mb-4">
              <Sparkles className="w-3.5 h-3.5" />
              Fear-Free Java &amp; DSA Mastery
            </div>
            <h1 className="text-3xl xl:text-4xl font-extrabold text-white tracking-tight leading-tight">
              Master Java &amp; DSA Through Stories, Not Cold Compiler Walls.
            </h1>
            <p className="mt-3 text-sm text-slate-400 leading-relaxed">
              Step onto a progressive mountain climb where concepts are introduced through intuitive real-world stories before writing code, backed by an empathetic 3-tier support safety net.
            </p>
          </div>

          {/* Pedagogical Showcase: The Solo Mountain Climb Journey */}
          <div className="rounded-2xl bg-slate-900/90 border border-slate-800 shadow-2xl p-6 backdrop-blur-xl space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800 text-xs">
              <span className="font-semibold text-slate-200 flex items-center gap-2">
                <Compass className="w-4 h-4 text-blue-400" />
                The Solo Mountain Climb Journey
              </span>
              <span className="text-blue-400 font-mono text-[11px] px-2 py-0.5 rounded-full bg-blue-500/10 border border-blue-500/20">
                Fear-Free Progression
              </span>
            </div>

            {/* 3 Step Interactive Progress Preview */}
            <div className="space-y-3">
              <div className="p-3.5 rounded-xl bg-slate-950/70 border border-slate-800/80 flex items-start gap-3">
                <div className="w-7 h-7 rounded-lg bg-blue-500/10 border border-blue-500/20 text-blue-400 flex items-center justify-center shrink-0 font-bold text-xs mt-0.5">
                  1
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white">Physical Real-World Intuition First</h4>
                  <p className="text-[11px] text-slate-400 mt-0.5 leading-relaxed">
                    Pointers, circular queues, and binary trees visualized through tangible physical mechanisms before touching syntax.
                  </p>
                </div>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-950/70 border border-slate-800/80 flex items-start gap-3">
                <div className="w-7 h-7 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 flex items-center justify-center shrink-0 font-bold text-xs mt-0.5">
                  2
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white">Private In-Browser Java 21 Sandbox</h4>
                  <p className="text-[11px] text-slate-400 mt-0.5 leading-relaxed">
                    Immediate execution feedback without compiler wall frustration or public leaderboard anxiety.
                  </p>
                </div>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-950/70 border border-slate-800/80 flex items-start gap-3">
                <div className="w-7 h-7 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center shrink-0 font-bold text-xs mt-0.5">
                  3
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white">Empathetic 3-Tier Safety Net</h4>
                  <p className="text-[11px] text-slate-400 mt-0.5 leading-relaxed">
                    Instant Socratic AI guidance &rarr; Classmate doubt channels &rarr; 1-on-1 Senior Staff reviews.
                  </p>
                </div>
              </div>
            </div>

            {/* Curriculum Tracks Pills */}
            <div className="pt-2 border-t border-slate-800/80 flex items-center justify-between text-[11px] text-slate-400">
              <span className="flex items-center gap-1.5 text-slate-300">
                <Layers className="w-3.5 h-3.5 text-blue-400" />
                <span>4 Curriculum Tracks</span>
              </span>
              <span className="text-slate-500">Java 21 &bull; DSA &bull; Design &bull; Clean Arch</span>
            </div>
          </div>
        </div>

        {/* Bottom Trust Badge */}
        <div className="relative z-10 flex items-center gap-2 text-xs text-slate-500">
          <Shield className="w-4 h-4 text-blue-400" />
          <span>Fear-Free Solo Learning Environment &bull; Zero Public Ranking Pressure</span>
        </div>
      </div>

      {/* ========================================================================= */}
      {/* RIGHT COLUMN: Form Presentation (Clean, Focused, Responsive)              */}
      {/* ========================================================================= */}
      <div className="lg:col-span-7 xl:col-span-6 bg-slate-50 flex flex-col justify-center items-center py-10 px-4 sm:px-8 lg:px-12 relative overflow-y-auto">
        {/* Subtle Background Pattern */}
        <div className="absolute inset-0 bg-grid-pattern opacity-60 pointer-events-none" />

        {/* Explicit Back to Home & Brand Navigation for Mobile & Desktop */}
        <div className="w-full max-w-[460px] mb-6 flex items-center justify-between relative z-10">
          <div className="lg:hidden">
            <Link
              href="/"
              className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-slate-900 bg-white px-3 py-1.5 rounded-xl border border-slate-200 shadow-sm transition-all hover:-translate-x-0.5"
            >
              <ArrowLeft className="w-3.5 h-3.5 text-slate-500" />
              <span>Back to Home</span>
            </Link>
          </div>

          <Link href="/login" className="text-xs font-semibold text-blue-600 hover:text-blue-700 hover:underline ml-auto">
            Already registered? Sign In &rarr;
          </Link>
        </div>

        {/* Card Container (Strictly bounded to max-w-[460px] to prevent horizontal stretching) */}
        <div className="w-full max-w-[460px] relative z-10">
          <div className="bg-white/95 backdrop-blur-md rounded-2xl shadow-xl shadow-slate-200/60 border border-slate-200/90 p-6 sm:p-8">
            {/* Form Title & Subtitle */}
            <div className="mb-6">
              <h2 className="text-2xl font-bold tracking-tight text-slate-900">
                Create your account
              </h2>
              <p className="mt-1 text-xs text-slate-500">
                Already registered?{" "}
                <Link
                  href="/login"
                  className="font-semibold text-blue-600 hover:text-blue-700 hover:underline transition-colors"
                >
                  Sign in to your account
                </Link>
              </p>
            </div>

            {/* Role Selection: Interactive Dual Card Picker */}
            <div className="mb-6">
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2.5">
                Choose Your Role
              </label>
              <div className="grid grid-cols-2 gap-3">
                {/* Student Card Option */}
                <button
                  type="button"
                  onClick={() => setFormData({ role: "ROLE_STUDENT" })}
                  className={`p-3.5 rounded-xl border text-left transition-all duration-200 cursor-pointer flex flex-col justify-between ${
                    !isMentor
                      ? "border-blue-600 bg-blue-50/50 ring-2 ring-blue-500/20 shadow-sm"
                      : "border-slate-200 hover:border-slate-300 bg-white"
                  }`}
                >
                  <div className="flex items-center justify-between w-full mb-2">
                    <span className="w-7 h-7 rounded-lg bg-blue-100 text-blue-700 flex items-center justify-center text-sm font-semibold">
                      <BookOpen className="w-4 h-4" />
                    </span>
                    {!isMentor && <CheckCircle2 className="w-4 h-4 text-blue-600" />}
                  </div>
                  <div>
                    <span className="block text-xs font-bold text-slate-900">Student</span>
                    <span className="block text-[11px] text-slate-500 mt-0.5 leading-snug">
                      Curriculum tracks &amp; Socratic AI
                    </span>
                  </div>
                </button>

                {/* Mentor Card Option */}
                <button
                  type="button"
                  onClick={() => setFormData({ role: "ROLE_MENTOR" })}
                  className={`p-3.5 rounded-xl border text-left transition-all duration-200 cursor-pointer flex flex-col justify-between ${
                    isMentor
                      ? "border-blue-600 bg-blue-50/50 ring-2 ring-blue-500/20 shadow-sm"
                      : "border-slate-200 hover:border-slate-300 bg-white"
                  }`}
                >
                  <div className="flex items-center justify-between w-full mb-2">
                    <span className="w-7 h-7 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center text-sm font-semibold">
                      <GraduationCap className="w-4 h-4" />
                    </span>
                    {isMentor && <CheckCircle2 className="w-4 h-4 text-blue-600" />}
                  </div>
                  <div>
                    <span className="block text-xs font-bold text-slate-900">Mentor</span>
                    <span className="block text-[11px] text-slate-500 mt-0.5 leading-snug">
                      Review queue &amp; escalations
                    </span>
                  </div>
                </button>
              </div>

              <p className="mt-2 text-[11px] text-slate-500 italic">
                {isMentor
                  ? "Requires administrative review before gaining mentor verification and escalation desk access."
                  : "Immediate access to Java curriculum tracks, Socratic AI coaching, and peer chats."}
              </p>
            </div>

            {/* Error Notification Banner */}
            {errorMessage && (
              <div className="mb-5">
                <AlertBanner
                  variant={isDuplicateEmail ? "warning" : "error"}
                  title={isDuplicateEmail ? "Account Already Exists" : "Registration Issue"}
                  message={errorMessage}
                />
              </div>
            )}

            {/* Registration Form */}
            <form onSubmit={handleSubmit} className="space-y-4">
              <FormInput
                id="displayName"
                name="displayName"
                label="Full Name"
                autoComplete="name"
                icon={<User className="w-4 h-4" />}
                value={formData.displayName}
                onChange={(e) => setFormData({ displayName: e.target.value })}
                placeholder="e.g. Alex Morgan"
                error={fieldErrors.displayName}
                disabled={isLoading}
              />

              <FormInput
                id="email"
                name="email"
                type="email"
                label="Email Address"
                autoComplete="email"
                icon={<Mail className="w-4 h-4" />}
                value={formData.email}
                onChange={(e) => setFormData({ email: e.target.value })}
                placeholder="alex.morgan@company.com"
                error={fieldErrors.email}
                disabled={isLoading}
              />

              <FormInput
                id="password"
                name="password"
                label="Password"
                autoComplete="new-password"
                showTogglePassword
                icon={<Lock className="w-4 h-4" />}
                value={formData.password}
                onChange={(e) => setFormData({ password: e.target.value })}
                placeholder="At least 8 characters"
                helperText="Must contain at least 8 characters."
                error={fieldErrors.password}
                disabled={isLoading}
              />

              {/* Dynamic Mentor-Specific Fields */}
              {isMentor && (
                <div className="space-y-4 pt-2 border-t border-slate-100">
                  <FormInput
                    id="linkedInUrl"
                    name="linkedInUrl"
                    type="url"
                    label="LinkedIn Profile URL"
                    icon={<Linkedin className="w-4 h-4" />}
                    value={formData.linkedInUrl}
                    onChange={(e) => setFormData({ linkedInUrl: e.target.value })}
                    placeholder="https://linkedin.com/in/username"
                    error={fieldErrors.linkedInUrl}
                    disabled={isLoading}
                  />

                  <div>
                    <label
                      htmlFor="bio"
                      className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5"
                    >
                      Professional Experience &amp; Bio
                    </label>
                    <div className="relative">
                      <div className="absolute top-3 left-3 text-slate-400 pointer-events-none">
                        <FileText className="w-4 h-4" />
                      </div>
                      <textarea
                        id="bio"
                        name="bio"
                        rows={3}
                        value={formData.bio}
                        onChange={(e) => setFormData({ bio: e.target.value })}
                        placeholder="Describe your Java background, engineering leadership, and mentoring focus (min 20 characters)..."
                        disabled={isLoading}
                        className={`w-full pl-9 pr-3.5 py-2.5 rounded-xl border text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-4 transition-all resize-none ${
                          fieldErrors.bio
                            ? "border-red-500 focus:ring-red-500/20 bg-red-50/20"
                            : "border-slate-200 hover:border-slate-300 focus:border-blue-600 focus:ring-blue-500/15 bg-white"
                        }`}
                      />
                    </div>
                    {fieldErrors.bio && (
                      <p className="mt-1 text-xs text-red-600 font-medium">{fieldErrors.bio}</p>
                    )}
                  </div>
                </div>
              )}

              {/* Submit CTA */}
              <div className="pt-2">
                <Button
                  type="submit"
                  id="signup-submit-btn"
                  variant="primary"
                  size="lg"
                  className="w-full py-3.5 font-bold tracking-tight text-sm shadow-lg shadow-blue-500/25"
                  isLoading={isLoading}
                  loadingText="Creating Account..."
                  rightIcon={!isLoading ? <ArrowRight className="w-4 h-4" /> : undefined}
                >
                  {isMentor ? "Apply for Mentor Verification" : "Start Your Learning Climb"}
                </Button>
              </div>
            </form>

            <div className="mt-6 pt-5 border-t border-slate-100 text-center">
              <p className="text-[11px] text-slate-400">
                Free to start &bull; No credit card required &bull; Learn at your own pace
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
