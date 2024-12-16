#include <iostream>
            #include <cstdlib>
            #include <fstream>
            #include <sys/stat.h> // Dla operacji na katalogach (Linux/MacOS)
            #include <direct.h>   // Dla operacji na katalogach (Windows)

            #ifdef _WIN32
            #define mkdir _mkdir // Dostępne tylko na platformie Windows
            #endif

            void createOutputDirectory() {
                const char* dirName = "out";
                struct stat info;

                // Sprawdzenie, czy katalog istnieje:
                if (stat(dirName, &info) != 0) {
                    // Katalog nie istnieje - tworzymy go
                    if (mkdir(dirName) == 0) {
                        std::cout << "Utworzono katalog 'out'.\n";
                    } else {
                        std::cerr << "Nie udało się utworzyć katalogu 'out'.\n";
                    }
                } else if (info.st_mode & S_IFDIR) {
                    // Katalog już istnieje
                    std::cout << "Katalog 'out' już istnieje.\n";
                } else {
                    std::cerr << "Błąd: scieżka 'out' istnieje, ale nie jest katalogiem.\n";
                }
            }

            void generateManifestFile() {
                std::ofstream manifest("manifest.txt");
                if (manifest.is_open()) {
                    manifest << "Manifest-Version: 1.0\n";
                    manifest << "Main-Class: com.example.mealapp.MainApp\n";
                    manifest << "Class-Path: .\n";
                    manifest.close();
                    std::cout << "Plik manifest.txt został wygenerowany.\n";
                } else {
                    std::cerr << "Nie udało się utworzyć pliku manifest.txt!\n";
                }
            }

            void compileJavaFiles(const std::string& javaFilesPath) {
                std::string compileCommand = "javac -d out -sourcepath . " + javaFilesPath + "/*.java";
                std::cout << "Kompilowanie plików Javy...\n";
                if (std::system(compileCommand.c_str()) == 0) {
                    std::cout << "Kompilacja zakończona pomyślnie.\n";
                } else {
                    std::cerr << "Błąd podczas kompilacji plików Javy.\n";
                }
            }

            void createJarFile() {
                std::string jarCommand = "jar cfm system-obiadowy.jar manifest.txt -C out .";
                std::cout << "Tworzenie pliku JAR...\n";
                if (std::system(jarCommand.c_str()) == 0) {
                    std::cout << "Plik JAR został pomyślnie utworzony (system-obiadowy.jar).\n";
                } else {
                    std::cerr << "Błąd podczas tworzenia pliku JAR.\n";
                }
            }

            int main() {
                using namespace std;

                string javaFilesPath = "./src"; // Zmień to na właściwą ścieżkę plików Java

                cout << "Automatyczny proces tworzenia JAR z konwersją na EXE \n";

                // 1. Upewnij się, że istnieją odpowiednie foldery
                createOutputDirectory();

                // 2. Generowanie pliku manifestu
                generateManifestFile();

                // 3. Kompilowanie plików .java
                compileJavaFiles(javaFilesPath);

                // 4. Tworzenie pliku JAR
                createJarFile();

                cout << "Proces zakończony. Sprawdź plik system-obiadowy.jar\n";
                return 0;
            }