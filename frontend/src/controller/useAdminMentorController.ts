"use client";

import { useEffect, useState, useTransition } from "react";
import { authService } from "@/service/AuthService";
import { mentorApprovalService } from "@/service/MentorApprovalService";
import { UserResponse } from "@/dto/response/UserResponse";
import { MentorApprovalResponse } from "@/dto/response/MentorApprovalResponse";

export interface FeedbackMessage {
  readonly type: "success" | "error";
  readonly text: string;
}

export interface AdminMentorControllerResult {
  readonly currentUser: UserResponse | null;
  readonly isAuthLoading: boolean;
  readonly unauthorized: boolean;
  readonly filteredApplications: MentorApprovalResponse[];
  readonly isDataLoading: boolean;
  readonly searchQuery: string;
  readonly actionInProgress: string | null;
  readonly feedbackMessage: FeedbackMessage | null;
  readonly setSearchQuery: (q: string) => void;
  readonly loadPendingApplications: () => Promise<void>;
  readonly handleApprove: (appId: string, email: string) => Promise<void>;
  readonly handleReject: (appId: string, email: string) => Promise<void>;
}

/**
 * Controller orchestrating mentor application queue review and approvals.
 * Adheres strictly to SRP by isolating mentor approvals from language testing.
 */
export function useAdminMentorController(): AdminMentorControllerResult {
  const [currentUser, setCurrentUser] = useState<UserResponse | null>(null);
  const [isAuthLoading, setIsAuthLoading] = useState(true);
  const [unauthorized, setUnauthorized] = useState(false);
  const [applications, setApplications] = useState<MentorApprovalResponse[]>([]);
  const [isDataLoading, setIsDataLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [actionInProgress, setActionInProgress] = useState<string | null>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<FeedbackMessage | null>(null);
  const [, startTransition] = useTransition();

  useEffect(() => {
    let isMounted = true;
    async function init() {
      const user = await authService.getCurrentUser();
      if (!isMounted) return;
      if (user && user.role === "ROLE_ADMIN") {
        setCurrentUser(user);
        loadPendingApplications();
      } else {
        setUnauthorized(true);
      }
      setIsAuthLoading(false);
    }
    init();
    return () => { isMounted = false; };
  }, []);

  const loadPendingApplications = async (): Promise<void> => {
    setIsDataLoading(true);
    setFeedbackMessage(null);
    try {
      const data = await mentorApprovalService.getPendingApplications();
      startTransition(() => setApplications(data));
    } catch (err: unknown) {
      const text = err instanceof Error ? err.message : "Failed to load pending applications";
      setFeedbackMessage({ type: "error", text });
    } finally {
      setIsDataLoading(false);
    }
  };

  const handleApprove = async (appId: string, email: string): Promise<void> => {
    setActionInProgress(appId);
    setFeedbackMessage(null);
    try {
      await mentorApprovalService.approveApplication(appId);
      setApplications((prev) => prev.filter((a) => a.id !== appId));
      setFeedbackMessage({
        type: "success",
        text: `Application for ${email} approved! Elevated user to ROLE_MENTOR and active Redis session updated in O(1) time.`,
      });
    } catch (err: unknown) {
      const text = err instanceof Error ? err.message : "Approval failed";
      setFeedbackMessage({ type: "error", text });
    } finally {
      setActionInProgress(null);
    }
  };

  const handleReject = async (appId: string, email: string): Promise<void> => {
    setActionInProgress(appId);
    setFeedbackMessage(null);
    try {
      await mentorApprovalService.rejectApplication(appId);
      setApplications((prev) => prev.filter((a) => a.id !== appId));
      setFeedbackMessage({ type: "success", text: `Application for ${email} has been rejected.` });
    } catch (err: unknown) {
      const text = err instanceof Error ? err.message : "Rejection failed";
      setFeedbackMessage({ type: "error", text });
    } finally {
      setActionInProgress(null);
    }
  };

  const filteredApplications = applications.filter((app) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      app.email.toLowerCase().includes(q) ||
      (app.bio && app.bio.toLowerCase().includes(q)) ||
      (app.linkedInUrl && app.linkedInUrl.toLowerCase().includes(q))
    );
  });

  return {
    currentUser,
    isAuthLoading,
    unauthorized,
    filteredApplications,
    isDataLoading,
    searchQuery,
    actionInProgress,
    feedbackMessage,
    setSearchQuery,
    loadPendingApplications,
    handleApprove,
    handleReject,
  };
}
