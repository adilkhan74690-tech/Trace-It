<img width="1535" height="807" alt="Screenshot 2026-11-02 152215" src="https://github.com/user-attachments/assets/55184780-6fa7-4f94-9671-03f32e3f8e0c" />

# TraceIt
### Smart Lost-Item Recovery & Matching Engine

TraceIt is a Java desktop application that helps students find their lost belongings on campus. It compares lost and found item reports and suggests possible matches based on details like item category, color, brand, location, time and description.

Instead of manually checking every report, users can use the matching feature to find items that are likely to belong to them.

## Features

- **Dashboard** – View lost items, found items, active cases and recovered items.
- **Report Lost Item** – Submit details about a lost belonging.
- **Report Found Item** – Add details of an item found on campus.
- **Smart Matching** – Compare lost and found reports and calculate a match score.
- **Match Analysis** – View which details match and where differences exist.
- **Search Items** – Find reports using item name, category, color, brand or location.
- **Recovery Management** – Verify matches and update the recovery status.
- **Reports** – View completed recoveries and generate reports.

## How Matching Works

TraceIt uses a rule-based scoring system to compare two item reports.

| Matching Criteria | Weight |
|---|---:|
| Category | 20 |
| Color | 15 |
| Brand | 15 |
| Location | 20 |
| Time | 15 |
| Description | 15 |
| **Total** | **100** |

Each criterion contributes to the final score. The system then sorts possible matches and displays the reasons behind each result.

## Technologies Used

- Java
- Java Swing and AWT
- Java Collections Framework
- Java File Handling
- Java Time API
- Object-Oriented Programming

No database or external API is required. Item records are stored locally in files.

## Project Structure

```text
TraceIt/
│
├── src/
│   └── traceit/
│       ├── model/
│       ├── matching/
│       ├── service/
│       ├── repository/
│       ├── ui/
│       ├── exception/
│       └── Main.java
│
├── data/
├── bin/
├── compile.bat
├── run.bat
└── README.md
```

## How to Run

**Requirements:**
- JDK 17 or later
- Windows

**Steps:**

1. Download or clone the project.
2. Open the project folder.
3. Run `compile.bat`.
4. Run `run.bat`.

Alternatively, use the terminal:

```bash
javac -encoding UTF-8 -d bin @sources.txt
java -cp bin traceit.Main
```

To run the command-line version:

```bash
run-cli.bat
```

Or:

```bash
java -cp bin traceit.Main --cli
```

## OOP Concepts Used

- Classes and Objects
- Encapsulation
- Inheritance
- Abstraction
- Polymorphism
- Interfaces
- Exception Handling
- Collections
- File Handling

## Project Purpose

The aim of TraceIt is to make it easier to reconnect students with their lost belongings by reducing the time spent manually searching through lost and found reports.

**Project Name:** TraceIt  
**Type:** Java Desktop Application  
**Domain:** Campus Utility
