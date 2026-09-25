"use client";

import React, { Suspense } from "react";
import { useSignupController } from "@/controller/useSignupController";
import { SignupFormView } from "@/presentation/views/SignupFormView";

function SignupContent() {
  const controller = useSignupController();
  return <SignupFormView controller={controller} />;
}

/**
 * Route controller for /signup.
 * Acts as the Spring @Controller shell, injecting useSignupController
 * inside Suspense boundary and binding to SignupFormView.
 */
export default function SignupPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-[#f8fafc]" />}>
      <SignupContent />
    </Suspense>
  );
}
