# 🏛️ Papal Conclave Simulation

A Java-based multi-threaded simulation of the papal conclave process where 135 cardinals gather to elect a new Pope through voting and discussion phases.

## 📋 Description

This project simulates the traditional papal conclave process where cardinals gather in the Sistine Chapel to elect a new Pope. The simulation continues until a cardinal obtains a two-thirds majority (90 out of 135 votes). Between voting rounds, cardinals engage in discussions where they may change their preferences based on the influence of leading candidates.

## 🎯 Key Features

- **Multi-threaded Architecture**: Each cardinal runs as an independent thread
- **Realistic Voting Process**: Two-thirds majority requirement (90 votes)
- **Dynamic Discussion Phase**: Cardinals can change preferences based on influence
- **Thread Synchronization**: Proper coordination using `synchronized`, `wait()`, and `notifyAll()`
- **Clean OOP Design**: Well-structured classes with clear separation of concerns

## 🏗️ Architecture

### Core Components

1. **`PapalConclave`** (Main Class)
   - Entry point and configuration manager
   - Creates and manages all cardinal threads
   - Handles clean shutdown

2. **`Cardinal`** (Inner Static Class)
   - Represents each cardinal as a `Runnable` thread
   - Manages individual voting preferences
   - Implements persuasion logic during discussions

3. **`Conclave`** (Inner Static Class)
   - Central coordinator for the election process
   - Manages voting and discussion phases
   - Handles vote counting and winner detection

### Execution Flow

```
Initialization → Voting Phase → Discussion Phase → Repeat until Winner
     ↓              ↓              ↓
Create Threads → Cast Votes → Change Preferences → Check Majority
```

## 🚀 How to Run

### Prerequisites
- Java 8 or higher
- Basic command line knowledge

### Compilation
```bash
cd papal-conclave-simulation
javac PapalConclave.java
```

### Execution
```bash
java PapalConclave
```

### Sample Output
```
Papal Conclave Simulation Starting...
135 cardinals are entering the Sistine Chapel
Two-thirds majority required: 90 votes

=== Round 1 Results ===
----------------------
Leader: Cardinal 0 with 1 votes

Discussion phase completed. Starting next voting round...

=== Round 2 Results ===
----------------------
Leader: Cardinal 124 with 4 votes

...

==================================================
ELECTION DECIDED IN ROUND 6
Cardinal 124 received 96 votes (>= 90 required)
==================================================

Habemus Papam! Cardinal 124 has been elected the new Pope.
Long live Pope Cardinal 124!
```

## 🔧 Technical Implementation

### Thread Synchronization

The simulation uses Java's built-in synchronization mechanisms:

- **`synchronized` methods**: Ensure thread-safe access to shared data
- **`wait()` and `notifyAll()`**: Coordinate phase transitions between voting and discussion
- **Interrupt handling**: Clean shutdown of all threads

### Key Algorithms

#### 1. Winner Detection
```java
if (lastVoteResults[i] >= TWO_THIRDS) {  // 90 votes
    winner = i;
    conclaveEnded = true;
    notifyAll();
    return true;
}
```

#### 2. Persuasion Logic
```java
int persuasionProbability = (leaderInfluence * this.persuasibility) / 100;
if (rnd.nextInt(100) < persuasionProbability) {
    preference = leader;
}
```

#### 3. Leading Candidate Detection
- Finds all candidates with maximum votes
- Handles ties by randomly selecting among leaders
- Updates cardinal preferences based on influence

### Configuration Constants

```java
static final int NUM_CARDINALS = 135;      // Total number of cardinals
static final int TWO_THIRDS = 90;          // Required majority
static final int MIN_SLEEP_TIME = 100;     // Minimum discussion time (ms)
static final int MAX_SLEEP_TIME = 300;     // Maximum discussion time (ms)
static final int PERSUASION_DIVISOR = 100; // Probability calculation divisor
```

## 📊 Cardinal Attributes

Each cardinal has two key characteristics:

1. **Influence** (0-100): How much this cardinal can influence others
2. **Persuasibility** (0-100): How easily this cardinal can be persuaded

These attributes are randomly assigned at creation and remain constant throughout the simulation.

## 🔄 Phase Management

### Voting Phase
- All cardinals cast their votes simultaneously
- Votes are counted atomically
- Results are processed when the last cardinal votes
- Winner is checked for two-thirds majority

### Discussion Phase
- Cardinals analyze the vote results
- They identify leading candidates
- May change preferences based on influence and persuasibility
- Phase ends when all cardinals complete their discussion

## 🎓 Learning Objectives

This project demonstrates:

- **Multi-threading concepts** in Java
- **Thread synchronization** and coordination
- **Object-oriented programming** principles
- **Concurrent programming** best practices
- **Real-world simulation** modeling

## 🛠️ Code Quality Features

- **Comprehensive Documentation**: JavaDoc comments for all methods
- **Constants Management**: No magic numbers in the code
- **Error Handling**: Proper interrupt status restoration
- **Clean Architecture**: Separation of concerns and modular design
- **Thread Safety**: Robust synchronization mechanisms

## 📝 Project Structure

```
papal-conclave-simulation/
├── README.md           # This file
├── PapalConclave.java  # Main implementation
└── .gitignore         # Git ignore file (if using version control)
```

## 🤝 Contributing

This is an educational project demonstrating Java multithreading concepts. Feel free to:

- Experiment with different parameters
- Add additional features
- Improve the simulation logic
- Enhance the output formatting

## 📄 License

This project is created for educational purposes. Feel free to use and modify as needed.

## 🎯 Future Enhancements

Potential improvements could include:

- GUI interface for visualization
- Configurable cardinal attributes
- Historical data analysis
- Network-based distributed simulation
- More sophisticated persuasion algorithms

---

**Note**: This simulation is for educational purposes and does not claim to accurately represent the actual papal conclave process, which involves many more complex factors and traditions. 