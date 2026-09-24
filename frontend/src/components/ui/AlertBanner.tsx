import React from "react";

export interface AlertBannerProps {
  variant?: "error" | "warning" | "success" | "info";
  title?: string;
  message: string;
  className?: string;
}

export const AlertBanner: React.FC<AlertBannerProps> = ({
  variant = "error",
  title,
  message,
  className = "",
}) => {
  const styles = {
    error: {
      container: "bg-red-50 border-red-300 text-red-900",
      icon: "❌",
      defaultTitle: "Operation Failed",
    },
    warning: {
      container: "bg-amber-50 border-amber-300 text-amber-900",
      icon: "⚠️",
      defaultTitle: "Attention Required",
    },
    success: {
      container: "bg-emerald-50 border-emerald-300 text-emerald-900",
      icon: "✅",
      defaultTitle: "Success",
    },
    info: {
      container: "bg-blue-50 border-blue-300 text-blue-900",
      icon: "ℹ️",
      defaultTitle: "Information",
    },
  }[variant];

  return (
    <div className={`rounded-xl p-4 text-sm flex items-start gap-3 border ${styles.container} ${className}`}>
      <span className="text-base leading-none select-none">{styles.icon}</span>
      <div className="flex-1">
        <p className="font-semibold">{title || styles.defaultTitle}</p>
        <p className="mt-0.5 text-xs opacity-90">{message}</p>
      </div>
    </div>
  );
};
