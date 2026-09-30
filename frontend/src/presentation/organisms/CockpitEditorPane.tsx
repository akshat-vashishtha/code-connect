"use client";

import React, { useEffect } from "react";
import dynamic from "next/dynamic";
import {
  Maximize2,
  Minimize2,
  RotateCcw,
  Check,
  Sun,
  Moon,
  Code2,
  Play,
  Loader2,
} from "lucide-react";

// Dynamic import for Monaco Editor to guarantee zero SSR hydration issues
const Editor = dynamic(() => import("@monaco-editor/react"), { ssr: false });

export interface CockpitEditorPaneProps {
  readonly code: string;
  readonly isExpanded: boolean;
  readonly isSaved: boolean;
  readonly isRunning?: boolean;
  readonly theme: "vs-dark" | "light";
  readonly fontSize: number;
  readonly onChange: (newCode: string) => void;
  readonly onToggleExpand: () => void;
  readonly onReset: () => void;
  readonly onThemeChange: (theme: "vs-dark" | "light") => void;
  readonly onFontSizeChange: (size: number) => void;
  readonly onRunCode?: () => void;
}

export const CockpitEditorPane: React.FC<CockpitEditorPaneProps> = ({
  code,
  isExpanded,
  isSaved,
  isRunning = false,
  theme,
  fontSize,
  onChange,
  onToggleExpand,
  onReset,
  onThemeChange,
  onFontSizeChange,
  onRunCode,
}) => {
  // Global shortcut: Cmd+Enter or Ctrl+Enter to Run Code
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        if (onRunCode && !isRunning) {
          onRunCode();
        }
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [onRunCode, isRunning]);

  return (
    <section className="flex flex-col h-full bg-slate-950 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden">
      {/* Editor Top Toolbar */}
      <header className="flex items-center justify-between px-4 py-2.5 bg-slate-900/90 border-b border-slate-800/80 backdrop-blur-md">
        {/* Left Toolbar Items: Language & Autosave Status */}
        <div className="flex items-center space-x-3">
          <div className="flex items-center space-x-2 px-2.5 py-1 bg-amber-950/40 border border-amber-800/60 rounded-md text-amber-300 text-xs font-semibold">
            <Code2 className="w-3.5 h-3.5 text-amber-400" />
            <span>Java 21</span>
          </div>

          <div className="flex items-center space-x-1.5 text-xs text-slate-400">
            {isSaved ? (
              <>
                <Check className="w-3.5 h-3.5 text-emerald-400" />
                <span className="text-emerald-400/90 hidden sm:inline">Draft saved</span>
              </>
            ) : (
              <span className="text-amber-400/90 italic">Saving draft...</span>
            )}
          </div>
        </div>

        {/* Right Toolbar Items: Run Code & Actions */}
        <div className="flex items-center space-x-2">
          {/* Run Code Primary Action Button */}
          {onRunCode && (
            <button
              type="button"
              onClick={onRunCode}
              disabled={isRunning}
              className={`flex items-center space-x-1.5 px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all shadow-md ${
                isRunning
                  ? "bg-amber-500/30 text-amber-300 border border-amber-500/40 cursor-not-allowed"
                  : "bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-950/50 hover:scale-105 active:scale-95"
              }`}
              title="Run Code against sandbox tests (Cmd+Enter / Ctrl+Enter)"
            >
              {isRunning ? (
                <>
                  <Loader2 className="w-3.5 h-3.5 animate-spin text-amber-300" />
                  <span>Running...</span>
                </>
              ) : (
                <>
                  <Play className="w-3.5 h-3.5 fill-current" />
                  <span>Run Code</span>
                </>
              )}
            </button>
          )}

          {/* Font Size Selector */}
          <select
            value={fontSize}
            aria-label="Editor Font Size"
            onChange={(e) => onFontSizeChange(Number(e.target.value))}
            className="px-2 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs rounded border border-slate-700 cursor-pointer focus:outline-none focus:ring-1 focus:ring-cyan-500"
          >
            <option value={13}>13px</option>
            <option value={14}>14px</option>
            <option value={16}>16px</option>
            <option value={18}>18px</option>
          </select>

          {/* Theme Switcher Button */}
          <button
            type="button"
            onClick={() => onThemeChange(theme === "vs-dark" ? "light" : "vs-dark")}
            className="p-1.5 text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded transition-colors"
            title={`Switch to ${theme === "vs-dark" ? "Light" : "Dark"} theme`}
          >
            {theme === "vs-dark" ? (
              <Sun className="w-4 h-4 text-amber-400" />
            ) : (
              <Moon className="w-4 h-4 text-slate-300" />
            )}
          </button>

          {/* Reset Code Button */}
          <button
            type="button"
            onClick={onReset}
            className="flex items-center space-x-1 px-2.5 py-1 text-xs text-slate-400 hover:text-rose-400 hover:bg-rose-950/30 rounded border border-transparent hover:border-rose-900/50 transition-colors"
            title="Reset to starter code"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            <span className="hidden md:inline">Reset</span>
          </button>

          {/* Expand / Collapse Button */}
          <button
            type="button"
            onClick={onToggleExpand}
            className="flex items-center space-x-1.5 px-3 py-1 bg-cyan-950/40 hover:bg-cyan-900/60 text-cyan-300 border border-cyan-800/60 rounded text-xs font-medium transition-all shadow-xs"
            title={isExpanded ? "Collapse to 50/50 split mode" : "Expand editor to full width"}
          >
            {isExpanded ? (
              <>
                <Minimize2 className="w-3.5 h-3.5" />
                <span>Split View</span>
              </>
            ) : (
              <>
                <Maximize2 className="w-3.5 h-3.5" />
                <span>Expand</span>
              </>
            )}
          </button>
        </div>
      </header>

      {/* Monaco Editor Container */}
      <div className="flex-1 w-full min-h-[450px] relative bg-slate-950">
        <Editor
          height="100%"
          language="java"
          theme={theme}
          value={code}
          onChange={(val) => onChange(val || "")}
          options={{
            fontSize: fontSize,
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            automaticLayout: true,
            tabSize: 4,
            wordWrap: "on",
            padding: { top: 16, bottom: 16 },
            fontFamily: "var(--font-geist-mono), 'Fira Code', Menlo, Monaco, monospace",
            formatOnPaste: true,
            formatOnType: true,
            suggestOnTriggerCharacters: true,
            cursorBlinking: "smooth",
            cursorSmoothCaretAnimation: "on",
            smoothScrolling: true,
          }}
          loading={
            <div className="flex items-center justify-center h-full bg-slate-950 text-slate-400 text-sm">
              <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-cyan-400 mr-3" />
              Loading Monaco Editor...
            </div>
          }
        />
      </div>
    </section>
  );
};
