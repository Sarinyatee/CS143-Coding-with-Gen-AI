# CS143-Coding-with-Gen-AI
The problem is that I want AI to write a solution is to create organization chart.
This is my prompt. 
Can you code OOP in Java to create organization chart?
- Reading organization chart from org.txt
- Reading data from employee.txt
- Output is summarize the position and name by department
  
# 🏢 Company Organization Chart

A Java console application that reads employee data and reporting relationships from text files, then builds and displays a visual **N-ary Tree** organization chart with a department summary.

Built as a hands-on exercise in **Java OOP** and **Tree data structures**.

---

## ✨ Features

- Parses two plain-text input files (`org.txt` and `employee.txt`)
- Builds an **N-ary Tree** representing the company hierarchy
- Prints a **visual tree** with branch connectors to the console
- Outputs a **department summary** showing each team and headcount
- Robust file parsing that handles non-standard line endings and special characters

---

## 📁 Project Structure

```
OrgChart/
├── OrgChart.java       # All source code (5 OOP classes)
├── org.txt             # Reporting relationships
├── employee.txt        # Employee names and positions
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher

### Compile & Run
```bash
javac OrgChart.java
java -Dfile.encoding=UTF-8 OrgChart employee.txt org.txt
```

---

## 📄 Input File Format

**`employee.txt`** — one employee per line: `ID, Name, Position`
```
E001, Diana Chen, CEO
E002, Alan Park, CTO
E003, Sarah Mills, CFO
E004, John Smith, Senior Dev
E005, Kate Brown, Junior Dev
E006, Mike Davis, Analyst
```

**`org.txt`** — reporting relationships: `Parent -> Child`
```
CEO -> CTO
CEO -> CFO
CTO -> Senior Dev
CTO -> Junior Dev
CFO -> Analyst
```

---

## 🖥️ Sample Output

```
╔══════════════════════════════════════════╗
║       COMPANY ORGANIZATION CHART         ║
╚══════════════════════════════════════════╝

  [CEO]        Diana Chen
  └── [CTO]        Alan Park
  │   ├── [Senior Dev] John Smith
  │   └── [Junior Dev] Kate Brown
  └── [CFO]        Sarah Mills
      └── [Analyst]    Mike Davis

╔══════════════════════════════════════════╗
║         DEPARTMENT SUMMARY               ║
╚══════════════════════════════════════════╝

● CEO            → Diana Chen
  Oversees 2 department(s)

  ┌─ Department: CTO        (Alan Park)
  │  • Senior Dev:    John Smith
  │  • Junior Dev:    Kate Brown
  └─ Total members: 2

  ┌─ Department: CFO        (Sarah Mills)
  │  • Analyst:       Mike Davis
  └─ Total members: 1
```

---

## 🏗️ OOP Class Design

| Class | Responsibility |
|---|---|
| `Employee` | Data object — stores `id`, `name`, `position` |
| `OrgNode` | N-ary tree node — wraps an `Employee` and holds a list of child nodes |
| `EmployeeRepository` | Reads `employee.txt` and provides lookup by position name |
| `OrgChartBuilder` | Reads `org.txt`, parses relationships, and builds the N-ary tree |
| `OrgChartPrinter` | Traverses the tree and renders the visual chart and department summary |

### Class Diagram
```
EmployeeRepository          OrgChartBuilder          OrgChartPrinter
      │                           │                        │
      │  load(file)               │  build(file)           │  printTree()
      │  findByPosition()         │  getRoot()             │  printDepartmentSummary()
      │                           │                        │
      └──────── Employee ◄────────┘                        │
                    ▲                                       │
                    │                                  OrgNode ◄──── (N-ary Tree)
                OrgNode                                    │
             ┌────────────┐                         List<OrgNode>
             │  employee  │                          children
             │  children  │
             └────────────┘
```

---

## 🔍 Key Technical Detail — Robust File Parsing

`org.txt` files saved on different editors or operating systems may contain **non-standard characters** as line separators (e.g. `§`, control bytes, `\r\n` variants). A naive `split("\n")` will produce corrupted tokens like `CTO§` that fail to match employee lookups.

The fix is to split on **any non-printable character sequence**:

```java
// ❌ Fragile — breaks on non-standard line endings
content.split("[\\r\\n]+")

// ✅ Robust — handles any garbage separator character
content.split("[^\\x20-\\x7E]+")
```

Combined with a `sanitize()` method that strips non-ASCII characters from every token before lookup, this ensures reliable parsing regardless of how the file was created.

---

## 💡 Concepts Demonstrated

- **N-ary Tree** — each node holds a list of children, naturally modelling a reporting hierarchy
- **Encapsulation** — each class owns its data and exposes only what other classes need
- **Single Responsibility** — reading, building, and printing are separated into distinct classes
- **Dependency Injection** — `OrgChartBuilder` receives `EmployeeRepository` rather than creating it
- **Recursive tree traversal** — both `printTree()` and `collectAll()` use depth-first recursion

---

## 🔄 Reflection — Working with GenAI

### What did I learn?
The N-ary Tree maps intuitively onto an org chart problem. The more practical lesson was about **defensive file parsing** — encoding bugs only appear at runtime, so robust input handling should be built in from the start, not added after a bug report.

### Benefits of working with GenAI
- **Speed** — full class structure, tree traversal, and formatted output scaffolded in one pass
- **Fast debugging** — describing the symptom (`CTO → Unknown, 0 members`) was enough to diagnose the root cause immediately
- **Exploration** — the AI surfaced related data structures (Trie, AVL Tree, Segment Tree) that expanded the learning beyond the original task

### Drawbacks
- **Silent assumptions** — the AI assumed clean newlines in `org.txt` and did not flag the encoding risk upfront
- **Iterative fixes needed** — the first fix was not robust enough; it took a second iteration to get it right
- **No live execution** — the AI cannot run code during generation, so runtime bugs require a human-in-the-loop feedback cycle

### Key takeaway
GenAI works best as a **thinking partner and first-draft generator**. The bug was caught because the developer read the output critically and described it precisely. Without that human judgment, the error would have gone unnoticed. **Prompting well is as important as coding well.**

---

## 📜 License

MIT License — free to use, modify, and distribute.
