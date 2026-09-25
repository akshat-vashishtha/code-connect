"use client";

import React, { useEffect } from "react";
import { useRouter } from "next/navigation";
import { LandingPageView } from "@/presentation/views/LandingPageView";
import { AscentDashboardView } from "@/presentation/views/AscentDashboardView";
import { useSessionController } from "@/controller/useSessionController";

/**
 * Dynamic Entry Point for CodeConnect.
 * - Unauthenticated Visitors: Renders clean Public Product Landing Page
 * - Authenticated Students: Renders clean Dashboard with Welcome Message (Zero static clutter)
 * - Authenticated Mentors/Admins: Routes to respective workspace
 */
export default function Home() {
  const router = useRouter();
  const { user, isLoading, isAuthenticated } = useSessionController();

  useEffect(() => {
    if (!isLoading && isAuthenticated && user) {
      if (user.role === "ROLE_ADMIN") {
        router.replace("/admin/mentors");
      } else if (user.role === "ROLE_MENTOR") {
        if (user.status === "PENDING_APPROVAL") {
          router.replace("/pending-approval");
        } else {
          router.replace("/mentor/desk");
        }
      }
    }
  }, [isLoading, isAuthenticated, user, router]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#f8fafc] flex items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center text-white font-bold animate-pulse">
            CC
          </div>
          <span className="text-xs text-slate-500 font-medium">Loading CodeConnect...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated || !user) {
    return <LandingPageView />;
  }

  return <AscentDashboardView />;
}
