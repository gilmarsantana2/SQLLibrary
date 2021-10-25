package sqlibrary.connection;

/**
 * Classe de Configuração automática do banco de dados
 */
public class DBSettings {

    private String schema, host, dataBase, user, password, classForName;
    private Integer porta;
    private DBType type;

    /**
     * Classe para configuração do banco de dados
     * @param type Tipo do Banco.
     *             SQLITE, MYSQL, MARIA_DB, HSQLDB
     * @param host Endereço do banco
     * @param dataBase Nome do banco de dados
     * @param user Usuário para logar no banco
     * @param password Senha do usuario
     */
    public DBSettings(DBType type, String host, Integer porta, String dataBase, String user, String password) {
        switch (type){
            case SQLITE:
                this.schema = "jdbc:sqlite:";
                this.classForName = "org.sqlite.JDBC";
                break;
            case MYSQL:
                this.schema = "jdbc:mysql://";
                this.classForName = "com.mysql.jdbc.Driver";
                break;
            case MARIA_DB:
                this.schema = "jdbc:mariadb://";
                this.classForName = "org.mariadb.jdbc.Driver";
                break;
            case HSQLDB:
                this.schema = "jdbc:hsqldb:file:";
                this.classForName = "org.hsqldb.jdbc.JDBCDriver";
                break;
        }
        this.type = type;
        this.host = host;
        this.porta = porta;
        this.dataBase = dataBase;
        this.user = user;
        this.password = password;
    }

    public DBSettings(DBType type, String host, String dataBase) {
        this(type, host , 0, dataBase, null, null);
    }

    public String getURL(){
        return switch (this.type){
            case SQLITE, HSQLDB -> schema + host + "/" + dataBase;
            case MYSQL, MARIA_DB -> schema + host + ":" + porta + "/" + dataBase;
        };
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DBType getType() {
        return type;
    }

    public String getClassForName() {
        return classForName;
    }

    public String getHost() {
        return host;
    }

    public String getDataBase() {
        return dataBase;
    }

    public Integer getPorta() {
        return porta;
    }

    @Override
    public String toString() {
        return "DBSettings{" +
                "type= '" + type.name() +'\'' +
                ", host= '" + host + '\'' +
                ", porta= '" + porta + '\'' +
                ", dataBase= '" + dataBase + '\'' +
                ", URL = '" + getURL() + '\'' +
                ", user= '" + user + '\'' +
                ", password= '" + password + '\'' +
                '}';
    }
}
