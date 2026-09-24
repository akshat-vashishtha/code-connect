import React from "react";
import Link from "next/link";
import { AppShell } from "@/presentation/organisms/AppShell";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatusPill } from "@/presentation/atoms/StatusPill";

/**
 * Modern SaaS Workbench Portal Home for CodeConnect.
 * Replaces the initial marketing text-walls with an active developer workbench entryway
 * matching Wireframes 00, 02, and 07.
 */
export default function Home() {
  const tracks = [
    {
      id: "track-1",
      number: "TRACK 1",
      title: "Java 21 Mental Models",
      description:
        "Objects as memory cells, value vs reference semantics, and records without dry boilerplate.",
      footholds: "5/5 Footholds Conquered",
      altitude: "500m Gain",
      status: "CONQUERED" as const,
      color: "emerald",
      href: "/curriculum",
    },
    {
      id: "track-2",
      number: "TRACK 2",
      title: "Data Structures Without Fear",
      description:
        "Circular queues as airport carousels, arrays as postal mailbox grids, and zero O(N) shift traps.",
      footholds: "Foothold 2.3 Active (In Progress)",
      altitude: "1,420m Altitude",
      status: "ACTIVE" as const,
      color: "blue",
      href: "/cockpit/2.3",
    },
    {
      id: "track-3",
      number: "TRACK 3",
      title: "Algorithmic Problem Solving",
      description:
        "Two-pointers, binary search trees, and dynamic programming anchored to physical intuition.",
      footholds: "12 Load-Bearing Challenges",
      altitude: "2,200m Elevation",
      status: "LOCKED" as const,
      color: "slate",
      href: "/curriculum",
    },
    {
      id: "track-4",
      number: "TRACK 4",
      title: "Enterprise Concurrency & Craft",
      description:
        "Thread-safe primitives, Spring Boot microservice resiliency, and clean architecture.",
      footholds: "8 Master Challenges",
      altitude: "3,100m High Camp",
      status: "LOCKED" as const,
      color: "slate",
      href: "/curriculum",
    },
  ];

  return (
    <AppShell
      trackTitle="Track 2: Data Structures"
      trackHref="/curriculum"
      footholdTitle="Foothold 2.3: Circular Queue"
    >
      <main className="max-w-6xl mx-auto w-full px-4 sm:px-6 py-8 space-y-8">
        {/* ========================================================================= */}
        {/* ACTIVE CLIMB HERO / RESUME WORKBENCH CARD                                  */}
        {/* ========================================================================= */}
        <section className="p-6 sm:p-8 rounded-2xl bg-white border border-slate-200/90 shadow-[0_4px_20px_rgba(15,23,42,0.04)] relative overflow-hidden">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 relative z-10">
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-blue-50 text-blue-700 border border-blue-200 uppercase tracking-wide">
                  Active Foothold 2.3
                </span>
                <span className="text-xs text-slate-400 font-medium">Altitude Gain: +150m</span>
              </div>

              <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
                Design Circular Queue (FIFO Wrap-Around)
              </h1>

              <p className="text-xs sm:text-sm text-slate-600 max-w-2xl leading-relaxed">
                Connect end-to-beginning like an airport baggage carousel to convert O(N) shifts into O(1) modulo operations. Test with isolated Java 21 sandbox execution.
              </p>
            </div>

            {/* Quick Actions */}
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 shrink-0">
              <Link
                href="/curriculum"
                className="h-11 px-5 rounded-xl border border-slate-200 bg-slate-50 hover:bg-slate-100 text-slate-700 text-xs font-bold inline-flex items-center justify-center gap-2 transition-colors"
              >
                <span>Read Story Spec</span>
              </Link>

              <Link
                href="/cockpit/2.3"
                className="h-11 px-6 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-bold inline-flex items-center justify-center gap-2 transition-all shadow-md shadow-blue-500/20"
              >
                <span>Step Into Cockpit</span>
                <VectorIcon name="arrow-right" size={14} className="text-white" />
              </Link>
            </div>
          </div>
        </section>

        {/* ========================================================================= */}
        {/* MOUNTAIN TRACK PROGRESSION GRID                                           */}
        {/* ========================================================================= */}
        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-slate-900 tracking-tight">
                Progressive Mountain Tracks
              </h2>
              <p className="text-xs text-slate-500">
                Four load-bearing routes taking you from zero intuition to enterprise system design.
              </p>
            </div>

            <Link
              href="/dashboard"
              className="text-xs font-bold text-blue-600 hover:text-blue-700 hover:underline inline-flex items-center gap-1"
            >
              <span>View Elevation Map</span>
              <VectorIcon name="arrow-right" size={12} />
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {tracks.map((track) => (
              <div
                key={track.id}
                className="p-5 rounded-2xl bg-white border border-slate-200 hover:border-slate-300 transition-all flex flex-col justify-between shadow-xs group"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <span className="text-[10px] font-extrabold uppercase tracking-wider text-slate-400">
                      {track.number}
                    </span>
                    <StatusPill status={track.status} size="sm" />
                  </div>

                  <h3 className="text-sm font-bold text-slate-900 group-hover:text-blue-600 transition-colors">
                    {track.title}
                  </h3>

                  <p className="text-xs text-slate-500 mt-1.5 leading-relaxed">
                    {track.description}
                  </p>
                </div>

                <div className="mt-5 pt-3.5 border-t border-slate-100 flex items-center justify-between text-xs">
                  <span className="font-semibold text-slate-700">{track.footholds}</span>
                  <span className="text-slate-400 font-mono text-[11px]">{track.altitude}</span>
                </div>
              </div>
            ))}
          </div>
        </section>

      </main>
    </AppShell>
  );
}
