# Invoice Management System

## 👤 Student Information
**Name:** Defi Maleji  

---

## 📌 Project Description
This is a **multi-phase, database-backed invoicing system** developed in **Java** for the *Very Good Building & Development Company (VGB)*.  
The system manages customer invoices, billing, item tracking, and report generation. It supports multiple item types:  

- Equipment  
- Lease  
- Rental  
- Material  
- Contract  

Key features:  
- ✅ Tax logic for accurate billing  
- ✅ UUID-based identifiers for customers & items  
- ✅ Object-oriented design for scalability  

---

## 📂 Project Structure
```
invoice-management-system/
│── src/                # Java source code
│   ├── models/         # Item, Customer, Invoice classes
│   ├── database/       # Database connection + queries
│   ├── services/       # Business logic (tax, billing, reports)
│   └── Main.java       # Entry point
│
├── resources/          # Config files, SQL scripts
├── tests/              # Unit tests
├── README.md           # Documentation
└── LICENSE             # Open-source license
```

---

## ⚙️ Tech Stack
- **Language:** Java  
- **Database:** MySQL (or SQLite for lightweight setups)  
- **Build Tool:** Maven / Gradle  
- **Testing:** JUnit  

---

## 🚀 How to Run
1. Clone the repository:  
   ```bash
   git clone https://github.com/defimaleji/invoice-management-system.git
   cd invoice-management-system
   ```
2. Set up the database (see `resources/schema.sql`).  
3. Build and run:  
   ```bash
   mvn clean install
   java -jar target/invoice-system.jar
   ```

---

## ✅ Current Phase
- [x] Database schema  
- [x] Customer & item models  
- [x] Invoice generation  
- [ ] Report generation (in progress)  
- [ ] UI layer (planned)  

---

## 📄 License
This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.
