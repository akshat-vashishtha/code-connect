"use client";

import React from "react";
import { AppShell } from "@/presentation/organisms/AppShell";

/**
 * Clean Peers view shell — blank screen inside AppShell.
 */
export default function PeersPage() {
  return (
    <AppShell trackTitle="Peers" trackHref="/peers" footholdTitle="Workspace">
      <main className="flex-1 p-8 max-w-4xl mx-auto w-full">
        <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-xs min-h-[400px]" />
      </main>
    </AppShell>
  );
}
