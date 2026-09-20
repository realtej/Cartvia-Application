# Cartvia - Dev Updates

This document tracks all major updates to the Cartvia Android application, organized by Build Version and Features.

---

## Build 1.7 — Virtual Shopping List
### Features:
- **Intelligent Shopping List**: Users can now create a shopping list that automatically checks items off as they are scanned in the cart.
- **Auto-Sync Engine**: Integrated `ShopViewModel` with `ShoppingListRepository` to perform real-time matching between physical cart items and the virtual list.
- **Tabbed List Management**: Added a dedicated "List" tab to the main navigation for easy planning.

## Build 1.6 — User-Driven Navigation
### Features:
- **Tabbed Navigation**: Integrated Bottom Navigation Bar with Home, Session, and Profile tabs.
- **Session Intelligence**: Added `SessionManager` to persist shopping state while navigating between tabs.
- **Home Dashboard**: Implemented a new Home screen with store maps and live offer feeds.

## Build 1.5 — Future Roadmap & Placeholders
### Features:
- **Order History**: Added a paginated Order History screen (via Profile) that displays past sessions and order details.
- **Store Traffic Indicator**: Implemented a `StoreTrafficBadge` on the "Join Cart" screen to show real-time trolley occupancy.
- **Resilient UI**: Integrated placeholder logic for non-existent backend endpoints, ensuring a clean "Coming Soon" experience instead of crashes.
- **Contract-first Development**: Established Retrofit interfaces and repository methods for future backend deployments.

## Build 1.4 — UX Refinements & Dev Tools
### Features:
- **QR-based Trolley Join**: Defaulted the "Join Cart" entry point to a QR scanner using CameraX and ML Kit.
- **Smart Fallback Logic**: Added automatic transition to manual entry on camera timeout (10s) or permission denial.
- **Shared Scanning Component**: Extracted `BarcodeScannerView` into a reusable UI component for both product and trolley scanning.

## Build 1.3 — Checkout & Payments
### Features:
- **Order Conversion**: Implemented session ending logic that converts active carts into finalized orders.
- **QR Payment Generation**: Integrated ZXing to generate payment QR codes from backend payloads.
- **UPI Integration**: Added support for launching external UPI apps via `Intent.ACTION_VIEW` using deep links.
- **Status Polling**: Implemented automated polling in the checkout flow to detect payment success/failure.

## Build 1.2 — Shopping & Cart Management
### Features:
- **Barcode Product Lookup**: Integrated ML Kit scanning to identify products by barcode and fetch details via REST.
- **Live Cart UI**: Built a Material 3 cart list with quantity controls, item removal, and subtotal calculation.
- **Verification Chips**: Added visual status indicators (Verified/Mismatch/Pending) based on weight validation results.
- **Product Preview**: Added a confirmation dialog for scanned items before they are added to the session.

## Build 1.1 — Authentication & Real-time Sync
### Features:
- **User Identity**: Implemented Login and Registration screens with full validation logic.
- **Real-time Synchronization**: Integrated STOMP-over-WebSocket to keep the cart UI in sync with physical trolley actions.
- **Event Handling**: Added listeners for `CART_UPDATED` and `PRODUCT_ADDED` socket frames to trigger background refreshes.

## Build 1.0 — Infrastructure & Core Architecture
### Features:
- **Base Scaffolding**: Configured Multi-module project structure with Hilt, Compose, and DataStore.
- **Networking Foundation**: Built a robust Retrofit client with `ApiEnvelope` unwrapping and custom error mapping.
- **Authenticated Session Logic**: Implemented `AuthInterceptor` and `AuthAuthenticator` for automatic JWT management and silent token refresh.
- **Base Repository**: Established a standardized `safeApiCall` pattern to handle network exceptions and mapping.
