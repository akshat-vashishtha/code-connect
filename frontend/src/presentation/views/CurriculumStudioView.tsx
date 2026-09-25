"use client";

import React from "react";
import { AppShell } from "@/presentation/organisms/AppShell";

/**
 * Clean Curriculum Studio view shell — blank screen inside AppShell.
 */
export const CurriculumStudioView: React.FC = () => {
  return (
    <AppShell trackTitle="Curriculum Studio" trackHref="/mentor/curriculum-studio" footholdTitle="Studio">
      <main className="flex-1 p-8 max-w-4xl mx-auto w-full">
        <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-xs min-h-[400px]" />
      </main>
    </AppShell>
  );
};
