import React, { useState } from "react";

export interface FormInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  helperText?: string;
  showTogglePassword?: boolean;
  icon?: React.ReactNode;
}

export const FormInput: React.FC<FormInputProps> = ({
  id,
  name,
  label,
  type = "text",
  error,
  helperText,
  showTogglePassword = false,
  icon,
  className = "",
  ...props
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const inputType = showTogglePassword ? (showPassword ? "text" : "password") : type;

  return (
    <div className="w-full">
      <div className="flex justify-between items-center mb-1.5">
        <label htmlFor={id || name} className="block text-xs font-semibold text-slate-700 tracking-tight">
          {label}
        </label>
      </div>

      <div className="relative group">
        {icon && (
          <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-600 transition-colors">
            {icon}
          </div>
        )}

        <input
          id={id || name}
          name={name}
          type={inputType}
          className={`block w-full ${icon ? "pl-10" : "px-3.5"} ${
            showTogglePassword ? "pr-14" : "pr-3.5"
          } py-2.5 rounded-xl border text-sm text-slate-900 placeholder:text-slate-400 bg-white/90 shadow-sm transition-all duration-200 outline-none ${
            error
              ? "border-red-400 bg-red-50/20 focus:border-red-500 focus:ring-4 focus:ring-red-500/10"
              : "border-slate-200 hover:border-slate-300 focus:border-blue-600 focus:ring-4 focus:ring-blue-500/15"
          } ${className}`}
          {...props}
        />

        {showTogglePassword && (
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            tabIndex={-1}
            className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-xs text-slate-400 hover:text-slate-700 font-semibold cursor-pointer select-none transition-colors"
          >
            {showPassword ? "Hide" : "Show"}
          </button>
        )}
      </div>

      {error ? (
        <p className="mt-1.5 text-xs text-red-600 font-medium flex items-center gap-1">
          <span className="inline-block w-1 h-1 rounded-full bg-red-500"></span>
          {error}
        </p>
      ) : helperText ? (
        <p className="mt-1 text-xs text-slate-500">{helperText}</p>
      ) : null}
    </div>
  );
};
