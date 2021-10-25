package sqlibrary;

import sqlibrary.annotation.ForeignKey;
import sqlibrary.annotation.PrimaryKey;
import sqlibrary.annotation.TableCollumn;
import sqlibrary.annotation.TableName;
import sqlibrary.connection.*;
import sqlibrary.util.Store;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * classe de teste de scripts de SQL
 */
public class Main {
    public static void main(String[] args) {

        /*Preferences preferences = Preferences.userNodeForPackage(Main.class);
        try {
            System.out.println(preferences.nodeExists("BancoDados"));
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }*/

        DBSettings banco = CriarBancoAuto.settingsByTerminal();
        //Store.createFile(banco);
        //System.out.println(Store.readFile());

        /*if (!ConnectionDB.dbExists(Main.class)) {
            try {
                boolean ok = CriarBancoAuto.autoCreate(Main.class, new FileInputStream("testedb.sql"));
                if (ok) System.out.println("Criado");
                else return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }*/

        /*Model model = new Model();
        System.out.println(SQLQueries.delete(model));
        System.out.println(SQLQueries.insertInto(model));
        System.out.println(SQLQueries.update(model));
        System.out.println(SQLQueries.selectAll("teste"));
        System.out.println(SQLQueries.getLastID(model));
        System.out.println(SQLQueries.selectById(model));

        ConnectionDB.setDBSettings(Store.readFile());

        DAO<Model> dao = new DAO() {
            @Override
            public Object selectById(Object model) {
                return null;
            }

            @Override
            public List<Model> selectWithFilter(String filter) {
                selectSQL(filter);
                Model model = new Model();
                List<Model> lista = new ArrayList<>();
                try {
                    while (getResultSet().next()) {
                        model.setId(getResultSet().getInt(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    close();
                }
                lista.add(model);
                return lista;
            }

            @Override
            public List<Model> selectAll() {
                return null;
            }
        };

        List<Model> id = dao.selectWithFilter("SELECT id FROM teste ORDER BY id DESC LIMIT 1;");
        System.out.println("Consulta pelo DAO: Ultimo ID foi " + id.get(0).getId());*/
    }

    @TableName(table = "teste")
    public static class Model {
        @PrimaryKey(key = "id")
        private int id;
        @TableCollumn(name = "nome")
        private String nome;
        @ForeignKey(key = "estrangeiro_key")
        private Model1 foreign;

        public Model() {
            id = 1;
            nome = "novo nome";
            foreign = new Model1();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Model1 getForeign() {
            return foreign;
        }

        public void setForeign(Model1 foreign) {
            this.foreign = foreign;
        }
    }

    @TableName(table = "estrangeiro")
    public static class Model1 {
        @PrimaryKey(key = "id")
        private int id;
        @TableCollumn(name = "nome_de_fora")
        private String nome;

        public Model1() {
            id = 100;
            nome = "nome estrangeiro";
        }
    }
}
