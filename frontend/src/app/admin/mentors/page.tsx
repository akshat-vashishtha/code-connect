"use client";

import React from "react";
import { useAdminMentorController } from "@/controller/useAdminMentorController";
import { useLanguageDetectorController } from "@/controller/useLanguageDetectorController";
import { MentorApprovalTableView } from "@/presentation/views/MentorApprovalTableView";
import { LanguageDetectorCardView } from "@/presentation/views/LanguageDetectorCardView";

/**
 * Route controller for /admin/mentors.
 * Composes AdminMentorController and LanguageDetectorController into presentational views.
 */
export default function AdminMentorsPage() {
  const adminMentorController = useAdminMentorController();
  const languageDetectorController = useLanguageDetectorController();

  return (
    <MentorApprovalTableView
      controller={adminMentorController}
      detectorSlot={<LanguageDetectorCardView controller={languageDetectorController} />}
    />
  );
}
