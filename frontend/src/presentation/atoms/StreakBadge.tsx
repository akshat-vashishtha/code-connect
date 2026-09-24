import React from "react";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

export interface StreakBadgeProps {
  readonly streakDays: number;
  readonly className?: string;
}

/**
 * Momentum Streak Badge matching Wireframe 00 and 07.
 * Renders pure vector flame path and warm amber styling.
 */
export const StreakBadge: React.FC<StreakBadgeProps> = ({
  streakDays,
  className = "",
}) => {
  return (
    <div
      className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full border border-amber-200/80 bg-amber-50/80 text-amber-800 text-xs font-bold select-none ${className}`}
    >
      <VectorIcon name="flame" size={13} className="text-amber-600" />
      <span>{streakDays} Days</span>
    </div>
  );
};
