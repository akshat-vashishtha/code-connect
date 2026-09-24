import React from "react";
import { CodingCockpitView } from "@/presentation/views/CodingCockpitView";

interface CockpitPageProps {
  params: Promise<{ footholdId: string }>;
}

export default async function CockpitPage({ params }: CockpitPageProps) {
  // Await params if Next.js 15 requires it asynchronously
  await params;

  return <CodingCockpitView />;
}
