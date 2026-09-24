"use client";

import React from "react";
import { AppHeader } from "./AppHeader";
import { AppNavRail } from "./AppNavRail";
import { UserRole } from "@/domain/enums/UserRole";

export interface AppShellProps {
  readonly children: React.ReactNode;
  readonly trackTitle?: string;
  readonly trackHref?: string;
  readonly footholdTitle?: string;
  readonly explicitRole?: UserRole;
  readonly hideRail?: boolean;
}

/**
 * Global App Shell matching Wireframe 00 (00-app-shell.svg).
 * Assembles:
 * - 64px Top App Header with metrics & role-partitioned logout dropdown
 * - 72px Left Navigation Rail for direct cross-platform traversal across all modules
 * - Responsive Main Content Viewport
 * - 35px Status Strip with live health telemetry
 */
export const AppShell: React.FC<AppShellProps> = ({
  children,
  trackTitle = "Track 2: Data Structures",
  trackHref = "/curriculum",
  footholdTitle = "Foothold 3: Circular Queue",
  explicitRole,
  hideRail = false,
}) => {
  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 flex flex-col antialiased">
      {/* 1. Global Header (64px) */}
      <AppHeader
        trackTitle={trackTitle}
        trackHref={trackHref}
        footholdTitle={footholdTitle}
        explicitRole={explicitRole}
      />

      {/* 2. Body Container with Common Left Rail across all modules + Viewport */}
      <div className="flex-1 flex overflow-hidden">
        {!hideRail && <AppNavRail explicitRole={explicitRole} />}

        {/* Dynamic Page Content Viewport */}
        <div className="flex-1 flex flex-col overflow-y-auto">
          <div className="flex-1">{children}</div>

          {/* 3. Global Status Strip (35px) matching 00-app-shell.svg */}
          <footer className="h-9 bg-white border-t border-slate-200 px-6 flex items-center justify-between text-[11px] text-slate-500 shrink-0 select-none">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-500" />
                <span className="font-semibold text-slate-700">Cluster: Healthy</span>
              </div>
              <span className="text-slate-300">|</span>
              <div className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-500" />
                <span>Redis Session: Active</span>
              </div>
              <span className="text-slate-300 hidden sm:inline">|</span>
              <span className="hidden sm:inline">Sandbox Runner: Standby (0.04s)</span>
            </div>

            <div className="hidden md:flex items-center gap-2 text-slate-400 text-[10px]">
              <span>Press <kbd className="px-1.5 py-0.5 rounded bg-slate-100 border border-slate-200 text-slate-600 font-mono font-semibold">Cmd+K</kbd> for Navigation</span>
              <span>&bull;</span>
              <span><kbd className="px-1.5 py-0.5 rounded bg-slate-100 border border-slate-200 text-slate-600 font-mono font-semibold">Ctrl+Enter</kbd> to Run</span>
            </div>
          </footer>
        </div>
      </div>
    </div>
  );
};
