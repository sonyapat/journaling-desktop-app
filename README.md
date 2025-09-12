# journaling-desktop-app
This desktop journal is a personal project built in Java to track private thoughts and reflect on growth. 
It features secure user authentication and persistent storage, and is designed to be accessible only locally on the developer’s machine, ensuring entries remain private and fully under the developer's control.
---

## ✨ Features
- User authentication (sign up / log in)  
- Secure password storage (hashed with [bcrypt/other])  
- Persistent storage of reflections using [SQLite/H2/etc.]  
- Encrypted entries to ensure privacy  
- Simple command-line interface (CLI) [or GUI if using JavaFX]  

## 🎥 Demo
![App Demo](assets/demo.gif)

[Watch the full demo on YouTube](https://youtu.be/your-demo-link)

## 🛠️ Tech Stack

Language: Java

Persistence: SQLite (via JDBC)

Security: bcrypt for password hashing, AES for entry encryption

Build Tool: Maven / Gradle

---

## 🔮 Future Improvements

Cloud synchronization

Tagging and searching reflections

UI with JavaFX

Export/import reflections
