"use client";

import React from "react";
import { AppShell } from "@/presentation/organisms/AppShell";

/**
 * Clean Code Cockpit view shell — blank screen inside AppShell.
 */
export const CodingCockpitView: React.FC = () => {
  return (
    <AppShell trackTitle="Code Cockpit" trackHref="/cockpit" footholdTitle="Sandbox">
      <main className="flex-1 p-8 max-w-4xl mx-auto w-full">
        <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-xs min-h-[400px]" />
      </main>
    </AppShell>
  );
};
