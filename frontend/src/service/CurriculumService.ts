import { CurriculumClient, curriculumClient } from "@/client/CurriculumClient";
import {
  TrackResponse,
  ModuleResponse,
  LessonResponse,
  StudentProgressResponse,
  CreateTrackRequest,
  CreateModuleRequest,
  CreateLessonRequest,
} from "@/types/curriculum";

export class CurriculumService {
  constructor(private readonly client: CurriculumClient = curriculumClient) {}

  public async getPublishedTracks(): Promise<TrackResponse[]> {
    const response = await this.client.getPublishedTracks();
    return response.data ?? [];
  }

  public async getTrackById(trackId: string): Promise<TrackResponse | null> {
    const response = await this.client.getTrackById(trackId);
    return response.data;
  }

  public async getModulesByTrackId(trackId: string): Promise<ModuleResponse[]> {
    const response = await this.client.getModulesByTrackId(trackId);
    return response.data ?? [];
  }

  public async getLessonById(lessonId: string, userId?: string): Promise<LessonResponse | null> {
    const response = await this.client.getLessonById(lessonId, userId);
    return response.data;
  }

  public async getStudentProgress(userId: string, trackId: string): Promise<StudentProgressResponse | null> {
    const response = await this.client.getStudentProgress(userId, trackId);
    return response.data;
  }

  public async createTrack(request: CreateTrackRequest): Promise<TrackResponse | null> {
    const response = await this.client.createTrack(request);
    return response.data;
  }

  public async createModule(request: CreateModuleRequest): Promise<ModuleResponse | null> {
    const response = await this.client.createModule(request);
    return response.data;
  }

  public async createLesson(request: CreateLessonRequest): Promise<LessonResponse | null> {
    const response = await this.client.createLesson(request);
    return response.data;
  }

  public async getAdminLessonById(lessonId: string): Promise<LessonResponse | null> {
    const response = await this.client.getAdminLessonById(lessonId);
    return response.data;
  }
}

export const curriculumService = new CurriculumService();
