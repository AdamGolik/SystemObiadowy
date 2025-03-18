package com.example.mealapp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainApp {
    private final JPanel mainPanel;
    private final JTable dataTable;
    private final JTextField inputField;
    private final JButton endButton;
    private final DefaultTableModel tableModel;
    private final JLabel totalMealsLabel;  // Etykieta do wyświetlania liczby obiadów

    private final List<Student> studentList = new ArrayList<>();
    // Ścieżki do plików
    private final String CSV_FILE = "data/students.csv";
    private static final String MEALS_FILE_PATH = "data/obiady.txt";
    private static final String REP_FILE_PATH = "data/raport.txt";
    private static final String LOG_FILE_PATH = "data/data.txt";
    private static final String SUM_FILE_PATH = "data/Obiady_Suma.txt";
    private static final String Data_Sum_FILE_PATH = "data/sum_obiad.txt";
    // Nowy plik z liczbą zapomnianych kart
    private static final String MISSING_CARD_COUNT_FILE = "data/missingCardCount.txt";

    // Liczniki
    private int totalMeals = 0;
    private int totalChildren = 0;
    private int totalTeachers = 0;
    private int totalStudents = 0;
    private int mealsLogCount = 0;
    private Timer autoInputTimer;
    private static boolean shutdownHookRegistered = false;

    public MainApp() {
        String[] columnNames = { "Imię", "Nazwisko", "Klasa", "Nr w dzienniku", "ID Karty" };
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        mainPanel = new JPanel(new BorderLayout());
        
        // Dodajemy etykietę na górze do wyświetlania liczby obiadów
        totalMealsLabel = new JLabel("Liczba obiadów: " + totalMeals);
        mainPanel.add(totalMealsLabel, BorderLayout.NORTH);
        
        configureRowColoring();
        inputField = new JTextField(20);
        JButton addButton = new JButton("Dodaj");
        JButton deleteButton = new JButton("Usuń");
        // Przycisk "Obiady" został przemianowany na "Koniec"
        endButton = new JButton("Koniec");
        // Po kliknięciu przycisku wyświetlamy alert i potwierdzenie zakończenia
        endButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(mainPanel, "Czy na pewno chcesz zakończyć aplikację?", "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION) {
                writeSummaryToFile();
                System.exit(0);
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Wpisz dane:"));
        controlPanel.add(inputField);
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(endButton);
        mainPanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Skróty klawiszowe:
        inputField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "addStudent");
        inputField.getActionMap().put("addStudent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addButton.doClick();
            }
        });
        
        KeyStroke deleteKey = KeyStroke.getKeyStroke("DELETE");
        KeyStroke backspaceKey = KeyStroke.getKeyStroke("BACK_SPACE");
        dataTable.getInputMap(JComponent.WHEN_FOCUSED).put(deleteKey, "deleteRow");
        dataTable.getInputMap(JComponent.WHEN_FOCUSED).put(backspaceKey, "deleteRow");
        dataTable.getActionMap().put("deleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRows();
            }
        });
        
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("control S"), "saveSummary");
        mainPanel.getActionMap().put("saveSummary", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeSummaryToFile();
            }
        });

        loadStudentData();

        addButton.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (!input.isEmpty()) {
                processInput(input);
                inputField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Proszę wpisać dane!");
            }
        });
        
        // Odczyt numeru ostatniego wpisu z pliku obiady.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(MEALS_FILE_PATH))) {
            String lastLine = null, line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            if (lastLine != null && lastLine.matches("\\d+\\. .*")) {
                String[] parts = lastLine.split("\\. ", 2);
                mealsLogCount = Integer.parseInt(parts[0]);
            }
        } catch (IOException | NumberFormatException e) {
            mealsLogCount = 0;
        }

        setupAutoInput();
        if (!shutdownHookRegistered) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Program zamknięto");
            }));
            shutdownHookRegistered = true;
        }
    }

    private void setupAutoInput() {
        inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleInputChange();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleInputChange();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleInputChange();
            }
        });
    }

    private void handleInputChange() {
        String input = inputField.getText().trim();
        if (input.length() > 3 && input.matches("\\d+")) {
            if (autoInputTimer != null && autoInputTimer.isRunning()) {
                autoInputTimer.stop();
            }
            autoInputTimer = new Timer(100, e -> {
                if (input.equals(inputField.getText().trim())) {
                    addStudentById(input);
                    inputField.setText("");
                }
            });
            autoInputTimer.setRepeats(false);
            autoInputTimer.start();
        }
    }

    /**
     * Logowanie do pliku LOG_FILE_PATH.
     */
    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(LocalDateTime.now() + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loguje informacje do raport.txt.
     */
    private void logToRaportFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REP_FILE_PATH, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Aktualizuje plik, w którym zapisywane są informacje, kto ile razy zapomniał kartę.
     */
    private void updateMissingCardCount(Student student) {
        Map<String, Integer> missingMap = new HashMap<>();
        File file = new File(MISSING_CARD_COUNT_FILE);
        // Odczyt istniejących danych
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while((line = br.readLine()) != null) {
                    if(line.contains(":")) {
                        String[] parts = line.split(":");
                        if(parts.length >= 2) {
                            String key = parts[0].trim();
                            int count = Integer.parseInt(parts[1].trim());
                            missingMap.put(key, count);
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        // Klucz z danymi ucznia
        String key = student.getFirstName() + " " + student.getLastName() + " " + student.getClassName() + student.getClassNumber();
        int count = missingMap.getOrDefault(key, 0);
        missingMap.put(key, count + 1);
        // Zapisujemy zaktualizowane dane do pliku
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for(Map.Entry<String, Integer> entry : missingMap.entrySet()) {
                bw.write(entry.getKey() + ": " + entry.getValue());
                bw.newLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ładuje dane z pliku CSV do listy studentList.
     */
    private void loadStudentData() {
        studentList.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    studentList.add(new Student(data[0], data[1], data[2], data[3], data[4]));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania pliku CSV: " + e.getMessage());
        }
    }

    private void generateMealSummary() {
        saveTotalMealsSummary();
        JOptionPane.showMessageDialog(mainPanel, "Podsumowanie obiadów zapisane do pliku: " + SUM_FILE_PATH);
    }

    private void writeSummaryToFile() {
        LocalDateTime now = LocalDateTime.now();
        String dateHeader = "===== dzień: " + now.getDayOfMonth() + " === miesiąc: " + now.getMonthValue() + " === rok: " + now.getYear() + " =====";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Data_Sum_FILE_PATH, true))) {
            writer.write(dateHeader);
            writer.newLine();
            writer.write("Całkowita liczba obiadów: " + totalMeals);
            writer.newLine();
            writer.write("Liczba obiadów dla dzieci: " + totalChildren);
            writer.newLine();
            writer.write("Liczba obiadów dla nauczycieli: " + totalTeachers);
            writer.newLine();
            writer.write("Liczba obiadów dla uczniów: " + totalStudents);
            writer.newLine();
            writer.write("Historia wpisów: " + mealsLogCount);
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Przetwarza dane wpisane przez użytkownika.
     * Modyfikacje: przyjmujemy zarówno małe, jak i duże litery.
     */
    private void processInput(String input) {
        if (input == null || input.trim().isEmpty())
            return;
        input = input.trim();
        // 1. Jeśli wejście pasuje do ID karty (10-cyfrowe lub dłuższe)
        if (input.matches("\\d{10}") || (input.length() > 8 && input.matches("\\d+"))) { 
            addStudentById(input);
            logToFile("Przetworzono dane wejściowe dla ID karty: " + input);
            return;
        } 
        // 2. Jeśli wejście pasuje do formatu klasy i numeru (np. "5A5" lub "5a5")
        else if (input.matches("(?i)\\d+[A-Z]\\d+")) { 
            // Konwersja do standardowego formatu (wszystko na wielkie litery)
            input = input.toUpperCase();
            String classAndLetter = input.replaceFirst("\\d+$", "");
            String number = input.replaceAll("^\\d+[A-Z]", "");
            addStudentByClassAndNumber(classAndLetter, number);
            return;
        } 
        // 3. Jeśli wejście pasuje do formatu dla nauczycieli (np. "8N" lub "8n")
        else if (input.matches("(?i)\\d+N")) { 
            // Konwersja do wielkich liter
            input = input.toUpperCase();
            int count = Integer.parseInt(input.substring(0, input.length() - 1));
            addTeachers(count);
            logToFile("Dodano nauczycieli: liczba = " + count);
            return;
        } 
        // 4. Jeśli wejście to liczba – traktujemy ją jako liczbę dzieci
        else if (input.matches("\\d+")) { 
            int count = Integer.parseInt(input);
            addChildren(count);
            logToFile("Dodano dzieci: liczba = " + count);
            return;
        } else {
            String errorMessage = "Nieodpowiedni format danych! Dane: " + input;
            JOptionPane.showMessageDialog(null, errorMessage);
            logToFile("Błąd: " + errorMessage);
            return;
        }
    }

    /**
     * Dodaje ucznia na podstawie ID karty.
     * Jeśli ucznia nie znaleziono, loguje komunikat o zapomnianej karcie.
     */
    private void addStudentById(String cardId) {
        for (Student student : studentList) {
            if (student.getCardId().equalsIgnoreCase(cardId)) {
                addStudentToTable(student);
                totalStudents++;
                logToFile("Dodano ucznia: " + student.getFirstName() + " " + student.getLastName() +
                          " (ID: " + cardId + ")");
                return;
            }
        }
        // Jeśli ucznia nie znaleziono – zakładamy, że zapomniał karty
        String message = LocalDateTime.now() + " - Uczeń o identyfikatorze " + cardId + " zapomniał karty";
        logToRaportFile(message);
        JOptionPane.showMessageDialog(null, "Nie znaleziono ucznia z ID: " + cardId + ". Być może zapomniał karty.");
    }

    /**
     * Dodaje ucznia na podstawie klasy i numeru.
     * Uczniu dodanemu w ten sposób przypisujemy log o zapomnianej karcie,
     * który zawiera dane: klasa i numer oraz (imię nazwisko).
     * Dodatkowo aktualizujemy plik z liczbą zapomnianych kart.
     */
    private void addStudentByClassAndNumber(String className, String classNumber) {
        for (Student student : studentList) {
            if (student.getClassName().equalsIgnoreCase(className) &&
                student.getClassNumber().equals(classNumber)) {
                addStudentToTable(student);
                logToFile("Dodano ucznia: " + student.getFirstName() + " " + student.getLastName() +
                          " (klasa: " + student.getClassName() + ", nr: " + student.getClassNumber() + ")");
                // Logowanie informacji, że uczeń zapomniał kartę
                String message = LocalDateTime.now() + " - Uczeń klasy " + student.getClassName() + student.getClassNumber() +
                        " zapomniał kartę (" + student.getFirstName() + " " + student.getLastName() + ")";
                logToRaportFile(message);
                updateMissingCardCount(student);
                return;
            }
        }
        String errorMessage = "Nie znaleziono ucznia w klasie: " + className + ", nr: " + classNumber;
        JOptionPane.showMessageDialog(null, errorMessage);
        logToFile("Błąd: " + errorMessage);
    }

    private void addChildren(int count) {
        for (int i = 1; i <= count; i++) {
            tableModel.addRow(new Object[] { "Dziecko" + i, "?", "?", "?", "brak" });
        }
        totalMeals += count;
        totalChildren += count;
        configureRowColoring();
        updateMealsLog();
    }

    private void addTeachers(int count) {
        for (int i = 1; i <= count; i++) {
            String teacherName = "Nauczyciel" + i;
            tableModel.addRow(new Object[] { teacherName, "Nowak", "Brak", "Brak", "Brak" });
        }
        totalMeals += count;
        totalTeachers += count;
        configureRowColoring();
        updateMealsLog();
    }

    private void addStudentToTable(Student student) {
        tableModel.addRow(new Object[] {
            student.getFirstName(),
            student.getLastName(),
            student.getClassName(),
            student.getClassNumber(),
            student.getCardId()
        });
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write("Dodano studenta: " + student.getFirstName() + ", ID karty: " + student.getCardId() +
                         ", Data: " + LocalDateTime.now());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalMeals++;
        updateMealsLog();
        configureRowColoring();
    }

    private void configureRowColoring() {
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String cardId = (String) table.getValueAt(row, 4);
                String name = (String) table.getValueAt(row, 0);
                Set<String> uniqueIds = new HashSet<>();
                boolean isDuplicate = false;
                for (int i = 0; i < table.getRowCount(); i++) {
                    String id = (String) table.getValueAt(i, 4);
                    if (!uniqueIds.add(id) && id.equals(cardId) && id != null && !id.isEmpty()) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (name != null && name.startsWith("Dziecko")) {
                    cell.setBackground(Color.BLUE);
                    cell.setForeground(Color.WHITE);
                } else if (name != null && name.startsWith("Nauczyciel")) {
                    cell.setBackground(Color.LIGHT_GRAY);
                    cell.setForeground(Color.BLACK);
                } else if (isDuplicate) {
                    cell.setBackground(Color.RED);
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setBackground(Color.GREEN);
                    cell.setForeground(Color.BLACK);
                }
                if (isSelected) {
                    cell.setBackground(Color.ORANGE);
                    cell.setForeground(Color.BLACK);
                }
                return cell;
            }
        });
    }

    private void deleteSelectedRows() {
        int[] selectedRows = dataTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            String name = (String) dataTable.getValueAt(selectedRows[i], 0);
            String cardId = (String) dataTable.getValueAt(selectedRows[i], 4);
            logToFile("Usunięto wiersz: Imię = " + name + ", ID karty = " + cardId);
            if (name != null) {
                if (name.startsWith("Dziecko")) {
                    totalMeals--;
                    totalChildren--;
                } else if (name.startsWith("Nauczyciel")) {
                    totalMeals--;
                    totalTeachers--;
                } else {
                    totalMeals--;
                    totalStudents--;
                }
            }
            tableModel.removeRow(selectedRows[i]);
        }
        updateMealsLog();
    }

    private void updateMealsLog() {
        mealsLogCount++;
        String message = mealsLogCount + ". " + LocalDateTime.now() +
                " - Liczba wydanych obiadów: " + totalMeals +
                " (Dzieci: " + totalChildren + ", Nauczyciele: " + totalTeachers + ", Uczniowie: " + totalStudents + ")";
        File file = new File(MEALS_FILE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveTotalMealsSummary();
        updateTotalMealsLabel();
    }

    /**
     * Aktualizuje etykietę wyświetlającą liczbę obiadów.
     */
    private void updateTotalMealsLabel() {
        totalMealsLabel.setText("Liczba obiadów: " + totalMeals);
    }

    private void appendTodayDateToFile(String filePath) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String todayHeader = "===== dzień: " + now.getDayOfMonth() + " ==== miesiąc: " + now.getMonthValue() + " =====";
            File file = new File(filePath);
            boolean alreadyLoggedToday = false;
            if (file.exists() && file.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String lastLine;
                    while ((lastLine = reader.readLine()) != null) {
                        if (lastLine.trim().equals(todayHeader)) {
                            alreadyLoggedToday = true;
                            break;
                        }
                    }
                }
            }
            if (!alreadyLoggedToday) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.write(todayHeader);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTotalMealsSummary() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUM_FILE_PATH))) {
            writer.write("===== Podsumowanie Obiadów =====");
            writer.newLine();
            writer.write("Całkowita liczba obiadów: " + totalMeals);
            writer.newLine();
            writer.write("Liczba obiadów dla dzieci: " + totalChildren);
            writer.newLine();
            writer.write("Liczba obiadów dla nauczycieli: " + totalTeachers);
            writer.newLine();
            writer.write("Liczba obiadów dla uczniów: " + totalStudents);
            writer.newLine();
            writer.write("Liczba wpisów w historii logów: " + mealsLogCount);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveStudentData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                bw.write(tableModel.getValueAt(i, 0) + "," + tableModel.getValueAt(i, 1) + "," +
                         tableModel.getValueAt(i, 2) + "," + tableModel.getValueAt(i, 3) + "," +
                         tableModel.getValueAt(i, 4));
                bw.newLine();
            }
            logToFile("Zapisano dane do pliku CSV: " + CSV_FILE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas zapisywania pliku CSV: " + e.getMessage());
            logToFile("Błąd: Podczas zapisywania pliku CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MainApp app = new MainApp();
        app.appendTodayDateToFile(LOG_FILE_PATH);
        app.appendTodayDateToFile(MEALS_FILE_PATH);
        app.appendTodayDateToFile(REP_FILE_PATH);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lista uczniów");
            frame.setContentPane(new MainApp().mainPanel);
            // Ustawiamy tryb zamykania okna na DO_NOTHING_ON_CLOSE i komunikat o konieczności kliknięcia przycisku "Koniec"
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    JOptionPane.showMessageDialog(frame, "Aby zamknąć aplikację, kliknij przycisk 'Koniec'");
                }
            });
            frame.pack();
            frame.setVisible(true);
        });
    }
}
