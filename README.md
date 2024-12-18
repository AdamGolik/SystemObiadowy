# **SystemObiadowy**
Projekt **SystemObiadowy** to aplikacja w Javie, która pozwala na zarządzanie liczbą obiadów dla różnych grup osób (dzieci, nauczycieli, uczniów) oraz automatycznie zapisuje podsumowanie danych do pliku przy zamykaniu programu. Dane są przechowywane w plikach CSV i tekstowych, a interfejs wykorzystuje bibliotekę Swing do prostej obsługi graficznej.

---

## **Wymagania**
Aby uruchomić projekt, upewnij się, że masz zainstalowane:
1. **Java Development Kit (JDK)** w wersji 8 lub nowszej.
2. System operacyjny:
    - Windows / macOS / Linux.
3. Kompatybilny IDE (np. IntelliJ IDEA, Eclipse) **lub** terminal.

---

## **Funkcje**
1. **Rejestracja obiadów dla dzieci, nauczycieli i uczniów.**
2. **Podsumowanie danych:**
    - Łączna liczba obiadów,
    - Poszczególne liczby obiadów dla różnych grup,
    - Historia wpisów.
3. **Automatyczny zapis do pliku przy zamykaniu programu.**
4. **Opcjonalne ładowanie i zapisywanie danych z plików CSV.**
5. **Interfejs graficzny stworzony w Swing.**

---

## **Pliki projektu**
Struktura katalogu projektu wygląda następująco:

```
SystemObiadowy/
│
├── data/
│   ├── students.csv               # Plik z informacjami o studentach
│   ├── obiady.txt                 # Logi z historią obiadów
│   ├── raport.txt                 # Opcjonalny plik raportów
│   ├── data.txt                   # Inne logi z danymi
│   ├── Obiady_Suma.txt            # Historyczne podsumowanie obiadów
│   └── sum_obiad.txt              # Dzienne podsumowania obiadów
│
├── src/
│   ├── com/example/mealapp/
│   │   ├── MainApp.java           # Główna klasa aplikacji
│   │   ├── Student.java           # Klasa obsługująca dane studentów
│   │   └── ... (inne klasy)
│
├── manifest.txt                   # Plik manifestu do tworzenia JAR
├── SystemObiadowy.jar             # Skonstruowany plik JAR
└── README.md                      # Dokumentacja projektu
```

---

## **Jak uruchomić projekt?**

### **Wykorzystując IntelliJ IDEA lub inne IDE**
1. Otwórz projekt w swoim IDE (np. IntelliJ IDEA).
2. Upewnij się, że zainstalowane jest odpowiednie JDK:
    - Przejdź do `File -> Project Structure` w IntelliJ i wybierz wersję JDK.
3. Uruchom klasę `MainApp`:
    - Znajdź plik `MainApp.java` w katalogu **src/com/example/mealapp**.
    - Kliknij prawym przyciskiem myszy na plik → `Run MainApp`.
4. Interfejs pojawi się na ekranie.

### **Uruchamianie w terminalu**
1. Skopiuj projekt na komputer.
2. Przejdź do katalogu projektu:
```shell script
cd ~/Desktop/Projects/java/app/app-to-stear-web/FullMoodProject/SystemObiadowy
```
3. Skompiluj pliki źródłowe:
```shell script
javac -d . src/com/example/mealapp/*.java
```
4. Utwórz plik JAR:
```shell script
jar cfm SystemObiadowy.jar manifest.txt com/example/mealapp/*.class data
```
5. Uruchom plik JAR:
```shell script
java -jar SystemObiadowy.jar
```

---

## **Przykładowe dane wejściowe**
Podczas działania aplikacji można dodawać dane poprzez pole tekstowe i przyciski:

- Dodanie ucznia: wpisz ID karty ucznia lub klasę i numer.
- Dodanie dzieci i nauczycieli: kliknij odpowiednie przyciski, aby dodać ich liczebność.
- Zapis danych: kliknij przycisk „Zapisz”, aby ręcznie zapisać obecną historię obiadów.

---

## **Opis plików wyjściowych**
Podczas działania programu generowane są/aktualizowane następujące pliki:

1. **`sum_obiad.txt`:**
    - Zawiera dzienne podsumowanie obiadów, np.:
```
===== dzień: 18 === miesiąc: 12 === rok: 2024 =====
     Całkowita liczba obiadów: 6
     Liczba obiadów dla dzieci: 6
     Liczba obiadów dla nauczycieli: 0
     Liczba obiadów dla uczniów: 0
     Historia wpisów: 13
```

2. **`obiady.txt`:**
    - Log danych wprowadzanych przez użytkownika.

3. **`students.csv`:**
    - Plik zawierający zapisane dane o studentach (jeśli wymagane w projekcie).

---

## **Automatyczny zapis przy zamykaniu programu**
Program wykorzystuje mechanizm **shutdown hook**, aby automatycznie zapisać dzienne podsumowanie do pliku **`sum_obiad.txt`** podczas zamykania aplikacji (np. poprzez kliknięcie przycisku zamknięcia okna).

---

## **Przykładowy wynik działania**
Po zamknięciu aplikacji dane są zapisywane w pliku `sum_obiad.txt` i mogą wyglądać w następujący sposób:

```
===== dzień: 18 === miesiąc: 12 === rok: 2024 =====
Całkowita liczba obiadów: 6
Liczba obiadów dla dzieci: 6
Liczba obiadów dla nauczycieli: 0
Liczba obiadów dla uczniów: 0
Historia wpisów: 13
```

---

## **Podsumowanie**
- Aplikacja pozwala na zarządzanie danymi dotyczącymi obiadów w łatwy i intuicyjny sposób.
- Pliki tekstowe i CSV umożliwiają trwałe przechowywanie danych.
- Interfejs GUI ułatwia obsługę programu bez potrzeby znajomości kodu źródłowego.

Jeśli masz pytania lub coś nie działa prawidłowo, zachęcamy do kontaktu! 😊

--- 

### **Autor**
**SystemObiadowy** – Stworzony w ramach projektu na temat zarządzania danymi obiadów z użyciem Javy.
