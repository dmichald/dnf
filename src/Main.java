import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        DNF dnf = new DNF();
        try {
            dnf.initializeDataArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dnf.solve();
    }
}
