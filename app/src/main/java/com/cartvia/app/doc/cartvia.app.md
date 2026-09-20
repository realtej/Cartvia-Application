# Cartvia Application Documentation

## 📱 Project Overview
**Cartvia** is a cutting-edge smart retail and collaborative shopping platform designed to eliminate traditional checkout queues. By bridging physical in-store hardware (smart carts, scales) with a modern mobile interface, Cartvia enables a seamless "Scan-Pay-Go" experience.

---

## 🏗️ Technical Architecture

### Core Tech Stack
- **UI Framework:** Jetpack Compose with Material 3 (M3).
- **Architecture Pattern:** MVVM (Model-View-ViewModel) following Clean Architecture principles.
- **Dependency Injection:** Dagger Hilt for modular and testable component management.
- **Networking:**
    - **REST:** Retrofit + OkHttp for standard API communications.
    - **Real-time:** WebSockets with STOMP protocol for live cart synchronization.
- **Local Persistence:** Jetpack DataStore (TokenManager) for secure session and preference management.
- **Hardware Bridge:** Google ML Kit for high-performance Barcode and QR code processing.
- **Navigation:** Type-Safe Jetpack Navigation using Kotlin Serialization.

### Project Structure
- `data/`: Contains DTOs, API definitions, Repositories, and local DataStore logic.
- `di/`: Hilt modules for Network, Data, and Utility bindings.
- `ui/`: Compose screens, ViewModels, theme definitions, and reusable components.
- `util/`: Hardware wrappers (Camera/Scanner) and network helpers (ApiResult).

---

## 🚀 Core Features

### 1. Advanced Authentication
- **Secure Onboarding:** Full Login, Registration, and Forgot Password flows.
- **Automatic Session Recovery:** JWT-based auth with automatic token refresh via OkHttp Authenticators.

### 2. Virtual Shopping List
- **List Planning:** Create and manage a digital shopping list before entering the store.
- **Auto-Check Intelligence:** Items are automatically marked as "Purchased" in your list the moment the Smart Cart hardware detects them in the physical trolley.
- **Custom Items:** Support for adding both store products and custom text-based reminders.

### 3. Smart Cart Interaction
- **QR Trolley Pairing:** Instantly link a physical trolley to a digital session via QR scanning.
- **Intelligent Fallback:** Auto-transitions to manual code entry if camera access is restricted or fails.
- **Live Occupancy Tracking:** Visual badges showing real-time trolley availability within the store.

### 3. Real-time Shopping Experience
- **Duplex Synchronization:** Uses a robust WebSocket manager with exponential backoff reconnection logic (2s to 30s retry ceiling).
- **Security & Weight Validation:** Real-time feedback from cart weight sensors to flag `MISMATCH` or `PENDING` items, preventing theft and errors.
- **Interactive Cart UI:** Live totals, line-item breakdowns, and item verification status indicators.

### 4. Frictionless Payments
- **UPI Deep-Linking:** Direct integration with native payment apps (GPay, PhonePe, etc.) via system Intents.
- **Dynamic Payment QR:** Generates on-device QR codes for terminal-based verification.
- **Active Status Polling:** Background listeners that confirm payment success and automatically transition the user to the receipt phase.

### 5. Post-Purchase Accounting
- **Itemized Digital Receipts:** Comprehensive breakdowns including tax, discounts, and precise issuance timestamps.
- **Order History:** A searchable ledger of all past shopping trips and expenditures.

---

## 🛣️ Roadmap & Future Enhancements
- **[ ] Store Navigation:** AR or Map-based guidance to help users find specific items on the live cart list.
- **[ ] Shared Cart Sessions:** Collaborative shopping where multiple users can contribute to the same physical cart session.

---

## 🛠️ Developer Notes
- **Build Command:** Use `./gradlew app:assembleDebug` to verify project integrity.
