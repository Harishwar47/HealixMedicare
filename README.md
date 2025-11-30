# Healix Medicare - Clinic Management System

A comprehensive medical clinic management system built with Spring Boot, featuring patient registration, doctor appointment scheduling, and administrative controls.

## 🏥 Features

### Patient Portal
- **Patient Registration & Login**: Secure authentication with role-based access
- **Profile Management**: Comprehensive patient profiles with personal and medical details
  - Date of Birth
  - Gender
  - Blood Group
  - Mobile Number
  - Email & Contact Information
- **Appointment Booking**: Interactive calendar-based appointment scheduling
- **Appointment History**: View past and upcoming appointments

### Doctor Portal
- **Doctor Dashboard**: Manage patient appointments
- **Appointment Management**: Confirm, reject, or reschedule appointments
- **Patient Records**: Access patient information and history
- **Status Tracking**: Real-time appointment status updates (Pending/Confirmed/Rejected)

### Admin Portal
- **User Management**: Create, update, and manage users (patients, doctors, staff)
- **Doctor Management**: Add and manage doctor profiles
- **Appointment Oversight**: Monitor all appointments across the system
- **System Analytics**: Dashboard with key metrics and statistics

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.1.4
- **Java Version**: 17
- **Database**: H2 (file-based)
- **ORM**: Hibernate/JPA
- **Security**: Spring Security with role-based authentication
- **Frontend**: Thymeleaf, Bootstrap 5.1.3, JavaScript
- **Build Tool**: Maven
- **Real-time Communication**: WebSocket with STOMP
- **Development Tools**: Spring DevTools with LiveReload

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Web Browser (Chrome, Firefox, Edge, Safari)

## 🚀 Getting Started

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/healix-medicare-clinic.git
   cd healix-medicare-clinic
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or use the provided batch file:
   ```bash
   start-healix.bat
   ```

4. **Access the application**
   - Open your browser and navigate to: `http://localhost:8082`

### Default Admin Credentials

- **Username**: `admin407`
- **Password**: `admin407`

Access admin panel by clicking the ⚙️ icon in the bottom-right corner of the login page.

## 📁 Project Structure

```
healix-medicare/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/clinic/
│   │   │       ├── ClinicApplication.java
│   │   │       ├── DataLoader.java
│   │   │       ├── config/           # Security, WebSocket, Web configs
│   │   │       ├── controller/       # REST & View controllers
│   │   │       ├── model/            # JPA entities
│   │   │       ├── repository/       # Data access layer
│   │   │       └── service/          # Business logic
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/               # CSS, JS, images
│   │       └── templates/            # Thymeleaf HTML templates
│   └── test/
├── pom.xml
└── README.md
```

## ⚙️ Configuration

### Database Configuration

The application uses H2 file-based database. Configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./data/healix_database
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

Access H2 Console: `http://localhost:8082/h2-console`

### Port Configuration

Default port: `8082`

To change the port, update `application.properties`:
```properties
server.port=YOUR_PORT
```

## 🔐 Security Features

- **Password Encryption**: BCrypt password hashing
- **Role-Based Access Control**: Three user roles (ADMIN, DOCTOR, PATIENT)
- **Session Management**: Secure session handling
- **CSRF Protection**: Configurable CSRF settings
- **Form-based Authentication**: Custom login with role validation

## 🎨 UI Features

- **Responsive Design**: Bootstrap 5 responsive layouts
- **Modern Interface**: Gradient themes and clean design
- **Interactive Calendar**: Date picker for appointment scheduling
- **Real-time Updates**: WebSocket support for live notifications
- **Modal Dialogs**: User-friendly popup interfaces

## 📊 Database Schema

### Key Entities

- **User**: Patient and staff user accounts
- **Doctor**: Doctor profiles and specializations
- **Appointment**: Appointment records with status tracking
- **Role**: User role management (Enum: ADMIN, DOCTOR, PATIENT)

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 🛑 Stopping the Application

Use the provided batch file:
```bash
stop-healix.bat
```

Or press `Ctrl+C` in the terminal running the application.

## 📝 API Endpoints

### Public Endpoints
- `GET /` - Login page
- `POST /login` - User authentication
- `POST /register` - Patient registration

### Patient Endpoints
- `GET /patient/dashboard` - Patient dashboard
- `POST /patient/book-appointment` - Book new appointment
- `GET /patient/appointments` - View appointments

### Doctor Endpoints
- `GET /doctor/dashboard` - Doctor dashboard
- `POST /doctor/confirm-appointment` - Confirm appointment
- `POST /doctor/reject-appointment` - Reject appointment

### Admin Endpoints
- `GET /admin/dashboard` - Admin control panel
- `POST /admin/users` - Manage users
- `POST /admin/doctors` - Manage doctors

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@YOUR_USERNAME](https://github.com/YOUR_USERNAME)

## 🙏 Acknowledgments

- Spring Boot Documentation
- Bootstrap Team
- H2 Database
- Thymeleaf

## 📧 Support

For support, email your-email@example.com or create an issue in the GitHub repository.

---

**Note**: This is a demonstration project for educational purposes. For production use, consider:
- Using a production-grade database (PostgreSQL, MySQL)
- Implementing comprehensive error handling
- Adding extensive unit and integration tests
- Setting up CI/CD pipelines
- Implementing proper logging and monitoring
