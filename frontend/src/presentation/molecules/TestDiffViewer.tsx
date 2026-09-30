'use client';

import React from 'react';
import { Check, X, Copy } from 'lucide-react';

interface TestDiffViewerProps {
  expected: string;
  actual: string;
  passed: boolean;
  hidden?: boolean;
}

export const TestDiffViewer: React.FC<TestDiffViewerProps> = ({
  expected,
  actual,
  passed,
  hidden = false,
}) => {
  const [copied, setCopied] = React.useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(actual);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  if (hidden) {
    return (
      <div className="p-4 rounded-lg bg-neutral-900/60 border border-neutral-800 text-sm text-neutral-400 italic">
        🔒 Hidden Test Case: Inputs and outputs are redacted to test boundary edge cases independently.
      </div>
    );
  }

  return (
    <div className="space-y-3 font-mono text-xs">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {/* Expected Output Pane */}
        <div className="rounded-lg border border-emerald-500/30 bg-emerald-950/20 p-3">
          <div className="flex items-center justify-between pb-2 mb-2 border-b border-emerald-500/20 text-emerald-400 font-sans font-semibold text-xs">
            <span className="flex items-center gap-1.5">
              <Check className="w-3.5 h-3.5" /> Expected Output
            </span>
          </div>
          <pre className="whitespace-pre-wrap break-all text-neutral-200 overflow-x-auto max-h-40">
            {expected || '<empty>'}
          </pre>
        </div>

        {/* Actual Output Pane */}
        <div
          className={`rounded-lg border p-3 ${
            passed
              ? 'border-emerald-500/30 bg-emerald-950/20'
              : 'border-rose-500/30 bg-rose-950/20'
          }`}
        >
          <div
            className={`flex items-center justify-between pb-2 mb-2 border-b text-xs font-sans font-semibold ${
              passed
                ? 'border-emerald-500/20 text-emerald-400'
                : 'border-rose-500/20 text-rose-400'
            }`}
          >
            <span className="flex items-center gap-1.5">
              {passed ? <Check className="w-3.5 h-3.5" /> : <X className="w-3.5 h-3.5" />}
              Actual Output
            </span>
            <button
              onClick={handleCopy}
              title="Copy Output"
              className="text-neutral-400 hover:text-neutral-200 transition-colors"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            </button>
          </div>
          <pre
            className={`whitespace-pre-wrap break-all overflow-x-auto max-h-40 ${
              passed ? 'text-neutral-200' : 'text-rose-200'
            }`}
          >
            {actual || '<empty>'}
          </pre>
        </div>
      </div>
    </div>
  );
};
