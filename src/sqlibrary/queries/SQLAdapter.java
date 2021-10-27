package sqlibrary.queries;

/**
 * Classe de manipulação de datas do banco de dados
 * getDate -> retorna os dados do banco
 * putDate -> transforma a entrada numa String para inserir no banco de dados
 * @param <T> Tipo do dado há ser adaptado.
 */
public abstract class SQLAdapter<T> {
    protected SQLAdapter(){}
    /**
     * metodo de adaptação de algum tipo diferente do padrao SQL
     * @param adapter tipo de dado
     * @return Objeto formatada ao padrão SQL
     */
    public abstract Object setAdapter(T adapter) throws Exception;
}
