"use client";

import React, { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { authService } from "@/service/AuthService";
import { SignupValidator } from "@/validator/SignupValidator";
import { ApiError } from "@/client/HttpClient";
import { UserRole } from "@/domain/enums/UserRole";

export interface SignupFormData {
  readonly role: UserRole;
  readonly displayName: string;
  readonly email: string;
  readonly password: string;
  readonly linkedInUrl: string;
  readonly bio: string;
}

export const INITIAL_SIGNUP_FORM_DATA: SignupFormData = {
  role: "ROLE_STUDENT",
  displayName: "",
  email: "",
  password: "",
  linkedInUrl: "",
  bio: "",
};

export interface SignupControllerResult {
  readonly formData: SignupFormData;
  readonly isLoading: boolean;
  readonly errorMessage: string | null;
  readonly isDuplicateEmail: boolean;
  readonly fieldErrors: Record<string, string>;
  readonly setFormData: (patch: Partial<SignupFormData>) => void;
  readonly handleSubmit: (e: React.FormEvent) => Promise<void>;
}

/**
 * Controller orchestrating client state and events for user registration.
 * Delegates validation to SignupValidator and execution to AuthService.
 */
export function useSignupController(): SignupControllerResult {
  const router = useRouter();
  const searchParams = useSearchParams();
  const defaultRole: UserRole = searchParams.get("role") === "mentor" ? "ROLE_MENTOR" : "ROLE_STUDENT";

  const [formData, setFormDataState] = useState<SignupFormData>({
    ...INITIAL_SIGNUP_FORM_DATA,
    role: defaultRole,
  });

  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isDuplicateEmail, setIsDuplicateEmail] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const setFormData = (patch: Partial<SignupFormData>): void => {
    setFormDataState((prev) => ({ ...prev, ...patch }));
  };

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    setErrorMessage(null);
    setIsDuplicateEmail(false);

    const validationErrors = SignupValidator.validate(formData);
    setFieldErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    setIsLoading(true);
    try {
      await authService.signup({
        displayName: formData.displayName.trim(),
        email: formData.email.trim(),
        password: formData.password,
        role: formData.role,
        linkedInUrl: formData.role === "ROLE_MENTOR" ? formData.linkedInUrl.trim() : undefined,
        bio: formData.role === "ROLE_MENTOR" ? formData.bio.trim() : undefined,
      });
      handleSignupSuccess(formData.role);
    } catch (err: unknown) {
      handleSignupError(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSignupSuccess = (selectedRole: UserRole): void => {
    if (selectedRole === "ROLE_MENTOR") {
      router.push("/pending-approval");
    } else {
      router.push("/?registered=true");
    }
  };

  const handleSignupError = (err: unknown): void => {
    if (err instanceof ApiError) {
      if (err.status === 409) {
        setIsDuplicateEmail(true);
        setErrorMessage(err.message);
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
    isDuplicateEmail,
    fieldErrors,
    setFormData,
    handleSubmit,
  };
}
