/**
 * Generic API envelope contract matching com.codeconnect.gateway.application.dto.response.ApiResponse<T>
 */
export interface ApiResponse<T> {
  readonly success: boolean;
  readonly message: string;
  readonly data: T;
  readonly timestamp: string;
}
