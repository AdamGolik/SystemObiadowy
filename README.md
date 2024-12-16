# Java to JAR and EXE Converter - System Obiadowy
## Opis projektu
Ten projekt jest narzędziem automatyzującym proces tworzenia pliku `.jar` z kodu napisanego w Javie. Finalnie możesz również przekształcić plik JAR w `.exe` (jeśli użyjesz zewnętrznego narzędzia, jak **Launch4j**). W projekcie zawarto przykładową aplikację w Javie o nazwie **System Obiadowy**, która jest prostą aplikacją kontekstową umożliwiającą przechowywanie i obsługę danych studentów pod kątem ich zamówień obiadowych.
## Jak działa system
Aplikacja **System Obiadowy** to przykład użycia Java będący prostą aplikacją konsolową, która demonstruje podstawowe funkcjonalności w odniesieniu do danych studentów. Działa w następujący sposób:
1. **Główna klasa: `MainApp`**
    - Jest to punkt wejścia do systemu i uruchamia główną logikę programu.

2. **Klasa `Student`**
    - Przechowuje szczegóły dotyczące studenta, takie jak:
        - Nazwa (imię i nazwisko studenta).
        - Numer ID studenta.
        - Informacje o zamówionych obiadach.

3. **Funkcjonalności systemu:**
    - Wczytywanie danych o studentach.
    - Wyświetlanie listy studentów i ich zamówień.
    - Obsługa prostych operacji na danych studentów, np. dodanie nowego studenta.

4. **Struktura danych:**
    - Aplikacja może przechowywać studentów w kolekcji list lub map, co pozwala na ich łatwe wyszukiwanie i modyfikację.

Ten projekt demonstruje prostą logikę biznesową i architekturę, jaką można wykorzystać w przypadku bardziej rozbudowanych systemów.
## Wymagania
### System:
- **Windows**, **Linux**, lub **macOS**

### Narzędzia:
- Kompilator C++: **GCC (`g++`)**.
- JDK (Java Development Kit):
    - `javac` (do kompilacji plików `.java`).
    - `jar` (do tworzenia plików `.jar`).

- Zewnętrzne narzędzia (opcjonalne): **Launch4j** (jeśli chcesz skonwertować `JAR` na `EXE`).

## Instalacja
1. **Pobierz projekt:** Sklonuj repozytorium i przejdź do katalogu projektu:
``` bash
   git clone <url-repozytorium>
   cd <katalog-repozytorium>
```
1. **Upewnij się, że posiadasz wymagane narzędzia:**
    - Kompilator C++: `g++` (GCC) lub `MinGW` w systemie Windows.
    - JDK (Java Development Kit) z narzędziami `javac` oraz `jar`.

2. **Przygotowanie środowiska:**
    - Upewnij się, że zmienne PATH zawierają ścieżki do `javac` i `jar`.
    - Sprawdź instalacje:
``` bash
     java -version
     javac -version
     jar --version
```
## Jak uruchomić projekt
### Krok 1: Kompilacja kodu w C++
W katalogu głównym projektu znajdziesz plik `JavaToExeConverter.cpp`. Skopiuj i skompiluj go za pomocą `g++`:
#### Kompilacja:
- Na macOS/Linux:
``` bash
  g++ -o JavaToExeConverter JavaToExeConverter.cpp
```
- Na Windows:
``` cmd
  g++ -o JavaToExeConverter.exe JavaToExeConverter.cpp
```
### Krok 2: Uruchomienie programu
Po skompilowaniu uruchom plik wykonywalny:
- Na macOS/Linux:
``` bash
  ./JavaToExeConverter
```
- Na Windows:
``` cmd
  JavaToExeConverter.exe
```
Podczas uruchamiania program wykona następujące kroki:
1. Utworzy katalog `out`, jeśli nie istnieje.
2. Wygeneruje plik `manifest.txt`, który wskaże klasę główną (`MainApp`).
3. Skompiluje pliki `.java` w katalogu `src` do plików `.class` umieszczonych w katalogu `out`.
4. Utworzy plik `system-obiadowy.jar` na podstawie przygotowanych plików.

Po zakończeniu procesu plik JAR zostanie przygotowany w katalogu głównym.
## Konwersja JAR na EXE (opcjonalnie)
Jeżeli chcesz skonwertować plik `.jar` do `.exe`, możesz użyć narzędzia **Launch4j**:
1. Pobierz narzędzie Launch4j: [https://launch4j.sourceforge.net/]().
2. Otwórz Launch4j i wskaż następujące parametry:
    - **Input Jar file:** Ścieżka do wygenerowanego pliku `system-obiadowy.jar`.
    - **Output exe file:** Ścieżka do pliku wynikowego `.exe`.
    - **Main class:** `com.example.mealapp.MainApp`.

3. Wygeneruj plik `.exe`.

## Struktura projektu
``` 
.
├── src/
│   ├── MainApp.java        (główna klasa uruchomieniowa aplikacji)
│   ├── Student.java        (przechowuje dane studenta)
├── JavaToExeConverter.cpp  (program automatyzujący proces budowania aplikacji Java)
├── manifest.txt            (automatycznie generowany plik manifestu)
├── out/                    (tworzony automatycznie folder zawierający pliki .class)
└── system-obiadowy.jar     (wynikowy plik JAR aplikacji)
```
## Przykładowe użycie systemu obiadowego
Po utworzeniu i uruchomieniu aplikacji (np. poprzez `java -jar system-obiadowy.jar` w terminalu), aplikacja wykona następujące kroki:
1. Wyświetli listę dostępnych operacji.
2. Pozwoli na dodawanie studentów oraz ich zamówień obiadowych.
3. Wyświetli listę studentów oraz przypisanych zamówień, np.:
``` 
   Student: Jan Kowalski (ID: 1)
   Zamówienia obiadowe: Zupa pomidorowa, Kotlet schabowy
```
## Rozwiązywanie problemów
- **Błąd: `program javac nie zostal znaleziony`** Upewnij się, że JDK jest zainstalowane i narzędzia Java (`javac`, `jar`) są dostępne w zmiennej PATH.
- **Błąd: `nie znaleziono katalogu src`** Sprawdź, czy Twoje pliki Javy znajdują się w katalogu `src`.
- **Błąd: `kompilacja programu C++ nie powiodła się`** Upewnij się, że kompilator `g++` działa prawidłowo i że uruchamiasz polecenia z właściwego katalogu.

## Autorzy
Projekt przygotowany jako przykład do automatyzacji budowy aplikacji Java oraz obsługi prostych systemów w Javie.
Jeśli masz pytania lub sugestie, skontaktuj się z Adam Golik.
