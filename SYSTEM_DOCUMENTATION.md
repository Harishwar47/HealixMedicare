# 🏥 Healix MediCare - Complete System Documentation

## 📋 Table of Contents
1. [System Overview](#system-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Database Design](#database-design)
5. [User Roles & Permissions](#user-roles--permissions)
6. [Features & Functionality](#features--functionality)
7. [Workflows](#workflows)
8. [API Endpoints](#api-endpoints)
9. [Security Implementation](#security-implementation)
10. [Frontend Details](#frontend-details)

---

## 🎯 System Overview

**Healix MediCare** is a comprehensive web-based Doctor Appointment Management System that connects patients with doctors, enabling seamless appointment booking, management, and tracking.

### Key Highlights
- **Application Name**: Healix MediCare
- **Type**: Web Application (Spring Boot MVC)
- **Port**: 8082
- **Database**: H2 (File-based for persistence)
- **Authentication**: Spring Security with role-based access

---

## 💻 Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Core programming language |
| **Spring Boot** | 3.1.4 | Application framework |
| **Spring Security** | 3.1.4 | Authentication & authorization |
| **Spring Data JPA** | 3.1.4 | Database operations |
| **Hibernate** | 6.2.9 | ORM framework |
| **Spring Mail** | 3.1.4 | Email notifications (OTP) |
| **Spring WebSocket** | 3.1.4 | Real-time updates |
| **Maven** | 3.x | Build & dependency management |

### Frontend
| Technology | Purpose |
|------------|---------|
| **Thymeleaf** | Server-side template engine |
| **Bootstrap 5** | Responsive UI framework |
| **Font Awesome** | Icons |
| **JavaScript (ES6)** | Client-side interactivity |
| **Fetch API** | AJAX requests |
| **CSS3** | Custom styling with gradients |

### Database
| Component | Details |
|-----------|---------|
| **Database**: H2 Database (Embedded) | |
| **Mode**: File-based | `./data/healix_database` |
| **Dialect**: H2Dialect | |
| **DDL**: Auto-update | Schema auto-generated |
| **Console**: Enabled | `/h2-console` |

### Email Service
| Component | Configuration |
|-----------|---------------|
| **Provider**: Gmail SMTP | |
| **Email**: healixmedicare@gmail.com | |
| **Port**: 587 (TLS) | |
| **Protocol**: SMTP | |

---

## 🏗️ Architecture

### Application Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                     Client Browser                          │
│  (Patient Dashboard / Doctor Dashboard / Admin Dashboard)   │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTP/HTTPS (Port 8082)
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                  Spring Boot Application                     │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │  Security Layer  │  │  WebSocket       │                │
│  │  (Authentication)│  │  (Real-time)     │                │
│  └──────────────────┘  └──────────────────┘                │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Controllers Layer                        │  │
│  │  - ViewController (MVC)                              │  │
│  │  - AppointmentController (REST)                      │  │
│  │  - RegistrationController (REST)                     │  │
│  └──────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Service Layer                            │  │
│  │  - AppointmentService                                │  │
│  │  - UserService                                       │  │
│  │  - DoctorService                                     │  │
│  └──────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Repository Layer                         │  │
│  │  - AppointmentRepository (JPA)                       │  │
│  │  - UserRepository (JPA)                              │  │
│  │  - DoctorRepository (JPA)                            │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────┬────────────────────────────────────────────┘
                 │ JPA/Hibernate
                 ▼
┌─────────────────────────────────────────────────────────────┐
│              H2 Database (File-based)                        │
│  ./data/healix_database.mv.db                               │
└─────────────────────────────────────────────────────────────┘
```

### Project Structure
```
doctor-appointment/
├── src/
│   ├── main/
│   │   ├── java/com/example/clinic/
│   │   │   ├── ClinicApplication.java          # Main entry point
│   │   │   ├── DataLoader.java                 # Initial data setup
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java              # Bean configurations
│   │   │   │   ├── SecurityConfig.java         # Security settings
│   │   │   │   ├── WebConfig.java              # MVC configuration
│   │   │   │   └── WebSocketConfig.java        # WebSocket setup
│   │   │   ├── controller/
│   │   │   │   ├── AppointmentController.java  # Appointment APIs
│   │   │   │   ├── RegistrationController.java # Registration/Auth APIs
│   │   │   │   ├── ViewController.java         # Page routing
│   │   │   │   └── ErrorController.java        # Error handling
│   │   │   ├── model/
│   │   │   │   ├── User.java                   # User entity
│   │   │   │   ├── Doctor.java                 # Doctor entity
│   │   │   │   ├── Appointment.java            # Appointment entity
│   │   │   │   └── Role.java                   # Role enum
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java         # User data access
│   │   │   │   ├── DoctorRepository.java       # Doctor data access
│   │   │   │   └── AppointmentRepository.java  # Appointment data access
│   │   │   └── service/
│   │   │       ├── UserService.java            # User business logic
│   │   │       ├── DoctorService.java          # Doctor business logic
│   │   │       └── AppointmentService.java     # Appointment logic
│   │   └── resources/
│   │       ├── application.properties          # App configuration
│   │       ├── static/
│   │       │   ├── css/styles.css              # Custom styles
│   │       │   ├── js/app.js                   # Client-side JS
│   │       │   └── images/                     # Images
│   │       └── templates/                      # Thymeleaf templates
│   │           ├── login.html                  # Login page
│   │           ├── dashboard.html              # Generic dashboard
│   │           ├── patient-dashboard.html      # Patient UI
│   │           ├── doctor-dashboard.html       # Doctor UI
│   │           └── admin-dashboard.html        # Admin UI
├── data/                                        # H2 database files
├── pom.xml                                      # Maven dependencies
├── start-healix.bat                             # Windows start script
└── stop-healix.bat                              # Windows stop script
```

---

## 🗄️ Database Design

### Entity-Relationship Diagram
```
┌──────────────────┐        ┌──────────────────┐
│      USER        │        │     DOCTOR       │
├──────────────────┤        ├──────────────────┤
│ id (PK)          │        │ id (PK)          │
│ username         │        │ name             │
│ password         │        │ specialty        │
│ fullName         │        │ email            │
│ email            │        │ phone            │
│ phone            │        │ experience       │
│ dateOfBirth      │        │ qualification    │
│ gender           │        │ available        │
│ bloodType        │        └──────────────────┘
│ roles (Set)      │                │
└──────────────────┘                │
        │                           │
        │    ┌──────────────────────┴──────────────┐
        │    │                                      │
        │    │         APPOINTMENT                  │
        └────┼──────────────────────────────────────┤
             │ id (PK)                              │
             │ patient_id (FK → USER)               │
             │ doctor_id (FK → DOCTOR)              │
             │ date                                 │
             │ time                                 │
             │ patientName                          │
             │ reason                               │
             │ confirmed (boolean)                  │
             │ status (Pending/Confirmed/Cancelled) │
             └──────────────────────────────────────┘
```

### Database Tables

#### 1. **USERS Table**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| username | VARCHAR(255) | UNIQUE, NOT NULL | Patient ID (e.g., PAT390583) |
| password | VARCHAR(255) | NOT NULL | Encrypted password |
| full_name | VARCHAR(255) | | Patient's full name |
| email | VARCHAR(255) | | Email address |
| phone | VARCHAR(20) | | Phone number |
| date_of_birth | DATE | | Date of birth |
| gender | VARCHAR(10) | | Male/Female/Other |
| blood_type | VARCHAR(5) | | A+, B+, O+, AB+, etc. |

#### 2. **USERS_ROLES Table** (Many-to-Many)
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT | FOREIGN KEY → USERS(id) | User reference |
| roles | VARCHAR(50) | | ROLE_PATIENT, ROLE_DOCTOR, ROLE_ADMIN |

#### 3. **DOCTOR Table**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| name | VARCHAR(255) | NOT NULL | Doctor's name |
| specialty | VARCHAR(255) | NOT NULL | Medical specialty |
| email | VARCHAR(255) | | Email address |
| phone | VARCHAR(20) | | Phone number |
| experience | VARCHAR(50) | | Years of experience |
| qualification | VARCHAR(255) | | Medical degrees |
| available | BOOLEAN | DEFAULT TRUE | Availability status |

#### 4. **APPOINTMENT Table**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| patient_id | BIGINT | FOREIGN KEY → USERS(id) | Patient reference |
| doctor_id | BIGINT | FOREIGN KEY → DOCTOR(id) | Doctor reference |
| date | DATE | NOT NULL | Appointment date |
| time | VARCHAR(10) | NOT NULL | Appointment time |
| patient_name | VARCHAR(255) | | Cached patient username |
| reason | VARCHAR(500) | | Consultation reason |
| confirmed | BOOLEAN | DEFAULT FALSE | Confirmation status |
| status | VARCHAR(20) | DEFAULT 'Pending' | Pending/Confirmed/Cancelled |

---

## 👥 User Roles & Permissions

### 1. **Patient (ROLE_PATIENT)**

**Access:** `/patient/dashboard`

**Capabilities:**
- ✅ View personal dashboard with statistics
- ✅ View all personal appointments (today and future)
- ✅ Book new appointments with doctors
- ✅ Search and filter available doctors
- ✅ View doctor profiles (name, specialty, experience)
- ✅ Update personal profile (name, email, phone, DOB, gender, blood type)
- ✅ View appointment status (Pending/Confirmed/Cancelled)
- ✅ Access medical history
- ✅ Receive appointment notifications

**Restrictions:**
- ❌ Cannot confirm/cancel appointments
- ❌ Cannot access doctor or admin dashboards
- ❌ Cannot view other patients' data

**Default Test Account:**
- Username: `harish` (or generated PAT ID)
- Password: `harish`

---

### 2. **Doctor (ROLE_DOCTOR)**

**Access:** `/doctor/dashboard`

**Capabilities:**
- ✅ View personal dashboard with today's appointments
- ✅ View all scheduled appointments
- ✅ Confirm pending appointments
- ✅ Cancel appointments (with status update)
- ✅ Start consultations
- ✅ View patient details (name, date, time, reason)
- ✅ View appointment statistics
- ✅ Receive real-time appointment updates via WebSocket

**Restrictions:**
- ❌ Cannot book appointments for patients
- ❌ Cannot access patient or admin dashboards
- ❌ Cannot modify doctor profiles

**Default Test Accounts:**
- Username: `dralice`, Password: `dralice` (Cardiology)
- Username: `drbob`, Password: `drbob` (Orthopedics)

---

### 3. **Admin (ROLE_ADMIN)**

**Access:** `/admin/dashboard`

**Capabilities:**
- ✅ View system overview dashboard
- ✅ Manage all users (patients, doctors, admins)
- ✅ View all appointments across the system
- ✅ View system statistics
- ✅ Monitor application health
- ✅ Access H2 database console (`/h2-console`)

**Restrictions:**
- ❌ Admin features are basic (can be expanded)

**Default Test Account:**
- Username: `admin407`
- Password: `admin407`

---

## 🎯 Features & Functionality

### 1. **Authentication & Authorization**

#### Login System
- **Endpoint:** `/login`
- **Method:** POST
- **Features:**
  - Username/password authentication
  - BCrypt password encryption
  - Session-based authentication
  - Role-based dashboard redirection
  - "Remember Me" option
  - Error handling with user-friendly messages

#### Registration System
- **Endpoint:** `/api/register`
- **Method:** POST
- **Process:**
  1. User provides full name, email, password
  2. System generates unique Patient ID (e.g., PAT390583)
  3. Password is encrypted (BCrypt)
  4. ROLE_PATIENT is assigned
  5. User is redirected to login

#### Forgot Password (OTP-based)
- **Endpoints:**
  - `/api/forgot-password` - Send OTP
  - `/api/verify-otp` - Verify OTP
  - `/api/reset-password` - Reset password

- **Process:**
  1. User enters Patient ID
  2. System generates 6-digit OTP (10-minute expiry)
  3. OTP sent via Gmail SMTP
  4. User enters OTP
  5. If valid, user can reset password
  6. "Resend OTP" option available

- **Email Configuration:**
  - From: healixmedicare@gmail.com
  - Subject: "Your Healix MediCare OTP Code"
  - Body includes OTP and expiry time

---

### 2. **Patient Features**

#### Dashboard Overview
- **Welcome Message:** Displays patient's full name
- **Quick Statistics:**
  - Total appointments count
  - Upcoming appointments count
  - Available doctors count

#### My Appointments
- **Display:** All appointments (today and future only)
- **Information Shown:**
  - Doctor name and specialty
  - Appointment date and time
  - Status badge (Pending/Confirmed/Cancelled)
  - Color-coded badges:
    - 🟢 Green: Confirmed
    - 🟡 Yellow: Pending
    - 🔴 Red: Cancelled

#### Book Appointment
- **Form Fields:**
  - Select doctor (dropdown with doctor names)
  - Select date (date picker, minimum: today)
  - Select time (dropdown with available slots)
  - Reason for visit (text area)

- **Available Time Slots:**
  - 09:00, 09:30, 10:00, 10:30, 11:00, 11:30
  - 14:00, 14:30, 15:00

- **Process:**
  1. Patient selects doctor
  2. Time slots appear dynamically
  3. Patient fills date, time, reason
  4. Click "Book Appointment"
  5. AJAX request to `/api/appointments`
  6. Success notification displayed
  7. Appointment appears in "My Appointments"

#### Find Doctors
- **Features:**
  - Search bar (by name or specialty)
  - Doctor cards display:
    - Name
    - Specialty
    - Experience
    - Qualification
    - Availability status
  - Real-time filtering

#### My Profile
- **Editable Fields:**
  - Full Name
  - Email
  - Phone Number
  - Date of Birth (date picker)
  - Gender (Male/Female/Other)
  - Blood Type (A+, A-, B+, B-, O+, O-, AB+, AB-)

- **Read-only Fields:**
  - Patient ID (auto-generated)

- **Features:**
  - Pre-populated with existing data
  - Save via AJAX (no page reload)
  - Success/error notifications
  - Data persists across sessions
  - All fields stored in database

#### Medical History
- Placeholder section for future implementation
- Will display past consultations, prescriptions, diagnoses

---

### 3. **Doctor Features**

#### Dashboard Overview
- **Welcome Message:** Displays doctor's name
- **Today's Appointments:**
  - All appointments scheduled for current date
  - Appointment cards show:
    - Patient name and ID
    - Date and time
    - Reason for visit
    - Status badge (Pending/Confirmed/Cancelled)

#### Appointment Management

##### Confirm Appointment
- **Button:** Green "Confirm" button (appears for Pending appointments)
- **Action:** 
  - Custom confirmation dialog (centered)
  - Yes/No buttons
  - Updates status to "Confirmed"
  - Updates badge color to green
  - Sends AJAX request to `/api/appointments/confirm/{id}`

##### Cancel Appointment
- **Button:** Red "Cancel" button (hidden for already cancelled)
- **Action:**
  - Custom confirmation dialog
  - Updates status to "Cancelled"
  - Updates badge color to red
  - Appointment remains visible (not deleted)
  - Sends AJAX request to `/api/appointments/cancel/{id}`

##### Start Consultation
- **Button:** Blue "Start Consultation" button (for Confirmed appointments)
- **Action:** Placeholder for future feature

#### Real-time Updates
- WebSocket connection for live appointment changes
- Automatically updates appointment list when changes occur

---

### 4. **Admin Features**

#### Dashboard Overview
- System statistics
- User management interface
- Appointment overview

---

### 5. **Notification System**

#### Custom Notifications
- **Types:**
  - Success (green with checkmark)
  - Error (red with X)

- **Features:**
  - Centered on screen
  - Smooth fade-in/fade-out
  - Auto-dismiss after 3 seconds
  - Modal overlay
  - "OK" button for manual dismiss

#### Custom Confirmation Dialogs
- **Used for:** Doctor appointment confirm/cancel
- **Features:**
  - Centered dialog box
  - Yes/No buttons
  - Green "Yes" button
  - Red "No" button
  - Modal overlay
  - Callback function on "Yes"

---

### 6. **Data Persistence**

#### Initial Data Setup (DataLoader.java)
- **Runs once on first startup** (when user count = 0)
- **Creates:**
  - 1 Admin (admin407)
  - 2 Sample Patients
  - 2 Sample Doctors (Dr. Alice, Dr. Bob)
  - 3 Sample Appointments

- **Note:** Deleted users stay deleted (not recreated on restart)

#### Database Persistence
- **Mode:** File-based H2 database
- **Location:** `./data/healix_database.mv.db`
- **Benefits:**
  - Data persists across server restarts
  - No data loss
  - Fast access
  - Easy backup (copy database file)

---

## 🔄 Workflows

### 1. **Patient Registration & Login Workflow**
```
START
  ↓
User visits: http://localhost:8082
  ↓
Redirected to: /login
  ↓
[New User] Click "Register Here"
  ↓
Fill registration form:
  - Full Name
  - Email
  - Password
  ↓
Submit → POST /api/register
  ↓
System generates Patient ID (e.g., PAT390583)
  ↓
Password encrypted (BCrypt)
  ↓
User created with ROLE_PATIENT
  ↓
Redirected to /login with success message
  ↓
[Login]
Enter username (Patient ID) and password
  ↓
Submit → POST /login
  ↓
Spring Security authenticates
  ↓
[Success] → Redirected to /patient/dashboard
  ↓
Dashboard loaded with:
  - Patient's appointments
  - Available doctors
  - Profile data
  ↓
END
```

### 2. **Forgot Password Workflow**
```
START
  ↓
User clicks "Forgot Password?"
  ↓
Enter Patient ID → POST /api/forgot-password
  ↓
System checks if user exists
  ↓
[User Found]
  ↓
Generate 6-digit OTP
  ↓
Store OTP in memory (ConcurrentHashMap)
  ↓
Set 10-minute expiry
  ↓
Send email via Gmail SMTP
  - To: user's email
  - Subject: "Your Healix MediCare OTP Code"
  - Body: OTP + expiry message
  ↓
User receives email
  ↓
Enter OTP on form → POST /api/verify-otp
  ↓
System validates:
  - OTP matches?
  - Not expired?
  ↓
[Valid]
  ↓
Show password reset form
  ↓
Enter new password → POST /api/reset-password
  ↓
Password encrypted (BCrypt)
  ↓
User updated in database
  ↓
OTP removed from memory
  ↓
Redirected to /login
  ↓
END
```

### 3. **Book Appointment Workflow**
```
START (Patient Dashboard)
  ↓
Click "Book Appointment" in sidebar
  ↓
Book Appointment section displayed
  ↓
[Step 1] Select Doctor from dropdown
  ↓
JavaScript fetches available time slots
  ↓
Time dropdown populated:
  - 09:00, 09:30, 10:00, 10:30, 11:00, 11:30
  - 14:00, 14:30, 15:00
  ↓
[Step 2] Select Date (minimum: today)
  ↓
[Step 3] Select Time from available slots
  ↓
[Step 4] Enter Reason for visit
  ↓
Click "Book Appointment" button
  ↓
JavaScript sends AJAX POST /api/appointments
  - Body: {doctorId, patientUsername, date, time, reason}
  ↓
Backend creates Appointment:
  - Links to Patient (via username)
  - Links to Doctor (via doctorId)
  - Sets status = "Pending"
  - Sets confirmed = false
  ↓
Saves to database
  ↓
Returns success response
  ↓
Frontend shows success notification
  ↓
"My Appointments" auto-updates
  ↓
Appointment visible with "Pending" badge
  ↓
END
```

### 4. **Doctor Confirm Appointment Workflow**
```
START (Doctor Dashboard)
  ↓
View "Today's Appointments"
  ↓
Appointment card shows:
  - Patient name
  - Date/Time
  - Reason
  - Status: "Pending" (yellow badge)
  - [Confirm] button (green)
  - [Cancel] button (red)
  ↓
Doctor clicks [Confirm] button
  ↓
Custom confirmation dialog appears:
  "Are you sure you want to confirm this appointment?"
  [Yes] [No]
  ↓
Doctor clicks [Yes]
  ↓
AJAX POST /api/appointments/confirm/{id}
  ↓
Backend finds appointment by ID
  ↓
Updates:
  - confirmed = true
  - status = "Confirmed"
  ↓
Saves to database
  ↓
Returns success message
  ↓
Frontend updates UI:
  - Badge changes to "Confirmed" (green)
  - [Confirm] button removed
  - [Start Consultation] button appears
  ↓
WebSocket notification sent (real-time update)
  ↓
Patient sees updated status in their dashboard
  ↓
END
```

### 5. **Doctor Cancel Appointment Workflow**
```
START (Doctor Dashboard)
  ↓
Doctor clicks [Cancel] button on appointment
  ↓
Custom confirmation dialog:
  "Are you sure you want to cancel this appointment?"
  [Yes] [No]
  ↓
Doctor clicks [Yes]
  ↓
AJAX POST /api/appointments/cancel/{id}
  ↓
Backend finds appointment by ID
  ↓
Updates:
  - status = "Cancelled"
  - confirmed remains same
  ↓
Saves to database (NOT deleted)
  ↓
Returns success message
  ↓
Frontend updates UI:
  - Badge changes to "Cancelled" (red)
  - [Cancel] button hidden
  - [Confirm] button hidden
  ↓
Appointment remains visible
  ↓
Patient sees "Cancelled" status in their appointments
  ↓
END
```

### 6. **Update Profile Workflow**
```
START (Patient Dashboard)
  ↓
Click "My Profile" in sidebar
  ↓
Profile form loaded with existing data:
  - Full Name (pre-filled)
  - Email (pre-filled)
  - Phone (pre-filled)
  - Date of Birth (pre-filled)
  - Gender (pre-selected)
  - Blood Type (pre-selected)
  ↓
Patient edits fields
  ↓
Click "Update Profile" button
  ↓
JavaScript prevents form default submission
  ↓
Gathers form data into JSON object
  ↓
Sends AJAX POST /api/update-profile
  - Body: {patientId, fullName, email, phone, dob, gender, bloodType}
  ↓
Backend validates Patient ID
  ↓
Finds User in database
  ↓
Updates fields:
  - fullName
  - email
  - phone
  - dateOfBirth (parsed from string)
  - gender
  - bloodType
  ↓
Saves User to database
  ↓
Returns success response
  ↓
Frontend shows "Profile updated successfully!" notification
  ↓
No page reload
  ↓
Data persists in database
  ↓
On page refresh/restart:
  - Form fields pre-populated with saved data
  ↓
END
```

---

## 🔌 API Endpoints

### Authentication APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/login` | Display login page | - | HTML page |
| POST | `/login` | Authenticate user | `username`, `password` | Redirect to dashboard |
| GET | `/logout` | Logout user | - | Redirect to login |

### Registration APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/register` | Register new patient | `{fullName, email, password}` | `{patientId, message}` |
| POST | `/api/forgot-password` | Send OTP | `{patientId}` | `{message, otp (demo)}` |
| POST | `/api/verify-otp` | Verify OTP | `{patientId, otp}` | `{success, message}` |
| POST | `/api/reset-password` | Reset password | `{patientId, newPassword}` | `{message}` |
| POST | `/api/update-profile` | Update profile | `{patientId, fullName, email, phone, dob, gender, bloodType}` | `{success, message}` |

### Appointment APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/appointments` | Get all appointments | - | `[{appointment}, ...]` |
| POST | `/api/appointments` | Book appointment | `{doctorId, patientUsername, date, time, reason}` | `{appointment}` |
| POST | `/api/appointments/confirm/{id}` | Confirm appointment | - | `{message}` |
| POST | `/api/appointments/cancel/{id}` | Cancel appointment | - | `{message}` |
| GET | `/api/appointments/{id}` | Get appointment details | - | `{appointment}` |

### View Controllers (MVC)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/` | Landing page redirect | Public |
| GET | `/patient/dashboard` | Patient dashboard | ROLE_PATIENT |
| GET | `/doctor/dashboard` | Doctor dashboard | ROLE_DOCTOR |
| GET | `/admin/dashboard` | Admin dashboard | ROLE_ADMIN |
| GET | `/dashboard` | Generic dashboard redirect | Authenticated |

---

## 🔒 Security Implementation

### Spring Security Configuration

#### 1. **Password Encryption**
- **Algorithm:** BCrypt
- **Strength:** 10 rounds
- **Implementation:** `BCryptPasswordEncoder`

#### 2. **Authentication**
- **Type:** Form-based login
- **Session:** Server-side session management
- **Remember Me:** Available via checkbox

#### 3. **Authorization Rules**
```java
http.authorizeHttpRequests()
    .requestMatchers("/patient/**").hasRole("PATIENT")
    .requestMatchers("/doctor/**").hasRole("DOCTOR")
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/**").permitAll()
    .requestMatchers("/login", "/register").permitAll()
    .anyRequest().authenticated();
```

#### 4. **Custom Success Handler**
- **Class:** `CustomAuthenticationSuccessHandler`
- **Logic:** Redirects based on role:
  - ROLE_PATIENT → `/patient/dashboard`
  - ROLE_DOCTOR → `/doctor/dashboard`
  - ROLE_ADMIN → `/admin/dashboard`

#### 5. **CSRF Protection**
- **Status:** Enabled (default)
- **Token:** Automatically included in Thymeleaf forms

#### 6. **Session Management**
- **Strategy:** Session fixation protection
- **Concurrent Sessions:** Allowed
- **Session Timeout:** Default (30 minutes)

---

## 🎨 Frontend Details

### Design System

#### Color Palette
- **Primary Blue:** `#2F80ED`
- **Primary Teal:** `#10B981`
- **Success Green:** `#27AE60`
- **Warning Orange:** `#F39C12`
- **Danger Red:** `#E74C3C`
- **Dark Text:** `#2C3E50`
- **Light Background:** `#F8F9FA`

#### Typography
- **Font Family:** System fonts (-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto)
- **Headings:** Bold, gradient text
- **Body:** 16px, readable line-height

#### UI Components

##### 1. **Navigation Sidebar**
- **Style:** Vertical, sticky
- **Items:**
  - My Appointments
  - Book Appointment
  - Find Doctors
  - My Profile
  - Medical History
- **Active State:** Blue background
- **Hover Effect:** Light blue highlight

##### 2. **Dashboard Cards**
- **Style:** White background, rounded corners
- **Shadow:** `0 5px 20px rgba(0,0,0,0.08)`
- **Header:** Gradient blue-teal
- **Content:** Padded, organized sections

##### 3. **Stat Cards**
- **Layout:** Flex, equal width
- **Content:** Large number + label
- **Animation:** Hover lift effect

##### 4. **Appointment Cards**
- **Style:** White, bordered
- **Layout:** Flexbox (info left, actions right)
- **Icons:** Font Awesome (calendar, clock, stethoscope)
- **Badges:** Bootstrap badges with custom colors

##### 5. **Buttons**
- **Primary:** Gradient blue-teal
- **Success:** Green (confirm)
- **Danger:** Red (cancel)
- **Hover:** Lift + shadow effect

##### 6. **Forms**
- **Inputs:** Bootstrap form-control
- **Validation:** HTML5 + custom JS
- **Layout:** Responsive grid

##### 7. **Notifications**
- **Position:** Fixed, centered
- **Z-index:** 10000
- **Animation:** Fade in/out
- **Overlay:** Semi-transparent backdrop

##### 8. **Confirmation Dialogs**
- **Position:** Fixed, centered
- **Buttons:** Yes (green) / No (red)
- **Overlay:** Modal backdrop

### Responsive Design
- **Breakpoints:**
  - Desktop: ≥ 992px
  - Tablet: 768px - 991px
  - Mobile: < 768px

- **Layout:**
  - Desktop: Sidebar + content (3-9 grid)
  - Tablet: Stacked layout
  - Mobile: Full-width, hamburger menu

### JavaScript Features

#### 1. **Real-time Search** (Find Doctors)
```javascript
// Filter doctors by name or specialty
searchInput.addEventListener('input', (e) => {
    const searchTerm = e.target.value.toLowerCase();
    doctorCards.forEach(card => {
        const name = card.querySelector('.name').textContent;
        const specialty = card.querySelector('.specialty').textContent;
        card.style.display = 
            name.includes(searchTerm) || specialty.includes(searchTerm) 
            ? 'block' : 'none';
    });
});
```

#### 2. **Dynamic Time Slots**
```javascript
// Populate time slots when doctor is selected
doctorSelect.addEventListener('change', (e) => {
    const slots = ['09:00', '09:30', '10:00', '10:30', 
                   '11:00', '11:30', '14:00', '14:30', '15:00'];
    timeSelect.innerHTML = '<option>Select time...</option>';
    slots.forEach(time => {
        timeSelect.innerHTML += `<option>${time}</option>`;
    });
});
```

#### 3. **AJAX Form Submission**
```javascript
// Book appointment without page reload
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = { /* form data */ };
    const response = await fetch('/api/appointments', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    });
    const result = await response.json();
    showNotification(result.message, 'success');
});
```

#### 4. **Custom Notifications**
```javascript
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <span class="icon"></span>
        <div class="message">${message}</div>
        <button class="notification-ok-btn">OK</button>
    `;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
}
```

#### 5. **Navigation System**
```javascript
// Switch between sections without page reload
navItems.forEach(item => {
    item.addEventListener('click', () => {
        const section = item.dataset.section;
        contentSections.forEach(s => s.style.display = 'none');
        document.getElementById(`${section}-section`).style.display = 'block';
        navItems.forEach(n => n.classList.remove('active'));
        item.classList.add('active');
    });
});
```

---

## 📊 Key Features Summary

### ✅ Implemented Features
1. ✅ User Registration with auto-generated Patient ID
2. ✅ Secure Login (BCrypt encryption)
3. ✅ Forgot Password with Email OTP
4. ✅ Role-based access control (Patient/Doctor/Admin)
5. ✅ Patient appointment booking
6. ✅ Doctor appointment confirmation
7. ✅ Doctor appointment cancellation (with status)
8. ✅ Real-time doctor search
9. ✅ Profile management (full 6 fields)
10. ✅ Appointment status tracking (Pending/Confirmed/Cancelled)
11. ✅ Custom notifications (centered, auto-dismiss)
12. ✅ Custom confirmation dialogs (Yes/No)
13. ✅ Responsive design (mobile-friendly)
14. ✅ Data persistence (file-based H2)
15. ✅ Email notifications (Gmail SMTP)
16. ✅ WebSocket real-time updates
17. ✅ Past appointment filtering
18. ✅ Initial data seeding

### 🚧 Future Enhancements
1. Medical history tracking
2. Prescription management
3. Video consultation
4. Payment integration
5. SMS notifications
6. Advanced search filters
7. Appointment reminders
8. Doctor availability calendar
9. Patient feedback/reviews
10. Analytics dashboard

---

## 🚀 Deployment & Running

### Start Application
```bash
# Windows
start-healix.bat

# Manual
java -jar target/doctor-appointment-0.0.1-SNAPSHOT.jar
```

### Stop Application
```bash
# Windows
stop-healix.bat

# Manual
taskkill /f /im java.exe
```

### Build Application
```bash
mvn clean package -DskipTests
```

### Access Points
- **Application:** http://localhost:8082
- **H2 Console:** http://localhost:8082/h2-console
  - JDBC URL: `jdbc:h2:file:./data/healix_database`
  - Username: `sa`
  - Password: (empty)

---

## 📝 Configuration Files

### application.properties
```properties
# Server
server.port=8082

# Database
spring.datasource.url=jdbc:h2:file:./data/healix_database
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=update

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=healixmedicare@gmail.com
spring.mail.password=oolmqqbnxgxgecdf
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Thymeleaf
spring.thymeleaf.cache=false
```

---

## 📞 Support & Maintenance

### Common Issues

#### 1. Port Already in Use
```bash
# Kill existing Java processes
taskkill /f /im java.exe
```

#### 2. Database Locked
```bash
# Delete lock file
rm data/healix_database.lock.db
```

#### 3. Email Not Sending
- Check Gmail app password
- Verify SMTP settings
- Check firewall rules

---

## 📈 System Statistics

- **Total Lines of Code:** ~3,500+
- **Java Classes:** 23
- **REST Endpoints:** 12+
- **Thymeleaf Templates:** 5
- **Database Tables:** 4
- **User Roles:** 3
- **Appointment States:** 3

---

## 🏆 Technology Achievements

✅ **Full-stack Spring Boot application**
✅ **RESTful API design**
✅ **Secure authentication & authorization**
✅ **Real-time updates (WebSocket)**
✅ **Email integration**
✅ **Responsive UI**
✅ **Data persistence**
✅ **AJAX-based interactions**
✅ **Custom notification system**

---

**Document Version:** 1.0  
**Last Updated:** November 3, 2025  
**Application:** Healix MediCare Doctor Appointment System  
**Author:** System Documentation (Auto-generated)
