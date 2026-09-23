# QR-Based Attendance System 📱📲

An Android-based attendance management application that uses **time-limited QR codes** to provide a faster, more secure, and transparent way to record student attendance.

The system supports separate **Teacher** and **Student** roles. Teachers generate a unique QR code for each class session, while students scan the code using their device camera to mark attendance. QR codes automatically expire after **5 minutes**, and duplicate scans are rejected.

> Academic project developed for the **Department of MCA, KLS Gogte Institute of Technology, Belagavi (2025–2026)**.

---

## 📌 Overview

Traditional attendance methods such as manual roll calls and paper-based attendance are time-consuming and can allow proxy attendance.

This project provides a digital alternative using:

- Kotlin-based Android development
- MVVM architecture
- Firebase Authentication
- Room Database
- ZXing QR code generation and scanning
- Role-based Teacher and Student workflows
- Five-minute QR code expiry
- Duplicate attendance prevention
- Real-time attendance count and reporting

The application stores attendance records locally using Room and uses Firebase Authentication for registration, email verification, login, and password reset.

---

## ✨ Key Features

### 👨‍🏫 Teacher

- Register using an institutional `.edu` email address
- Email verification through Firebase
- Secure login and password reset
- Enter faculty, department, subject, and session details
- Generate a unique QR code for each class session
- QR code automatically expires after 5 minutes
- Live countdown timer for QR validity
- View live attendance count
- View student names and scan timestamps
- Access session-specific attendance reports

### 👨‍🎓 Student

- Register using an institutional `.edu` email address
- Verify email before account activation
- Secure login
- Scan teacher-generated QR codes
- Automatically mark attendance after successful validation
- Prevent duplicate attendance for the same session
- View attendance count and percentage
- Receive validation feedback for expired or duplicate QR scans

---

## 🔐 Security & Validation

The system includes multiple mechanisms to reduce unauthorized or duplicate attendance:

| Mechanism | Purpose |
|---|---|
| `.edu` email validation | Restricts registration to institutional email addresses |
| Firebase Authentication | Handles authentication and email verification |
| Email verification | Confirms the registered email before account activation |
| Unique session ID | Identifies each generated class session |
| 5-minute QR expiry | Limits the period during which a QR code is valid |
| Duplicate scan prevention | Prevents a student from marking attendance twice for one session |
| Role-based access | Separates Teacher and Student functionality |

---

## 🏗️ Architecture

The application follows the **Model-View-ViewModel (MVVM)** architecture.

```text
┌──────────────────────────────┐
│          UI Layer            │
│ Activities + XML Layouts     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│       ViewModel Layer        │
│ State + Business Logic       │
│ LiveData                     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│       Repository Layer       │
│ Single Source of Truth       │
│ Data Coordination            │
└──────────────┬───────────────┘
               │
        ┌──────┴───────┐
        ▼              ▼
┌──────────────┐ ┌──────────────────┐
│ Room Database│ │ Firebase Auth    │
│ Local Storage│ │ Authentication   │
└──────────────┘ └──────────────────┘
```

The architecture separates UI, business logic, repository operations, and data sources to improve maintainability and testability.

---

## 🔄 Application Flow

### Teacher Flow

```text
Register
   ↓
Email Verification
   ↓
Login
   ↓
Teacher Dashboard
   ↓
Enter Faculty & Session Details
   ↓
Generate QR Code
   ↓
QR Valid for 5 Minutes
   ↓
Monitor Live Attendance
   ↓
View Attendance Report
```

### Student Flow

```text
Register
   ↓
Email Verification
   ↓
Login
   ↓
Student Dashboard
   ↓
Scan QR Code
   ↓
Validate Expiry
   ↓
Check Duplicate Scan
   ↓
Record Attendance
   ↓
Update Attendance Percentage
```

---

## 🧩 QR Code Validation

Each generated QR code contains session-specific information including:

- Session ID
- Faculty information
- Subject
- Session details
- Date
- Expiry timestamp

When a student scans the QR code, the application:

1. Reads the QR content.
2. Parses the embedded JSON data.
3. Checks whether the QR code has expired.
4. Checks whether the student has already attended that session.
5. Records the attendance if all validations pass.
6. Stores the attendance record in the Room database.

---

## 🗄️ Database Design

The local Room database contains the primary entities:

### Users

Stores user information and role details.

```text
users
├── id
├── name
├── email
├── password
└── role
```

### Classes

Stores class and subject information.

```text
classes
├── id
├── name
├── subject
├── teacherId
└── classCode
```

### Sessions

Stores QR session information.

```text
sessions
├── id
├── classId
├── qrData
├── generatedAt
├── expireAt
└── isActive
```

### Attendance

Stores student attendance records.

```text
attendance
├── id
├── sessionId
├── studentId
├── classId
├── markedAt
└── status
```

---

## 🛠️ Technology Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Android application development |
| **Android Studio** | Development environment |
| **MVVM** | Application architecture |
| **Firebase Authentication** | Registration, login, email verification and password reset |
| **Room Database** | Local persistent storage |
| **SQLite** | Underlying local database |
| **ZXing** | QR generation and scanning |
| **LiveData** | Reactive UI updates |
| **ViewModel** | Lifecycle-aware state and business logic |
| **Kotlin Coroutines** | Asynchronous database operations |
| **Gradle Kotlin DSL** | Build and dependency management |
| **Git / GitHub** | Version control |

---

## 📦 Major Dependencies

The project report specifies the following versions:

```text
Kotlin                    1.9.0
Room Database             2.6.1
Firebase Authentication   22.3.1
ZXing Android Embedded    4.3.0
ZXing Core                3.5.2
ViewModel                 2.7.0
LiveData                  2.7.0
Kotlin Coroutines         1.7.3
```

---

## 📁 Project Structure

The project follows an MVVM-oriented Android structure:

```text
app/
└── src/
    └── main/
        └── java/
            └── com.student.attendanceapp/
                ├── QRAttendanceApp.kt
                ├── MainActivity.kt
                │
                ├── data/
                │   ├── entities/
                │   │   ├── User.kt
                │   │   ├── ClassRoom.kt
                │   │   ├── Session.kt
                │   │   └── Attendance.kt
                │   │
                │   ├── dao/
                │   │   ├── UserDao.kt
                │   │   ├── ClassRoomDao.kt
                │   │   ├── SessionDao.kt
                │   │   └── AttendanceDao.kt
                │   │
                │   └── AppDatabase.kt
                │
                ├── repository/
                │   └── AppRepository.kt
                │
                ├── utils/
                │   └── QRCodeUtil.kt
                │
                ├── viewmodel/
                │   ├── AuthViewModel.kt
                │   ├── TeacherViewModel.kt
                │   └── StudentViewModel.kt
                │
                └── ui/
                    ├── auth/
                    │   └── RegisterActivity.kt
                    │
                    ├── teacher/
                    │   └── TeacherDashboardActivity.kt
                    │
                    └── student/
                        └── StudentDashboardActivity.kt
```

---

## ⚙️ Requirements

### Development Environment

- Android Studio Flamingo or later
- Kotlin 1.9.0
- Android SDK API 24–34
- Gradle with Kotlin DSL
- Minimum 8 GB RAM recommended
- 256 GB SSD recommended
- Android device or emulator with camera support

### Supported Android Version

```text
Minimum SDK: API 24
Android 7.0 or higher
```

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd <your-project-directory>
```

### 2. Open in Android Studio

Open the project in **Android Studio** and allow Gradle to sync the project dependencies.

### 3. Configure Firebase

Create/configure a Firebase project and connect it to the Android application.

Firebase Authentication is used for:

- User registration
- Email verification
- Login
- Password reset

> Keep Firebase configuration files and credentials out of public repositories when they contain sensitive project information.

### 4. Build the Project

From Android Studio:

```text
Build → Make Project
```

or use:

```bash
./gradlew build
```

### 5. Run the Application

Run the application on:

- Android Emulator
- Physical Android device with camera support

---

## 🧪 Testing

The application was tested using both an Android emulator and physical Android devices.

### Unit Testing

Unit tests were performed for key application components, including:

- `AuthViewModel` — registration, login, and password reset operations
- `TeacherViewModel` — session ID generation and QR content creation
- `AttendanceDao` — attendance insertion, duplicate detection, and count queries
- ViewModel, DAO, and Repository components

### Functionality Testing

| # |         Test Case                 |         Expected Result                       | Status |
|---:|---|---|:---:|
| 1 | Register with `.edu` email        | Account created and verification email sent   | ✅ PASS |
| 2 | Register with non-`.edu` email    | Registration blocked with an error            | ✅ PASS |
| 3 | Register with duplicate email     | Duplicate registration blocked                | ✅ PASS |
| 4 | Login with valid credentials      | Navigate to the correct dashboard             | ✅ PASS |
| 5 | Login with invalid credentials    | Error message displayed                       | ✅ PASS |
| 6 | Forgot password with valid email  | Reset link sent through Firebase              | ✅ PASS |
| 7 | Generate QR code                  | QR displayed with 5-minute timer              | ✅ PASS |
| 8 | Scan valid QR code                | Attendance marked and details displayed       | ✅ PASS |
| 9 | Scan expired QR code              | Expired error shown and attendance not recorded | ✅ PASS |
| 10 | Scan the same QR twic            | Duplicate warning displayed                    | ✅ PASS |
| 11 | View attendance report           | Student names and scan times displayed         | ✅ PASS |
| 12 | Attendance percentage update     | Percentage recalculated after scan             | ✅ PASS |

### Integration Testing

Integration testing covered the interaction between:

- UI layer
- ViewModel layer
- Repository layer
- Room Database
- Firebase Authentication
- QR code generation
- Session management

Testing confirmed that authentication, session creation, QR generation, and attendance recording work together correctly.

### Verification & Validation

The application was also tested on physical Android devices to verify camera-based QR scanning and the implemented attendance workflow under real-world conditions.

## 📊 Results

The implemented modules were successfully tested on Android devices, including:

- Firebase registration and email verification
- Login and password reset
- QR code generation
- Session management
- QR code scanning
- Expiry validation
- Duplicate scan detection
- Attendance recording
- Live attendance count
- Attendance reports
- Attendance percentage calculation

Physical-device testing also verified camera-based QR scanning using ZXing.

---

## 📸 Application Screens

The project report includes screenshots of:

- Login screen
- Registration screen
- Email verification
- Forgot password
- Teacher dashboard
- QR code generation
- Student QR scanning
- Attendance result
- Attendance report

You can add the corresponding screenshots to your GitHub repository under:

```text
screenshots/
├── login.png
├── register.png
├── teacher-dashboard.png
├── qr-code.png
├── student-scan.png
└── attendance-report.png
```

Then display them in this README using:

```markdown
![Login Screen](screenshots/login.png)
![Teacher Dashboard](screenshots/teacher-dashboard.png)
![Student Dashboard](screenshots/student-scan.png)
```

---

## 🔮 Future Enhancements

The project report identifies several possible improvements:

- Firebase Firestore or a backend server for cloud synchronization
- REST API backend for centralized attendance management
- Timetable integration
- Student attendance history
- Attendance filtering by date, subject, or faculty
- Per-subject attendance percentage
- PDF/Excel attendance export
- Push notifications before QR expiry
- Real-time teacher attendance analytics
- iOS application support
- Database indexing and query optimization
- Institution-wide attendance reporting

---

## 🎯 Project Highlights

- 📱 Android mobile application
- 🔐 Firebase-based authentication
- ⏱️ Five-minute QR validity
- 🚫 Duplicate attendance prevention
- 👨‍🏫 Teacher-specific workflow
- 👨‍🎓 Student-specific workflow
- 🗄️ Room local database
- 🏗️ MVVM architecture
- ⚡ LiveData and Kotlin Coroutines
- 📷 ZXing camera-based QR scanning
- 🧪 Unit, functionality, integration, verification and validation testing

---

## 📚 References

The project report references the following official/documentation resources:

- Android Developers Documentation
- Firebase Authentication Documentation
- Room Database Documentation
- ZXing Project
- ZXing Android Embedded
- Kotlin Documentation
- Android Guide to App Architecture
- LiveData Documentation
- Kotlin Coroutines Documentation
- Gradle Documentation

---

This README is based on the project report:

**QR-Based Attendance System — Department of MCA, KLS Gogte Institute of Technology, 2025–2026.**
