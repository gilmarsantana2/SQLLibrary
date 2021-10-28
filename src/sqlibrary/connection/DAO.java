package sqlibrary.connection;

import sqlibrary.queries.SQLQueries;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe para consulta no banco de dados de forma automática
 * Scripts SQL já prontos
 * @param <T> Model Classe onde deve haver as anotações da tabela
 */

public abstract class DAO<T> extends ConnectionDB {

    /**
     * Metodo INSERT INTO do SQL
     * @param model Classe onde deve haver as anotações da tabela
     * @return Retorna o último ID inserido na tabela tipo INTEGER
     */
    public int insert(T model) {
        int valid = insertSQL(SQLQueries.insertInto(model));
        int id = -1;
        if (valid == 1){
            selectSQL(SQLQueries.getLastID(model));
            try {
                id = getResultSet().getInt(1);
            } catch (SQLException e) {
                id = -2;
                e.printStackTrace();
            }finally {
                this.close();
            }
        }
        return id;
    }

    /**
     * Método DELETE do SQL
     * @param model Classe onde deve haver as anotações da tabela
     * @return true se obtiver sucesso e false caso contrário
     */
    public boolean delete(T model) {
        return executarUpdateDeleteSQL(SQLQueries.delete(model));
    }

    /**
     * Método UPDATE do SQL
     * @param model Classe onde deve haver as anotações da tabela
     * @return true se obtiver sucesso e false caso contrário
     */
    public boolean update(T model) {
        return executarUpdateDeleteSQL(SQLQueries.delete(model));
    }

    /**
     * Metodo para selecionar apenas 1 elemento da tabela de acordo com um PrimaryKey
     * @param model Um model contendo apenas o ID é suficiente para executar
     * @return Model Class da consulta mesmo tipo do parâmetro
     */
    public abstract T selectById(T model);

    /**
     * Metodo para selecionar itens da tabela com consulta personalizada
     * @param filter O SQL deve ser escrito manualmente pelo usuário
     * @return Lista de Model para os itens da consulta personalizada
     */
    public abstract List<T> selectWithFilter(String filter);

    /**
     * Metodo de consulta de todos os itens da tabela
     * @return Lista de Model
     */
    public abstract List<T> selectAll();

    /**
     * Metodo para Imprimir o Script SQL
     * @param print true pra imprimir, false para ocultar
     * @return Script SQl, ou show in console launcher
     */
    public String showSQL(boolean print){
        return this.printSQL(print);
    }

}
