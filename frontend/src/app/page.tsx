import Link from "next/link";
import {
  Compass,
  BookOpen,
  GraduationCap,
  Users,
  Sparkles,
  ArrowRight,
  CheckCircle2,
  HelpCircle,
  Code2,
  Terminal,
  MessageSquare,
  Shield,
  Layers,
  Flame,
  ArrowUpRight,
} from "lucide-react";
import { AuthNav } from "@/components/auth/AuthNav";

/**
 * Landing Page for CodeConnect — The Fear-Free Solo Mountain Climb to Java & DSA.
 * Focuses 100% on learner outcomes, story-driven mental models, and the 3-tier support hierarchy.
 */
export default function Home() {
  const tracks = [
    {
      step: "TRACK 01",
      title: "Java Foundations & Object Intuition",
      description:
        "Build deep intuition for objects, memory references, and polymorphic dispatch through real-world systems analogies—no dry textbook theory.",
      icon: <Compass className="w-5 h-5 text-blue-400" />,
      tag: "Foundational Foothold",
      badge: "Beginner Friendly",
    },
    {
      step: "TRACK 02",
      title: "Data Structures Without Fear",
      description:
        "Master Arrays, Linked Lists, Stacks, Queues, and HashMaps by visualizing them as physical package hubs, conveyors, and indexing registries.",
      icon: <Layers className="w-5 h-5 text-indigo-400" />,
      tag: "Core DSA",
      badge: "Visual Mental Models",
    },
    {
      step: "TRACK 03",
      title: "Algorithmic Problem Solving",
      description:
        "Demystify Recursion, Binary Search Trees, Graphs, and Dynamic Programming with progressive step-by-step intuition and zero math intimidation.",
      icon: <Code2 className="w-5 h-5 text-emerald-400" />,
      tag: "Interview & Systems",
      badge: "High Leverage",
    },
    {
      step: "TRACK 04",
      title: "Production Java Craftsmanship",
      description:
        "Learn concurrency, thread-safe patterns, robust exception handling, and enterprise clean design by building real resilient components.",
      icon: <GraduationCap className="w-5 h-5 text-purple-400" />,
      tag: "Senior Engineering",
      badge: "Industry Ready",
    },
  ];

  const supportTiers = [
    {
      tier: "Tier 1",
      name: "Socratic AI Story Coach",
      badge: "Instant • 24/7",
      description:
        "Diagnoses errors inside the story analogy. Instead of dumping code or cryptic stack traces, it asks the guiding question that sparks your breakthrough.",
      icon: <Sparkles className="w-6 h-6 text-blue-400" />,
      highlight: "Explains bugs in your spoken language",
    },
    {
      tier: "Tier 2",
      name: "Peer Solver Friend",
      badge: "Classmate Chat",
      description:
        "Connect 1-on-1 with a fellow student who just conquered that exact challenge. Pure classmate camaraderie without toxic leaderboards or public judgment.",
      icon: <Users className="w-6 h-6 text-indigo-400" />,
      highlight: "Lightweight, distraction-free doubt sharing",
    },
    {
      tier: "Tier 3",
      name: "Vetted Senior Mentor",
      badge: "1-on-1 Escalation",
      description:
        "When an architectural bottleneck blocks your climb, escalate directly to an industry mentor who reviews your live code context and guides your path.",
      icon: <GraduationCap className="w-6 h-6 text-purple-400" />,
      highlight: "Contextual feedback from experienced engineers",
    },
  ];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans selection:bg-blue-500 selection:text-white relative overflow-hidden">
      {/* Background Ambient Glow Gradients */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[1100px] h-[500px] bg-gradient-to-b from-blue-600/15 via-indigo-600/10 to-transparent blur-3xl pointer-events-none" />
      <div className="absolute top-[600px] -left-40 w-96 h-96 bg-purple-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-[1200px] -right-40 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />

      {/* ========================================================================= */}
      {/* TOP NAVBAR                                                                */}
      {/* ========================================================================= */}
      <header className="sticky top-0 z-50 backdrop-blur-xl bg-slate-950/85 border-b border-slate-800/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <Link href="/" className="flex items-center gap-3 group">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-500 flex items-center justify-center text-white font-extrabold text-lg shadow-lg shadow-blue-500/20 group-hover:scale-105 transition-transform">
              CC
            </div>
            <div>
              <span className="text-xl font-bold tracking-tight text-white">
                Code<span className="text-blue-400">Connect</span>
              </span>
              <span className="hidden sm:inline-block ml-2 text-[11px] text-slate-400 font-medium">
                The Solo Mountain Climb
              </span>
            </div>
          </Link>

          <nav className="hidden md:flex items-center gap-7 text-xs font-semibold text-slate-300">
            <a href="#philosophy" className="hover:text-blue-400 transition-colors">
              The Solo Climb
            </a>
            <a href="#curriculum" className="hover:text-blue-400 transition-colors">
              Curriculum Footholds
            </a>
            <a href="#support" className="hover:text-blue-400 transition-colors">
              3-Tier Guidance
            </a>
            <a href="#socratic" className="hover:text-blue-400 transition-colors">
              Socratic Coach
            </a>
          </nav>

          <div className="flex items-center gap-3">
            <AuthNav />
          </div>
        </div>
      </header>

      {/* ========================================================================= */}
      {/* HERO SECTION                                                              */}
      {/* ========================================================================= */}
      <section className="relative pt-20 pb-16 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto text-center flex flex-col items-center">
        {/* Eyebrow Badge */}
        <div className="inline-flex items-center gap-2 px-3.5 py-1 rounded-full text-xs font-semibold bg-blue-500/10 text-blue-400 border border-blue-500/20 mb-6 backdrop-blur-sm shadow-sm shadow-blue-500/10">
          <Sparkles className="w-3.5 h-3.5 text-blue-400" />
          <span>Fear-Free Java &amp; Data Structures Mastery</span>
          <span className="w-1.5 h-1.5 rounded-full bg-blue-400 animate-pulse" />
        </div>

        {/* Hero Title */}
        <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight text-white leading-tight max-w-4xl">
          Master Java &amp; DSA Through Stories,{" "}
          <br className="hidden sm:inline" />
          <span className="bg-gradient-to-r from-blue-400 via-indigo-300 to-purple-400 bg-clip-text text-transparent">
            One Solid Foothold at a Time.
          </span>
        </h1>

        {/* Hero Subtitle */}
        <p className="mt-6 text-base sm:text-lg text-slate-400 max-w-2xl leading-relaxed">
          No dry textbook jargon. No passive 40-minute videos. No cold compiler walls.
          Build real mental models through intuitive text stories, in-browser coding, and an empathetic 3-tier support hierarchy whenever you stumble.
        </p>

        {/* CTAs */}
        <div className="mt-8 flex flex-col sm:flex-row items-center gap-4 w-full sm:w-auto">
          <Link
            href="/signup"
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-7 py-3.5 rounded-xl font-bold text-sm bg-gradient-to-r from-blue-600 to-indigo-600 text-white hover:from-blue-700 hover:to-indigo-700 shadow-xl shadow-blue-500/25 hover:shadow-blue-500/40 hover:-translate-y-0.5 transition-all duration-200"
          >
            <span>Start Your Mountain Climb Free</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
          <a
            href="#curriculum"
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl font-semibold text-sm bg-slate-900/80 hover:bg-slate-800 text-slate-200 border border-slate-700/80 backdrop-blur-sm hover:border-slate-600 transition-all duration-200"
          >
            <BookOpen className="w-4 h-4 text-slate-400" />
            <span>Explore Footholds</span>
          </a>
        </div>

        {/* Value Prop Badges */}
        <div className="mt-14 grid grid-cols-2 md:grid-cols-4 gap-4 w-full max-w-4xl pt-8 border-t border-slate-800/80 text-left">
          <div className="p-3 rounded-xl bg-slate-900/40 border border-slate-800/60">
            <div className="text-sm font-bold text-white flex items-center gap-2">
              <BookOpen className="w-4 h-4 text-blue-400" />
              Text-First Stories
            </div>
            <div className="text-xs text-slate-400 mt-1">Zero video fatigue; mental models first.</div>
          </div>
          <div className="p-3 rounded-xl bg-slate-900/40 border border-slate-800/60">
            <div className="text-sm font-bold text-white flex items-center gap-2">
              <Compass className="w-4 h-4 text-indigo-400" />
              Progressive Footholds
            </div>
            <div className="text-xs text-slate-400 mt-1">Every step verifies prior understanding.</div>
          </div>
          <div className="p-3 rounded-xl bg-slate-900/40 border border-slate-800/60">
            <div className="text-sm font-bold text-white flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-purple-400" />
              Socratic Coach
            </div>
            <div className="text-xs text-slate-400 mt-1">Guiding questions without code spoilers.</div>
          </div>
          <div className="p-3 rounded-xl bg-slate-900/40 border border-slate-800/60">
            <div className="text-sm font-bold text-white flex items-center gap-2">
              <Users className="w-4 h-4 text-emerald-400" />
              Classmates &amp; Mentors
            </div>
            <div className="text-xs text-slate-400 mt-1">Real people ready to help you unlock the fix.</div>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* SECTION: THE SOLO MOUNTAIN CLIMB PHILOSOPHY                               */}
      {/* ========================================================================= */}
      <section id="philosophy" className="py-20 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto border-t border-slate-800/60">
        <div className="text-center max-w-3xl mx-auto mb-14">
          <h2 className="text-xs font-bold uppercase tracking-wider text-blue-400 mb-2">The Philosophy</h2>
          <p className="text-3xl font-extrabold text-white tracking-tight">
            Why Solo Learners Hit the Wall — And How We Change It
          </p>
          <p className="mt-3 text-sm text-slate-400 leading-relaxed">
            Traditional programming platforms present solo learners with brutal academic cliffs: dry syntax drills, cryptic compiler errors, and lonely frustration. CodeConnect re-architects learning around human intuition.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-7 rounded-2xl bg-slate-900/70 border border-slate-800 hover:border-slate-700 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 flex items-center justify-center mb-5">
                <HelpCircle className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">The Problem: Academic Jargon</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Textbooks throw abstract definitions at you before explaining what problem a concept solves or why it exists in the physical world.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 text-xs text-emerald-400 font-semibold flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>CodeConnect: Real-world text story analogies</span>
            </div>
          </div>

          <div className="p-7 rounded-2xl bg-slate-900/70 border border-slate-800 hover:border-slate-700 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-xl bg-amber-500/10 border border-amber-500/20 text-amber-400 flex items-center justify-center mb-5">
                <Terminal className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">The Problem: Cold Compiler Walls</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                When automated tests fail, you get an impenetrable stack trace (<code className="text-amber-300">NullPointerException</code>). Without a guide, learners quit.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 text-xs text-emerald-400 font-semibold flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>CodeConnect: Socratic story-based error diagnosis</span>
            </div>
          </div>

          <div className="p-7 rounded-2xl bg-slate-900/70 border border-slate-800 hover:border-slate-700 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-xl bg-purple-500/10 border border-purple-500/20 text-purple-400 flex items-center justify-center mb-5">
                <Users className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">The Problem: Isolated Learning</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Solo learners have nowhere to turn between AI bots dumping full answers and toxic forums with sarcastic comments.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 text-xs text-emerald-400 font-semibold flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>CodeConnect: 3-tier friend &amp; mentor safety net</span>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* SECTION: CURRICULUM FOOTHOLDS                                            */}
      {/* ========================================================================= */}
      <section id="curriculum" className="py-20 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto border-t border-slate-800/60">
        <div className="text-center max-w-3xl mx-auto mb-14">
          <h2 className="text-xs font-bold uppercase tracking-wider text-blue-400 mb-2">The Ascent</h2>
          <p className="text-3xl font-extrabold text-white tracking-tight">
            Curriculum Footholds Designed for Intuition
          </p>
          <p className="mt-3 text-sm text-slate-400 leading-relaxed">
            Every algorithmic and Java concept is structured as a load-bearing foothold. You master the visual mechanics before writing code.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {tracks.map((track) => (
            <div
              key={track.step}
              className="p-7 rounded-2xl bg-slate-900/70 border border-slate-800/80 hover:border-blue-500/40 transition-all duration-300 group hover:shadow-2xl hover:shadow-blue-500/5 backdrop-blur-sm flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-10 h-10 rounded-xl bg-slate-800/80 border border-slate-700/60 flex items-center justify-center group-hover:scale-105 transition-transform">
                    {track.icon}
                  </div>
                  <span className="text-[11px] font-mono font-semibold px-2.5 py-1 rounded-full bg-slate-800 text-blue-400 border border-slate-700">
                    {track.step}
                  </span>
                </div>
                <h3 className="text-lg font-bold text-white group-hover:text-blue-400 transition-colors">
                  {track.title}
                </h3>
                <p className="mt-2 text-xs text-slate-400 leading-relaxed">{track.description}</p>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-800/60 flex items-center justify-between text-xs">
                <span className="text-slate-400 font-medium px-2 py-0.5 rounded bg-slate-800/60">
                  {track.badge}
                </span>
                <Link
                  href="/signup"
                  className="text-blue-400 font-semibold group-hover:translate-x-1 transition-transform flex items-center gap-1 hover:text-blue-300"
                >
                  Start Track &rarr;
                </Link>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ========================================================================= */}
      {/* SECTION: 3-TIER SUPPORT HIERARCHY                                         */}
      {/* ========================================================================= */}
      <section id="support" className="py-20 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto border-t border-slate-800/60">
        <div className="text-center max-w-3xl mx-auto mb-14">
          <h2 className="text-xs font-bold uppercase tracking-wider text-blue-400 mb-2">The Safety Net</h2>
          <p className="text-3xl font-extrabold text-white tracking-tight">
            Never Stuck Alone: The 3-Tier Support Hierarchy
          </p>
          <p className="mt-3 text-sm text-slate-400 leading-relaxed">
            When you hit a tough bug, you never have to give up in defeat. Our progressive support loop gives you just the right level of insight to earn your own breakthrough.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {supportTiers.map((tier) => (
            <div
              key={tier.tier}
              className="p-7 rounded-2xl bg-slate-900/80 border border-slate-800 flex flex-col justify-between hover:border-slate-700 transition-colors relative"
            >
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 rounded-xl bg-slate-800 border border-slate-700/80 flex items-center justify-center">
                    {tier.icon}
                  </div>
                  <span className="text-[11px] font-mono font-semibold px-2.5 py-0.5 rounded-full bg-blue-500/10 text-blue-400 border border-blue-500/20">
                    {tier.badge}
                  </span>
                </div>
                <div className="text-xs font-mono text-slate-500 uppercase tracking-wider mb-1">
                  {tier.tier}
                </div>
                <h3 className="text-lg font-bold text-white mb-2">{tier.name}</h3>
                <p className="text-xs text-slate-400 leading-relaxed">{tier.description}</p>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-800/80 text-xs text-slate-300 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>{tier.highlight}</span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ========================================================================= */}
      {/* SECTION: SOCRATIC AI DIALOGUE SPOTLIGHT                                   */}
      {/* ========================================================================= */}
      <section id="socratic" className="py-16 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto border-t border-slate-800/60">
        <div className="rounded-3xl bg-gradient-to-br from-slate-900 via-indigo-950/40 to-slate-900 border border-slate-800 p-8 sm:p-12 shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 right-0 w-80 h-80 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
            <div className="lg:col-span-6 space-y-4">
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-purple-500/10 text-purple-400 border border-purple-500/20">
                <Flame className="w-3.5 h-3.5" />
                Socratic Error Diagnosis
              </span>
              <h3 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
                An AI Coach that Never Robs You of the Breakthrough.
              </h3>
              <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
                Most AI tools make learners weak by dumping raw code solutions. The CodeConnect Socratic Coach anchors its feedback inside the concept story analogy, asking guiding questions that allow you to discover the fix yourself.
              </p>
              <div className="pt-2">
                <Link
                  href="/signup"
                  className="inline-flex items-center gap-2 text-xs font-bold text-blue-400 hover:text-blue-300 transition-colors"
                >
                  Experience the Socratic Story Coach &rarr;
                </Link>
              </div>
            </div>

            <div className="lg:col-span-6">
              <div className="rounded-2xl bg-slate-950 border border-slate-800 p-5 font-mono text-xs shadow-2xl">
                <div className="flex items-center justify-between pb-2 mb-3 border-b border-slate-800 text-[11px] text-slate-500">
                  <div className="flex items-center gap-2">
                    <span className="w-2.5 h-2.5 rounded-full bg-red-500/80" />
                    <span className="w-2.5 h-2.5 rounded-full bg-amber-500/80" />
                    <span className="w-2.5 h-2.5 rounded-full bg-emerald-500/80" />
                    <span className="ml-2 font-mono text-slate-400">ArrayConveyorTest.java</span>
                  </div>
                  <span className="text-emerald-400 text-[10px] font-sans font-semibold">● ACTIVE COACH</span>
                </div>
                <div className="space-y-3.5">
                  <div className="p-2.5 rounded-xl bg-red-950/30 border border-red-900/40 text-red-300 text-[11px]">
                    <span className="font-bold text-red-400">Compiler Test Error:</span>{" "}
                    ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5
                  </div>
                  <div className="text-slate-400 text-[11px]">
                    <span className="text-blue-400">&gt; Student:</span> I don&apos;t get why my loop is crashing at the very end of the parcel queue...
                  </div>
                  <div className="p-3.5 rounded-xl bg-purple-950/30 border border-purple-800/30 text-purple-200 leading-relaxed text-[11px]">
                    <span className="font-bold text-purple-400">Socratic Coach:</span> &ldquo;Think back to our package conveyor belt story: if there are 5 packages on the belt indexed 0 to 4, what package slot is your sensor trying to grab when <code className="text-amber-300">index == 5</code>? What should your loop boundary look like?&rdquo;
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* SECTION: CTA BANNER                                                       */}
      {/* ========================================================================= */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto text-center">
        <div className="p-8 sm:p-12 rounded-3xl bg-gradient-to-r from-blue-900/40 via-indigo-900/30 to-purple-900/40 border border-blue-500/30 shadow-2xl relative overflow-hidden">
          <h2 className="text-2xl sm:text-4xl font-extrabold text-white tracking-tight">
            Ready to Begin Your Mountain Climb?
          </h2>
          <p className="mt-3 text-sm text-slate-300 max-w-xl mx-auto leading-relaxed">
            Master Java and Data Structures fearlessly. Free to start, no credit card required, and learn at your own pace with genuine community guidance.
          </p>
          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link
              href="/signup"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-8 py-3.5 rounded-xl font-bold text-sm bg-gradient-to-r from-blue-600 to-indigo-600 text-white hover:from-blue-700 hover:to-indigo-700 shadow-xl shadow-blue-500/30 hover:-translate-y-0.5 transition-all"
            >
              <span>Get Started Free</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
            <Link
              href="/login"
              className="w-full sm:w-auto inline-flex items-center justify-center px-6 py-3.5 rounded-xl font-semibold text-sm bg-slate-900/80 hover:bg-slate-800 text-slate-200 border border-slate-700 transition-colors"
            >
              Already Have an Account? Sign In
            </Link>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* FOOTER                                                                    */}
      {/* ========================================================================= */}
      <footer className="pt-12 pb-12 border-t border-slate-900 bg-slate-950/80 text-xs text-slate-400">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-500 flex items-center justify-center text-white font-extrabold text-sm shadow-md shadow-blue-500/20">
              CC
            </div>
            <div>
              <span className="font-bold text-white text-sm">CodeConnect</span>
              <p className="text-[11px] text-slate-500">The Fear-Free Mountain Climb to Java Mastery.</p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-6 text-xs font-medium text-slate-400">
            <a href="#philosophy" className="hover:text-white transition-colors">
              Philosophy
            </a>
            <a href="#curriculum" className="hover:text-white transition-colors">
              Footholds
            </a>
            <a href="#support" className="hover:text-white transition-colors">
              Support Hierarchy
            </a>
            <Link href="/signup" className="hover:text-white transition-colors">
              Apply as Mentor
            </Link>
            <Link href="/login" className="hover:text-white transition-colors">
              Sign In
            </Link>
          </div>

          <div className="text-[11px] text-slate-500 text-center md:text-right">
            &copy; {new Date().getFullYear()} CodeConnect. Dedicated to fear-free software education.
          </div>
        </div>
      </footer>
    </div>
  );
}
