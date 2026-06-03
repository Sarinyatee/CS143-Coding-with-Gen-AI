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
I learned that coding problems are not always caused by bad code. In this case, code was correct, but a file encoding issue caused some employees to show up as "Unknown." This taught me that small issues with input files can create unexpected bugs.

### Benefits of working with GenAI
Using AI was saving time. I showed the AI the wrong output, and it quickly helped me figure out what was causing the problem. This made debugging much faster than trying to find the issue by myself.

### Drawbacks
Code generated by AI is not always complete. Sometimes it gives incorrect results, uses unnecessary fields, or creates code that is not concise. AI is helpful, but I still need to check and improve the code to make sure it works properly.

### Key takeaway
Using AI, I still needed to think about what might be wrong with my prompt and check the text file for errors. AI is helpful, but but I still have to make sure the final answer works.

---

## 📜 License

jGRASP License — free to use, modify, and distribute.
