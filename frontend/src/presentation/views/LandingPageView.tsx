"use client";

import React from "react";
import Link from "next/link";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

/**
 * Public Landing Page matching exact top hero from screenshot.
 * Strictly excludes:
 * - Physical Intuition Over Dry Theory
 * - The 3-Tier Resolution Ecosystem
 * - Ready to Begin Your Solo Climb?
 */
export const LandingPageView: React.FC = () => {
  return (
    <div className="min-h-screen bg-white text-slate-900 font-sans flex flex-col justify-between selection:bg-blue-100 selection:text-blue-900">
      {/* Public Header */}
      <header className="h-16 bg-white border-b border-slate-100 px-6 sm:px-12 flex items-center justify-between sticky top-0 z-50">
        <Link href="/" className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center text-white font-extrabold text-sm shadow-sm">
            CC
          </div>
          <div>
            <span className="text-base font-bold tracking-tight text-slate-900">
              Code<span className="text-blue-600">Connect</span>
            </span>
            <span className="block text-[10px] text-slate-400 font-medium">The Solo Mountain Climb</span>
          </div>
        </Link>

        <div className="flex items-center gap-4">
          <Link
            href="/login"
            className="text-xs font-semibold text-slate-600 hover:text-slate-900 transition-colors"
            id="landing-signin-btn"
          >
            Sign In
          </Link>
          <Link
            href="/signup?role=student"
            className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-sm transition-all"
            id="landing-signup-btn"
          >
            Start Free Ascent &rarr;
          </Link>
        </div>
      </header>

      {/* Hero Section — EXACT Hero from Screenshot */}
      <main className="flex-1 flex flex-col items-center justify-center text-center px-6 py-16 max-w-4xl mx-auto space-y-8">
        {/* Pill Badge */}
        <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-blue-50 text-blue-700 text-xs font-bold border border-blue-200">
          <VectorIcon name="mountain" size={14} className="text-blue-600" />
          <span>The Solo Mountain Climb &bull; Java 21 LTS Masterclass</span>
        </div>

        {/* Title */}
        <h1 className="text-4xl sm:text-6xl font-black text-slate-900 tracking-tight leading-[1.1]">
          Master Data Structures &amp; Java 21 Through{" "}
          <span className="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
            Physical Mental Models
          </span>
        </h1>

        {/* Subtitle */}
        <p className="text-sm sm:text-base text-slate-600 max-w-2xl leading-relaxed">
          Stop watching passive coding tutorials. Understand circular queues as airport baggage carousels, arrays as postal mailbox grids, and virtual threads as industrial conveyor belts. When blocked, climb through instant Socratic AI clues, peer classmates, and staff mentor code reviews.
        </p>

        {/* Dual CTA Buttons */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-2">
          <Link
            href="/signup?role=student"
            className="w-full sm:w-auto px-7 py-3.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-sm font-bold shadow-md shadow-blue-500/20 transition-all flex items-center justify-center gap-2"
          >
            <VectorIcon name="mountain" size={16} />
            <span>Begin Ascent as Student (Free)</span>
          </Link>
          <Link
            href="/signup?role=mentor"
            className="w-full sm:w-auto px-7 py-3.5 rounded-xl bg-white hover:bg-slate-50 text-slate-700 border border-slate-200 text-sm font-bold shadow-xs transition-all flex items-center justify-center gap-2"
          >
            <VectorIcon name="shield" size={16} className="text-amber-500" />
            <span>Apply as Faculty Mentor</span>
          </Link>
        </div>

        {/* 4 Feature Pill Indicators */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-6 text-slate-600 text-xs font-semibold">
          <div className="flex items-center gap-1.5 justify-center">
            <VectorIcon name="check-circle" size={14} className="text-emerald-500 shrink-0" />
            <span>Isolated Java 21 Sandbox</span>
          </div>
          <div className="flex items-center gap-1.5 justify-center">
            <VectorIcon name="check-circle" size={14} className="text-emerald-500 shrink-0" />
            <span>Anti-Spoiler Socratic AI</span>
          </div>
          <div className="flex items-center gap-1.5 justify-center">
            <VectorIcon name="check-circle" size={14} className="text-emerald-500 shrink-0" />
            <span>Classmate Study Circles</span>
          </div>
          <div className="flex items-center gap-1.5 justify-center">
            <VectorIcon name="check-circle" size={14} className="text-emerald-500 shrink-0" />
            <span>Verified Staff Mentor AST Diffs</span>
          </div>
        </div>
      </main>

      {/* Minimal Footer */}
      <footer className="py-6 text-center text-xs text-slate-400 border-t border-slate-100 bg-white">
        &copy; {new Date().getFullYear()} CodeConnect. All rights reserved.
      </footer>
    </div>
  );
};
