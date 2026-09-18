# Whack-A-Mole

A desktop **Whack-A-Mole game built in Java Swing**, designed to demonstrate practical application of **Object-Oriented Programming, multithreading, exception handling, Java serialization, collections, and event-driven GUI development**.

The game features multiple interactive entities, a timed game engine, dynamic scoring, bonus mechanics, persistent high scores, and a responsive Swing-based interface.

---

## Features

* **Interactive 4×3 game board** with 12 playable holes
* **Multiple game entities**

  * Mole — awards points when whacked
  * Bomb — deducts points
  * Bonus Mole — awards additional points and extra game time
* **30-second timed gameplay**
* **Dynamic score tracking**
* **High-score system** with persistent storage
* **Top-10 high-score management**
* **Randomized entity spawning**
* **Threaded game engine** for independent game execution
* **Thread-safe Swing UI updates** using the Event Dispatch Thread
* **Custom exception handling** for invalid game states and high-score operations
* **Java object serialization** for persistent high-score storage
* **Comparator-based score sorting**
* **Stream-based high-score calculation**

---

## Technologies Used

* **Java**
* **Java Swing**
* **Object-Oriented Programming**
* **Multithreading**
* **Java Collections Framework**
* **Java Serialization**
* **Exception Handling**
* **Event-Driven Programming**

---

## Object-Oriented Design

The project uses an inheritance-based model for game entities.

```text
                 HoleOccupant
                 (abstract class)
                       |
          +------------+------------+
          |            |            |
        Mole         Bomb       BonusMole
```

`HoleOccupant` defines the common behavior and state for objects appearing in game holes.

Each specialized entity overrides its behavior, including:

* `whack()`
* `getImage()`
* `getTypeName()`

This allows the game engine to interact with different entity types through a common abstraction while preserving their individual scoring behavior.

---

## Multithreaded Game Engine

The game logic is handled by a dedicated `GameEngine` implementing `Runnable`.

The engine manages:

* Entity spawning
* Game timing
* Entity expiration
* Score updates
* Bonus-time mechanics
* Game-state transitions

The game engine runs independently from the Swing interface, while UI changes are dispatched through Swing's **Event Dispatch Thread (EDT)**.

Synchronized access is used for critical game-state operations such as processing a player action on a hole.

---

## High-Score Persistence

High scores are persisted locally using Java's object serialization mechanism.

The application uses:

* `ObjectOutputStream` to save scores
* `ObjectInputStream` to load scores
* `ArrayList` for score management
* `Comparator` for descending score ordering

Only the top 10 scores are retained.

The generated `scores.dat` file is intentionally excluded from version control through `.gitignore`.

---

## Exception Handling

The application defines custom exceptions for different error scenarios:

* `HighScoreException` — handles high-score persistence and loading failures
* `InvalidGameStateException` — handles invalid game operations such as an invalid hole index

This separates application-specific error handling from the core game logic.

---

## Project Structure

```text
Whack-A-Mole/
│
├── Main.java
│
├── gui/
│   └── WhackAMole.java
│
├── engine/
│   └── GameEngine.java
│
├── model/
│   ├── HoleOccupant.java
│   ├── Mole.java
│   ├── Bomb.java
│   └── BonusMole.java
│
├── data/
│   ├── PlayerScore.java
│   └── HighScoreManager.java
│
└── exception/
    ├── HighScoreException.java
    └── InvalidGameStateException.java
```

---

## How to Run

### Prerequisites

* **Java Development Kit (JDK) 8 or later**
* A terminal or Java-compatible IDE

Verify your Java installation:

```bash
java -version
javac -version
```

### Compile

From the project root directory:

#### Linux / macOS / Git Bash

```bash
javac -d out $(find . -name "*.java")
```

#### Windows PowerShell

```powershell
Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName } | javac -d out
```

### Run

```bash
java -cp out Main
```

The game's graphical interface will launch.

---

## Gameplay

1. Launch the application using `Main.java`.
2. Start a new game from the graphical interface.
3. Click visible objects appearing in the holes.
4. **Moles** increase your score.
5. **Bombs** reduce your score.
6. **Bonus Moles** provide additional points and extra time.
7. At the end of the game, your score is evaluated against the stored high scores.
8. High scores are saved locally for future sessions.

---

## Key Java Concepts Demonstrated

This project provides practical implementation of several Java concepts:

* Abstraction
* Inheritance
* Polymorphism
* Interfaces
* Encapsulation
* Method overriding
* Exception handling
* Custom exceptions
* Multithreading
* Synchronization
* Java Swing
* Event Dispatch Thread
* Collections
* `Comparator`
* Java Streams
* Object serialization
* File I/O
* Lambda expressions