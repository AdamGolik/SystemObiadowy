package com.example.mealapp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainApp {
    private JPanel mainPanel;
    private JTable dataTable;
    private JTextField inputField;
    private JButton addButton, deleteButton, saveButton;

    private DefaultTableModel tableModel;
    private List<Student> studentList = new ArrayList<>();
    private final String CSV_FILE = "data/students.csv";
    private Timer autoInputTimer; // Timer do obsługi opóźnionego dodawania danych
    public MainApp() {
        // Inicjalizacja GUI
        String[] columnNames = {"Imię", "Nazwisko", "Klasa", "Nr w dzienniku", "ID Karty"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);

        configureRowColoring();

        inputField = new JTextField(20);
        addButton = new JButton("Dodaj");
        deleteButton = new JButton("Usuń");
        saveButton = new JButton("Zapisz");

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Wpisz dane:"));
        controlPanel.add(inputField);
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(saveButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

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

        setupAutoInput();
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

        if (input.length() > 8 && input.matches("\\d+")) {
            if (autoInputTimer != null && autoInputTimer.isRunning()) {
                autoInputTimer.stop();
            }

            autoInputTimer = new Timer(800, e -> {
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
     *
     * @param message
     * wpisuje logi
     */
    private static final String LOG_FILE_PATH = "data/data.txt";

    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(LocalDateTime.now() + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Ładuje dane z pliku CSV do pamięci (lista studentList) bez dodawania ich do tabeli.
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

    /**
     * Przetwarza dane wpisane przez użytkownika i dodaje odpowiednią osobę do tabeli.
     */
    private void processInput(String input) {
        if (input.matches("\\d{10}") || input.length() > 8 && input.matches("\\d+")) { // ID Karty
            addStudentById(input);
            logToFile("Przetworzono dane wejściowe dla ID karty: " + input);
        } else if (input.matches("\\d+[A-Z]\\d+")) { // Klasa i numer w dzienniku np. 5A5
            String classAndLetter = input.replaceFirst("\\d+$", ""); // Wyodrębnia "5A"
            String number = input.replaceAll("^\\d+[A-Z]", "");      // Wyodrębnia "5"

            addStudentByClassAndNumber(classAndLetter, number);
            logToFile("Przetworzono dane wejściowe dla klasy i numeru: " +
                    classAndLetter + ", Nr: " + number);
        } else if (input.matches("\\d+N")) { // Liczba nauczycieli
            int count = Integer.parseInt(input.replaceAll("N", ""));
            addTeachers(count);
            logToFile("Dodano nauczycieli: liczba = " + count);
        } else if (input.matches("\\d+")) { // Liczba dzieci
            int count = Integer.parseInt(input);
            addChildren(count);
            logToFile("Dodano dzieci: liczba = " + count);
        } else {
            String errorMessage = "Nieodpowiedni format danych! Dane: " + input;
            JOptionPane.showMessageDialog(null, errorMessage);
            logToFile("Błąd: " + errorMessage);
        }
    }

private void addStudentById(String cardId) {
    for (Student student : studentList) {
        if (student.getCardId().equals(cardId)) {
            addStudentToTable(student);
            logToFile("Dodano ucznia na podstawie ID karty: " + cardId);
            return;
        }
    }
    String errorMessage = "Nie znaleziono ucznia z ID: " + cardId;
    JOptionPane.showMessageDialog(null, errorMessage);
    logToFile("Błąd: " + errorMessage);
}
    private void addStudentByClassAndNumber(String className, String classNumber) {
        for (Student student : studentList) {
            if (student.getClassName().equalsIgnoreCase(className) &&
                    student.getClassNumber().equals(classNumber)) {
                addStudentToTable(student);
                logToFile("Dodano ucznia na podstawie klasy: " + className + ", numer: " + classNumber);
                return;
            }
        }
        String errorMessage = "Nie znaleziono ucznia w klasie: " + className + ", nr: " + classNumber;
        JOptionPane.showMessageDialog(null, errorMessage);
        logToFile("Błąd: " + errorMessage);
    }

    /**
     * Dodaje ucznia do tabeli na podstawie ID karty.
     */

    /**
     * Dodaje ucznia do tabeli na podstawie klasy i numeru w dzienniku.
     */

    /**
     * Symuluje dodanie dzieci (tylko do tabeli, brak danych w pliku CSV).
     */
    private void addChildren(int count) {
        for (int i = 1; i <= count; i++) {
            tableModel.addRow(new Object[]{"Dziecko" + i, "?", "?", "?", "brak"});
        }
        configureRowColoring();
    }

    /**
     * Symuluje dodanie nauczycieli (tylko do tabeli, brak danych w pliku CSV).
     */
    /**
     * Symuluje dodanie nauczycieli (tylko do tabeli, brak rzeczywistych danych w pliku CSV).
     */
    private void addTeachers(int count) {
        for (int i = 1; i <= count; i++) {
            String teacherName = "Nauczyciel" + i; // Generowanie nazwy
            tableModel.addRow(new Object[]{teacherName, "Nowak", "Brak", "Brak", "Brak"});
        }
        configureRowColoring(); // Dodanie kolorowania
    }
    /**
     * Dodaje studenta do tabeli w GUI.
     */
    private void addStudentToTable(Student student) {
        tableModel.addRow(new Object[]{
                student.getFirstName(),
                student.getLastName(),
                student.getClassName(),
                student.getClassNumber(),
                student.getCardId()
        });
        logToFile("Dodano studenta do tabeli: " +
                "Imię: " + student.getFirstName() + ", Nazwisko: " + student.getLastName() +
                ", Klasa: " + student.getClassName() + ", Nr w dzienniku: " + student.getClassNumber() +
                ", ID karty: " + student.getCardId());
        configureRowColoring(); // Kolorowanie wierszy
    }

    /**
     * Kolorowanie wierszy w tabeli.
     * Zielony: Uczeń dodany po raz pierwszy.
     * Czerwony: Duplikat danych.
     */
    private void configureRowColoring() {
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String cardId = (String) table.getValueAt(row, 4); // ID karty
                String name = (String) table.getValueAt(row, 0);  // "Imię"

                // Sprawdź identyfikatory dla duplikatów
                Set<String> uniqueIds = new HashSet<>();
                boolean isDuplicate = false;
                for (int i = 0; i < table.getRowCount(); i++) {
                    String id = (String) table.getValueAt(i, 4);
                    if (!uniqueIds.add(id) && id.equals(cardId) && id != null && !id.isEmpty()) {
                        isDuplicate = true;
                        break;
                    }
                }

                // Sprawdzenie, czy jest to dziecko, nauczyciel czy uczeń
                if (name != null && name.startsWith("Dziecko")) {
                    cell.setBackground(Color.BLUE); // Dzieci na niebiesko
                    cell.setForeground(Color.WHITE); // Biały tekst
                } else if (name != null && name.startsWith("Nauczyciel")) {
                    cell.setBackground(Color.LIGHT_GRAY); // Nauczyciele na szaro
                    cell.setForeground(Color.BLACK); // Czarny tekst
                } else if (isDuplicate) {
                    cell.setBackground(Color.RED); // Powtórzenia na czerwono
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setBackground(Color.GREEN); // Brak powtórzeń na zielono
                    cell.setForeground(Color.BLACK);
                }

                // Zachowaj kolor zaznaczonego wiersza, niezależnie od statusu
                if (isSelected) {
                    cell.setBackground(Color.ORANGE);
                    cell.setForeground(Color.BLACK);
                }

                return cell;
            }
        });
    }

    /**
     * Usuwanie zaznaczonych wierszy z tabeli.
     */
    private void deleteSelectedRows() {
        int[] selectedRows = dataTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            String name = (String) dataTable.getValueAt(selectedRows[i], 0);
            String cardId = (String) dataTable.getValueAt(selectedRows[i], 4);

            // Dodaj logowanie informacji o usunięciu
            logToFile("Usunięto wiersz: Imię = " + name + ", ID karty = " + cardId);
            tableModel.removeRow(selectedRows[i]);
        }
    }

    /**
     * Zapisuje aktualne dane z tabeli do pliku CSV.
     */
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
        new MainApp().logToFile("Testowy log - czy działa poprawnie?");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lista uczniów");
            frame.setContentPane(new MainApp().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}