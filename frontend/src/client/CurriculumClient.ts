import { HttpClient } from "./HttpClient";
import { ApiResponse } from "@/dto/response/ApiResponse";
import {
  TrackResponse,
  ModuleResponse,
  LessonResponse,
  StudentProgressResponse,
  CreateTrackRequest,
  CreateModuleRequest,
  CreateLessonRequest,
} from "@/types/curriculum";

export class CurriculumClient {
  public async getPublishedTracks(): Promise<ApiResponse<TrackResponse[]>> {
    return HttpClient.execute<TrackResponse[]>(
      "/api/v1/curriculum/tracks",
      { method: "GET" },
      "Failed to retrieve published curriculum tracks"
    );
  }

  public async getTrackById(trackId: string): Promise<ApiResponse<TrackResponse>> {
    return HttpClient.execute<TrackResponse>(
      `/api/v1/curriculum/tracks/${trackId}`,
      { method: "GET" },
      "Failed to retrieve track details"
    );
  }

  public async getModulesByTrackId(trackId: string): Promise<ApiResponse<ModuleResponse[]>> {
    return HttpClient.execute<ModuleResponse[]>(
      `/api/v1/curriculum/tracks/${trackId}/modules`,
      { method: "GET" },
      "Failed to retrieve modules for track"
    );
  }

  public async getLessonById(lessonId: string, userId?: string): Promise<ApiResponse<LessonResponse>> {
    const query = userId ? `?userId=${encodeURIComponent(userId)}` : "";
    return HttpClient.execute<LessonResponse>(
      `/api/v1/curriculum/lessons/${lessonId}${query}`,
      { method: "GET" },
      "Failed to retrieve lesson details"
    );
  }

  public async getStudentProgress(userId: string, trackId: string): Promise<ApiResponse<StudentProgressResponse>> {
    return HttpClient.execute<StudentProgressResponse>(
      `/api/v1/curriculum/progress/${userId}/${trackId}`,
      { method: "GET" },
      "Failed to retrieve student progress"
    );
  }

  public async createTrack(request: CreateTrackRequest): Promise<ApiResponse<TrackResponse>> {
    return HttpClient.execute<TrackResponse>(
      "/api/v1/admin/curriculum/tracks",
      {
        method: "POST",
        body: JSON.stringify(request),
      },
      "Failed to create track"
    );
  }

  public async createModule(request: CreateModuleRequest): Promise<ApiResponse<ModuleResponse>> {
    return HttpClient.execute<ModuleResponse>(
      "/api/v1/admin/curriculum/modules",
      {
        method: "POST",
        body: JSON.stringify(request),
      },
      "Failed to create module"
    );
  }

  public async createLesson(request: CreateLessonRequest): Promise<ApiResponse<LessonResponse>> {
    return HttpClient.execute<LessonResponse>(
      "/api/v1/admin/curriculum/lessons",
      {
        method: "POST",
        body: JSON.stringify(request),
      },
      "Failed to create lesson"
    );
  }

  public async getAdminLessonById(lessonId: string): Promise<ApiResponse<LessonResponse>> {
    return HttpClient.execute<LessonResponse>(
      `/api/v1/admin/curriculum/lessons/${lessonId}`,
      { method: "GET" },
      "Failed to retrieve admin lesson details"
    );
  }
}

export const curriculumClient = new CurriculumClient();
