package main;

import java.security.MessageDigest;

public class KyberStudent extends Student {

    public KyberStudent(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public void spustDovednost() {
        System.out.println("Hash: " + hash(jmeno + prijmeni));
    }

    private String hash(String vstup) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(vstup.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "Hashování selhalo";
        }
    }
}
