import React from "react";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

export interface AltitudeBadgeProps {
  readonly currentAltitude: number;
  readonly maxAltitude?: number;
  readonly variant?: "light-blue" | "neutral";
  readonly className?: string;
  readonly showLabel?: boolean;
}

/**
 * Elevation altitude badge matching CodeConnect Wireframes 00, 02, and 07.
 * Standardizes right-padding and mountain vector rendering.
 */
export const AltitudeBadge: React.FC<AltitudeBadgeProps> = ({
  currentAltitude,
  maxAltitude = 4000,
  variant = "light-blue",
  className = "",
  showLabel = true,
}) => {
  const isLightBlue = variant === "light-blue";

  return (
    <div
      className={`inline-flex items-center gap-2 px-3.5 py-1.5 rounded-lg border text-xs font-semibold select-none ${
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
    </div>
  );
};
