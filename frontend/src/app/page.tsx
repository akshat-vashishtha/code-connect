import { Terminal, Database, Server, Cpu, CheckCircle2, ShieldCheck, Zap } from "lucide-react";

export default function Home() {
  const services = [
    {
      name: "Gateway Service",
      port: 8080,
      description: "Edge Gateway & Auth Gatekeeper (Spring Cloud Gateway, Redis Session)",
      status: "Configured",
      type: "Spring Boot 3.3.4 (Java 21)",
    },
    {
      name: "User Service",
      port: 8081,
      description: "User Profiles, RBAC & Mentor Verification (MongoDB, Redis, Kafka)",
      status: "Configured",
      type: "Spring Boot 3.3.4 (Java 21)",
    },
    {
      name: "MongoDB 7.0",
      port: 27017,
      description: "Unified Document Store (codeconnect_db)",
      status: "Running (Healthy)",
      type: "Docker Container",
    },
    {
      name: "Redis 7.2",
      port: 6379,
      description: "Distributed Session & Presence Cache (spring:session)",
      status: "Running (Healthy)",
      type: "Docker Container",
    },
    {
      name: "Apache Kafka",
      port: 9092,
      description: "Asynchronous Streaming Backbone (KRaft Mode)",
      status: "Running (Healthy)",
      type: "Docker Container",
    },
  ];

  return (
    <main className="min-h-screen p-8 md:p-12 max-w-6xl mx-auto flex flex-col gap-10">
      {/* Header */}
      <header className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-border">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-brand-primary text-white flex items-center justify-center font-bold text-xl shadow-sm">
              CC
            </div>
            <div>
              <h1 className="text-2xl font-bold tracking-tight text-foreground">CodeConnect</h1>
              <p className="text-sm text-muted">Mountain Climb Java Mastery Platform</p>
            </div>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
            <CheckCircle2 className="w-3.5 h-3.5" />
            Infrastructure Ready (&lt; 900MB RAM)
          </span>
        </div>
      </header>

      {/* Hero Badge Section */}
      <section className="bg-card rounded-2xl p-6 md:p-8 border border-border shadow-sm flex flex-col gap-4">
        <div className="flex items-center gap-2 text-brand-primary text-sm font-semibold uppercase tracking-wider">
          <Zap className="w-4 h-4" />
          Story 1.1 Verified Architecture
        </div>
        <h2 className="text-xl md:text-2xl font-bold">
          Modular Monorepo Skeleton &amp; Distributed Infrastructure
        </h2>
        <p className="text-muted leading-relaxed max-w-3xl">
          CodeConnect is structured as an event-driven microservices architecture. Backend services run as independent,
          standalone Maven projects on Java 21 LTS and Spring Boot 3.3.4, bound to high-performance local Docker backing services.
        </p>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
          <div className="p-4 rounded-xl bg-background border border-border flex items-start gap-3">
            <ShieldCheck className="w-5 h-5 text-brand-primary mt-0.5" />
            <div>
              <div className="text-xs text-muted font-medium">Session Strategy</div>
              <div className="text-sm font-semibold">Opaque Redis Cookies (Zero JWT in Browser)</div>
            </div>
          </div>
          <div className="p-4 rounded-xl bg-background border border-border flex items-start gap-3">
            <Cpu className="w-5 h-5 text-brand-primary mt-0.5" />
            <div>
              <div className="text-xs text-muted font-medium">Build Independence</div>
              <div className="text-sm font-semibold">Standalone POMs (No Reactor Coupling)</div>
            </div>
          </div>
          <div className="p-4 rounded-xl bg-background border border-border flex items-start gap-3">
            <Terminal className="w-5 h-5 text-brand-primary mt-0.5" />
            <div>
              <div className="text-xs text-muted font-medium">Domain Architecture</div>
              <div className="text-sm font-semibold">Strict 4-Tier DDD Packaging</div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Grid */}
      <section className="flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-bold flex items-center gap-2">
            <Server className="w-5 h-5 text-brand-primary" />
            Platform Services &amp; Infrastructure Grid
          </h3>
          <span className="text-xs text-muted">5 Services Configured</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {services.map((svc) => (
            <div
              key={svc.name}
              className="bg-card rounded-xl p-5 border border-border shadow-sm flex flex-col justify-between hover:border-brand-primary/40 transition-colors"
            >
              <div className="flex flex-col gap-2">
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-base flex items-center gap-2">
                    <Database className="w-4 h-4 text-brand-primary" />
                    {svc.name}
                  </span>
                  <span className="text-xs font-mono px-2 py-0.5 rounded bg-background border border-border text-muted">
                    Port :{svc.port}
                  </span>
                </div>
                <p className="text-xs text-muted">{svc.description}</p>
              </div>
              <div className="flex items-center justify-between pt-4 mt-4 border-t border-border text-xs">
                <span className="text-muted font-mono">{svc.type}</span>
                <span className="inline-flex items-center gap-1 font-medium text-emerald-600">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                  {svc.status}
                </span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer className="pt-6 border-t border-border flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-muted">
        <div>CodeConnect Platform &copy; 2026. Built with Clean Code &amp; DDD Standards.</div>
        <div className="font-mono">Next.js 15 &bull; Spring Boot 3.3.4 &bull; Java 21 LTS</div>
      </footer>
    </main>
  );
}
