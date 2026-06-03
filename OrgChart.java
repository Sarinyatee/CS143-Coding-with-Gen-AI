import java.io.*;
import java.util.*;

// ─── Employee (data object) ───────────────────────────────────────────────────
class Employee {
    String id, name, position;

    Employee(String id, String name, String position) {
        this.id       = id.trim();
        this.name     = name.trim();
        this.position = position.trim();
    }

    @Override
    public String toString() {
        return String.format("%-12s %s", "[" + position + "]", name);
    }
}

// ─── OrgNode (N-ary Tree node) ────────────────────────────────────────────────
class OrgNode {
    Employee employee;
    List<OrgNode> children = new ArrayList<>();

    OrgNode(Employee employee) {
        this.employee = employee;
    }

    void addChild(OrgNode child) {
        children.add(child);
    }
}

// ─── EmployeeRepository (reads employee.txt) ──────────────────────────────────
class EmployeeRepository {
    private Map<String, Employee> byPosition = new LinkedHashMap<>();

    void load(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                Employee emp = new Employee(parts[0], parts[1], parts[2]);
                byPosition.put(emp.position, emp);
            }
        }
    }

    Employee findByPosition(String position) {
        return byPosition.getOrDefault(position.trim(),
               new Employee("???", "Unknown", position.trim()));
    }

    Collection<Employee> all() { return byPosition.values(); }
}

// ─── OrgChartBuilder (reads org.txt, builds N-ary tree) ──────────────────────
class OrgChartBuilder {
    private EmployeeRepository repo;
    private Map<String, OrgNode> nodeMap = new LinkedHashMap<>();
    private OrgNode root;

    OrgChartBuilder(EmployeeRepository repo) {
        this.repo = repo;
    }

    // Strip everything that is not a printable ASCII character
    private String sanitize(String s) {
        return s.replaceAll("[^\\x20-\\x7E]", "").trim();
    }

    void build(String filePath) throws IOException {
        List<String[]> relations = new ArrayList<>();

        // Read entire file as raw bytes
        byte[] raw = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath));
        String content = new String(raw);

        // KEY FIX: split on ANY non-printable character sequence
        // This handles \n, \r, §, MM and any other garbage separators
        for (String token : content.split("[^\\x20-\\x7E]+")) {
            String cleaned = sanitize(token);
            if (!cleaned.contains("->")) continue;

            String[] rel = cleaned.split("->");
            if (rel.length == 2) {
                String parent = sanitize(rel[0]);
                String child  = sanitize(rel[1]);
                if (!parent.isEmpty() && !child.isEmpty()) {
                    relations.add(new String[]{parent, child});
                    ensureNode(parent);
                    ensureNode(child);
                }
            }
        }

        // Link parent -> child
        Set<String> hasParent = new HashSet<>();
        for (String[] rel : relations) {
            OrgNode parent = nodeMap.get(rel[0]);
            OrgNode child  = nodeMap.get(rel[1]);
            if (parent != null && child != null) {
                parent.addChild(child);
                hasParent.add(rel[1]);
            }
        }

        // Root = node with no parent
        for (Map.Entry<String, OrgNode> entry : nodeMap.entrySet()) {
            if (!hasParent.contains(entry.getKey())) {
                root = entry.getValue();
                break;
            }
        }
    }

    private void ensureNode(String position) {
        nodeMap.putIfAbsent(position, new OrgNode(repo.findByPosition(position)));
    }

    OrgNode getRoot() { return root; }
}

// ─── OrgChartPrinter (renders tree + department summary) ─────────────────────
class OrgChartPrinter {

    void printTree(OrgNode node, String prefix, boolean isLast) {
        if (node == null) return;
        String connector = isLast ? "└── " : "├── ";
        System.out.println(prefix + connector + node.employee);
        String childPrefix = prefix + (isLast ? "    " : "│   ");
        for (int i = 0; i < node.children.size(); i++)
            printTree(node.children.get(i), childPrefix, i == node.children.size() - 1);
    }

    void printDepartmentSummary(OrgNode root) {
        if (root == null) return;
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         DEPARTMENT SUMMARY               ║");
        System.out.println("╚══════════════════════════════════════════╝");

        System.out.printf("%n● %-14s → %s%n", root.employee.position, root.employee.name);
        System.out.println("  Oversees " + root.children.size() + " department(s)");

        for (OrgNode dept : root.children) {
            System.out.println();
            System.out.printf("  ┌─ Department: %-10s (%s)%n",
                dept.employee.position, dept.employee.name);

            List<OrgNode> members = new ArrayList<>();
            collectAll(dept.children, members);

            if (members.isEmpty()) {
                System.out.println("  │  (no direct reports)");
            } else {
                for (OrgNode m : members)
                    System.out.printf("  │  • %-16s %s%n",
                        m.employee.position + ":", m.employee.name);
            }
            System.out.printf("  └─ Total members: %d%n", members.size());
        }
    }

    private void collectAll(List<OrgNode> nodes, List<OrgNode> result) {
        for (OrgNode n : nodes) {
            result.add(n);
            collectAll(n.children, result);
        }
    }
}

// ─── Main ─────────────────────────────────────────────────────────────────────
public class OrgChart {
    public static void main(String[] args) {
        String empFile = args.length > 0 ? args[0] : "employee.txt";
        String orgFile = args.length > 1 ? args[1] : "org.txt";

        try {
            EmployeeRepository repo = new EmployeeRepository();
            repo.load(empFile);

            OrgChartBuilder builder = new OrgChartBuilder(repo);
            builder.build(orgFile);
            OrgNode root = builder.getRoot();

            if (root == null) {
                System.out.println("Error: could not build tree. Check org.txt format.");
                return;
            }

            OrgChartPrinter printer = new OrgChartPrinter();
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║       COMPANY ORGANIZATION CHART         ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println();
            System.out.println("  " + root.employee);
            printer.printTree(root, "  ", true);
            printer.printDepartmentSummary(root);

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}
/*
# PROGRAM OUTPUT
 ╔══════════════════════════════════════════╗
 ║       COMPANY ORGANIZATION CHART         ║
 ╚══════════════════════════════════════════╝
 
   [CEO]        Diana Chen
   └── [CEO]        Diana Chen
       ├── [CTO]        Alan Park
       │   ├── [Senior Dev] John Smith
       │   └── [Junior Dev] Kate Brown
       └── [CFO]        Sarah Mills
           └── [Analyst]    Micheale Olive
 
 ╔══════════════════════════════════════════╗
 ║         DEPARTMENT SUMMARY               ║
 ╚══════════════════════════════════════════╝
 
 ● CEO            → Diana Chen
   Oversees 2 department(s)
 
   ┌─ Department: CTO        (Alan Park)
   │  • Senior Dev:      John Smith
   │  • Junior Dev:      Kate Brown
   └─ Total members: 2
 
   ┌─ Department: CFO        (Sarah Mills)
   │  • Analyst:         Micheale Olive
   └─ Total members: 1
 */
 