"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { VectorIcon, IconName } from "@/presentation/atoms/VectorIcon";
import { useSessionController } from "@/controller/useSessionController";
import { UserRole } from "@/domain/enums/UserRole";

export interface NavRailItem {
  readonly id: string;
  readonly label: string;
  readonly href: string;
  readonly icon: IconName;
  readonly hasPulse?: boolean;
}

const STUDENT_NAV_ITEMS: readonly NavRailItem[] = [
  { id: "tracks", label: "TRACKS", href: "/", icon: "mountain" },
  { id: "climb", label: "CLIMB", href: "/dashboard", icon: "trending-up" },
  { id: "coach", label: "COACH", href: "/curriculum", icon: "lightbulb" },
  { id: "code", label: "CODE", href: "/cockpit/2.3", icon: "code" },
  { id: "peers", label: "PEERS", href: "/cockpit/2.3", icon: "users", hasPulse: true },
];

const MENTOR_NAV_ITEMS: readonly NavRailItem[] = [
  { id: "desk", label: "DESK", href: "/mentor/desk", icon: "shield" },
  { id: "studio", label: "STUDIO", href: "/mentor/curriculum-studio", icon: "book" },
  { id: "tracks", label: "TRACKS", href: "/", icon: "mountain" },
];

const ADMIN_NAV_ITEMS: readonly NavRailItem[] = [
  { id: "govern", label: "GOVERN", href: "/admin/mentors", icon: "settings" },
  { id: "desk", label: "DESK", href: "/mentor/desk", icon: "shield" },
  { id: "tracks", label: "TRACKS", href: "/", icon: "mountain" },
];

export interface AppNavRailProps {
  readonly className?: string;
  readonly explicitRole?: UserRole;
}

/**
 * Universal 72px Left Navigation Rail matching Wireframe 00 (00-app-shell.svg).
 * Enforces strict role-based module partitioning:
 * - Student: Tracks, Climb, Coach, Code, Peers (Mentor & Admin strictly hidden)
 * - Mentor: Desk, Studio, Tracks (Admin strictly hidden)
 * - Admin: Govern, Desk, Tracks
 *
 * Eliminates disjointed back buttons by serving as the unified cross-module anchor.
 */
export const AppNavRail: React.FC<AppNavRailProps> = ({
  className = "",
  explicitRole,
}) => {
  const pathname = usePathname();
  const { role: sessionRole } = useSessionController();
  const currentRole = explicitRole ?? sessionRole;

  const navItems =
    currentRole === "ROLE_ADMIN"
      ? ADMIN_NAV_ITEMS
      : currentRole === "ROLE_MENTOR"
      ? MENTOR_NAV_ITEMS
      : STUDENT_NAV_ITEMS;

  const isItemActive = (item: NavRailItem): boolean => {
    if (item.href === "/") {
      return pathname === "/";
    }
    if (item.id === "code") {
      return pathname.startsWith("/cockpit");
    }
    if (item.id === "climb") {
      return pathname.startsWith("/dashboard");
    }
    if (item.id === "coach") {
      return pathname.startsWith("/curriculum") && !pathname.includes("/studio");
    }
    if (item.id === "studio") {
      return pathname.includes("/studio");
    }
    if (item.id === "desk") {
      return pathname.startsWith("/mentor/desk") || pathname === "/mentor";
    }
    if (item.id === "govern") {
      return pathname.startsWith("/admin");
    }
    return pathname === item.href || pathname.startsWith(item.href);
  };

  return (
    <aside
      className={`w-18 shrink-0 bg-white border-r border-slate-200 flex flex-col items-center justify-between py-4 select-none z-30 ${className}`}
      aria-label="Platform Sidebar Navigation"
    >
      {/* Top Nav Items */}
      <div className="flex flex-col items-center space-y-3 w-full px-2.5">
        {navItems.map((item) => {
          const active = isItemActive(item);

          return (
            <Link
              key={item.id}
              href={item.href}
              className={`w-12 h-12 rounded-xl flex flex-col items-center justify-center transition-all relative group ${
                active
                  ? "bg-blue-50 border border-blue-200 text-blue-600 shadow-2xs font-bold"
                  : "bg-white hover:bg-slate-50 text-slate-500 hover:text-slate-900 border border-transparent"
              }`}
              title={item.label}
              id={`nav-item-${item.id}`}
            >
              {/* Optional live peer presence pulse */}
              {item.hasPulse && (
                <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-emerald-500 ring-2 ring-white animate-pulse" />
              )}
              <VectorIcon
                name={item.icon}
                size={18}
                strokeWidth={active ? 2.2 : 1.8}
                className={active ? "text-blue-600" : "text-slate-500 group-hover:text-slate-900"}
              />
              <span className="text-[9px] mt-0.5 tracking-tight font-bold">
                {item.label}
              </span>
            </Link>
          );
        })}
      </div>

      {/* Bottom Rail: Subtle Brand Crest & Status (Zero awkward back buttons) */}
      <div className="flex flex-col items-center space-y-1.5 w-full px-2.5 pt-3">
        <div className="w-8 h-[1px] bg-slate-100 mb-1" />
        <Link
          href="/"
          className="w-10 h-10 rounded-xl bg-slate-50 hover:bg-slate-100 text-slate-400 hover:text-blue-600 flex items-center justify-center transition-colors"
          title="CodeConnect Mountain Portal"
        >
          <VectorIcon name="mountain" size={16} strokeWidth={1.7} />
        </Link>
      </div>
    </aside>
  );
};
