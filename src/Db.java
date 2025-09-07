import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "127.0.0.1");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String NAME = System.getenv().getOrDefault("DB_NAME", "emotionalbook");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "emo");
    private static final String PASS = System.getenv().getOrDefault("DB_PASSWORD", "emo_pwd");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[Db] Driver MySQL introuvable: " + e.getMessage());
        }
    }

    public static Connection connect() throws SQLException {
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false",
                HOST, PORT, NAME
        );
        return DriverManager.getConnection(url, USER, PASS);
    }
}

