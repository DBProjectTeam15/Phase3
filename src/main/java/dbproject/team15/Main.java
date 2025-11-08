package dbproject.team15;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {

        Class.forName("oracle.jdbc.driver.OracleDriver");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}