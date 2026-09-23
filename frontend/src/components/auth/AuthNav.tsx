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
        <div className="h-8 w-20 bg-slate-200 rounded-xl" />
        <div className="h-8 w-24 bg-slate-200 rounded-xl" />
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
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-purple-700 bg-purple-50 hover:bg-purple-100 border border-purple-200 transition-colors"
          >
            Admin Dashboard
          </Link>
        )}

        <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-100 border border-slate-200 text-xs">
          <div className="w-5 h-5 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-[10px]">
            {user.displayName ? user.displayName.charAt(0).toUpperCase() : <UserIcon className="w-3 h-3" />}
          </div>
          <span className="font-semibold text-slate-800">{user.displayName}</span>
          <span
            className={`px-1.5 py-0.5 rounded text-[10px] font-bold ${
              isAdmin
                ? "bg-purple-100 text-purple-800"
                : isMentor
                ? isPending
                  ? "bg-amber-100 text-amber-800"
                  : "bg-indigo-100 text-indigo-800"
                : "bg-blue-100 text-blue-800"
            }`}
          >
            {isAdmin ? "Admin" : isMentor ? (isPending ? "Pending Mentor" : "Mentor") : "Student"}
          </span>
        </div>

        <button
          type="button"
          id="logout-btn"
          onClick={handleLogout}
          disabled={isLoggingOut}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-700 hover:text-red-600 bg-white border border-slate-200 hover:border-red-200 hover:bg-red-50/50 shadow-sm transition-all disabled:opacity-50"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span>{isLoggingOut ? "Signing out..." : "Sign Out"}</span>
        </button>
      </div>
    );
  }

  return (
    <div className="flex items-center gap-2">
      <Link
        href="/login"
        id="nav-login-link"
        className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-700 hover:text-slate-900 hover:bg-slate-100 border border-transparent transition-colors"
      >
        <LogIn className="w-3.5 h-3.5" />
        Sign In
      </Link>
      <Link
        href="/signup"
        id="nav-signup-link"
        className="inline-flex items-center gap-1.5 px-4 py-1.5 rounded-xl text-xs font-bold bg-blue-600 hover:bg-blue-700 text-white shadow-sm transition-colors"
      >
        <UserPlus className="w-3.5 h-3.5" />
        Sign Up
      </Link>
    </div>
  );
}
