# Vendor Portal (Spring Boot)

A clean separation of your original single-file HTML mock into a real
Java backend + HTML/CSS/JS frontend, with an actual database and file storage.

## Project structure

```
vendor-portal/
├── pom.xml                                  Maven build file (dependencies live here)
├── src/main/java/com/vendorportal/
│   ├── VendorPortalApplication.java         App entry point
│   ├── config/
│   │   ├── SecurityConfig.java              Login/logout rules, password hashing
│   │   └── DataInitializer.java             Seeds demo vendor + POs on first run
│   ├── model/                               JPA entities (= your database tables)
│   │   ├── Vendor.java
│   │   ├── PurchaseOrder.java
│   │   └── UploadedDocument.java
│   ├── repository/                          Data access (Spring Data JPA)
│   │   ├── VendorRepository.java
│   │   ├── PurchaseOrderRepository.java
│   │   └── UploadedDocumentRepository.java
│   ├── service/
│   │   ├── VendorUserDetailsService.java    Wires Vendor table into Spring Security
│   │   └── FileStorageService.java          Saves uploaded files to disk safely
│   └── controller/                          HTTP endpoints (this is "the backend")
│       ├── PageController.java              /login, /summary
│       ├── PurchaseOrderController.java     /po/{ref}/upload (GET form, POST submit)
│       └── DocumentController.java          JSON API + file download
└── src/main/resources/
    ├── application.properties               DB/file-storage/server config
    ├── templates/                           HTML views (Thymeleaf)
    │   ├── login.html
    │   ├── summary.html
    │   ├── upload.html
    │   └── fragments/header.html
    └── static/                              Pure frontend assets
        ├── css/style.css
        └── js/upload.js, modal.js
```

**How the separation works:** Java code (`src/main/java`) never contains HTML.
HTML templates (`src/main/resources/templates`) never contain business logic or
database code — they only display data the Java controllers hand them, using
Thymeleaf's `th:*` attributes. Plain JS (`static/js`) only handles browser-side
UI behavior (drag-and-drop, opening the modal) and talks to the Java backend
over normal HTTP/JSON — it never touches the database directly.

## Running it in Visual Studio / VS Code

1. Install the **Extension Pack for Java** and **Spring Boot Extension Pack**
   from the VS Code marketplace (Java isn't built into VS Code by default).
2. Open the `vendor-portal` folder in VS Code (`File > Open Folder`).
3. Wait for Maven to download dependencies (bottom-right progress notification).
4. Open `VendorPortalApplication.java` and click **Run** above the `main`
   method — or from a terminal in the project root:
   ```
   mvn spring-boot:run
   ```
5. Visit **http://localhost:8080/login** in your browser.
6. Demo login: `VEND-8891` / `password123`

(If you're using classic **Visual Studio** rather than VS Code: Visual Studio
doesn't support Java projects. You'll want VS Code, IntelliJ IDEA, or Eclipse
instead — any of them will open this Maven project as-is.)

## What's real vs. what's still a stub

- **Real**: login (BCrypt-hashed passwords checked against the database),
  file uploads (saved to `./uploads/<PO reference>/`), PO status auto-updates
  to "Completed" once all required documents are attached, download links,
  ownership checks (a vendor can't see another vendor's PO or files).
- **Data**: stored in a local H2 file database (`./data/vendorportal.mv.db`).
  Browse it live at `http://localhost:8080/h2-console` (JDBC URL:
  `jdbc:h2:file:./data/vendorportal`, user `sa`, blank password).
- **To swap in MySQL/Postgres later**: change the `spring.datasource.*` lines
  in `application.properties` and add the matching JDBC driver dependency to
  `pom.xml` — no controller or template code needs to change.
- **Registering new vendors**: there's currently no signup form — vendors are
  seeded by `DataInitializer`. Let me know if you want a registration flow added.
