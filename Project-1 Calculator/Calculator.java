import java.util.Scanner;

public class Calculator {

    // ---------- Operations ----------
    static double add(double a, double b)      { return a + b; }
    static double subtract(double a, double b) { return a - b; }
    static double multiply(double a, double b) { return a * b; }

    static double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Zero se divide nahi kiya ja sakta.");
        }
        return a / b;
    }

    static double modulus(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Zero ke saath modulus nahi ho sakta.");
        }
        return a % b;
    }

    static double power(double base, double exp) {
        return Math.pow(base, exp);
    }

    static double squareRoot(double a) {
        if (a < 0) {
            throw new ArithmeticException("Negative number ka square root nahi hota.");
        }
        return Math.sqrt(a);
    }

    // ---------- Input helper ----------
    static double readNumber(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Galat input! Kripya valid number daalein.");
            }
        }
    }

    static void printMenu() {
        System.out.println("\n===== JAVA CALCULATOR =====");
        System.out.println("1. Addition (+)");
        System.out.println("2. Subtraction (-)");
        System.out.println("3. Multiplication (*)");
        System.out.println("4. Division (/)");
        System.out.println("5. Modulus (%)");
        System.out.println("6. Power (a^b)");
        System.out.println("7. Square Root");
        System.out.println("0. Exit");
        System.out.print("Apna choice chunein: ");
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Dhanyavaad! Calculator band ho raha hai.");
                break;
            }

            try {
                double result;
                switch (choice) {
                    case "1": {
                        double a = readNumber(sc, "Pehla number: ");
                        double b = readNumber(sc, "Doosra number: ");
                        result = add(a, b);
                        break;
                    }
                    case "2": {
                        double a = readNumber(sc, "Pehla number: ");
                        double b = readNumber(sc, "Doosra number: ");
                        result = subtract(a, b);
                        break;
                    }
                    case "3": {
                        double a = readNumber(sc, "Pehla number: ");
                        double b = readNumber(sc, "Doosra number: ");
                        result = multiply(a, b);
                        break;
                    }
                    case "4": {
                        double a = readNumber(sc, "Pehla number (dividend): ");
                        double b = readNumber(sc, "Doosra number (divisor): ");
                        result = divide(a, b);
                        break;
                    }
                    case "5": {
                        double a = readNumber(sc, "Pehla number: ");
                        double b = readNumber(sc, "Doosra number: ");
                        result = modulus(a, b);
                        break;
                    }
                    case "6": {
                        double a = readNumber(sc, "Base: ");
                        double b = readNumber(sc, "Exponent: ");
                        result = power(a, b);
                        break;
                    }
                    case "7": {
                        double a = readNumber(sc, "Number: ");
                        result = squareRoot(a);
                        break;
                    }
                    default:
                        System.out.println("Invalid choice! 0 se 7 ke beech chunein.");
                        continue;
                }
                System.out.println("Result = " + result);

            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }
}