import React from "react";
import { VectorIcon } from "@/presentation/atoms/VectorIcon";

export interface AlertBannerProps {
  readonly variant?: "error" | "warning" | "success" | "info";
  readonly title?: string;
  readonly message: string;
  readonly className?: string;
}

/**
 * Clean AlertBanner atom using pure SVG vector icons.
 * Eliminates all emojis and comic glyphs.
 */
export const AlertBanner: React.FC<AlertBannerProps> = ({
  variant = "error",
  title,
  message,
  className = "",
}) => {
  const getVariantStyles = () => {
    switch (variant) {
      case "error":
        return {
          container: "bg-rose-50 border-rose-200 text-rose-900",
          iconName: "error-x" as const,
          iconClass: "text-rose-600",
          defaultTitle: "Operation Failed",
        };
      case "warning":
        return {
          container: "bg-amber-50 border-amber-200 text-amber-900",
          iconName: "shield" as const,
          iconClass: "text-amber-600",
          defaultTitle: "Attention Required",
        };
      case "success":
        return {
          container: "bg-emerald-50 border-emerald-200 text-emerald-900",
          iconName: "checkmark" as const,
          iconClass: "text-emerald-600",
          defaultTitle: "Success",
        };
      case "info":
      default:
        return {
          container: "bg-blue-50 border-blue-200 text-blue-900",
          iconName: "lightbulb" as const,
          iconClass: "text-blue-600",
          defaultTitle: "Notice",
        };
    }
  };

  const { container, iconName, iconClass, defaultTitle } = getVariantStyles();

  return (
    <div
      className={`rounded-xl p-3.5 text-xs flex items-start gap-3 border ${container} ${className}`}
    >
      <VectorIcon name={iconName} size={15} className={`shrink-0 mt-0.5 ${iconClass}`} />
      <div className="flex-1">
        <p className="font-bold">{title || defaultTitle}</p>
        <p className="mt-0.5 text-[11px] leading-relaxed opacity-90">{message}</p>
      </div>
    </div>
  );
};
