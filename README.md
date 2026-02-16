# PerFin — Personal Finance Manager (Android)

**PerFin** is a modern Android personal finance app that helps users track expenses, manage monthly budgets, set savings goals, and receive alerts when spending exceeds limits.

---

## ✨ Features

- 📌 Add, edit, and delete daily expenses  
- 📊 Visual expense analytics using charts  
- 💰 Set monthly budget and savings goals  
- 🚨 Smart notifications when spending exceeds limits  
- 🔐 Secure authentication (Email, Google, Guest login)  
- ☁️ Cloud data storage with Firebase Firestore  
- 🔄 Real-time UI updates using ViewModel & LiveData  

---

## 📱 Screens

- **Home** — Overview of spending, budget, savings, and remaining balance  
- **Expenses** — Full expense list with edit/delete support  
- **Analytics** — Pie chart visualization of spending categories  
- **Profile** — Set budget & savings, manage account, logout  

---

## 🛠 Tech Stack

| Layer | Technology |
|------|------------|
| Language | Java |
| Architecture | MVVM (ViewModel + LiveData) |
| UI | XML + Material Design |
| Database | Firebase Firestore |
| Authentication | Firebase Auth (Email, Google, Anonymous) |
| Charts | MPAndroidChart |
| Notifications | Android Notification Manager |

---

## 🔥 Core Logic

- **Effective Budget = Monthly Budget − Monthly Savings**
- Users are notified when:
  - Spending exceeds their effective budget
  - Spending starts affecting their savings goal
- All calculations update in real-time via LiveData observers

---

## ☁️ Firebase Structure

```
users
 └── userId
      ├── expenses (collection)
      │     └── expenseId
      └── settings
            └── finance
```

---

## 🚀 Getting Started

1. Clone the repository  
   ```bash
   git clone https://github.com/your-username/perfin.git
   ```

2. Open in **Android Studio**

3. Connect Firebase:
   - Add your `google-services.json`
   - Enable **Authentication**
   - Enable **Firestore Database**

4. Build & Run 🎉

---

## 🔐 Permissions Used

- Internet access (Firebase)
- Notification permission (Android 13+)

---

## 📌 Future Improvements

- Monthly reset automation  
- Export reports (PDF/CSV)  
- Dark mode  
- Recurring expenses  
- Multi-device sync

---

## 👩‍💻 Developer

Built with ❤️ as an Android + Firebase project focused on real-world financial tracking.
