export type ExecutionStatus =
  | 'PASSED'
  | 'FAILED'
  | 'COMPILATION_ERROR'
  | 'RUNTIME_ERROR'
  | 'TIMED_OUT'
  | 'MEMORY_EXCEEDED';

export interface SanitizedTestResult {
  testCaseId: string;
  input: string;
  expectedOutput: string;
  actualOutput: string;
  passed: boolean;
  hidden: boolean;
  executionTimeMs: number;
  errorMessage?: string | null;
}

export interface SubmissionResultResponse {
  submissionId: string;
  footholdId: string;
  userId: string;
  status: ExecutionStatus;
  compilerOutput?: string | null;
  testResults: SanitizedTestResult[];
  totalDurationMs: number;
  allPassed: boolean;
  completedAt: string;
}

export interface CreateSubmissionRequest {
  footholdId: string;
  code: string;
}

export interface CreateSubmissionResponse {
  submissionId: string;
  footholdId: string;
  status: string;
  createdAt: string;
}
