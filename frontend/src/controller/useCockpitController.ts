"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { LessonResponse, LanguageMode } from "@/types/curriculum";
import { SubmissionResultResponse } from "@/types/submission";
import { SubmissionService } from "@/service/submissionService";
import { getStompClient } from "@/lib/websocket/stompClient";

export interface UseCockpitControllerProps {
  initialLesson?: LessonResponse | null;
  lessonId?: string;
  onFootholdCompleted?: () => void;
}

export interface CockpitControllerResult {
  readonly lesson: LessonResponse | null;
  readonly code: string;
  readonly isExpanded: boolean;
  readonly activeTab: "story" | "editor" | "tests";
  readonly editorTheme: "vs-dark" | "light";
  readonly fontSize: number;
  readonly isSaved: boolean;
  readonly languageMode: LanguageMode;
  readonly isRunning: boolean;
  readonly consoleOpen: boolean;
  readonly submissionId: string | null;
  readonly submissionResult: SubmissionResultResponse | null;
  readonly submitError: string | null;
  readonly isAllPassed: boolean;
  readonly setCode: (newCode: string) => void;
  readonly toggleExpanded: () => void;
  readonly setActiveTab: (tab: "story" | "editor" | "tests") => void;
  readonly setEditorTheme: (theme: "vs-dark" | "light") => void;
  readonly setFontSize: (size: number) => void;
  readonly setLanguageMode: (mode: LanguageMode) => void;
  readonly setConsoleOpen: (open: boolean) => void;
  readonly resetToStarterCode: () => void;
  readonly handleRunCode: () => Promise<void>;
}

const DEFAULT_JAVA_BOILERPLATE = `public class Solution {
    public static void main(String[] args) {
        // Write your solution here
        System.out.println("Hello, CodeConnect!");
    }
}`;

const STORAGE_PREFIX = "codeconnect_draft_";

/**
 * Controller hook encapsulating Coding Cockpit state, Monaco Editor buffer,
 * debounced LocalStorage draft autosave, split/expanded layout toggling,
 * editor preferences, and real-time Kafka/STOMP code execution subscription.
 */
export function useCockpitController(props?: UseCockpitControllerProps): CockpitControllerResult {
  const [lesson, setLesson] = useState<LessonResponse | null>(props?.initialLesson || null);
  const [code, setCodeState] = useState<string>(() => {
    if (props?.initialLesson?.id) {
      if (typeof window !== "undefined") {
        const savedDraft = localStorage.getItem(`${STORAGE_PREFIX}${props.initialLesson.id}`);
        if (savedDraft) return savedDraft;
      }
      return props.initialLesson.starterCode || DEFAULT_JAVA_BOILERPLATE;
    }
    return DEFAULT_JAVA_BOILERPLATE;
  });

  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<"story" | "editor" | "tests">("editor");
  const [editorTheme, setEditorTheme] = useState<"vs-dark" | "light">("vs-dark");
  const [fontSize, setFontSize] = useState<number>(14);
  const [languageMode, setLanguageMode] = useState<LanguageMode>("ENGLISH");
  const [isSaved, setIsSaved] = useState<boolean>(true);

  // Execution & STOMP state
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [consoleOpen, setConsoleOpen] = useState<boolean>(false);
  const [submissionId, setSubmissionId] = useState<string | null>(null);
  const [submissionResult, setSubmissionResult] = useState<SubmissionResultResponse | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const saveTimerRef = useRef<NodeJS.Timeout | null>(null);
  const stompUnsubRef = useRef<(() => void) | null>(null);

  // Sync when initialLesson prop updates
  useEffect(() => {
    if (props?.initialLesson) {
      setLesson(props.initialLesson);
      const lessonId = props.initialLesson.id;
      if (typeof window !== "undefined") {
        const savedDraft = localStorage.getItem(`${STORAGE_PREFIX}${lessonId}`);
        if (savedDraft) {
          setCodeState(savedDraft);
          return;
        }
      }
      setCodeState(props.initialLesson.starterCode || DEFAULT_JAVA_BOILERPLATE);
    }
  }, [props?.initialLesson]);

  // Cleanup STOMP subscription on unmount
  useEffect(() => {
    return () => {
      if (stompUnsubRef.current) {
        stompUnsubRef.current();
      }
    };
  }, []);

  // Debounced Autosave to LocalStorage
  const handleCodeChange = useCallback(
    (newCode: string) => {
      setCodeState(newCode);
      setIsSaved(false);

      if (saveTimerRef.current) {
        clearTimeout(saveTimerRef.current);
      }

      saveTimerRef.current = setTimeout(() => {
        if (typeof window !== "undefined" && lesson?.id) {
          try {
            localStorage.setItem(`${STORAGE_PREFIX}${lesson.id}`, newCode);
            setIsSaved(true);
          } catch {
            // LocalStorage quota handling
            setIsSaved(false);
          }
        }
      }, 400);
    },
    [lesson?.id]
  );

  const resetToStarterCode = useCallback(() => {
    const defaultCode = lesson?.starterCode || DEFAULT_JAVA_BOILERPLATE;
    setCodeState(defaultCode);
    if (typeof window !== "undefined" && lesson?.id) {
      localStorage.removeItem(`${STORAGE_PREFIX}${lesson.id}`);
    }
    setIsSaved(true);
  }, [lesson]);

  const toggleExpanded = useCallback(() => {
    setIsExpanded((prev) => !prev);
  }, []);

  const handleRunCode = useCallback(async () => {
    if (!lesson?.id) return;

    setIsRunning(true);
    setConsoleOpen(true);
    setSubmitError(null);
    setSubmissionResult(null);

    try {
      const resp = await SubmissionService.createSubmission({
        footholdId: lesson.id,
        code,
      });

      setSubmissionId(resp.submissionId);

      // Clean up previous subscription if any
      if (stompUnsubRef.current) {
        stompUnsubRef.current();
      }

      // Subscribe to real-time STOMP topic for this submission
      const unsubscribe = getStompClient().subscribeToSubmission(
        resp.submissionId,
        (result: SubmissionResultResponse) => {
          setSubmissionResult(result);
          setIsRunning(false);
          if (result.allPassed && props?.onFootholdCompleted) {
            props.onFootholdCompleted();
          }
        }
      );

      stompUnsubRef.current = unsubscribe;
    } catch (err: unknown) {
      setIsRunning(false);
      setSubmitError(err instanceof Error ? err.message : 'Execution submission failed');
    }
  }, [lesson?.id, code, props]);

  return {
    lesson,
    code,
    isExpanded,
    activeTab,
    editorTheme,
    fontSize,
    isSaved,
    languageMode,
    isRunning,
    consoleOpen,
    submissionId,
    submissionResult,
    submitError,
    isAllPassed: submissionResult?.allPassed ?? false,
    setCode: handleCodeChange,
    toggleExpanded,
    setActiveTab,
    setEditorTheme,
    setFontSize,
    setLanguageMode,
    setConsoleOpen,
    resetToStarterCode,
    handleRunCode,
  };
}
