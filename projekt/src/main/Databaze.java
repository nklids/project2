package main;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class Databaze {
    private static final List<Student> studenti = new ArrayList<>();
    private static int idCounter = 1;
    private static final Scanner sc = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:studenti.db";

    public static void main(String[] args) {
        initDatabase();
        loadFromDatabase();

        while (true) {
            System.out.println("\n1) Přidat studenta\n2) Přidat známku\n3) Smazat studenta\n4) Najít studenta");
            System.out.println("5) Spustit dovednost\n6) Výpis abecedně\n7) Výpis Průměru studentu\n8) Výpis Počtu studentu");
            System.out.println("9) Uložit do souboru\n10) Načíst ze souboru\n11) Konec");
            int volba = Integer.parseInt(sc.nextLine());

            switch (volba) {
                case 1 -> {
                    System.out.print("Jméno: ");
                    String jmeno = sc.nextLine();
                    System.out.print("Příjmení: ");
                    String prijmeni = sc.nextLine();
                    System.out.print("Rok narození: ");
                    int rok = Integer.parseInt(sc.nextLine());
                    System.out.print("Skupina (1=IT, 2=Kyber): ");
                    int obor = Integer.parseInt(sc.nextLine());

                    Student student = (obor == 1)
                            ? new ITStudent(idCounter++, jmeno, prijmeni, rok)
                            : new KyberStudent(idCounter++, jmeno, prijmeni, rok);
                    studenti.add(student);
                    System.out.println("Student přidán.");
                }
                case 2 -> {
                    Student student = najdiStudenta();
                    if (student != null) {
                        System.out.print("Zadej známku (1–5): ");
                        student.pridejZnamku(Integer.parseInt(sc.nextLine()));
                    }
                }
                case 3 -> {
                    Student student = najdiStudenta();
                    if (student != null) studenti.remove(student);
                }
                case 4 -> {
                    Student student = najdiStudenta();
                    System.out.println(student != null ? student : "Nenalezen.");
                }
                case 5 -> {
                    Student student = najdiStudenta();
                    if (student != null) student.spustDovednost();
                }
                case 6 -> studenti.stream()
                        .sorted(Comparator.comparing(Student::getPrijmeni))
                        .forEach(System.out::println);
                case 7 -> {
                    double prumIT = studenti.stream().filter(student -> student instanceof ITStudent)
                            .mapToDouble(Student::getStudijniPrumer).average().orElse(0.0);
                    double prumKyb = studenti.stream().filter(student -> student instanceof KyberStudent)
                            .mapToDouble(Student::getStudijniPrumer).average().orElse(0.0);
                    System.out.printf("IT průměr: %.2f, Kyber průměr: %.2f%n", prumIT, prumKyb);
                }
                case 8 -> {
                    long it = studenti.stream().filter(student -> student instanceof ITStudent).count();
                    long kyb = studenti.stream().filter(student -> student instanceof KyberStudent).count();
                    System.out.printf("IT: %d, Kyber: %d%n", it, kyb);
                }
                case 9 -> {
                    Student student = najdiStudenta();
                    if (student == null) break;
                    try (PrintWriter pw = new PrintWriter("student_" + student.getId() + ".txt")) {
                        pw.println(student.getClass().getSimpleName());
                        pw.println(student.getId());
                        pw.println(student.getJmeno());
                        pw.println(student.getPrijmeni());
                        pw.println(student.getRokNarozeni());
                        pw.println(student.getZnamky().stream().map(String::valueOf).collect(Collectors.joining(",")));
                        System.out.println("Uloženo.");
                    } catch (IOException e) {
                        System.out.println("Chyba: " + e.getMessage());
                    }
                }
                case 10 -> {
                    System.out.print("Zadej ID souboru: ");
                    int id = Integer.parseInt(sc.nextLine());
                    try (BufferedReader br = new BufferedReader(new FileReader("student_" + id + ".txt"))) {
                        String typ = br.readLine();
                        int sid = Integer.parseInt(br.readLine());
                        String jmeno = br.readLine();
                        String prijmeni = br.readLine();
                        int rok = Integer.parseInt(br.readLine());
                        String znamkyStr = br.readLine();

                        Student student = typ.equals("ITStudent")
                                ? new ITStudent(sid, jmeno, prijmeni, rok)
                                : new KyberStudent(sid, jmeno, prijmeni, rok);

                        if (!znamkyStr.isEmpty()) {
                            for (String z : znamkyStr.split(",")) {
                                student.pridejZnamku(Integer.parseInt(z));
                            }
                        }
                        studenti.add(student);
                        idCounter = Math.max(idCounter, sid + 1);
                        System.out.println("Načteno.");
                    } catch (IOException e) {
                        System.out.println("Chyba: " + e.getMessage());
                    }
                }
                case 11 -> {
                    saveToDatabase();
                    System.out.println("Uloženo do databáze.");
                    return;
                }
            }
        }
    }

    private static Student najdiStudenta() {
        System.out.print("ID: ");
        int id = Integer.parseInt(sc.nextLine());
        return studenti.stream().filter(student -> student.getId() == id).findFirst().orElse(null);
    }

    private static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS studenti (
                id INTEGER PRIMARY KEY,
                jmeno TEXT,
                prijmeni TEXT,
                rok_narozeni INTEGER,
                obor TEXT,
                znamky TEXT)
            """);
        } catch (SQLException e) {
            System.out.println("DB chyba při inicializaci: " + e.getMessage());
        }
    }

    private static void loadFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM studenti")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String jmeno = rs.getString("jmeno");
                String prijmeni = rs.getString("prijmeni");
                int rok = rs.getInt("rok_narozeni");
                String obor = rs.getString("obor");
                String znamkyStr = rs.getString("znamky");

                Student student = obor.equals("ITStudent")
                        ? new ITStudent(id, jmeno, prijmeni, rok)
                        : new KyberStudent(id, jmeno, prijmeni, rok);

                if (znamkyStr != null && !znamkyStr.isEmpty()) {
                    for (String z : znamkyStr.split(",")) {
                        student.pridejZnamku(Integer.parseInt(z));
                    }
                }
                studenti.add(student);
                idCounter = Math.max(idCounter, id + 1);
            }

        } catch (SQLException e) {
            System.out.println("Chyba při načítání DB: " + e.getMessage());
        }
    }

    private static void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute("DELETE FROM studenti");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO studenti (id, jmeno, prijmeni, rok_narozeni, obor, znamky)
                VALUES (?, ?, ?, ?, ?, ?)
            """);

            for (Student student : studenti) {
                ps.setInt(1, student.getId());
                ps.setString(2, student.getJmeno());
                ps.setString(3, student.getPrijmeni());
                ps.setInt(4, student.getRokNarozeni());
                ps.setString(5, student.getClass().getSimpleName());
                ps.setString(6, student.getZnamky().stream().map(String::valueOf).collect(Collectors.joining(",")));
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Chyba při ukládání DB: " + e.getMessage());
        }
    }
}
