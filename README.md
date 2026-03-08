# AURA — Project Setup & Quick Reference

## Prerequisites
- Android Studio Ladybug or later
- Android SDK 36 (compileSdk)
- Min SDK 26
- Kotlin 2.0.21
- Gemini API Key (from [Google AI Studio](https://aistudio.google.com/apikey))

## First-Time Setup

```bash
# 1. Clone
git clone https://github.com/shachafha/Aura.git
cd Aura

# 2. Set your Gemini API key
echo 'GEMINI_API_KEY=your-key-here' >> local.properties

# 3. Open in Android Studio → Sync Gradle → Run on physical device
```

## Branch Workflow (Hackathon)

```bash
# Create your module branch
git checkout -b feature/<module-name>

# Work, commit often
git add . && git commit -m "feat(module): description"

# When done, push and create PR
git push -u origin feature/<module-name>
```

## Key Files

| What | Where |
|------|-------|
| Architecture | `docs/ARCHITECTURE.md` |
| Module Specs | `docs/TEAM_SKILLS.md` |
| Agent Guide | `docs/AGENT_WORKFLOW.md` |
| Demo Script | `docs/DEMO_SCRIPT.md` |
| Hackathon Rules | `docs/HACKATHON_BRIEF.md` |
| Gradle deps | `gradle/libs.versions.toml` |
| App entry | `app/src/main/java/com/example/aura/MainActivity.kt` |

## Dependencies to Add (All Modules)

Add these to `gradle/libs.versions.toml` and `app/build.gradle.kts` as you start each module:

```toml
[versions]
camerax = "1.4.0"
generativeai = "0.9.0"
gson = "2.11.0"
coil = "2.7.0"
navigationCompose = "2.7.7"
```

## Useful Commands

```bash
# Build (check for compile errors)
./gradlew assembleDebug

# Run tests
./gradlew test

# Clean
./gradlew clean
```
## Base Prompt to work on the project

```
Hi! I'm joining a hackathon project called Aura — a live AI fashion stylist Android app. My teammate has already set up the entire project foundation. Here's everything you need to know to get me productive immediately.

Repo: https://github.com/shachafha/Aura.git (already cloned and open)

Read these docs first — they have everything:


docs/HACKATHON_BRIEF.md
 — what the hackathon requires, why Aura fits

docs/ARCHITECTURE.md
 — full system architecture, data models, API contracts

docs/TEAM_SKILLS.md
 — the module breakdown for parallel work

docs/AGENT_WORKFLOW.md
 — how to use AI agents effectively on this project
The project is a Kotlin + Jetpack Compose Android app. The full scaffold is already built and compiles. Here's what exists:

data/model/ — all 4 data models (OutfitAnalysis, ChatMessage, Recommendation, StylistResponse)

data/remote/GeminiServiceImpl.kt
 — Gemini 2.0 Flash multimodal integration

data/repository/AuraRepository.kt
 — central state (StateFlow-based)
ui/camera/ — CameraX capture screen + ViewModel
ui/analysis/ — outfit analysis results screen + ViewModel
ui/chat/ — stylist chat screen + ViewModel + MessageBubble, ChatInput, RecommendationCard
ui/components/ — OutfitTagChip, LoadingAnimation, AuraTopBar

ui/navigation/AuraNavGraph.kt
 — full nav graph (Camera → Analysis → Chat)
ui/theme/ — premium dark theme (gold/purple/teal palette)
util/ — BitmapUtils, PromptTemplates (all Gemini prompts)
Critical setup:

Add GEMINI_API_KEY=<your-key> to 

local.properties
 (not committed — get a key from aistudio.google.com)
Sync Gradle in Android Studio before anything else
What I need your help with: Read 

docs/TEAM_SKILLS.md
 and tell me which module makes the most sense for me to own and improve. The main areas that need polish are: (1) camera UI / capture experience, (2) Gemini prompt tuning and response parsing, (3) chat UI animations and recommendation cards, and (4) overall theme/branding polish. Based on what you see in the code and what's most impactful for the hackathon demo, recommend which module to start with and let's get to work.
```

## Team Members
<!-- Add your names here — REQUIRED by hackathon rules -->
- 
- 
- 
- 
