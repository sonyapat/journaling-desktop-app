# journaling-desktop-app
A secure personal-reflection journal built in Java that helps the user capture private thoughts and track personal growth. Sign in with a protected account and rest easy, as entries are stored persistently to keep entries safe.
---

## ✨ Features
- User authentication (sign up / log in)  
- Secure password storage (hashed with [bcrypt/other])  
- Persistent storage of reflections using [SQLite/H2/etc.]  
- Encrypted entries to ensure privacy  
- Simple command-line interface (CLI) [or GUI if using JavaFX]  

## 🎥 Demo
![App Demo](assets/demo.gif)

---
🛠️ Tech Stack

Language: Java

Persistence: SQLite (via JDBC)

Security: bcrypt for password hashing, AES for entry encryption

Build Tool: Maven / Gradle

🔮 Future Improvements

Cloud synchronization

Tagging and searching reflections

UI with JavaFX

Export/import reflections

## 🚀 Getting Started

### Prerequisites
- Java 17+  
- Maven or Gradle (depending on your setup)  
- [SQLite/H2 driver if needed]  

### Installation
```bash
# Clone the repository
git clone https://github.com/your-username/personal-reflection-app.git
cd personal-reflection-app

# Compile and run (Maven example)
mvn compile
mvn exec:java
📝 Usage
Launch the app.

Create a new account or log in.

Add your reflection entries.

Entries are securely stored and accessible only after login.

Example CLI session:

mathematica
Copy code
Welcome to Personal Reflection App
1. Log In
2. Sign Up
Choose an option: 2
Enter username: alice
Enter password: ****
Account created successfully!
