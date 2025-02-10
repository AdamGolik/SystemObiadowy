package com.example.mealapp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainApp {
  private final JPanel mainPanel;
  private final JTable dataTable;
  private final JTextField inputField;
  private final JButton addButton;
  private final JButton deleteButton;
  private final JButton saveButton;

  private final DefaultTableModel tableModel;
  private final List<Student> studentList = new ArrayList<>();
  // data
  private final String CSV_FILE = "data/students.csv";
  private static final String MEALS_FILE_PATH = "data/obiady.txt";
  private static final String REP_FILE_PATH = "data/raport.txt";
  private static final String LOG_FILE_PATH = "data/data.txt";
  private static final String SUM_FILE_PATH = "data/Obiady_Suma.txt";
  private static final String Data_Sum_FILE_PATH = "data/sum_obiad.txt";
  // obiady counting
  private int totalMeals = 0; // Całkowita liczba obiadów
  private int totalChildren = 0; // Liczba obiadów dla dzieci
  private int totalTeachers = 0; // Liczba obiadów dla nauczycieli
  private int totalStudents = 0; // Liczba obiadów dla uczniów
  private int mealsLogCount = 0; // Numeracja wpisów w pliku obiady.txt
  private Timer autoInputTimer; // Timer do obsługi opóźnionego dodawania danych
  private static boolean shutdownHookRegistered = false;

  public MainApp() {
    // Inicjalizacja GUI
    String[] columnNames = { "Imię", "Nazwisko", "Klasa", "Nr w dzienniku", "ID Karty" };
    tableModel = new DefaultTableModel(columnNames, 0);
    dataTable = new JTable(tableModel);
    mainPanel = new JPanel(new BorderLayout());
    configureRowColoring();
    inputField = new JTextField(20);
    addButton = new JButton("Dodaj");
    deleteButton = new JButton("Usuń");
    saveButton = new JButton("Zapisz"); // Przyciski zainicjalizowane
    saveButton.setText("Obiady"); // Bezpieczne wywołanie metody po inicjalizacji
    saveButton.addActionListener(e -> {
      writeSummaryToFile();
    });
    JPanel controlPanel = new JPanel();
    controlPanel.add(new JLabel("Wpisz dane:"));
    controlPanel.add(inputField);
    controlPanel.add(addButton);
    controlPanel.add(deleteButton);
    controlPanel.add(saveButton);
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
    // zapisuje pliki
    try (BufferedReader reader = new BufferedReader(new FileReader(MEALS_FILE_PATH))) {
      String lastLine = null, line;
      while ((line = reader.readLine()) != null) {
        lastLine = line; // Pobierz ostatnią linię
      }

      if (lastLine != null && lastLine.matches("\\d+\\. .*")) {
        String[] parts = lastLine.split("\\. ", 2);
        mealsLogCount = Integer.parseInt(parts[0]); // Ustaw numer ostatniego wpisu
      }
    } catch (IOException | NumberFormatException e) {
      mealsLogCount = 0; // Jeśli plik nie istnieje lub błąd odczytu, ustaw na 0
    }

    setupAutoInput();
    if (!shutdownHookRegistered) {
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          // writeSummaryToFile();
          System.out.println("Program zamknięto");
        }
      }));
      shutdownHookRegistered = true; // Oznacz hook jako zarejestrowany
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

    if (input.length() > 8 && input.matches("\\d+")) {
      if (autoInputTimer != null && autoInputTimer.isRunning()) {
        autoInputTimer.stop();
      }

      autoInputTimer = new Timer(300, e -> {
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
   * @param message wpisuje logi
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
   * Ładuje dane z pliku CSV do pamięci (lista studentList) bez dodawania ich do
   * tabeli.
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
    // Wywołanie metody zapisującej sumaryczne dane do pliku
    saveTotalMealsSummary();

    // Opcjonalnie: komunikat w GUI, że podsumowanie zostało zapisane
    JOptionPane.showMessageDialog(mainPanel, "Podsumowanie obiadów zapisane do pliku: " + SUM_FILE_PATH);
  }

  /**
   * Zapisuje dane o obiadach (dzień, miesiąc, rok oraz szczegóły dotyczące liczby
   * obiadów) do pliku sum_obiady.txt.
   * Jeśli plik nie istnieje, zostanie automatycznie utworzony.
   */
  private void writeSummaryToFile() {
    // Pobierz bieżącą datę
    LocalDateTime now = LocalDateTime.now();
    String dateHeader = "===== dzień: " + now.getDayOfMonth() + " === miesiąc: " + now.getMonthValue() + " === rok: "
        + now.getYear() + " =====";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(Data_Sum_FILE_PATH, true))) {
      // Najpierw zapisz nagłówek z datą
      writer.write(dateHeader);
      writer.newLine();

      // Zapisz szczegóły dotyczące liczby obiadów
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
      writer.newLine(); // Dodaj pustą linię dla lepszej czytelności
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Przetwarza dane wpisane przez użytkownika i dodaje odpowiednią osobę do
   * tabeli.
   */
  private void processInput(String input) {
    if (input.matches("\\d{10}") || input.length() > 8 && input.matches("\\d+")) { // ID Karty
      addStudentById(input);
      logToFile("Przetworzono dane wejściowe dla ID karty: " + input);
    } else if (input.matches("\\d+[A-Z]\\d+")) { // Klasa i numer w dzienniku np. 5A5
      String classAndLetter = input.replaceFirst("\\d+$", ""); // Wyodrębnia "5A"
      String number = input.replaceAll("^\\d+[A-Z]", ""); // Wyodrębnia "5"

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

    if (input == null || input.isEmpty())
      return;
    boolean cardFound = false;
    // Sprawdzenie wprowadzonego inputu
    for (Student student : studentList) {
      if (student.getCardId().equalsIgnoreCase(input)) {
        addStudentToTable(student); // Dodanie ucznia do tabeli
        totalMeals++; // Zwiększ liczbę wydanych obiadów
        totalStudents++; // Zwiększ liczbę obiadów dla uczniów
        cardFound = true;
        break;
      }
    }

    if (!cardFound) {
      // Przetwarzanie dzieci (np. "20")
      if (input.matches("\\d+")) {
        int count = Integer.parseInt(input); // Pobieramy liczbę dzieci
        totalMeals += count; // Dodajemy do całkowitej liczby obiadów
        totalChildren += count; // Dodajemy liczbę dzieci
      }
      // Przetwarzanie nauczycieli (np. "20N")
      else if (input.matches("\\d+N")) {
        int count = Integer.parseInt(input.substring(0, input.length() - 1)); // Pobieramy liczbę nauczycieli
        totalMeals += count; // Dodajemy do całkowitej liczby obiadów
        totalTeachers += count; // Dodajemy liczbę nauczycieli
      }
      // Brak kategorii - logujemy brak karty
      else {
        logNoCardEntry(input);
      }
    }

    updateMealsLog();
  }

  private void updateMealsLog() {
    mealsLogCount++; // Numer kolejnego wpisu w logu

    // Tworzenie wiadomości dla logu
    String message = mealsLogCount + ". " + LocalDateTime.now() +
        " - Liczba wydanych obiadów: " + totalMeals +
        " (Dzieci: " + totalChildren + ", Nauczyciele: " + totalTeachers + ", Uczniowie: " + totalStudents + ")";

    File file = new File(MEALS_FILE_PATH); // Plik, do którego zapiszemy wiadomość
    try {
      // Jeśli plik nie istnieje, utworz go
      if (!file.exists()) {
        file.createNewFile();
      }

      // Dopisz wiadomość do pliku
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) { // 'true' oznacza dopisywanie
        writer.write(message);
        writer.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace(); // Obsługa błędów wejścia/wyjścia
    }
    saveTotalMealsSummary();

  }

  private void logNoCardEntry(String input) {
    String message = LocalDateTime.now() + " - Brak karty dla wprowadzonego identyfikatora: " + input;

    File file = new File(REP_FILE_PATH); // Plik, do którego zapiszemy wiadomość
    try {
      // Jeśli plik nie istnieje, utworz go
      if (!file.exists()) {
        file.createNewFile();
      }

      // Dopisz wiadomość do pliku
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
        writer.write(message);
        writer.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace(); // Obsługa błędów wejścia/wyjścia
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
      tableModel.addRow(new Object[] { "Dziecko" + i, "?", "?", "?", "brak" });
    }
    configureRowColoring();
  }

  /**
   * Symuluje dodanie nauczycieli (tylko do tabeli, brak danych w pliku CSV).
   */
  /**
   * Symuluje dodanie nauczycieli (tylko do tabeli, brak rzeczywistych danych w
   * pliku CSV).
   */
  private void addTeachers(int count) {
    for (int i = 1; i <= count; i++) {
      String teacherName = "Nauczyciel" + i; // Generowanie nazwy
      tableModel.addRow(new Object[] { teacherName, "Nowak", "Brak", "Brak", "Brak" });
    }
    configureRowColoring(); // Dodanie kolorowania
  }

  /**
   * Dodaje studenta do tabeli w GUI.
   */
  private void addStudentToTable(Student student) {
    tableModel.addRow(new Object[] {
        student.getFirstName(),
        student.getLastName(),
        student.getClassName(),
        student.getClassNumber(),
        student.getCardId()
    });
    // Automatyczny zapis danych do pliku data.txt
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
      writer.write("Dodano studenta: " + student.getFirstName() + ", ID karty: " + student.getCardId() + ", Data: "
          + LocalDateTime.now());
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    totalMeals++; // Zwiększ licznik obiadów za każdą dodaną osobę
    updateMealsLog(); // Aktualizuj liczbę obiadów w obiady.txt
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
        String name = (String) table.getValueAt(row, 0); // "Imię"

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

  private void appendTodayDateToFile(String filePath) {
    try {
      // Pobierz dzisiejszą datę w formacie "dzień i miesiąc"
      LocalDateTime now = LocalDateTime.now();
      String todayHeader = "===== dzień: " + now.getDayOfMonth() + " ==== miesiąc: " + now.getMonthValue() + " =====";

      File file = new File(filePath);

      // Sprawdź, czy plik istnieje i czy zawiera już nagłówek z dzisiejszą datą
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

      // Jeśli nie ma dzisiejszego nagłówka, dodaj go
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

  /**
   * Zapisuje sumaryczne dane o obiadach do pliku sumarycznego (Obiady_Suma.txt).
   */
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
    // new MainApp().logToFile("Testowy log - czy działa poprawnie?");
    MainApp app = new MainApp();
    app.appendTodayDateToFile(LOG_FILE_PATH);
    app.appendTodayDateToFile(MEALS_FILE_PATH);
    app.appendTodayDateToFile(REP_FILE_PATH);
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Lista uczniów");
      frame.setContentPane(new MainApp().mainPanel);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
    });
  }
}
