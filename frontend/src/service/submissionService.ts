import { CreateSubmissionRequest, CreateSubmissionResponse } from '@/types/submission';
import { ApiResponse } from '@/dto/response/ApiResponse';

export class SubmissionService {
  public static async createSubmission(
    request: CreateSubmissionRequest
  ): Promise<CreateSubmissionResponse> {
    const response = await fetch('/api/v1/submissions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.detail || errorData.message || `Failed to submit code (${response.status})`
      );
    }

    const payload: ApiResponse<CreateSubmissionResponse> = await response.json();
    return payload.data;
  }
}
