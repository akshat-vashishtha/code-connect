"use client";

import React, { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { AltitudeBadge } from "@/presentation/atoms/AltitudeBadge";
import { StreakBadge } from "@/presentation/atoms/StreakBadge";
import { BreadcrumbBar } from "@/presentation/molecules/BreadcrumbBar";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { useSessionController } from "@/controller/useSessionController";
import { UserRole } from "@/domain/enums/UserRole";

export interface AppHeaderProps {
  readonly trackTitle?: string;
  readonly trackHref?: string;
  readonly footholdTitle?: string;
  readonly currentAltitude?: number;
  readonly streakDays?: number;
  readonly explicitRole?: UserRole;
  readonly activeLanguage?: "EN" | "HINGLISH";
  readonly onToggleLanguage?: () => void;
  readonly isAuthenticated?: boolean;
  readonly className?: string;
}

/**
 * Global SaaS Navigation Header matching Wireframe 00 (App Shell).
 * Integrates context breadcrumbs, metrics, language mode, and a role-partitioned
 * user profile dropdown with instant Logout capability.
 */
export const AppHeader: React.FC<AppHeaderProps> = ({
  trackTitle = "Track 2: Data Structures",
  trackHref = "/curriculum",
  footholdTitle = "Foothold 3: Circular Queue",
  currentAltitude = 1420,
  streakDays = 5,
  explicitRole,
  activeLanguage = "EN",
  onToggleLanguage,
  isAuthenticated,
  className = "",
}) => {
  const { user, role: sessionRole, logout, isAuthenticated: sessionIsAuthenticated } = useSessionController();
  const currentRole = explicitRole ?? sessionRole;
  const isAuth = isAuthenticated !== undefined ? isAuthenticated : sessionIsAuthenticated;

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [currentLang, setCurrentLang] = useState<"EN" | "HINGLISH">(activeLanguage);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const userName = user?.displayName || (currentRole === "ROLE_ADMIN" ? "System Admin" : currentRole === "ROLE_MENTOR" ? "Lead Mentor" : "Alex Vashishtha");
  const userEmail = user?.email || (currentRole === "ROLE_ADMIN" ? "admin@codeconnect.dev" : currentRole === "ROLE_MENTOR" ? "mentor@codeconnect.dev" : "alex@codeconnect.dev");
  const roleLabel = currentRole === "ROLE_ADMIN" ? "ADMIN" : currentRole === "ROLE_MENTOR" ? "MENTOR" : "STUDENT";
  const userInitials = userName
    .split(" ")
    .map((part) => part[0])
    .join("")
    .substring(0, 2)
    .toUpperCase();

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleToggleLang = () => {
    const next = currentLang === "EN" ? "HINGLISH" : "EN";
    setCurrentLang(next);
    if (onToggleLanguage) {
      onToggleLanguage();
    }
  };

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      await logout();
    } catch {
      // Handled in controller
    } finally {
      setIsDropdownOpen(false);
      setIsLoggingOut(false);
    }
  };

  return (
    <header
      className={`h-16 bg-white border-b border-slate-200 px-4 sm:px-6 flex items-center justify-between sticky top-0 z-50 select-none shadow-[0_1px_3px_rgba(0,0,0,0.03)] ${className}`}
    >
      {/* Brand Logo & Context Breadcrumb */}
      <div className="flex items-center gap-4 sm:gap-6">
        <Link href="/" className="flex items-center gap-3 group">
          <div className="w-9 h-9 rounded-lg bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-extrabold text-sm shadow-md shadow-blue-500/20 group-hover:scale-105 transition-transform">
            CC
          </div>
          <div>
            <span className="text-base font-bold tracking-tight text-slate-900">
              Code<span className="text-blue-600">Connect</span>
            </span>
            <span className="block text-[10px] font-medium text-slate-400">
              The Solo Mountain Climb
            </span>
          </div>
        </Link>

        {/* Context Divider & Breadcrumb (Only when authenticated) */}
        {isAuth && trackTitle && footholdTitle ? (
          <>
            <div className="h-6 w-[1px] bg-slate-200 hidden md:block" />
            <div className="hidden md:block">
              <BreadcrumbBar
                trackTitle={trackTitle}
                trackHref={trackHref}
                footholdTitle={footholdTitle}
                status="ACTIVE"
              />
            </div>
          </>
        ) : !isAuth ? (
          <nav className="hidden md:flex items-center gap-6 pl-4 text-xs font-semibold text-slate-500">
            <Link href="/#pedagogy" className="hover:text-blue-600 transition-colors">
              Physical Models
            </Link>
            <Link href="/#tracks" className="hover:text-blue-600 transition-colors">
              Mountain Tracks
            </Link>
            <Link href="/#resolution" className="hover:text-blue-600 transition-colors">
              3-Tier Resolution
            </Link>
          </nav>
        ) : null}
      </div>

      {/* Header Right Utilities (Altitude, Streak, Language, Profile) */}
      <div className="flex items-center gap-2.5 sm:gap-3">
        {isAuth && currentRole === "ROLE_STUDENT" && (
          <>
            <div className="hidden sm:block">
              <AltitudeBadge currentAltitude={currentAltitude} maxAltitude={4000} />
            </div>

            <div className="hidden md:block">
              <StreakBadge streakDays={streakDays} />
            </div>
          </>
        )}

        {/* Language Mode Toggle */}
        <button
          type="button"
          onClick={handleToggleLang}
          className="inline-flex items-center gap-1 px-2.5 py-1.5 rounded-full border border-slate-200 bg-slate-50 text-slate-700 text-xs font-semibold hover:bg-slate-100 transition-colors"
          title="Toggle Hinglish / English"
        >
          <span className="text-[10px] uppercase font-bold text-slate-400">Lang:</span>
          <span>{currentLang === "HINGLISH" ? "Hinglish" : "English"}</span>
        </button>

        {/* User Profile Dropdown or Guest Sign In */}
        {isAuth ? (
          <div className="relative" ref={dropdownRef}>
            <button
              type="button"
              onClick={() => setIsDropdownOpen(!isDropdownOpen)}
              className="flex items-center gap-2 pl-2 border-l border-slate-200 hover:opacity-90 focus:outline-hidden transition-opacity"
              id="user-profile-menu-button"
            >
              <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-xs ring-2 ring-blue-100">
                {userInitials}
              </div>
              <span
                className={`text-xs font-bold px-2 py-0.5 rounded border hidden lg:inline-block ${
                  currentRole === "ROLE_ADMIN"
                    ? "bg-rose-50 text-rose-700 border-rose-200"
                    : currentRole === "ROLE_MENTOR"
                    ? "bg-amber-50 text-amber-700 border-amber-200"
                    : "bg-blue-50 text-blue-700 border-blue-200"
                }`}
              >
                {roleLabel}
              </span>
              <VectorIcon
                name="chevron-down"
                size={14}
                className={`text-slate-400 transition-transform ${
                  isDropdownOpen ? "rotate-180" : ""
                }`}
              />
            </button>

            {/* Profile Dropdown Popover */}
            {isDropdownOpen && (
              <div className="absolute right-0 mt-2 w-64 rounded-xl bg-white border border-slate-200 shadow-xl py-2 z-50 animate-fadeIn">
                {/* User Info Header */}
                <div className="px-4 py-3 border-b border-slate-100">
                  <div className="text-xs font-bold text-slate-900">{userName}</div>
                  <div className="text-[11px] text-slate-500 font-mono truncate">
                    {userEmail}
                  </div>
                  <div className="mt-1.5 flex items-center gap-2">
                    <span
                      className={`text-[9px] font-bold px-2 py-0.5 rounded border uppercase ${
                        currentRole === "ROLE_ADMIN"
                          ? "bg-rose-50 text-rose-700 border-rose-200"
                          : currentRole === "ROLE_MENTOR"
                          ? "bg-amber-50 text-amber-700 border-amber-200"
                          : "bg-blue-50 text-blue-700 border-blue-200"
                      }`}
                    >
                      {roleLabel}
                    </span>
                    {currentRole === "ROLE_STUDENT" && (
                      <span className="text-[10px] text-slate-400">
                        {currentAltitude}m Altitude
                      </span>
                    )}
                  </div>
                </div>

                {/* Quick Navigation Links: Strictly role-partitioned */}
                <div className="py-1 text-xs text-slate-700 font-medium">
                  {currentRole === "ROLE_STUDENT" && (
                    <>
                      <Link
                        href="/"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="mountain" size={14} className="text-blue-600" />
                        <span>Mountain Tracks Overview</span>
                      </Link>

                      <Link
                        href="/dashboard"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="trending-up" size={14} className="text-blue-600" />
                        <span>Mountain Ascent Dashboard</span>
                      </Link>

                      <Link
                        href="/curriculum"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="lightbulb" size={14} className="text-slate-600" />
                        <span>Socratic Curriculum Reader</span>
                      </Link>

                      <Link
                        href="/cockpit/2.3"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="code" size={14} className="text-slate-600" />
                        <span>Coding Cockpit</span>
                      </Link>
                    </>
                  )}

                  {currentRole === "ROLE_MENTOR" && (
                    <>
                      <Link
                        href="/mentor/desk"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 text-amber-800 transition-colors"
                      >
                        <VectorIcon name="shield" size={14} className="text-amber-700" />
                        <span>Mentor Resolution Desk</span>
                      </Link>

                      <Link
                        href="/mentor/curriculum-studio"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="book" size={14} className="text-slate-600" />
                        <span>Curriculum Authoring Studio</span>
                      </Link>

                      <Link
                        href="/"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="mountain" size={14} className="text-blue-600" />
                        <span>Mountain Tracks Overview</span>
                      </Link>
                    </>
                  )}

                  {currentRole === "ROLE_ADMIN" && (
                    <>
                      <Link
                        href="/admin/mentors"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 text-rose-800 transition-colors"
                      >
                        <VectorIcon name="settings" size={14} className="text-rose-600" />
                        <span>Admin Governance Studio</span>
                      </Link>

                      <Link
                        href="/mentor/desk"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 text-amber-800 transition-colors"
                      >
                        <VectorIcon name="shield" size={14} className="text-amber-700" />
                        <span>Mentor Resolution Desk</span>
                      </Link>

                      <Link
                        href="/"
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-2.5 px-4 py-2 hover:bg-slate-50 transition-colors"
                      >
                        <VectorIcon name="mountain" size={14} className="text-blue-600" />
                        <span>Mountain Tracks Overview</span>
                      </Link>
                    </>
                  )}
                </div>

                {/* Logout Action */}
                <div className="pt-1 mt-1 border-t border-slate-100 px-2">
                  <button
                    type="button"
                    onClick={handleLogout}
                    disabled={isLoggingOut}
                    id="logout-button"
                    className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-xs font-bold text-red-600 hover:bg-red-50 hover:text-red-700 transition-colors text-left"
                  >
                    <VectorIcon name="log-out" size={14} className="text-red-500" />
                    <span>{isLoggingOut ? "Logging out..." : "Log Out of CodeConnect"}</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
            <Link
              href="/login"
              className="px-3 py-1.5 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
            >
              Sign In
            </Link>
            <Link
              href="/signup"
              className="px-3 py-1.5 rounded-lg text-xs font-bold bg-blue-600 text-white hover:bg-blue-700 shadow-2xs transition-colors"
            >
              Sign Up
            </Link>
          </div>
        )}
      </div>
    </header>
  );
};
