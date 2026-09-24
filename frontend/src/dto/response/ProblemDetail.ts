/**
 * RFC 7807 Problem Details representation matching Spring Boot's ProblemDetail
 */
export interface ProblemDetail {
  readonly type: string;
  readonly title: string;
  readonly status: number;
  readonly detail: string;
  readonly instance?: string;
  readonly timestamp?: string;
  readonly errors?: Record<string, string>;
}
