package sqlibrary.connection;

import java.sql.*;
import java.util.prefs.Preferences;

public class ConnectionDB {
    private Connection con = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    private static DBSettings dbSettings;

    /**
     * Metodo de configuração automática do Banco de dados
     * Deve ser chamado no método Main
     *
     * @param set Configurações pre-programadas
     */
    public static void setDBSettings(DBSettings set) {
        dbSettings = set;
    }

    public static boolean dbExists(Class<?> x) {
        Preferences prefs = Preferences.userRoot().node(x.getName());
        return prefs.getBoolean("BancoDados", false);
    }

    protected Connection getConnection() {
        if (dbSettings == null) {
            System.out.println("Configurações do Banco de dados não disponível");
            return null;
        }
        try {
            Class.forName(dbSettings.getClassForName());
            switch (dbSettings.getType()) {
                case SQLITE:
                    return DriverManager.getConnection(dbSettings.getURL());
                default:
                    return DriverManager.getConnection(dbSettings.getURL(), dbSettings.getUser(), dbSettings.getPassword());
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
            return null;
        }
    }

    /*
     * Execute Query retorna um ResultSet
     * Ideal para SQL tipo Select
     * */
    protected void selectSQL(String pSQL) {
        try {
            con = getConnection();
            statement = con.createStatement();
            resultSet = statement.executeQuery(pSQL);
        } catch (SQLException e) {
            System.out.println(e.getCause());
            System.out.println(pSQL);
        }
    }

    protected boolean executarUpdateDeleteSQL(String pSQL) {
        try {
            con = getConnection();
            statement = con.createStatement();
            statement.executeUpdate(pSQL);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(pSQL);
            return false;
        } finally {
            close();
        }
    }

    protected int insertSQL(String pSQL) {
        try {
            con = getConnection();
            statement = con.createStatement();
            return statement.executeUpdate(pSQL);//Select id from TABLENAME ORDER BY ID DESC LIMIT 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(pSQL);
            return 0;
        } finally {
            close();
        }
    }

    protected ResultSet getResultSet() {
        return resultSet;
    }

    // You need to close the resultSet
    protected void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
