import React from "react";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

export type StatusPillType =
  | "ACTIVE"
  | "SOLVING"
  | "CONQUERED"
  | "LOCKED"
  | "PASS"
  | "FAIL"
  | "TIMED_OUT"
  | "COMPILE_ERROR"
  | "PENDING_VERIFICATION"
  | "TIER_3_ESCALATED"
  | "DRAFT"
  | "PUBLISHED"
  | "ONLINE"
  | "OFFLINE";

export interface StatusPillProps {
  readonly status: StatusPillType;
  readonly label?: string;
  readonly size?: "sm" | "md";
  readonly className?: string;
}

/**
 * Universal Status Pill component matching all Wireframe badges.
 * Supports automated coloring, icons, and text formatting.
 */
export const StatusPill: React.FC<StatusPillProps> = ({
  status,
  label,
  size = "md",
  className = "",
}) => {
  const isSm = size === "sm";

  const getStyleAndContent = (): {
    classes: string;
    text: string;
    icon?: React.ReactNode;
  } => {
    switch (status) {
      case "ACTIVE":
        return {
          classes: "bg-emerald-50 border-emerald-200 text-emerald-700",
          text: label ?? "ACTIVE",
        };
      case "SOLVING":
        return {
          classes: "bg-emerald-50 border-emerald-200 text-emerald-700",
          text: label ?? "SOLVING",
        };
      case "CONQUERED":
        return {
          classes: "bg-emerald-100 border-emerald-300 text-emerald-800",
          text: label ?? "CONQUERED",
          icon: <VectorIcon name="checkmark" size={12} className="text-emerald-700" />,
        };
      case "LOCKED":
        return {
          classes: "bg-slate-100 border-slate-200 text-slate-500",
          text: label ?? "LOCKED",
          icon: <VectorIcon name="lock" size={11} className="text-slate-400" />,
        };
      case "PASS":
        return {
          classes: "bg-emerald-50 border-emerald-200 text-emerald-700 font-bold",
          text: label ?? "PASS",
          icon: <VectorIcon name="checkmark" size={12} className="text-emerald-600" />,
        };
      case "FAIL":
        return {
          classes: "bg-rose-50 border-rose-200 text-rose-700 font-bold",
          text: label ?? "FAIL",
          icon: <VectorIcon name="error-x" size={12} className="text-rose-600" />,
        };
      case "TIMED_OUT":
        return {
          classes: "bg-amber-50 border-amber-300 text-amber-800 font-bold",
          text: label ?? "TIMED OUT",
          icon: <VectorIcon name="clock" size={12} className="text-amber-700" />,
        };
      case "COMPILE_ERROR":
        return {
          classes: "bg-rose-100 border-rose-300 text-rose-800 font-bold",
          text: label ?? "COMPILE ERROR",
          icon: <VectorIcon name="error-x" size={12} className="text-rose-700" />,
        };
      case "PENDING_VERIFICATION":
        return {
          classes: "bg-amber-50 border-amber-200 text-amber-800 font-semibold",
          text: label ?? "Pending Verification",
        };
      case "TIER_3_ESCALATED":
        return {
          classes: "bg-rose-50 border-rose-200 text-rose-700 font-bold tracking-wide",
          text: label ?? "TIER 3 ESCALATED",
        };
      case "DRAFT":
        return {
          classes: "bg-slate-100 border-slate-300 text-slate-600 font-semibold",
          text: label ?? "DRAFT",
        };
      case "PUBLISHED":
        return {
          classes: "bg-emerald-50 border-emerald-200 text-emerald-700 font-bold",
          text: label ?? "PUBLISHED",
          icon: <VectorIcon name="checkmark" size={12} className="text-emerald-600" />,
        };
      case "ONLINE":
        return {
          classes: "bg-emerald-50 border-emerald-200 text-emerald-700 font-semibold",
          text: label ?? "ONLINE",
          icon: <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />,
        };
      case "OFFLINE":
        return {
          classes: "bg-slate-100 border-slate-200 text-slate-500",
          text: label ?? "OFFLINE",
          icon: <span className="w-2 h-2 rounded-full bg-slate-400" />,
        };
    }
  };

  const { classes, text, icon } = getStyleAndContent();

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full border select-none ${
        isSm ? "px-2 py-0.5 text-[10px]" : "px-2.5 py-1 text-xs"
      } ${classes} ${className}`}
    >
      {icon}
      <span>{text}</span>
    </span>
  );
};
