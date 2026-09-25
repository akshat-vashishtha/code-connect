import React from "react";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

export interface AltitudeBadgeProps {
  readonly currentAltitude: number;
  readonly maxAltitude?: number;
  readonly variant?: "light-blue" | "neutral";
  readonly className?: string;
  readonly showLabel?: boolean;
  readonly showProgress?: boolean;
}

/**
 * Elevation altitude badge matching CodeConnect Wireframes 00, 02, and 07.
 * Shows current/max altitude with optional mini progress bar.
 *
 * UX Improvements (Sally's Epic 1 pass):
 * - animate-altitude entrance animation on first mount
 * - Optional inline progress bar to visualise summit progress
 * - More compact label (no verbose "Altitude:" prefix by default)
 */
export const AltitudeBadge: React.FC<AltitudeBadgeProps> = ({
  currentAltitude,
  maxAltitude = 4000,
  variant = "light-blue",
  className = "",
  showLabel = false,
  showProgress = false,
}) => {
  const isLightBlue = variant === "light-blue";
  const progressPercent = Math.min((currentAltitude / maxAltitude) * 100, 100);

  return (
    <div
      className={`inline-flex items-center gap-2 px-3.5 py-1.5 rounded-lg border text-xs font-semibold select-none animate-altitude ${
        isLightBlue
          ? "bg-blue-50/80 border-blue-200/80 text-blue-700"
          : "bg-slate-100 border-slate-200 text-slate-700"
      } ${className}`}
    >
      <VectorIcon
        name="mountain"
        size={14}
        strokeWidth={1.8}
        className={isLightBlue ? "text-blue-600" : "text-slate-600"}
      />
      <span>
        {showLabel && <span className="font-medium mr-1">Altitude:</span>}
        <span className="font-bold">{currentAltitude.toLocaleString()}m</span>
        <span className="text-slate-400 font-normal ml-1">/ {maxAltitude.toLocaleString()}m</span>
      </span>

      {showProgress && (
        <div className="w-12 h-1.5 rounded-full bg-blue-100 overflow-hidden ml-0.5">
          <div
            className="h-full rounded-full bg-blue-600 transition-all duration-700"
            style={{ width: `${progressPercent}%` }}
          />
        </div>
      )}
    </div>
  );
};
