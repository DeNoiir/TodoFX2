# TodoFX

TodoFX is a JavaFX-based todo list management application with a beautiful user interface and rich features. It not only helps users manage daily tasks but also integrates a Pomodoro timer to improve work efficiency.

## Key Features

1. **Todo Management**
   - Add, edit, and delete todo items
   - Set categories, due dates, and statuses for todo items
   - Visualize todo items using a kanban board view

2. **Task Categories**
   - Support multiple task categories: Learning, Growth, Life, Interpersonal, Health & Entertainment

3. **Pomodoro Timer**
   - Integrated Pomodoro Technique to help users focus on work and take timely breaks

4. **User System**
   - Support user registration and login
   - Encrypted password storage to ensure user information security

5. **Data Persistence**
   - Use MySQL database to store user data and todo items

6. **Beautiful User Interface**
   - Custom CSS styles for a clean and modern interface design

## Tech Stack

- Java
- JavaFX
- MySQL
- CSS

## How to Run

1. Ensure that your system has Java Development Kit (JDK) and MySQL database installed.

2. Clone this repository to your local machine.
   
4. Create a database named `todo_fx` in MySQL.

5. Modify the database connection information in the `DatabaseConnection.java` file, including username and password.

6. Compile and run the `MainApp.java` file.

## How to Use

1. For first-time use, please register a new account.

2. After logging in, you can see the kanban view of todo items on the main interface.

3. Use the "+" button in the bottom right corner to add new todo items.

4. Click on existing todo items to edit or delete them.

5. Use the sidebar to filter todo items by different categories or time ranges.

6. Click the Pomodoro button to start the timer and help you focus on work.
