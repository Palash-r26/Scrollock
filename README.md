# Scrollock – The Reel Blocker App

Scrollock is an **Android productivity app** designed to help users stay focused by **blocking YouTube Shorts and Instagram Reels**
using the Android Accessibility framework. Instead of blocking entire apps, Scrollock **intelligently detects only short-form addictive content** 
and immediately stops it while allowing normal usage of YouTube and Instagram.

## Project Overview :

Short-form content like Reels and Shorts can easily waste hours of time.  
Scrollock acts as a digital self-control tool that:

- Detects when Shorts or Reels are opened
- Instantly blocks them
- Shows a short popup message
- Redirects the user back to the app’s home feed

All of this happens in real time, without rooting the device.

## Objective :

- Reduce addiction to short-form video content  
- Allow healthy usage of social media apps  
- Help students and users improve focus and productivity  

## Key Features :

Blocks **YouTube Shorts only** (not full YouTube)  
Blocks **Instagram Reels only** (not full Instagram)  
Instant popup message when content is blocked  
No noticeable delay in detection  
Works completely offline  
No ads, no trackers  
Lightweight and battery-friendly  

## How It Works :
Scrollock uses the Android Accessibility API to:
1. Observe what is currently visible on the screen  
2. Detect Shorts or Reels using:
   - UI structure
   - View IDs
   - Visible text context  
3. Immediately perform a Back action to stop the content  
4. Show a popup like:  
   > YouTube Shorts Blocked .
5. Return the user to the app’s home feed, not the phone home screen  

## Technologies Used :

- **Java** – Core application logic  
- **Android Accessibility API** – Content detection and control  
- **WindowManager Overlay** – Popup message display  
- **Android SDK** – App framework  

### Permissions Required :

Scrollock requires **Accessibility Permission** to function.

> This permission is only used to detect and block Shorts/Reels.  
> No data is collected or sent anywhere.

##  Known Behavior :

- A **very small delay (milliseconds)** may occur due to system UI rendering  
- This is normal and unavoidable with Accessibility services  
- Popup and blocking happen almost instantly in real usage  

## Tested On :

- Android 10+
- YouTube (latest version)
- Instagram (latest version)
  
## Future Improvements

- Custom block messages  
- Daily usage statistics  
- Time-based blocking  
- Support for more apps  

## Developer
1. Palash Rai
B.Tech CSD – MITS Gwalior
2. Prarthana Sharma
B.Tech CSD – MITS Gwalior
3. Sarvesh Baghel
B.Tech CSD – MITS Gwalior

If you find this project helpful, ⭐ star the repository.

## License

This project is for **educational and personal productivity use**.  
Feel free to learn, modify, and improve it.
