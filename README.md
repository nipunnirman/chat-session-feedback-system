# Chat Session Feedback System

A full-stack feedback system that lets companies collect star ratings from customers after chat sessions — similar to how WhatsApp Business sends "Rate your experience" links.

---

## Project Structure

```
chat-session-feedback-system/
├── backend /          ← Spring Boot + Kotlin API
├── frontend/          ← React + Vite Admin & Customer UI
└── README.md
```

---

## Prerequisites

| Tool | Version | Check |
|---|---|---|
| Java | 17+ | `java -version` |
| Node.js | 18+ | `node -v` |
| MongoDB | Local (27017) or Atlas | — |

---

## 1. Run the Backend

```bash
cd "backend "
./gradlew bootRun --no-daemon
```

Backend starts at → **http://localhost:8080**

On startup, demo data is automatically seeded for two enterprises:
- `enterprise-001`
- `hsenid-mobile`

### MongoDB Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://<user>:<password>@cluster.mongodb.net/?appName=Cluster0
spring.data.mongodb.database=feedback_db
```

---

## 2. Run the Frontend

```bash
cd frontend
npm install       # first time only
npm run dev
```

Frontend starts at → **http://localhost:5174**

---

## Pages

| Page | URL | Description |
|---|---|---|
| Admin Dashboard | http://localhost:5174 | Enter Enterprise ID to manage |
| Form Config Editor | http://localhost:5174/editor/hsenid-mobile | Edit feedback form fields |
| Responses Dashboard | http://localhost:5174/responses/hsenid-mobile | View customer ratings & generate links |
| Customer Feedback | http://localhost:5174/feedback/hsenid-valid-001 | Star rating form (customer-facing) |

---

## Demo Links (pre-seeded)

### enterprise-001
| State | URL |
|---|---|
| ⭐ Active form | http://localhost:5174/feedback/feedback-valid-001 |
| ⏰ Expired | http://localhost:5174/feedback/feedback-expired-001 |
| ✅ Already responded | http://localhost:5174/feedback/feedback-done-001 |

### hsenid-mobile
| State | URL |
|---|---|
| ⭐ Active form | http://localhost:5174/feedback/hsenid-valid-001 |
| ⏰ Expired | http://localhost:5174/feedback/hsenid-expired-001 |
| ✅ Already responded | http://localhost:5174/feedback/hsenid-done-001 |

---

## API Endpoints

### Admin API
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/enterprises/{id}/session-feedback-form` | Get form config |
| `PUT` | `/api/admin/enterprises/{id}/session-feedback-form` | Update form config |
| `GET` | `/api/admin/enterprises/{id}/responses` | View all customer responses |
| `POST` | `/api/admin/enterprises/{id}/create-feedback-link?channel=WHATSAPP` | Generate unique customer link |

### Public API
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/public/feedback/{feedbackId}` | Get feedback form (customer) |
| `POST` | `/api/public/feedback/{feedbackId}/respond` | Submit rating `{"rating": 4}` |

---

## How It Works

1. **Enterprise sets up their form** via the Admin UI
2. **A chat session ends** → call `POST /create-feedback-link` to get a unique URL
3. **Send the URL** to the customer via WhatsApp/SMS
4. **Customer opens the link** → sees the branded star rating form → submits
5. **Enterprise views responses** via the Responses Dashboard

> Each customer **must** receive their own unique link. Sending the same link to multiple customers means only the first person can submit — the rest will see "Already Responded".

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Kotlin, Spring Boot, MongoDB |
| Frontend | React 18, Vite, React Router v6 |
| Database | MongoDB (local or Atlas) |
| Styling | Vanilla CSS (dark glassmorphism) |
