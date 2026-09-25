"use client";

import React from "react";
import { AppShell } from "@/presentation/organisms/AppShell";

/**
 * Clean Mentor Desk view shell — blank screen inside AppShell.
 */
export const MentorDeskView: React.FC = () => {
  return (
    <AppShell trackTitle="Mentor Desk" trackHref="/mentor/desk" footholdTitle="Desk">
      <main className="flex-1 p-8 max-w-4xl mx-auto w-full">
        <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-xs min-h-[400px]" />
      </main>
    </AppShell>
  );
};
