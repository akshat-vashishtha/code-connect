"use client";

import React from "react";
import { useLoginController } from "@/controller/useLoginController";
import { LoginFormView } from "@/presentation/views/LoginFormView";

/**
 * Route controller for /login.
 * Acts as the Spring @Controller shell, injecting the useLoginController
 * and binding it to the LoginFormView presentation component.
 */
export default function LoginPage() {
  const controller = useLoginController();
  return <LoginFormView controller={controller} />;
}
