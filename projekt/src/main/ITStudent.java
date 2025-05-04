package main;

public class ITStudent extends Student {

    public ITStudent(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public void spustDovednost() {
        System.out.println("Jméno a Příjmení v morseové abecedě: " + doMorse(jmeno + " " + prijmeni));
    }

    private String doMorse(String vstup) {
        String[] morse = {
            ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
            "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-",
            "..-", "...-", ".--", "-..-", "-.--", "--.."
        };
        vstup = vstup.toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (char c : vstup.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(morse[c - 'A']).append(" ");
            } else if (c == ' ') {
                sb.append(" / ");
            }
        }
        return sb.toString();
    }
}
