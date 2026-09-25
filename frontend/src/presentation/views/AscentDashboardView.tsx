"use client";

import React from "react";
import { AppShell } from "@/presentation/organisms/AppShell";
import { useSessionController } from "@/controller/useSessionController";

/**
 * Clean, Simple Dashboard View for Signed-In User.
 * Strictly displays ONLY the welcome message with user's name inside AppShell.
 */
export const AscentDashboardView: React.FC = () => {
  const { user } = useSessionController();
  const displayName = user?.displayName ?? "User";

  return (
    <AppShell trackTitle="Dashboard" trackHref="/dashboard" footholdTitle="Overview">
      <main className="flex-1 p-8 max-w-4xl mx-auto w-full">
        <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-xs">
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Welcome back, {displayName}!
          </h1>
        </div>
      </main>
    </AppShell>
  );
};
