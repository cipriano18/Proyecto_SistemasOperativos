package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexion con la base de datos.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://172.16.9.42:3306/auditorium";

    private static final String USER = "appuser";

    private static final String PASSWORD = "SistemasOperativos26!";

    /**
     * Obtiene una conexion con la base de datos.
     *
     * @return conexion activa
     *
     * @throws SQLException si no es posible conectar con la base de datos
     */
    public static Connection getConnection() throws SQLException {

        try {

            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (SQLException e) {

            throw new SQLException(
                    "No fue posible conectar a la base de datos en "
                    + URL
                    + " con el usuario "
                    + USER
                    + ". "
                    + e.getMessage(),
                    e
            );
        }
    }
}