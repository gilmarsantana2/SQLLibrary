package sqlibrary.connection;

import sqlibrary.util.Store;

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

    public static boolean autoCreate(Class<?> wrapper, FileInputStream sql){
        DBSettings dbSettings = settingsByTerminal();
        Store.createFile(dbSettings);
        createBancoPadrao(dbSettings, wrapper, sql);
        return ConnectionDB.dbExists(wrapper);
    }

    public static void createBancoPadrao(DBSettings settings, Class<?> wrapper, FileInputStream inputFile) {
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
        Preferences prefs = Preferences.userNodeForPackage(wrapper);
        prefs.putBoolean("BancoDados", true);
    }

    public static DBSettings settingsByTerminal() {
        Scanner scan = new Scanner(System.in);
        Integer tipoBanco;
        String host, user, password , name, correto;
        Integer porta = 0;
        DBType type;

        System.out.print(WHITE);
        System.out.println("Configuração Inicial!");

        while (true) {
            user = "default";
            password = null;
            System.out.print(WHITE);
            System.out.println("-------------------------------------------------");
            System.out.println("Tipo de banco há ser configurado.");
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

            if (type != DBType.SQLITE) {

                if (type != DBType.HSQLDB){
                    System.out.print(WHITE);
                    System.out.println("-------------------------------------------------");
                    System.out.println("Digite a porta correspondente ao banco de dados.");
                    System.out.print("Porta: ");
                    while (true) {
                        try {
                            System.out.print(GREEN);
                            porta = Integer.parseInt(scan.next());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.print(RED);
                            System.out.println("Entrada inválida!");
                            System.out.println("Digite novamente a porta correspondente ao Banco escolhido.");
                            System.out.print("Porta: ");
                            System.out.print(GREEN);
                        }
                    }
                }

                System.out.print(WHITE);
                System.out.println("-------------------------------------------------");
                System.out.println("Digite o usuario para acesso no banco.");
                System.out.print("Usuário: ");
                System.out.print(GREEN);
                user = scan.next();


                System.out.print(WHITE);
                System.out.println("-------------------------------------------------");
                System.out.println("Digite a senha para acesso ao banco.");
                System.out.println("Sua senha será Criptografada");
                System.out.print("Senha: ");
                System.out.print(GREEN);
                password = scan.next();
            }

            System.out.print(WHITE);
            System.out.println("-------------------------------------------------");
            System.out.println("Digite o nome para o banco de dados. ");
            System.out.print("Nome do banco: ");
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
            System.out.print("Digite 'S' para Sim ou 'N' para Não. ");
            System.out.print(GREEN);
            while (true) {
                correto = scan.next();
                if (correto.equals("S") || correto.equals("s") || correto.equals("N") || correto.equals("n")) {
                    break;
                } else {
                    System.out.print(RED);
                    System.out.println("Entrada inválida!");
                    System.out.print("Digite 'S' para Sim ou 'N' para Não. ");
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
        if (senha == null) return "default";

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
