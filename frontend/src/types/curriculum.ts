/**
 * Curriculum domain and DTO types with 1:1 parity with Java backend records
 * com.codeconnect.curriculum.application.dto.*
 */

export type TrackStatus = "PUBLISHED" | "DRAFT" | "ARCHIVED";

export type LanguageMode = "ENGLISH" | "HINGLISH";

export interface ModuleSummary {
  id: string;
  title: string;
  slug: string;
  sequence: number;
  lessonCount: number;
}

export interface StoryContent {
  title: string;
  narrative: string;
  realWorldAnalogy: string;
  socraticPrompts: string[];
}

export interface TestCase {
  id: string;
  name: string;
  input: string;
  expectedOutput: string;
  isHidden: boolean;
}

export interface TrackResponse {
  id: string;
  title: string;
  slug: string;
  description: string;
  estimatedHours: number;
  status: TrackStatus;
  modules: ModuleSummary[];
  createdAt: string;
}

export interface PrerequisiteRecommendationResponse {
  prerequisiteLessonId: string;
  prerequisiteLessonTitle: string;
  isCompleted: boolean;
  isRecommended: boolean;
  recommendationBadgeText: string;
}

export interface LessonResponse {
  id: string;
  moduleId: string;
  trackId: string;
  title: string;
  slug: string;
  sequence: number;
  storyAnalogies: Record<LanguageMode, StoryContent>;
  starterCode: string;
  solutionTemplate?: string;
  testCases: TestCase[];
  prerequisiteLessonId?: string;
  prerequisiteRecommendation?: PrerequisiteRecommendationResponse | null;
}

export interface ModuleResponse {
  id: string;
  trackId: string;
  title: string;
  slug: string;
  sequence: number;
  description: string;
  prerequisiteModuleId?: string;
  lessons: LessonResponse[];
}

export interface StudentProgressResponse {
  id: string;
  userId: string;
  trackId: string;
  currentModuleId: string;
  currentLessonId: string;
  completedLessonIds: string[];
  ascentPoints: number;
  streakDays: number;
  lastCompletedAt?: string;
}

export interface CreateTrackRequest {
  title: string;
  slug: string;
  description: string;
  estimatedHours: number;
  status: TrackStatus;
}

export interface CreateModuleRequest {
  trackId: string;
  title: string;
  slug: string;
  sequence: number;
  description: string;
  prerequisiteModuleId?: string;
}

export interface CreateLessonRequest {
  moduleId: string;
  trackId: string;
  title: string;
  slug: string;
  sequence: number;
  storyAnalogies: Record<LanguageMode, StoryContent>;
  starterCode: string;
  solutionTemplate?: string;
  testCases?: TestCase[];
  prerequisiteLessonId?: string;
}
