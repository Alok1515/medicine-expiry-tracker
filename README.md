# 💊 Medicine Expiry Tracker

A premium, modern, and real-time web application designed to help individuals, pharmacies, and clinics manage their medicine inventory, monitor expiry dates, and receive real-time alerts before medications expire.

Built with a robust **Spring Boot (Java)** backend and a highly responsive, modern **Next.js** frontend.

---

## ✨ Features

- **🔒 Secure Authentication:** JWT-based user login and registration with encrypted passwords and token-based state.
- **📦 Inventory Management:** Add, update, delete, and view medicines with details like batch number, category, manufacturer, purchase date, expiry date, dosage, notes, and imageUrl.
- **🔍 Quick Search & Filter:** Instantly look up medicines in your inventory using keywords.
- **📊 Real-time Dashboard Analytics:** Get stats on total, expired, and close-to-expiry medicines at a glance.
- **🔔 Live Notifications:** Receive real-time STOMP WebSocket notifications and in-app alerts when medicines are nearing their expiry date.
- **⚙️ Customizable Notification Preferences:** Toggle email notifications on or off according to your preference.

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security (JWT Authentication)
- **Database:** MongoDB (via Spring Data MongoDB)
- **Real-time Updates:** WebSocket / STOMP Protocol
- **Build Tool:** Maven

### Frontend
- **Framework:** Next.js 14 (App Router)
- **Styling:** Tailwind CSS (Modern, dark-themed responsive design)
- **State/API:** Axios & React Hooks
- **WebSockets:** `@stomp/stompjs` & `sockjs-client`

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK 17 or higher)**
- **Node.js (v18 or higher)** & **npm** or **bun**
- **MongoDB** (Local instance or MongoDB Atlas URI)

---

### 📂 Backend Setup

1. **Navigate to the root directory**:
   ```bash
   cd medicine-expiry-tracker
   ```

2. **Configure environment variables**:
   Create a `.env` file in the root directory (already added to `.gitignore` to protect secrets):
   ```env
   MONGO_URI=mongodb://localhost:27017/medtracker
   JWT_SECRET=your_jwt_secret_key_should_be_long_and_secure
   EMAIL_USERNAME=your_email@gmail.com
   EMAIL_PASSWORD=your_app_specific_password
   ```

3. **Build the application**:
   ```bash
   ./mvnw clean install
   ```

4. **Run the backend**:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend will start running on* `http://localhost:8080`*.*

---

### 📂 Frontend Setup

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   # or if you use bun
   bun install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   # or
   bun dev
   ```
   *The frontend will be active at* `http://localhost:3000`*.*

---

## 📖 API Documentation

All API requests (except login/registration) require a bearer token in the `Authorization` header:
`Authorization: Bearer <your_jwt_token>`

### 🔐 Authentication API (`/api/auth`)

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/register` | Register a new user | `RegisterDTO` (username, email, password) |
| `POST` | `/login` | Log in and receive a JWT | `LoginDTO` (email, password) |
| `PUT` | `/preferences` | Update email notification preference | `{"emailNotifications": true/false}` |

#### Sample Login Request Body:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

#### Sample Login Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "johndoe",
  "email": "user@example.com",
  "emailNotifications": true
}
```

---

### 💊 Medicine Inventory API (`/api/medicines`)

| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | Add a new medicine to inventory | `MedicineDTO` |
| `GET` | `/` | Get all medicines for authenticated user | *None* |
| `GET` | `/{id}` | Get specific medicine by its unique ID | *None* |
| `PUT` | `/{id}` | Update details of an existing medicine | `MedicineDTO` |
| `DELETE` | `/{id}` | Remove a medicine from the inventory | *None* |
| `GET` | `/search` | Search inventory by name or category | `?keyword=acetaminophen` |
| `GET` | `/stats` | Retrieve dashboard stats (total, expired, warning) | *None* |

#### Sample `MedicineDTO` Schema:
```json
{
  "name": "Amoxicillin",
  "manufacturer": "Sandoz",
  "batchNumber": "AMX-90812",
  "category": "Antibiotic",
  "purchaseDate": "2026-01-10",
  "expiryDate": "2026-12-30",
  "quantity": 30,
  "dosage": "500mg twice daily",
  "notes": "Take with meals",
  "imageUrl": "https://example.com/images/amox.jpg"
}
```

#### Sample `/stats` Response:
```json
{
  "totalMedicines": 12,
  "expiredCount": 2,
  "closeToExpiryCount": 3,
  "safeCount": 7
}
```

---

### 🔔 Notifications API (`/api/notifications`)

Used for managing in-app notifications and viewing alerts about expiring medications.

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | List all notifications for user | *None* |
| `PUT` | `/{id}/read` | Mark a specific notification as read | *None* |
| `PUT` | `/read-all` | Mark all notifications as read | *None* |
| `GET` | `/unread-count` | Get the total number of unread notifications | *None* |

---

### 🔌 WebSocket Connections

The application supports real-time communication via WebSockets using STOMP.

- **Connection Endpoint:** `/ws`
- **Topic Subscription:** `/user/queue/notifications` (triggers real-time alert updates for notifications and inventory stats).

---

## 🛡️ License

This project is licensed under the MIT License. Feel free to use and adapt it for personal or commercial projects.
