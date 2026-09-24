"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { getCurrentUser, logout } from "@/lib/api/auth";
import { UserResponse } from "@/types/auth";
import { LogIn, LogOut, User as UserIcon, UserPlus } from "lucide-react";

export function AuthNav() {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  useEffect(() => {
    let isMounted = true;

    async function checkSession() {
      try {
        const response = await getCurrentUser();
        if (isMounted && response.success && response.data) {
          setUser(response.data);
        }
      } catch {
        if (isMounted) {
          setUser(null);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    checkSession();

    return () => {
      isMounted = false;
    };
  }, []);

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      await logout();
      setUser(null);
    } catch {
      // Force clear client state on error as well
      setUser(null);
    } finally {
      setIsLoggingOut(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center gap-2 animate-pulse">
        <div className="h-8 w-20 bg-slate-800 rounded-xl" />
        <div className="h-8 w-24 bg-slate-800 rounded-xl" />
      </div>
    );
  }

  if (user) {
    const isAdmin = user.role === "ROLE_ADMIN";
    const isMentor = user.role === "ROLE_MENTOR";
    const isPending = user.status === "PENDING_APPROVAL";

    return (
      <div className="flex items-center gap-3">
        {isAdmin && (
          <Link
            href="/admin/mentors"
            id="nav-admin-link"
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-purple-300 bg-purple-950/60 hover:bg-purple-900/60 border border-purple-800/60 transition-colors"
          >
            Admin Desk
          </Link>
        )}

        <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-900 border border-slate-800 text-xs">
          <div className="w-5 h-5 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-[10px]">
            {user.displayName ? user.displayName.charAt(0).toUpperCase() : <UserIcon className="w-3 h-3" />}
          </div>
          <span className="font-semibold text-slate-200">{user.displayName || user.email}</span>
          <span
            className={`px-1.5 py-0.5 rounded text-[10px] font-bold ${
              isAdmin
                ? "bg-purple-900/60 text-purple-300 border border-purple-700/50"
                : isMentor
                ? isPending
                  ? "bg-amber-900/60 text-amber-300 border border-amber-700/50"
                  : "bg-indigo-900/60 text-indigo-300 border border-indigo-700/50"
                : "bg-blue-900/60 text-blue-300 border border-blue-700/50"
            }`}
          >
            {isAdmin ? "Admin" : isMentor ? (isPending ? "Pending Review" : "Mentor") : "Student"}
          </span>
        </div>

        <button
          type="button"
          id="logout-btn"
          onClick={handleLogout}
          disabled={isLoggingOut}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-400 hover:text-red-400 bg-slate-900 hover:bg-red-950/30 border border-slate-800 hover:border-red-900/50 transition-all disabled:opacity-50 cursor-pointer"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span>{isLoggingOut ? "..." : "Sign Out"}</span>
        </button>
      </div>
    );
  }

  return (
    <div className="flex items-center gap-2.5">
      <Link
        href="/login"
        id="nav-login-link"
        className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl text-xs font-semibold text-slate-300 hover:text-white hover:bg-slate-850 border border-transparent transition-colors"
      >
        <LogIn className="w-3.5 h-3.5" />
        Sign In
      </Link>
      <Link
        href="/signup"
        id="nav-signup-link"
        className="inline-flex items-center gap-1.5 px-4 py-1.5 rounded-xl text-xs font-bold bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white shadow-md shadow-blue-500/20 hover:shadow-blue-500/30 transition-all"
      >
        <UserPlus className="w-3.5 h-3.5" />
        Start Free
      </Link>
    </div>
  );
}
