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
   git clone <https://github.com/AdamGolik/SystemObiadowy>
   cd <SystemObiadowy>
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
## **Instrukcja kompilacji i uruchamiania Javy na Windows**
### Krok 1: Instalacja JDK
1. Pobierz **Java Development Kit (JDK)** z [oficjalnej strony Oracle]() lub z [OpenJDK]().
2. Zainstaluj JDK, upewniając się, że dodano narzędzia (`javac`, `java`, `jar`) do zmiennej środowiskowej **PATH**.
3. Zweryfikuj instalację, otwierając wiersz polecenia (`cmd`) i wpisując następujące komendy:
``` cmd
   java -version
   javac -version
   jar --version
```
Jeżeli wersje są poprawnie wyświetlone, środowisko zostało poprawnie skonfigurowane.
### Krok 2: Kompilacja programu Java
1. Załóżmy, że Twoje pliki `.java` znajdują się w katalogu `src`. W naszym przypadku struktura projektu wygląda tak:
``` 
SystemObiadowy/
├── src/
│   ├── MainApp.java        (klasa główna)
│   └── Student.java        (pomocnicza klasa obsługująca informacje o studentach)
└── JavaToExeConverter.cpp  (program automatyzacji)
```
1. Otwórz **cmd** (wiersz polecenia), przejdź do folderu `SystemObiadowy`:
``` cmd
   cd C:\Users\<Twoja_Nazwa_Użytkownika>\Desktop\Projects\SystemObiadowy
```
1. Skorzystaj z następującego polecenia, aby skompilować wszystkie pliki `.java` znajdujące się w katalogu `src`:
``` cmd
   javac -d out -sourcepath src src\*.java
```
**Wyjaśnienie parametrów:**
- `-d out`: Tworzy katalog `out` i umieszcza tam pliki `.class`.
- `-sourcepath src`: Określa lokalizację plików `.java`.
- `src\*.java`: Kompiluje wszystkie pliki `.java` w katalogu `src`.

1. Po wykonaniu tego polecenia, w katalogu `SystemObiadowy` zostanie utworzony folder `out`, a w nim znajdą się skompilowane pliki `.class`.

### Krok 3: Tworzenie pliku JAR
Po skompilowaniu plików `.java` do `.class` utwórz plik `.jar` za pomocą narzędzia **jar**:
1. Wygeneruj **plik manifestu** (jeśli chcesz stworzyć plik ręcznie, wykonaj poniższe kroki, jednak program C++ generuje go automatycznie):
    - Utwórz plik tekstowy `manifest.txt` o następującej treści:
``` 
     Manifest-Version: 1.0
     Main-Class: com.example.mealapp.MainApp
     Class-Path: .
```
1. Użyj następującego polecenia, aby stworzyć plik `.jar`:
``` cmd
   jar cfm system-obiadowy.jar manifest.txt -C out .
```
**Wyjaśnienie parametrów:**
- `c`: Tworzenie nowego pliku `.jar`.
- `f`: Określenie nazwy wynikowego pliku JAR.
- `m`: Dołączenie pliku manifestu.
- `-C out .`: Dołączenie wszystkich plików `.class` z katalogu `out`.

1. Po wykonaniu tego polecenia powstanie plik `system-obiadowy.jar`.

### Krok 4: Uruchamianie programu `.jar`
Aby uruchomić plik `.jar`, wykonaj następujące polecenie w wierszu polecenia:
``` cmd
java -jar system-obiadowy.jar
```
Program JAR uruchomi się w konsoli i pozwoli na interakcję z aplikacją.
## Przykład z wiersza polecenia
Zakładając, że Twoje pliki znajdują się w `C:\Projects\SystemObiadowy`:
``` cmd
C:\> cd C:\Projects\SystemObiadowy
C:\Projects\SystemObiadowy> javac -d out -sourcepath src src\*.java
C:\Projects\SystemObiadowy> jar cfm system-obiadowy.jar manifest.txt -C out .
C:\Projects\SystemObiadowy> java -jar system-obiadowy.jar
```
Rezultatem powinna być działająca aplikacja konsolowa.
### Tip: Tworzenie skryptu `.bat` do automatyzacji
Aby ułatwić cały proces, możesz utworzyć plik `.bat`, który automatycznie przeprowadzi wszystko:
1. Utwórz plik o nazwie `build_and_run.bat`:
``` bat
   @echo off
   echo Kompilacja kodu Java...
   javac -d out -sourcepath src src\*.java

   echo Tworzenie pliku JAR...
   jar cfm system-obiadowy.jar manifest.txt -C out .

   echo Uruchamianie programu...
   java -jar system-obiadowy.jar
   pause
```
1. Kliknij dwukrotnie plik `build_and_run.bat`, aby zautomatyzować cały proces kompilacji, tworzenia JAR i uruchomienia aplikacji.

## Rozwiązywanie problemów na Windows
1. **Problem: `javac` lub `java` nie znaleziono.**
    - Upewnij się, że ścieżka do JDK (np. `C:\Program Files\Java\jdk-XX\bin`) znajduje się w zmiennej środowiskowej **PATH**.

2. **Problem: `Nieznany manifest: Main class not found` w momencie uruchamiania JAR.**
    - Upewnij się, że plik `manifest.txt` zawiera poprawną nazwę klasy głównej (`Main-Class: com.example.mealapp.MainApp`).
    - Sprawdź, czy classpath jest ustawiony na `.` w manifest.txt (`Class-Path: .`).

3. **Problem: Brak narzędzia `jar`.**
    - Upewnij się, że używasz pełnej wersji JDK (Java Development Kit), a nie tylko JRE (Java Runtime Environment).

4. **Problem: Błędy kompilacji.**
    - Upewnij się, że pliki .java są w poprawnym formacie, a katalog `src` istnieje.
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
