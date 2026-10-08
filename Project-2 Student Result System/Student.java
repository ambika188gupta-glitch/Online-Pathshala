import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ---------- Student model ----------
class Student {
    private final int rollNo;
    private final String name;
    private final double[] marks;

    Student(int rollNo, String name, double[] marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
    }

    int getRollNo()      { return rollNo; }
    String getName()     { return name; }
    double[] getMarks()  { return marks; }

    double getTotal() {
        double total = 0;
        for (double m : marks) total += m;
        return total;
    }

    double getPercentage() {
        return getTotal() / marks.length;   // har subject 100 me se
    }

    // Pass tabhi jab har subject me kam se kam 33 marks hon
    boolean isPass() {
        for (double m : marks) {
            if (m < StudentResultSystem.PASS_MARKS) return false;
        }
        return true;
    }

    String getGrade() {
        if (!isPass()) return "F";
        double p = getPercentage();
        if (p >= 90) return "A+";
        if (p >= 80) return "A";
        if (p >= 70) return "B";
        if (p >= 60) return "C";
        if (p >= 50) return "D";
        return "E";
    }
}

// ---------- Main program ----------
class StudentResultSystem {

    static final String[] SUBJECTS = {"Maths", "Science", "English", "Hindi", "Computer"};
    static final double MAX_MARKS = 100;
    static final double PASS_MARKS = 33;

    static final List<Student> students = new ArrayList<>();
    static final Scanner sc = new Scanner(System.in);

    // ---------- Input helpers ----------
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Galat input! Valid number daalein.");
            }
        }
    }

    static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) return name;
            System.out.println("Naam khaali nahi ho sakta.");
        }
    }

    static double readMarks(String subject) {
        while (true) {
            System.out.print(subject + " ke marks (0-" + (int) MAX_MARKS + "): ");
            try {
                double m = Double.parseDouble(sc.nextLine().trim());
                if (m >= 0 && m <= MAX_MARKS) return m;
                System.out.println("Marks 0 se " + (int) MAX_MARKS + " ke beech hone chahiye.");
            } catch (NumberFormatException e) {
                System.out.println("Galat input! Valid number daalein.");
            }
        }
    }

    // ---------- Features ----------
    static Student findByRoll(int roll) {
        for (Student s : students) {
            if (s.getRollNo() == roll) return s;
        }
        return null;
    }

    static void addStudent() {
        int roll = readInt("Roll number: ");
        if (roll <= 0) {
            System.out.println("Roll number positive hona chahiye.");
            return;
        }
        if (findByRoll(roll) != null) {
            System.out.println("Is roll number ka student pehle se maujood hai.");
            return;
        }
        String name = readName("Student ka naam: ");

        double[] marks = new double[SUBJECTS.length];
        for (int i = 0; i < SUBJECTS.length; i++) {
            marks[i] = readMarks(SUBJECTS[i]);
        }

        students.add(new Student(roll, name, marks));
        System.out.println("Student successfully add ho gaya!");
    }

    static void printResultCard(Student s) {
        System.out.println("\n---------------------------------");
        System.out.println("Roll No : " + s.getRollNo());
        System.out.println("Naam    : " + s.getName());
        System.out.println("---------------------------------");
        for (int i = 0; i < SUBJECTS.length; i++) {
            System.out.printf("%-10s : %.1f%n", SUBJECTS[i], s.getMarks()[i]);
        }
        System.out.println("---------------------------------");
        System.out.printf("Total      : %.1f / %.0f%n", s.getTotal(), MAX_MARKS * SUBJECTS.length);
        System.out.printf("Percentage : %.2f%%%n", s.getPercentage());
        System.out.println("Grade      : " + s.getGrade());
        System.out.println("Result     : " + (s.isPass() ? "PASS" : "FAIL"));
        System.out.println("---------------------------------");
    }

    static void viewAll() {
        if (students.isEmpty()) {
            System.out.println("Abhi tak koi student add nahi hua.");
            return;
        }
        System.out.println("\n%-6s %-20s %8s %8s %6s %s".formatted("Roll", "Naam", "Total", "%", "Grade", "Result"));
        System.out.println("-----------------------------------------------------------------");
        for (Student s : students) {
            System.out.println("%-6d %-20s %8.1f %8.2f %6s %s".formatted(
                    s.getRollNo(), s.getName(), s.getTotal(),
                    s.getPercentage(), s.getGrade(), s.isPass() ? "PASS" : "FAIL"));
        }
    }

    static void searchStudent() {
        int roll = readInt("Search karne ke liye roll number: ");
        Student s = findByRoll(roll);
        if (s == null) {
            System.out.println("Is roll number ka koi student nahi mila.");
        } else {
            printResultCard(s);
        }
    }

    static void showTopper() {
        if (students.isEmpty()) {
            System.out.println("Abhi tak koi student add nahi hua.");
            return;
        }
        Student topper = students.get(0);
        for (Student s : students) {
            if (s.getPercentage() > topper.getPercentage()) topper = s;
        }
        System.out.println("\n*** TOPPER ***");
        printResultCard(topper);
    }

    static void showSummary() {
        if (students.isEmpty()) {
            System.out.println("Abhi tak koi student add nahi hua.");
            return;
        }
        int pass = 0;
        double sumPercent = 0;
        for (Student s : students) {
            if (s.isPass()) pass++;
            sumPercent += s.getPercentage();
        }
        int total = students.size();
        System.out.println("\n===== CLASS SUMMARY =====");
        System.out.println("Total students  : " + total);
        System.out.println("Pass            : " + pass);
        System.out.println("Fail            : " + (total - pass));
        System.out.printf("Pass percentage : %.2f%%%n", pass * 100.0 / total);
        System.out.printf("Class average   : %.2f%%%n", sumPercent / total);
    }

    static void printMenu() {
        System.out.println("\n===== STUDENT RESULT SYSTEM =====");
        System.out.println("1. Student add karein");
        System.out.println("2. Sabhi results dekhein");
        System.out.println("3. Roll number se result dekhein");
        System.out.println("4. Topper dekhein");
        System.out.println("5. Class summary");
        System.out.println("0. Exit");
        System.out.print("Apna choice chunein: ");
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": addStudent();    break;
                case "2": viewAll();       break;
                case "3": searchStudent(); break;
                case "4": showTopper();    break;
                case "5": showSummary();   break;
                case "0":
                    System.out.println("Dhanyavaad! Program band ho raha hai.");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice! 0 se 5 ke beech chunein.");
            }
        }
    }
}