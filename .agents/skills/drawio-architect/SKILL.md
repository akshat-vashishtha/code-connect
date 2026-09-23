---
name: drawio-architect
description: Generates clean, professional, publication-grade Draw.io (diagrams.net) architecture diagrams using the strict Grid-Matrix geometric pattern to prevent overlapping text, tangled lines, and colliding boxes.
---

# Draw.io Architect Skill

## Purpose
Produce presentation-grade and publication-ready architecture diagrams in Draw.io (diagrams.net) format (`.drawio` or XML) without the common flaws of auto-layout tools (misaligned boxes, overlapping text, diagonal lines cutting through nodes).

## The Core Problem with Standard Generation
When LLMs or auto-layout tools (like Graphviz/PlantUML imports) generate diagrams without spatial constraints:
1. Circular or multi-directional flows cause random node placement.
2. Text labels wrap unpredictably and collide with neighboring containers.
3. Connectors cross directly over unrelated shapes and obscure database/service labels.

## The Grid-Matrix Pattern
To eliminate collisions, every diagram produced by this skill must follow the **Grid-Matrix Specification**:
- See detailed mathematical rules in [references/grid-matrix-pattern.md](./references/grid-matrix-pattern.md).

### Quick Rules for Generating Diagrams:
1. **Strict 2D Coordinate Grid**:
   - Assign every node to a discrete `(Row, Column)` cell in advance.
   - Use fixed width (`w=240` to `280`) and fixed height (`h=100` to `120`).
   - Use consistent horizontal gutters (`dx ≥ 60px`) and vertical gutters (`dy ≥ 80px`).
2. **Standard C4 Visual Palette**:
   - Systems / Boundaries: `#ECEFF1` fill, `#78909C` dashed border.
   - Containers / Microservices: `#236CB0` fill, `#1B4E80` border, white text.
   - Databases / Caches: `#1B5E20` (Green) or `#00695C` (Teal) cylinder shapes.
   - Brokers / Event Streams: `#E65100` (Amber/Orange) step/queue shapes.
   - External / Clients: `#37474F` (Dark Slate) rounded rectangle.
3. **Deterministic Orthogonal Routing**:
   - Every connector must include: `edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;`
   - Explicitly anchor exit and entry points:
     - Left-to-Right: `exitX=1;exitY=0.5;entryX=0;entryY=0.5;`
     - Top-to-Bottom: `exitX=0.5;exitY=1;entryX=0.5;entryY=0;`
     - Bottom-to-Top: `exitX=0.5;exitY=0;entryX=0.5;entryY=1;`
   - Always set `labelBackgroundColor=#ffffff;` on edges so connector labels never clash with lines or container borders.
4. **File Output**:
   - Write output as a valid `.drawio` (or `.drawio.xml`) file in the project's architecture/documentation directory.
