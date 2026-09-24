import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/presentation/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--bg-primary)",
        foreground: "var(--text-primary)",
        card: "var(--bg-card)",
        brand: {
          primary: "var(--brand-primary)",
          hover: "var(--brand-primary-hover)",
          accent: "var(--brand-accent)",
        },
        border: "var(--border-subtle)",
        muted: "var(--text-secondary)",
      },
      fontFamily: {
        sans: ["var(--font-sans)", "system-ui", "-apple-system", "sans-serif"],
      },
      boxShadow: {
        "glow-blue": "0 0 40px -10px rgba(37, 99, 235, 0.25)",
        "card-hover": "0 20px 35px -10px rgba(15, 23, 42, 0.08)",
      },
    },
  },
  plugins: [],
};

export default config;
