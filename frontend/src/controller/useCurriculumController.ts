"use client";

import { useEffect, useState, useTransition } from "react";
import { curriculumService } from "@/service/CurriculumService";
import {
  TrackResponse,
  ModuleResponse,
  LessonResponse,
  StudentProgressResponse,
  LanguageMode,
} from "@/types/curriculum";

export interface CurriculumControllerResult {
  readonly tracks: TrackResponse[];
  readonly activeTrack: TrackResponse | null;
  readonly modules: ModuleResponse[];
  readonly activeLesson: LessonResponse | null;
  readonly studentProgress: StudentProgressResponse | null;
  readonly languageMode: LanguageMode;
  readonly isLoading: boolean;
  readonly error: string | null;
  readonly setLanguageMode: (mode: LanguageMode) => void;
  readonly selectTrack: (trackId: string) => Promise<void>;
  readonly selectLesson: (lessonId: string, userId?: string) => Promise<void>;
  readonly loadStudentProgress: (userId: string, trackId: string) => Promise<void>;
}

/**
 * Controller Hook encapsulating curriculum state, tracks, modules, lessons,
 * bilingual story mode selection (EN | HINGLISH), and student progression.
 */
export function useCurriculumController(initialUserId?: string): CurriculumControllerResult {
  const [tracks, setTracks] = useState<TrackResponse[]>([]);
  const [activeTrack, setActiveTrack] = useState<TrackResponse | null>(null);
  const [modules, setModules] = useState<ModuleResponse[]>([]);
  const [activeLesson, setActiveLesson] = useState<LessonResponse | null>(null);
  const [studentProgress, setStudentProgress] = useState<StudentProgressResponse | null>(null);
  const [languageMode, setLanguageMode] = useState<LanguageMode>("ENGLISH");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [, startTransition] = useTransition();

  useEffect(() => {
    let isMounted = true;
    async function loadInitialTracks() {
      setIsLoading(true);
      setError(null);
      try {
        const publishedTracks = await curriculumService.getPublishedTracks();
        if (!isMounted) return;
        startTransition(() => {
          setTracks(publishedTracks);
          if (publishedTracks.length > 0) {
            const firstTrack = publishedTracks[0];
            setActiveTrack(firstTrack);
          }
        });
        if (publishedTracks.length > 0) {
          const firstTrackId = publishedTracks[0].id;
          const mods = await curriculumService.getModulesByTrackId(firstTrackId);
          if (isMounted) {
            startTransition(() => setModules(mods));
            if (initialUserId) {
              const prog = await curriculumService.getStudentProgress(initialUserId, firstTrackId);
              if (isMounted) setStudentProgress(prog);
            }
          }
        }
      } catch (err: unknown) {
        if (isMounted) {
          setError(err instanceof Error ? err.message : "Failed to load curriculum");
        }
      } finally {
        if (isMounted) setIsLoading(false);
      }
    }
    loadInitialTracks();
    return () => {
      isMounted = false;
    };
  }, [initialUserId]);

  const selectTrack = async (trackId: string): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      const track = await curriculumService.getTrackById(trackId);
      const mods = await curriculumService.getModulesByTrackId(trackId);
      startTransition(() => {
        setActiveTrack(track);
        setModules(mods);
        setActiveLesson(null);
      });
      if (initialUserId) {
        const prog = await curriculumService.getStudentProgress(initialUserId, trackId);
        setStudentProgress(prog);
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to select track");
    } finally {
      setIsLoading(false);
    }
  };

  const selectLesson = async (lessonId: string, userId?: string): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      const lesson = await curriculumService.getLessonById(lessonId, userId || initialUserId);
      startTransition(() => setActiveLesson(lesson));
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to load lesson");
    } finally {
      setIsLoading(false);
    }
  };

  const loadStudentProgress = async (userId: string, trackId: string): Promise<void> => {
    try {
      const prog = await curriculumService.getStudentProgress(userId, trackId);
      setStudentProgress(prog);
    } catch (err: unknown) {
      console.warn("Failed to load student progress for userId and trackId:", userId, trackId, err);
    }
  };

  return {
    tracks,
    activeTrack,
    modules,
    activeLesson,
    studentProgress,
    languageMode,
    isLoading,
    error,
    setLanguageMode,
    selectTrack,
    selectLesson,
    loadStudentProgress,
  };
}
