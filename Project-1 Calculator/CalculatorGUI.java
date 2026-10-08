import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.MathContext;

class CalculatorGUI extends JFrame {

    // ---------- Button labels (unicode escapes => har system par sahi compile hoga) ----------
    private static final String ADD  = "+";
    private static final String SUB  = "\u2212";   // −
    private static final String MUL  = "\u00D7";   // ×
    private static final String DIV  = "\u00F7";   // ÷
    private static final String MOD  = "mod";
    private static final String POW  = "x^y";
    private static final String SQRT = "\u221A";   // √
    private static final String SQR  = "x\u00B2";  // x²
    private static final String INV  = "1/x";
    private static final String NEG  = "\u00B1";   // ±
    private static final String CLEAR = "C";
    private static final String DEL   = "DEL";
    private static final String EQ    = "=";
    private static final String DOT   = ".";

    private static final String[][] LAYOUT = {
            {SQR,   SQRT, POW, INV},
            {CLEAR, DEL,  MOD, DIV},
            {"7",   "8",  "9", MUL},
            {"4",   "5",  "6", SUB},
            {"1",   "2",  "3", ADD},
            {NEG,   "0",  DOT, EQ}
    };

    // ---------- UI ----------
    private final JLabel expressionLabel = new JLabel(" ", SwingConstants.RIGHT);
    private final JTextField display = new JTextField("0");

    // ---------- State ----------
    private double firstOperand = 0;
    private String operator = null;
    private boolean startNew = true;   // agla digit naya number shuru karega
    private boolean error = false;

    public CalculatorGUI() {
        super("Java Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(new Color(0x202124));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Display area
        expressionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        expressionLabel.setForeground(new Color(0xBDC1C6));

        display.setEditable(false);
        display.setFocusable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("SansSerif", Font.BOLD, 34));
        display.setForeground(Color.WHITE);
        display.setBackground(new Color(0x2B2B2B));
        display.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(expressionLabel, BorderLayout.NORTH);
        top.add(display, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        // Buttons
        JPanel grid = new JPanel(new GridLayout(LAYOUT.length, 4, 8, 8));
        grid.setOpaque(false);
        for (String[] row : LAYOUT) {
            for (String label : row) {
                grid.add(createButton(label));
            }
        }
        root.add(grid, BorderLayout.CENTER);

        setContentPane(root);
        root.setPreferredSize(new Dimension(340, 480));
        pack();
        setLocationRelativeTo(null);

        // Keyboard support
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKey(e.getKeyChar());
            }
        });
    }

    // ---------- Button factory ----------
    private JButton createButton(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setFocusable(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(colorFor(label));
        b.addActionListener(e -> handle(label));
        return b;
    }

    private Color colorFor(String label) {
        if (label.equals(EQ))    return new Color(0x34A853);
        if (label.equals(CLEAR)) return new Color(0xEA4335);
        if (isBinary(label))     return new Color(0xFF9500);
        if (label.equals(SQR) || label.equals(SQRT) || label.equals(INV)
                || label.equals(DEL) || label.equals(NEG)) {
            return new Color(0x5F6368);
        }
        return new Color(0x3C3F41);   // digits aur dot
    }

    // ---------- Keyboard mapping ----------
    private void handleKey(char c) {
        if (c >= '0' && c <= '9') handle(String.valueOf(c));
        else if (c == '.')  handle(DOT);
        else if (c == '+')  handle(ADD);
        else if (c == '-')  handle(SUB);
        else if (c == '*')  handle(MUL);
        else if (c == '/')  handle(DIV);
        else if (c == '%')  handle(MOD);
        else if (c == '^')  handle(POW);
        else if (c == '=' || c == '\n') handle(EQ);
        else if (c == '\b') handle(DEL);
        else if (c == 27)   handle(CLEAR);   // Esc
    }

    // ---------- Core logic ----------
    private void handle(String cmd) {
        if (error && !cmd.equals(CLEAR)) {
            clearAll();   // error ke baad nayi shuruaat
        }

        try {
            if (cmd.length() == 1 && Character.isDigit(cmd.charAt(0))) {
                inputDigit(cmd);
            } else if (cmd.equals(DOT)) {
                inputDot();
            } else if (isBinary(cmd)) {
                inputOperator(cmd);
            } else if (cmd.equals(EQ)) {
                calculateEquals();
            } else if (cmd.equals(CLEAR)) {
                clearAll();
            } else if (cmd.equals(DEL)) {
                backspace();
            } else if (cmd.equals(NEG)) {
                negate();
            } else {
                applyUnary(cmd);
            }
        } catch (ArithmeticException ex) {
            showError(ex.getMessage());
        }
    }

    private boolean isBinary(String s) {
        return s.equals(ADD) || s.equals(SUB) || s.equals(MUL)
                || s.equals(DIV) || s.equals(MOD) || s.equals(POW);
    }

    private void inputDigit(String d) {
        if (startNew || display.getText().equals("0")) {
            display.setText(d);
        } else if (display.getText().length() < 16) {
            display.setText(display.getText() + d);
        }
        startNew = false;
    }

    private void inputDot() {
        if (startNew) {
            display.setText("0.");
            startNew = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void inputOperator(String op) {
        if (operator != null && !startNew) {
            // chained: 2 + 3 + ... => pehle 2 + 3 nikaalo
            double result = compute(firstOperand, current(), operator);
            show(result);
            firstOperand = result;
        } else if (operator == null) {
            firstOperand = current();
        }
        // agar operator badla ja raha hai (startNew true) to sirf operator replace hoga
        operator = op;
        startNew = true;
        expressionLabel.setText(format(firstOperand) + " " + op);
    }

    private void calculateEquals() {
        if (operator == null) return;
        double second = current();
        double result = compute(firstOperand, second, operator);
        expressionLabel.setText(format(firstOperand) + " " + operator + " " + format(second) + " =");
        show(result);
        operator = null;
        startNew = true;
    }

    private void applyUnary(String cmd) {
        double v = current();
        double result;
        if (cmd.equals(SQRT)) {
            if (v < 0) throw new ArithmeticException("Negative number ka square root nahi hota");
            result = Math.sqrt(v);
        } else if (cmd.equals(SQR)) {
            result = v * v;
        } else if (cmd.equals(INV)) {
            if (v == 0) throw new ArithmeticException("Zero se divide nahi kiya ja sakta");
            result = 1 / v;
        } else {
            return;
        }
        expressionLabel.setText(cmd + "(" + format(v) + ")");
        show(result);
        startNew = true;
    }

    private void negate() {
        String t = display.getText();
        if (t.equals("0")) return;
        display.setText(t.startsWith("-") ? t.substring(1) : "-" + t);
    }

    private void backspace() {
        if (startNew) return;   // result ko delete nahi karte
        String t = display.getText();
        t = t.substring(0, t.length() - 1);
        if (t.isEmpty() || t.equals("-")) t = "0";
        display.setText(t);
    }

    private void clearAll() {
        display.setText("0");
        expressionLabel.setText(" ");
        firstOperand = 0;
        operator = null;
        startNew = true;
        error = false;
    }

    private void showError(String message) {
        display.setText("Error");
        expressionLabel.setText(message);
        operator = null;
        startNew = true;
        error = true;
    }

    // ---------- Math ----------
    private double compute(double a, double b, String op) {
        switch (op) {
            case ADD: return a + b;
            case SUB: return a - b;
            case MUL: return a * b;
            case DIV:
                if (b == 0) throw new ArithmeticException("Zero se divide nahi kiya ja sakta");
                return a / b;
            case MOD:
                if (b == 0) throw new ArithmeticException("Zero ke saath modulus nahi ho sakta");
                return a % b;
            case POW: return Math.pow(a, b);
            default:  return b;
        }
    }

    private double current() {
        return Double.parseDouble(display.getText());
    }

    private void show(double value) {
        display.setText(format(value));
    }

    // 12 significant digits tak round: 0.1 + 0.2 => 0.3
    private String format(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            throw new ArithmeticException("Result valid nahi hai");
        }
        if (Math.abs(v) >= 1e15) {
            return String.format("%.6e", v);
        }
        BigDecimal bd = BigDecimal.valueOf(v).round(new MathContext(12)).stripTrailingZeros();
        return bd.toPlainString();
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorGUI calc = new CalculatorGUI();
            calc.setVisible(true);
            calc.requestFocusInWindow();
        });
    }
}
