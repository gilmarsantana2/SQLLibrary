package sqlibrary.connection;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.prefs.Preferences;


public class CriarBancoAuto {

    private static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors

    private static final String RED = "\033[0;31m";     // RED
    private static final String GREEN = "\033[0;32m";   // GREEN
    private static final String WHITE = "\033[0;97m";   // WHITE
    public static final String CYAN = "\033[0;36m";    // CYAN

    private final String sql = "CREATE TABLE user (\n" +
            "id int not null primary key identity,\n" +
            "nome varchar(100) not null,\n" +
            "password varchar(256) not null,\n" +
            "user_image longvarchar,\n" +
            "type varchar(10) default 'comum');";

    private String[] stataments;

    private boolean executeCreation() {
        stataments = sql.split(";|;\\s");
        Statement currentStatement = null;

        ConnectionDB con = new ConnectionDB();

        for (String comand : stataments) {
            try {
                // Execute statement
                currentStatement = con.getConnection().createStatement();
                currentStatement.execute(comand.concat(";"));

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                // Release resources
                if (currentStatement != null) {
                    try {
                        con.getConnection().close();
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        return true;
    }

    public static void createBancoPadrao(DBSettings settings, Class<?> x, FileInputStream inputFile) {
        ConnectionDB.setDBSettings(settings);
        // Delimiter
        String delimiter = ";";
        Scanner scanner = new Scanner(inputFile).useDelimiter(delimiter);

        ConnectionDB con = new ConnectionDB();
        // Loop through the SQL file statements
        Statement currentStatement = null;
        while (scanner.hasNext()) {
            // Get statement
            String rawStatement = scanner.next() + delimiter;
            System.out.println(rawStatement);
            try {
                // Execute statement
                currentStatement = con.getConnection().createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            } finally {
                // Release resources
                if (currentStatement != null) {
                    try {
                        con.close();
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        //fim do loop

        //Ativa o banco de dados padrao
        Preferences prefs = Preferences.userNodeForPackage(x);
        prefs.put("BancoDados", "true");
    }

    public static DBSettings settingsByTerminal() {
        Scanner scan = new Scanner(System.in);
        Integer tipoBanco;
        String host, user = null, password = null, name, correto;
        Integer porta = 0;
        DBType type;
        DBSettings banco;

        System.out.print(WHITE);
        System.out.println("Configuração Inicial!");

        while (true) {
            System.out.print(WHITE);
            System.out.println("-------------------------------------------------");
            System.out.println("Tipo de banco há ser configurado");
            System.out.println("1 - Default");
            System.out.println("2 - MySQL");
            System.out.println("3 - Maria DB");
            System.out.println("4 - Hsqldb");
            System.out.print("Digite o número correspondente ao Banco escolhido: ");
            System.out.print(GREEN);

            while (true) {
                try {
                    tipoBanco = Integer.parseInt(scan.next());
                    if (tipoBanco <= 0 || tipoBanco > 4) {
                        System.out.print(RED);
                        System.out.println("Entrada inválida!");
                        System.out.print("Digite novamente o número correspondente ao Banco escolhido: ");
                        System.out.print(GREEN);
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.print(RED);
                    System.out.println("Entrada inválida!");
                    System.out.print("Digite novamente o número correspondente ao Banco escolhido: ");
                    System.out.print(GREEN);
                }
            }
            type = switch (tipoBanco) {
                case 1 -> DBType.SQLITE;
                case 2 -> DBType.MYSQL;
                case 3 -> DBType.MARIA_DB;
                case 4 -> DBType.HSQLDB;
                default -> DBType.SQLITE;
            };

            System.out.print(WHITE);
            System.out.println("-------------------------------------------------");
            System.out.println("Digite o \"HOST\", pasta ou endereço de IP do banco de dados:");
            System.out.println("Exemplo: localhost, 192.168.0.1 ou C://Banco");
            System.out.print("Host: ");
            System.out.print(GREEN);
            host = scan.next();


            if (type == DBType.MYSQL || type == DBType.MARIA_DB) {
                System.out.print(WHITE);
                System.out.println("-------------------------------------------------");
                System.out.print("Digite a porta correspondente ao banco de dados: ");
                while (true) {
                    try {
                        System.out.print(GREEN);
                        porta = Integer.parseInt(scan.next());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.print(RED);
                        System.out.println("Entrada inválida!");
                        System.out.print("Digite novamente a porta correspondente ao Banco escolhido: ");
                        System.out.print(GREEN);
                    }
                }

                System.out.print(WHITE);
                System.out.println("-------------------------------------------------");
                System.out.print("Digite o usuario para acesso no banco: ");
                System.out.print(GREEN);
                user = scan.next();


                System.out.print(WHITE);
                System.out.println("-------------------------------------------------");
                System.out.println("Digite a senha para acesso ao banco");
                System.out.print("Sua senha será Criptografada: ");
                System.out.print(GREEN);
                password = scan.next();
            }

            System.out.print(WHITE);
            System.out.println("-------------------------------------------------");
            System.out.print("Digite o nome para o banco de dados: ");
            System.out.print(GREEN);
            name = scan.next();


            System.out.print(WHITE);
            System.out.println("Configuração Concluida!");
            System.out.println("-------------------------------------------------");
            System.out.println("Configurações{" +
                    "type= '" + CYAN +  type.name() + WHITE + '\'' +
                    ", host= '" + CYAN + host + WHITE + '\'' +
                    ", porta= '" + CYAN + porta + WHITE + '\'' +
                    ", dataBase= '" + CYAN + name + WHITE + '\'' +
                    ", user= '" + CYAN + user + WHITE + '\'' +
                    ", password= '" + CYAN + password + WHITE + '\'' +
                    '}');

            System.out.println("-------------------------------------------------");
            System.out.println("Verifique se as informações acima estão corretas.");
            System.out.print("Digite 'S' para sim ou 'N' para Não. ");
            System.out.print(GREEN);
            while (true) {
                correto = scan.next();
                if (correto.equals("S") || correto.equals("s") || correto.equals("N") || correto.equals("n")) {
                    break;
                } else {
                    System.out.print(RED);
                    System.out.println("Entrada inválida!");
                    System.out.print("Digite 'S' para sim ou 'N' para Não. ");
                    System.out.print(GREEN);
                }
            }

            //final do Scanner
            if (correto.equals("S") || correto.equals("s")) break;
        }
        System.out.print(RESET);
        return new DBSettings(type, host, porta, name, user, criptoHash(password));
    }


    private static String criptoHash(String senha) {
        if (senha == null) return null;

        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigestSenhaAdmin[] = algorithm.digest(senha.getBytes("UTF-8"));

            StringBuilder hexSenha = new StringBuilder();
            for (byte b : messageDigestSenhaAdmin) {
                hexSenha.append(String.format("%02X", 0xFF & b));
            }
            return hexSenha.toString();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return senha;
        }
    }

}
