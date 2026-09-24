import React from "react";
import Link from "next/link";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";
import { StatusPill, StatusPillType } from "@/presentation/atoms/StatusPill";

export interface BreadcrumbBarProps {
  readonly trackTitle: string;
  readonly trackHref?: string;
  readonly footholdTitle: string;
  readonly status?: StatusPillType;
  readonly backHref?: string;
  readonly backLabel?: string;
  readonly className?: string;
}

/**
 * Context Breadcrumb Bar matching Wireframe 00 and 03.
 * Cleanly separates track route, foothold name, and live status pill with optional back navigation.
 */
export const BreadcrumbBar: React.FC<BreadcrumbBarProps> = ({
  trackTitle,
  trackHref = "/curriculum",
  footholdTitle,
  status = "ACTIVE",
  backHref,
  backLabel = "Back",
  className = "",
}) => {
  return (
    <div className={`inline-flex items-center gap-3 text-xs select-none ${className}`}>
      {backHref && (
        <Link
          href={backHref}
          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold transition-colors mr-1"
          title={`Go back to ${backLabel}`}
        >
          <VectorIcon name="arrow-left" size={12} strokeWidth={2} />
          <span>{backLabel}</span>
        </Link>
      )}

      <div className="w-5 h-5 rounded-md bg-blue-50 flex items-center justify-center text-blue-600">
        <VectorIcon name="mountain" size={13} strokeWidth={1.8} />
      </div>

      <Link
        href={trackHref}
        className="font-medium text-slate-500 hover:text-blue-600 transition-colors"
      >
        {trackTitle}
      </Link>

      <span className="text-slate-300 font-light">/</span>

      <span className="font-semibold text-slate-900">{footholdTitle}</span>

      {status && <StatusPill status={status} size="sm" className="ml-1" />}
    </div>
  );
};
