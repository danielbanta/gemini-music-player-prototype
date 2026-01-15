# Gemini Music Player Prototype (Exercise)

## The Problem
To listen to audio, users must leave our app. This disrupts their experience. How might we help users listen to content without leaving Gemini? 

## Requirements 
We need a compact, embeddable music player component. A design mock has been created to provide a visual starting point and general aesthetic direction.
- Playback Control: The user should have control over the audio playback. This includes initiating, pausing, and navigating between a short list of tracks. 
- Track Context: The user must be able to identify the track that is currently playing.
- Timeline Navigation: The user should be able to see their progress through a track and navigate to different points in its timeline. 
- User Feedback: The user should be able to perform a simple, positive action on a track they enjoy (e.g., "favorite" or "like"). The player should provide clear visual feedback when this action is taken. 

## UX Mocks 
The UX mocks to serve as the primary inspiration for the MVP.
- [Link to Figma Mock Here](https://www.figma.com/design/9nAnRIT2fm55KzqbzbEdS9/Design-Exercise?node-id=0-1&p=f&t=xWyF6sMmdPf2kmmw-0)
- [Link to PNG](https://drive.google.com/file/d/1bJte_UcFjz81vb-RIpu2_ZRCJVAy7OaT/view?usp=sharing)

## Introduction to my Prototype
This project is a native Android prototype implemented in Kotlin using Jetpack Compose. It simulates a seamless integration of a music player within the Gemini chat interface.

**App Setup**: The prototype begins with a user interaction: "Hey Gemini, can you find me some Lo-Fi tracks for my coding video?". This prompts the system to display a list of suggested tracks, demonstrating how media playback can be intuitively embedded into a conversational UI.

- <img width="703" height="1440" alt="Screenshot 2026-01-14 at 6 49 22 PM" src="https://github.com/user-attachments/assets/4bc0b6be-0b94-4e02-8c07-00d60dc96b58" />

## My Design Touches
### 1. Match the Design Spec Requirements, add touches
Wanted to match the Design Spec Requirements as closely as possible. Where there weren't exact specs, I tried to add my own touches to enhance UX.
- Made sure the font (Google Sans), spacing, text size, colors, and icons matched the provided Figma spec exactly.
   - <img width="703" height="474" alt="Screenshot 2026-01-14 at 6 58 27 PM" src="https://github.com/user-attachments/assets/67c7b333-c34a-467e-838e-4cab5fcf88c1" />
- Then added my own touches as I saw fit to enhance the UX. See examples below.

### 2. Minimize-able/Dismissable Player
Scrolling or tapping back on the chat will minimize the player with a subset of available actions/content.
- **Minimize-able**: Playing the media is a sub-action of the Gemini Chat; it shouldn’t get in the way of the user interacting with the chat. Minimizing the player when the user interacts with the chat or prompt input helps enforce this.
    - ![Minimize-able Player](https://github.com/user-attachments/assets/f35ccfb4-9f86-4d81-aaa8-a6ae6aa8beaa)
- **Dismissal**: The player should be dismissable too. If the user wants to free up screen real estate, then they should be able to dismss the player (i.e. by *swiping it away*).
    - ![Mini_player_dismissal](https://github.com/user-attachments/assets/d93b0736-a340-4d25-a624-8049f00889ac)


### 3. Expanded <-> Minimized Transition
Smooth, intuitive transitions between the expanded and minimized player states.
- **Iteration**: Initially, when transitioning from Expanded ↔ Minimized, the animation was a little clunky. It was too fast, which made it a bit jarring, and the content wasn’t anchored properly, so at one point it looked like it was floating.
- **Solution**: I refactored the animation to closer match the deliberate feel of a scroll interaction and to have a smoother transition, ensuring the player feels grounded to the bottom of the screen (see Minimize-able gif above).

### 4.Small animations as the “Soul” of the App
For me, much of the “Soul” of an app comes from the small UX features that spark joy. Especially ones that elicit a physical feeling that makes the UI feel interactive.

#### “Like/Favorite” Button
One small interaction I focused on was the “Like/Favorite” button. Instead of just a generic red pop on a heart, what I implemented was a multi-stage organic burst when the user presses the button.
- **Animation**: Anticipation (The "Squish") → Impact (The "Splash") → Fill (The "Wick") → Haptic "Thump". "Feels" much better live than on a video.
- **Effects**:
    - **Feedback**: The user knows exactly when the action was registered.
    - **Delight**: The particles add a "celebration" feel to a routine action.
    - **Physics**: Using "Spring" curves rather than linear motion makes the app feel less like a machine and more like a responsive tool.
 
- ![Like_Favorite Button](https://github.com/user-attachments/assets/f7ceab98-69b8-4cfc-b70c-b978f4915934)

#### Repeat Button
This is also a button you should expect feedback from. To incorporate “Feel & Motion”, I added a rotation animation. When clicked, it spins 180 degrees, changes color, and provides a small haptic feedback.

- ![Repeat button animation](https://github.com/user-attachments/assets/427cfbeb-d96d-44b5-85bc-1a040f0c5b8d)

### 5. Minimized Player Actions
The actions on the minimized player should be pertinent and intuitive.

- **Pertinent**: Which actions are necessary even on a minimized player?
    - Being able to play/pause the music from the player is non-negotiable.
    - Since we put so much focus on the like/favorite animation, we should include that button on the mini player too.

- **Intuitive**: What actions on the mini player are intuitive without needing to be explicitly shown?
    - **Swiping away** this mini player is a very intuitive action for smaller pop-ups (think notifications or emails).
    - Initially, the dismiss button was displayed on the mini player, but since this action is intuitive, we can remove it and leave the real estate to a more pertinent action (like/favorite).
 
- ![Mini player actions](https://github.com/user-attachments/assets/6b00998e-92a4-43b8-a567-c28a0faef8b4)

### 6. Text Overflow
Instead of standard ellipses, I implemented a **self-scrolling composable** (Marquee) to show the overflow of the song title and artist strings.

## Full functionality video
Drive link: [here](https://drive.google.com/file/d/1zHYY_xQedLnD0DTeh_VDZCGFZXiwdNLz/view?usp=sharing)
