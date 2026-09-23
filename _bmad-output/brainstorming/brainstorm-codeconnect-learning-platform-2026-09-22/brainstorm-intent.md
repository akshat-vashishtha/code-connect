# CodeConnect: Product Intent Document

## Executive Summary
**CodeConnect** is a structured, fear-free learning platform for Java and Data Structures & Algorithms (DSA). It replaces traditional, intimidating textbook curriculum with an empathetic **"Mountain Climb"** progression model: concepts are introduced through real-world visual stories, code problems feel like tangible puzzles, and a 3-tier support hierarchy (Multilingual AI Socratic Assistant → Peer Solver Friend → Human Mentor) ensures no student is ever stranded by a failing test.

---

## Target Audience
1. **Solo Students**: Independent learners seeking structured, intuitive mastery of Java & DSA at their own pace without peer pressure or cohort deadlines.
2. **Mentors**: Educators and senior guides who author tailored tracks/problems and intervene for high-impact 1-on-1 coaching when students face genuine conceptual hurdles.

---

## Core Philosophy & Design Principles
* **The Mountain Climb (Progressive Footholds)**: Programming is presented as an incremental ascent rather than an intimidating vertical cliff. Each concept is a secure foothold anchored directly into prior learning.
* **Story-First Pedagogy ("Connect the Dots" & KISS)**:
  1. *Real-World Story*: Build an imaginative, visual mental model first with zero academic jargon.
  2. *Practical Problem Tension*: Relate the story to a concrete puzzle/challenge.
  3. *Code as Resolution*: Introduce programming syntax only as the natural tool that resolves the tension.
* **Narrative-Anchored AI Debugging**: The AI Assistant diagnoses test failures and bugs through the lens of the concept's original story analogy, maintaining unbroken visual intuition rather than dumping raw compiler errors.
* **Load-Bearing Prerequisite Checks**: The platform quietly verifies only the specific foundational concepts the next move depends on before unlocking harder peaks.
* **Lightweight School-Friend Collaboration**: Authentic peer connections modeled after classmates helping each other after school, avoiding artificial points or gamification barriers.

---

## The Core Loop
```
[ 1. Story Concept ] ──► [ 2. Problem Challenge ] ──► [ 3. Code & Auto-Test ]
                                                             │
                                                             ▼
                                                    [ Pass or Fail? ]
                                                             │
                           ┌─────────────────────────────────┴─────────────────────────────────┐
                           ▼                                                                   ▼
                       [ PASS ]                                                             [ FAIL ]
                           │                                                                   │
             [ Load-Bearing Prereq Check ]                                              [ 3-Tier Help Loop ]
                           │                                                                   │
              [ Unlock Next Foothold ]                                                  Tier 1: AI Story Debugger
                                                                                        Tier 2: Peer Solver Friend
                                                                                        Tier 3: Human Mentor Escalation
```

---

## Three-Tier Support Hierarchy
1. **Tier 1: Multilingual AI Socratic Assistant**
   * Instant, on-demand hints in the student's preferred language.
   * Guides with minimal footholds so the student earns the "aha" breakthrough themselves.
   * Translates compiler errors into story-world explanations.
2. **Tier 2: Peer Solver Doubt Chat**
   * Direct, friendly connection to a peer who has already cleared that exact problem.
   * Natural, judgment-free collaboration.
3. **Tier 3: Human Mentor Escalation**
   * Triggers when AI hints and peer chat do not resolve the blocker.
   * Mentors receive the problem context, student code, and prior chat transcript for seamless intervention.
   * Mentors publish custom tracks, concepts, and problems.

---

## Downstream Implementation Target
This intent document serves as foundational input for:
* **`bmad-product-brief`** or **`bmad-prd`** for detailed feature specs and user stories.
* **`bmad-architecture`** for technical design (compiler sandbox, telemetry, real-time peer matching, LLM prompt orchestration).
* **`bmad-ux`** for visual interface design (curriculum maps, code editor, story visualization pane, chat drawer).
