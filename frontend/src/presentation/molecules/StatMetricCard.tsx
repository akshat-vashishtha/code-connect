import React from "react";

export interface StatMetricCardProps {
  readonly title: string;
  readonly value: string | number;
  readonly subtitle?: string;
  readonly isUrgent?: boolean;
  readonly valueColor?: "default" | "emerald" | "amber" | "blue";
  readonly className?: string;
}

/**
 * Metric Card matching Wireframe 09 and Wireframe 07.
 * Standardizes 16px margins, left-aligned values, and right-aligned status labels.
 */
export const StatMetricCard: React.FC<StatMetricCardProps> = ({
  title,
  value,
  subtitle,
  isUrgent = false,
  valueColor = "default",
  className = "",
}) => {
  const getValueColor = () => {
    switch (valueColor) {
      case "emerald":
        return "text-emerald-700";
      case "amber":
        return "text-amber-800";
      case "blue":
        return "text-blue-700";
      default:
        return "text-slate-900";
    }
  };

  return (
    <div
      className={`relative p-4 rounded-xl border bg-white shadow-sm flex flex-col justify-between ${
        isUrgent
          ? "border-amber-300 ring-1 ring-amber-200/50 bg-amber-50/20"
          : "border-slate-200"
      } ${className}`}
    >
      <div
        className={`text-[10px] font-bold tracking-wider uppercase ${
          isUrgent ? "text-amber-800" : "text-slate-500"
        }`}
      >
        {title}
      </div>

      <div className="flex items-baseline justify-between mt-2">
        <span className={`text-2xl font-extrabold tracking-tight ${getValueColor()}`}>
          {value}
        </span>

        {subtitle && (
          <span
            className={`text-xs font-semibold text-right ${
              isUrgent ? "text-amber-700" : "text-slate-600"
            }`}
          >
            {subtitle}
          </span>
        )}
      </div>
    </div>
  );
};
