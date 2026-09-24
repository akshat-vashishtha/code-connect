"use client";

import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import { authService } from "@/service/AuthService";
import { UserResponse } from "@/dto/response/UserResponse";
import { UserRole } from "@/domain/enums/UserRole";

export interface SessionControllerResult {
  readonly user: UserResponse | null;
  readonly role: UserRole;
  readonly isLoading: boolean;
  readonly isAuthenticated: boolean;
  readonly logout: () => Promise<void>;
}

/**
 * Controller providing verified user session, discrete role resolution,
 * and session lifecycle commands across the platform shell.
 */
export function useSessionController(): SessionControllerResult {
  const pathname = usePathname();
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    async function fetchSession() {
      try {
        const currentUser = await authService.getCurrentUser();
        if (isMounted) {
          setUser(currentUser);
        }
      } catch {
        if (isMounted) {
          setUser(null);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    fetchSession();
    return () => {
      isMounted = false;
    };
  }, []);

  const handleLogout = async () => {
    try {
      await authService.logout();
    } catch {
      // Allow graceful redirect on network edge failure
    } finally {
      setUser(null);
      window.location.href = "/login";
    }
  };

  // Resolve role: prefer verified session role, else infer from route domain
  let resolvedRole: UserRole = "ROLE_STUDENT";
  if (user?.role) {
    resolvedRole = user.role;
  } else if (pathname.startsWith("/admin")) {
    resolvedRole = "ROLE_ADMIN";
  } else if (pathname.startsWith("/mentor")) {
    resolvedRole = "ROLE_MENTOR";
  }

  return {
    user,
    role: resolvedRole,
    isLoading,
    isAuthenticated: !!user,
    logout: handleLogout,
  };
}
