package sqlibrary;

import sqlibrary.annotation.*;
import sqlibrary.connection.ConnectionDB;
import sqlibrary.connection.CriarBancoAuto;
import sqlibrary.queries.SQLQueries;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * classe de teste de scripts de SQL
 */
public class Main {

    public static void main(String[] args) {

       // DBSettings banco = CriarBancoAuto.settingsByTerminal();
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

        Model model = new Model();
        Model1 model1 = new Model1();
        Model2 model2 = new Model2();

        System.out.println(SQLQueries.insertInto(model));
        System.out.println(SQLQueries.update(model));
        System.out.println(SQLQueries.insertInto(model1));
        System.out.println(SQLQueries.update(model1));
        System.out.println(SQLQueries.insertInto(model2));
        System.out.println(SQLQueries.update(model2));

        System.out.println(SQLQueries.delete(model));
        System.out.println(SQLQueries.selectAll("teste"));
        System.out.println(SQLQueries.getLastID(model));
        System.out.println(SQLQueries.selectById(model));


        /*ConnectionDB.setDBSettings(Store.readFile());

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

    @TableName("teste")
    public static class Model {
        @PrimaryKey("id")
        private int anInt;
        @TableCollumn("nome_completo")
        private String nome;
        @ForeignKey
        private Model1 foreign;
        @Ignore
        private boolean maisUm;
        @ForeignKey
        private Model2 forerign2;
        private boolean hasKey;

        public Model() {
            anInt = 1;
            nome = "novo nome";
            foreign = new Model1();
            maisUm = false;
            forerign2 = new Model2();
            hasKey = true;
        }
    }

    @TableName("estrangeiro")
    public static class Model1 {
        @PrimaryKey("identidade")
        private int id;
        @TableCollumn("nome_de_fora")
        private String nome;

        public Model1() {
            id = 100;
            nome = "nome estrangeiro";
        }
    }

    @TableName("modelo 2")
    public static class Model2 {
        @PrimaryKey("identidade")
        private int id;
        private String nome;

        public Model2() {
            id = 53;
            nome = "nome esquisito";
        }
    }
}
