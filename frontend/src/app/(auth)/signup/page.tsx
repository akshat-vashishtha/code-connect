"use client";

import React from "react";
import { useSignupController } from "@/controller/useSignupController";
import { SignupFormView } from "@/presentation/views/SignupFormView";

/**
 * Route controller for /signup.
 * Acts as the Spring @Controller shell, injecting the useSignupController
 * and binding it to the SignupFormView presentation component.
 */
export default function SignupPage() {
  const controller = useSignupController();
  return <SignupFormView controller={controller} />;
}
