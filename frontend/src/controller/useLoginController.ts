"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { authService } from "@/service/AuthService";
import { LoginValidator } from "@/validator/LoginValidator";
import { ApiError } from "@/client/HttpClient";
import { UserResponse } from "@/dto/response/UserResponse";

export interface LoginFormData {
  readonly email: string;
  readonly password: string;
}

export const INITIAL_LOGIN_FORM_DATA: LoginFormData = {
  email: "",
  password: "",
};

export interface LoginControllerResult {
  readonly formData: LoginFormData;
  readonly isLoading: boolean;
  readonly errorMessage: string | null;
  readonly fieldErrors: Record<string, string>;
  readonly setFormData: (patch: Partial<LoginFormData>) => void;
  readonly handleSubmit: (e: React.FormEvent) => Promise<void>;
}

/**
 * Controller orchestrating client state and events for user login.
 * Delegates validation to LoginValidator and execution to AuthService.
 */
export function useLoginController(): LoginControllerResult {
  const router = useRouter();
  const [formData, setFormDataState] = useState<LoginFormData>(INITIAL_LOGIN_FORM_DATA);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const setFormData = (patch: Partial<LoginFormData>): void => {
    setFormDataState((prev) => ({ ...prev, ...patch }));
  };

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    setErrorMessage(null);

    const validationErrors = LoginValidator.validate(formData);
    setFieldErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    setIsLoading(true);
    try {
      const user = await authService.login({ email: formData.email.trim(), password: formData.password });
      handleLoginSuccess(user);
    } catch (err: unknown) {
      handleLoginError(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLoginSuccess = (user: UserResponse): void => {
    if (user.role === "ROLE_MENTOR" && user.status === "PENDING_APPROVAL") {
      router.push("/pending-approval");
    } else {
      router.push("/?authenticated=true");
    }
  };

  const handleLoginError = (err: unknown): void => {
    if (err instanceof ApiError) {
      if (err.status === 401) {
        setErrorMessage("Invalid email or password. Please verify your credentials.");
      } else if (err.status === 403) {
        setErrorMessage("Access denied. You do not have permission to log in.");
      } else if (err.problemDetail?.errors) {
        setFieldErrors(err.problemDetail.errors);
        setErrorMessage("Please review and correct the errors below.");
      } else {
        setErrorMessage(err.message);
      }
    } else {
      setErrorMessage("An unexpected network error occurred. Please try again.");
    }
  };

  return {
    formData,
    isLoading,
    errorMessage,
    fieldErrors,
    setFormData,
    handleSubmit,
  };
}
