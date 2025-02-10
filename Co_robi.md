# MealApp

**Autor:** Adam G  

MealApp to aplikacja do zarządzania wydawaniem obiadów w placówkach edukacyjnych. Program umożliwia dodawanie uczniów, nauczycieli i dzieci do systemu, zapisuje informacje o wydanych posiłkach oraz generuje raporty.  

---

## 📌 Instalacja i uruchomienie  

### 1. Wymagania  
- **Java 8+** (zalecane najnowsze JDK)  
- **System operacyjny:** Windows / macOS / Linux  
- **Pliki CSV** z listą uczniów  

### 2. Kompilacja i uruchomienie  
- Skopiuj kod źródłowy do projektu Java.  
- Upewnij się, że katalog `data/` istnieje i zawiera wymagane pliki (`students.csv`).  
- Skorzystaj z terminala lub IDE, aby skompilować i uruchomić aplikację:  

```sh
javac -d bin src/com/example/mealapp/MainApp.java
java -cp bin com.example.mealapp.MainApp
```

---

## 🎯 Funkcjonalności  

### 📌 Dodawanie uczniów i nauczycieli  
- Wprowadź **ID karty**, **klasę i numer ucznia** lub **liczbę nauczycieli/dzieci**.  
- Obsługiwane formaty:  
  - `1234567890` – ID karty ucznia  
  - `5A5` – klasa + numer w dzienniku  
  - `10N` – 10 nauczycieli  
  - `20` – 20 dzieci  

### 📌 Zapisywanie danych  
Dane są zapisywane w plikach w folderze `data/`:  

| Plik | Opis |
|------|------|
| `students.csv` | Lista uczniów |
| `obiady.txt` | Log wydawania obiadów |
| `raport.txt` | Log błędnych wpisów |
| `data.txt` | Szczegółowe logi |
| `Obiady_Suma.txt` | Podsumowanie dzienne |
| `sum_obiad.txt` | Historia posiłków |

### 📌 Generowanie raportów  
Po zamknięciu programu automatycznie zapisywane jest podsumowanie w `sum_obiad.txt`.  

---

## ℹ️ Jak używać  

1. **Uruchom aplikację** – pojawi się interfejs z tabelą.  
2. **Wprowadź dane** w polu tekstowym:  
   - Jeśli ID istnieje w bazie → pojawi się w tabeli.  
   - Jeśli ID nie jest znane → zostanie zapisane do `raport.txt`.  
3. **Kliknij "Dodaj"**, aby zatwierdzić wpis.  
4. **Kliknij "Usuń"**, aby usunąć zaznaczone wiersze.  
5. **Kliknij "Obiady"**, aby zapisać raport końcowy.  

---

## 🎨 Zapis danych do plików  

| Plik | Opis |
|------|------|
| `data/students.csv` | Lista uczniów (Imię, Nazwisko, Klasa, Nr, ID Karty) |
| `data/obiady.txt` | Historia wydawania posiłków |
| `data/raport.txt` | Log błędnych wpisów (brak ID w bazie) |
| `data/data.txt` | Log szczegółowy z datami operacji |
| `data/Obiady_Suma.txt` | Podsumowanie dnia (liczba posiłków) |
| `data/sum_obiad.txt` | Historia wszystkich dni |

---

## 🎨 Dodatkowe informacje  

### 📌 Kolorowanie tabeli  
- 🟩 **Zielony** – pierwszy raz dodany uczeń  
- 🟥 **Czerwony** – duplikat wpisu  
- 🟦 **Niebieski** – dziecko  
- ⬜ **Szary** – nauczyciel  

### 📌 Automatyczne zamykanie  
- Przy zamknięciu aplikacji zapisuje dane do `sum_obiad.txt`.  

---

## ✅ Przykłady wpisów  

**Dodanie ucznia po ID karty**  
```txt
1234567890
```
Aplikacja doda ucznia o tym ID do tabeli.  

**Dodanie ucznia po klasie i numerze**  
```txt
5A5
```
Uczeń z klasy **5A**, numer **5** zostanie dodany.  

**Dodanie nauczycieli**  
```txt
5N
```
Doda **5 nauczycieli** do listy.  

**Dodanie dzieci**  
```txt
10
```
Doda **10 dzieci** do listy.  

---

## 🔴 Zamykanie programu  
- Przed zamknięciem aplikacja **automatycznie zapisuje stan** do `sum_obiad.txt`.  
- Możesz ręcznie zapisać stan klikając **"Obiady"**.  

---

## 👨‍💻 Autor  
Adam G  
