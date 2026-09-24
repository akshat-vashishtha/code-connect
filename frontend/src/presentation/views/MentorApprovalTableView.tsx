"use client";

import React, { useState } from "react";
import Link from "next/link";
import { logout } from "@/lib/api/auth";
import { AdminMentorControllerResult } from "@/controller/useAdminMentorController";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatMetricCard } from "@/presentation/molecules/StatMetricCard";
import { StatusPill } from "@/presentation/atoms/StatusPill";
import { AppNavRail } from "@/presentation/organisms/AppNavRail";

export interface MentorApprovalTableViewProps {
  readonly controller: AdminMentorControllerResult;
  readonly detectorSlot?: React.ReactNode;
}

/**
 * Admin Studio View strictly adhering to Wireframe 09-admin-studio-user-governance.svg.
 * Provides 4 top metric cards, Mentor Application Triage with pure vector icons,
 * and Platform User & Role Governance directory.
 */
export const MentorApprovalTableView: React.FC<MentorApprovalTableViewProps> = ({
  controller,
  detectorSlot,
}) => {
  const {
    currentUser,
    isAuthLoading,
    unauthorized,
    filteredApplications,
    isDataLoading,
    searchQuery,
    actionInProgress,
    feedbackMessage,
    setSearchQuery,
    loadPendingApplications,
    handleApprove,
    handleReject,
  } = controller;

  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // allow redirect
    }
    window.location.href = "/login";
  };

  if (isAuthLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center p-6 bg-[#f8fafc]">
        <div className="flex flex-col items-center gap-3">
          <VectorIcon name="refresh" size={28} className="animate-spin text-blue-600" />
          <p className="text-xs font-semibold text-slate-600">Verifying administrative credentials...</p>
        </div>
      </div>
    );
  }

  if (unauthorized) {
    return (
      <div className="min-h-screen bg-[#f8fafc] flex flex-col justify-center items-center p-6 font-sans">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8 border border-slate-200 text-center flex flex-col items-center gap-4">
          <div className="w-14 h-14 rounded-2xl bg-rose-50 border border-rose-200 text-rose-600 flex items-center justify-center">
            <VectorIcon name="shield" size={28} />
          </div>
          <div>
            <h1 className="text-xl font-extrabold text-slate-900 tracking-tight">Access Restricted</h1>
            <p className="mt-2 text-xs text-slate-600 leading-relaxed">
              The Admin Governance Desk requires elevated{" "}
              <code className="px-1.5 py-0.5 rounded bg-slate-100 text-rose-600 font-mono font-bold border border-slate-200">
                ROLE_ADMIN
              </code>{" "}
              privilege.
              {currentUser
                ? ` Current active session role is ${currentUser.role}.`
                : " No active administrative session was found."}
            </p>
          </div>
          <div className="w-full flex flex-col gap-2 pt-2">
            <Link
              href="/login"
              className="w-full py-2.5 px-4 rounded-xl text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 transition-colors shadow-sm"
            >
              Sign In as Administrator
            </Link>
            <Link
              href="/"
              className="w-full py-2 px-4 rounded-xl text-xs font-semibold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 transition-colors"
            >
              Return to Platform Home
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 font-sans pb-16">
      {/* ========================================================================= */}
      {/* TOP CONTEXT BAR (Wireframe 09)                                            */}
      {/* ========================================================================= */}
      <header className="h-16 bg-white border-b border-slate-200 px-6 flex items-center justify-between sticky top-0 z-40 shadow-xs">
        <div>
          <h1 className="text-sm font-bold text-slate-900 tracking-tight">
            Admin Control Center &amp; User Governance
          </h1>
          <p className="text-[11px] text-slate-500">
            Mentor Approvals, Role Transitions &amp; Platform Security Governance
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={loadPendingApplications}
            disabled={isDataLoading}
            className="p-2 rounded-lg border border-slate-200 bg-slate-50 text-slate-600 hover:bg-slate-100 transition-colors"
            title="Refresh Data"
          >
            <VectorIcon
              name="refresh"
              size={14}
              className={isDataLoading ? "animate-spin text-blue-600" : ""}
            />
          </button>

          {/* Admin User Badge with Logout Dropdown */}
          <div className="relative pl-3 border-l border-slate-200">
            <button
              type="button"
              onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
              className="flex items-center gap-2 text-left hover:opacity-90 focus:outline-hidden"
              id="admin-user-menu-btn"
            >
              <div className="w-8 h-8 rounded-full bg-slate-900 text-white flex items-center justify-center font-bold text-xs">
                AD
              </div>
              <div className="hidden sm:block">
                <span className="text-xs font-bold text-slate-900 block leading-tight">
                  {currentUser?.displayName ?? "System Admin"}
                </span>
                <span className="text-[10px] font-bold text-rose-600 uppercase tracking-wider">
                  SUPERUSER
                </span>
              </div>
              <VectorIcon name="chevron-down" size={12} className="text-slate-400" />
            </button>

            {isUserMenuOpen && (
              <div className="absolute right-0 mt-2 w-56 rounded-xl bg-white border border-slate-200 shadow-xl py-2 z-50 animate-fadeIn text-xs">
                <div className="px-4 py-2 border-b border-slate-100">
                  <div className="font-bold text-slate-900">{currentUser?.displayName ?? "Admin"}</div>
                  <div className="text-[10px] text-slate-500 font-mono">{currentUser?.email}</div>
                </div>
                <div className="py-1 text-slate-700 font-medium">
                  <Link href="/" className="block px-4 py-1.5 hover:bg-slate-50">
                    Platform Overview
                  </Link>
                  <Link href="/dashboard" className="block px-4 py-1.5 hover:bg-slate-50">
                    Ascent Dashboard
                  </Link>
                  <Link href="/curriculum" className="block px-4 py-1.5 hover:bg-slate-50">
                    Curriculum Reader
                  </Link>
                  <Link href="/mentor/desk" className="block px-4 py-1.5 hover:bg-slate-50 text-amber-800">
                    Mentor Resolution Desk
                  </Link>
                </div>
                <div className="pt-1 mt-1 border-t border-slate-100 px-2">
                  <button
                    type="button"
                    onClick={handleLogout}
                    className="w-full flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold text-red-600 hover:bg-red-50 text-left"
                    id="admin-logout-btn"
                  >
                    <VectorIcon name="log-out" size={13} />
                    <span>Log Out</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      <div className="flex-1 flex overflow-hidden">
        <AppNavRail explicitRole="ROLE_ADMIN" />

        <div className="flex-1 overflow-y-auto pb-16">
          <main className="max-w-[1152px] mx-auto px-4 sm:px-6 pt-6 space-y-6">
        {/* Feedback Alert */}
        {feedbackMessage && (
          <div
            className={`p-3 rounded-xl border text-xs font-medium flex items-center justify-between ${
              feedbackMessage.type === "success"
                ? "bg-emerald-50 border-emerald-200 text-emerald-800"
                : "bg-rose-50 border-rose-200 text-rose-800"
            }`}
          >
            <span>{feedbackMessage.text}</span>
          </div>
        )}

        {/* ========================================================================= */}
        {/* TOP METRIC CARDS ROW (Wireframe 09)                                       */}
        {/* ========================================================================= */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <StatMetricCard
            title="ACTIVE STUDENTS"
            value="4,290"
            subtitle="+12% this wk"
            valueColor="default"
          />
          <StatMetricCard
            title="VERIFIED MENTORS"
            value="86"
            subtitle="Staff & Tech Leads"
            valueColor="blue"
          />
          <StatMetricCard
            title="PENDING MENTOR APPS"
            value={filteredApplications.length > 0 ? `${filteredApplications.length} Urgent` : "0 Pending"}
            subtitle="Action Required"
            isUrgent={filteredApplications.length > 0}
            valueColor="amber"
          />
          <StatMetricCard
            title="CLUSTER SANDBOX HEALTH"
            value="100% OK"
            subtitle="k3d • Redis • Mongo"
            valueColor="emerald"
          />
        </div>

        {/* ========================================================================= */}
        {/* MAIN SECTION 1: MENTOR APPROVALS QUEUE (Wireframe 09)                      */}
        {/* ========================================================================= */}
        <section className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          {/* Section Header */}
          <div className="h-12 bg-slate-50/80 border-b border-slate-200 px-5 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <h2 className="text-xs font-bold text-slate-900 tracking-tight">
                Mentor Application Triage
              </h2>
              <StatusPill status="PENDING_VERIFICATION" size="sm" />
              <span className="hidden md:inline text-[11px] text-slate-500">
                Applicants redirected to /pending-approval until approved here
              </span>
            </div>

            {/* Search Filter */}
            <div className="relative w-48 sm:w-64">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Filter applicants..."
                className="w-full h-8 px-3 pl-8 rounded-lg border border-slate-200 bg-white text-xs placeholder:text-slate-400 focus:outline-none focus:ring-1 focus:ring-blue-500"
              />
              <div className="absolute left-2.5 top-2 text-slate-400">
                <VectorIcon name="search" size={13} />
              </div>
            </div>
          </div>

          {/* Applications List */}
          <div className="p-4 space-y-3">
            {isDataLoading && filteredApplications.length === 0 ? (
              <div className="py-12 text-center text-xs text-slate-500">
                <VectorIcon name="refresh" size={20} className="animate-spin text-blue-600 mx-auto mb-2" />
                <span>Loading pending mentor applications...</span>
              </div>
            ) : filteredApplications.length === 0 ? (
              <div className="py-10 text-center text-xs text-slate-500">
                <VectorIcon name="check-circle" size={24} className="text-emerald-500 mx-auto mb-2" />
                <p className="font-semibold text-slate-700">All Mentor Applications Reviewed</p>
                <p className="text-[11px] text-slate-400 mt-0.5">
                  No pending mentor applications require triage at this time.
                </p>
              </div>
            ) : (
              filteredApplications.map((app) => {
                const initials = (app.email.slice(0, 2) || "ME").toUpperCase();
                const applicantHandle = app.email.split("@")[0] || "Mentor Applicant";

                return (
                  <div
                    key={app.id}
                    className="p-4 rounded-xl border border-slate-200 bg-white hover:border-slate-300 transition-all flex flex-col md:flex-row md:items-center justify-between gap-4"
                  >
                    {/* Left: Applicant Bio & Credentials */}
                    <div className="flex items-start gap-3.5">
                      <div className="w-9 h-9 rounded-full bg-indigo-600 text-white font-bold text-xs flex items-center justify-center shrink-0">
                        {initials}
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-bold text-slate-900">
                            {applicantHandle}
                          </span>
                          <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
                            Engineering Lead
                          </span>
                        </div>
                        <p className="text-[11px] text-slate-500 mt-0.5">
                          {app.email} &bull; {app.bio ?? "Senior Backend Specialist"}
                        </p>
                      </div>
                    </div>

                    {/* Middle: LinkedIn / GitHub Profile Link with Vector Arrow */}
                    <div className="flex items-center gap-4">
                      {app.linkedInUrl && (
                        <a
                          href={app.linkedInUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1.5 text-xs font-semibold text-blue-600 hover:text-blue-700 hover:underline"
                        >
                          <span>{app.linkedInUrl.replace(/^https?:\/\//, "")}</span>
                          <VectorIcon name="external-link" size={13} />
                        </a>
                      )}

                      {/* Right: Actions */}
                      <div className="flex items-center gap-2">
                        <button
                          type="button"
                          onClick={() => handleApprove(app.id, app.email)}
                          disabled={actionInProgress === app.id}
                          className="h-8 px-3.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 active:bg-emerald-800 text-white text-xs font-bold inline-flex items-center gap-1.5 shadow-sm transition-colors disabled:opacity-50"
                        >
                          <VectorIcon name="checkmark" size={12} strokeWidth={2} />
                          <span>Approve Mentor</span>
                        </button>

                        <button
                          type="button"
                          onClick={() => handleReject(app.id, app.email)}
                          disabled={actionInProgress === app.id}
                          className="h-8 px-3 rounded-lg border border-slate-200 bg-slate-50 hover:bg-slate-100 text-slate-600 text-xs font-semibold transition-colors disabled:opacity-50"
                        >
                          Reject
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </section>

        {/* ========================================================================= */}
        {/* OPTIONAL SLOT: Language Detector / Secondary Widgets                      */}
        {/* ========================================================================= */}
        {detectorSlot && (
          <div className="mb-2">
            {detectorSlot}
          </div>
        )}

        {/* ========================================================================= */}
        {/* MAIN SECTION 2: USER & ROLE GOVERNANCE DIRECTORY (Wireframe 09)           */}
        {/* ========================================================================= */}
        <section className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="h-12 bg-slate-50/80 border-b border-slate-200 px-5 flex items-center justify-between">
            <h2 className="text-xs font-bold text-slate-900 tracking-tight">
              Platform User Directory &amp; Role Management
            </h2>

            {/* Filter Pills */}
            <div className="hidden sm:flex items-center gap-1.5">
              <span className="px-2.5 py-1 rounded-full bg-slate-900 text-white text-[10px] font-bold">
                ALL (4.3k)
              </span>
              <span className="px-2.5 py-1 rounded-full bg-white border border-slate-200 text-slate-600 text-[10px] font-semibold hover:bg-slate-50 cursor-pointer">
                STUDENTS
              </span>
              <span className="px-2.5 py-1 rounded-full bg-white border border-slate-200 text-slate-600 text-[10px] font-semibold hover:bg-slate-50 cursor-pointer">
                MENTORS
              </span>
              <span className="px-2.5 py-1 rounded-full bg-white border border-slate-200 text-slate-600 text-[10px] font-semibold hover:bg-slate-50 cursor-pointer">
                ADMINS
              </span>
            </div>
          </div>

          {/* Directory Table */}
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50/50 border-b border-slate-100 text-[10px] font-bold text-slate-500 uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-5">User / Climber</th>
                  <th className="py-3 px-4">Role</th>
                  <th className="py-3 px-4">Altitude / Progress</th>
                  <th className="py-3 px-4">Account Status</th>
                  <th className="py-3 px-5 text-right">Governance Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                <tr className="hover:bg-slate-50/80 transition-colors">
                  <td className="py-3 px-5">
                    <span className="font-bold text-slate-900 block">Alex Vashishtha</span>
                    <span className="text-[11px] text-slate-400">alex@codeconnect.dev</span>
                  </td>
                  <td className="py-3 px-4">
                    <span className="inline-block px-2 py-0.5 rounded-full text-[10px] font-bold bg-blue-50 text-blue-700 border border-blue-200">
                      STUDENT
                    </span>
                  </td>
                  <td className="py-3 px-4 font-medium text-slate-600">
                    1,420m &bull; 9 Footholds Mastered
                  </td>
                  <td className="py-3 px-4">
                    <StatusPill status="ACTIVE" size="sm" />
                  </td>
                  <td className="py-3 px-5 text-right">
                    <button
                      type="button"
                      className="px-2.5 py-1 rounded-lg border border-slate-200 text-[11px] font-semibold text-slate-600 hover:bg-slate-100 transition-colors"
                    >
                      Manage User &darr;
                    </button>
                  </td>
                </tr>

                <tr className="hover:bg-slate-50/80 transition-colors">
                  <td className="py-3 px-5">
                    <span className="font-bold text-slate-900 block">Dr. Priya Sen</span>
                    <span className="text-[11px] text-slate-400">priya.sen@swiggy.in</span>
                  </td>
                  <td className="py-3 px-4">
                    <span className="inline-block px-2 py-0.5 rounded-full text-[10px] font-bold bg-purple-50 text-purple-700 border border-purple-200">
                      MENTOR
                    </span>
                  </td>
                  <td className="py-3 px-4 font-medium text-slate-600">
                    Verified by Admin &bull; 48 Reviews
                  </td>
                  <td className="py-3 px-4">
                    <StatusPill status="ACTIVE" size="sm" />
                  </td>
                  <td className="py-3 px-5 text-right">
                    <button
                      type="button"
                      className="px-2.5 py-1 rounded-lg border border-slate-200 text-[11px] font-semibold text-slate-600 hover:bg-slate-100 transition-colors"
                    >
                      Manage User &darr;
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* Audit Strip */}
          <div className="p-3 bg-slate-50/60 border-t border-slate-200/80 text-[11px] text-slate-500 flex items-center gap-2">
            <VectorIcon name="shield" size={13} className="text-slate-400 shrink-0" />
            <span>
              <strong className="font-semibold text-slate-700">Security Audit Trail:</strong> All role promotions, suspensions, and mentor activations are permanently logged in the compliance store.
            </span>
          </div>
        </section>
      </main>
        </div>
      </div>
    </div>
  );
};
