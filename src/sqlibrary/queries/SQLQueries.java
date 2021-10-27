package sqlibrary.queries;

import sqlibrary.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SQLQueries {
    /**
     * Script INSERT INTO
     *
     * @param model Classe Model que deve conter as anotações
     * @return INSERT INTO teste (nome,estrangeiro_key) VALUES ('novo nome','100');
     */
    public static String insertInto(Object model) {
        Class<?> classe = model.getClass();
        StringBuilder sql = new StringBuilder("INSERT INTO " + getTableName(model) + " (");
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) continue;
            if (field.isAnnotationPresent(PrimaryKey.class)) continue;
            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey att = field.getAnnotation(ForeignKey.class);

                if (att.value().isBlank()) sql.append(field.getName()).append(", ");
                else sql.append(att.value()).append(", ");
                continue;
            }
            if (field.isAnnotationPresent(TableCollumn.class)) {
                TableCollumn att = field.getAnnotation(TableCollumn.class);

                if (att.value().isBlank()) sql.append(field.getName()).append(", ");
                else sql.append(att.value()).append(", ");
            } else {
                sql.append(field.getName()).append(", ");
            }
        }
        //remove a ultima virgula
        sql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")) + ") VALUES (");

        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) continue;
            if (field.isAnnotationPresent(PrimaryKey.class)) continue;
            if (field.isAnnotationPresent(ForeignKey.class)) {
                field.setAccessible(true);
                Class<?> foreignTable = field.getType();
                boolean hasKey = false;
                for (Field foreignKey : foreignTable.getDeclaredFields()) {
                    foreignKey.setAccessible(true);
                    if (foreignKey.isAnnotationPresent(PrimaryKey.class)) {
                        try {
                            sql.append(getFieldType(foreignKey.get(field.get(model))));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        hasKey = true;
                        break;
                    }
                }
                if (hasKey) continue;
                else
                    throw new NullPointerException("Anotação PrimaryKey não encontrada na classe " + foreignTable.getName());
            }
            if (field.isAnnotationPresent(SQLAdapterFormat.class)) {
                field.setAccessible(true);
                sql.append(getAdapterFormater(field, model));
            } else {
                field.setAccessible(true);
                try {
                    sql.append(getFieldType(field.get(model)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //remove as ulimas aspas
        sql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")) + ");");
        return sql.toString();
    }

    /**
     * Script DELETE
     *
     * @param model Classe Model que deve conter as anotações
     * @return DELETE FROM teste WHERE id = '1';
     */
    public static String delete(Object model) {
        return "DELETE FROM " + getTableName(model) + " WHERE " + getPrimaryKey(model) + ";";
    }

    /**
     * Script UPDATE
     *
     * @param model Classe Model que deve conter as anotações
     * @return UPDATE teste SET nome = 'novo nome', estrangeiro_key = '100' WHERE id = '1';
     */
    public static String update(Object model) {
        Class<?> classe = model.getClass();
        StringBuilder sql = new StringBuilder("UPDATE " + getTableName(model) + " SET ");
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) continue;
            if (field.isAnnotationPresent(PrimaryKey.class)) continue;
            if (field.isAnnotationPresent(ForeignKey.class)) {
                field.setAccessible(true);
                Class<?> foreignTable = field.getType();
                ForeignKey key = field.getAnnotation(ForeignKey.class);
                boolean hasKey = false;
                for (Field foreignKey : foreignTable.getDeclaredFields()) {
                    foreignKey.setAccessible(true);
                    if (foreignKey.isAnnotationPresent(PrimaryKey.class)) {
                        try {
                            if (key.value().isBlank())
                                sql.append(field.getName()).append(" = ").append(getFieldType(foreignKey.get(field.get(model))));
                            else
                                sql.append(key.value()).append(" = ").append(getFieldType(foreignKey.get(field.get(model))));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        hasKey = true;
                        break;
                    }
                }
                if (hasKey) continue;
                else
                    throw new NullPointerException("Anotação PrimaryKey não encontrada na classe " + foreignTable.getName());
            }
            if (field.isAnnotationPresent(TableCollumn.class)) {
                field.setAccessible(true);
                TableCollumn collum = field.getAnnotation(TableCollumn.class);
                try {
                    if (collum.value().isBlank()) sql.append(field.getName()).append(" = ");
                    else sql.append(collum.value()).append(" = ");
                    if (field.isAnnotationPresent(SQLAdapterFormat.class)) {
                        sql.append(getAdapterFormater(field, model));
                    } else sql.append(getFieldType(field.get(model)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                field.setAccessible(true);
                try {
                    sql.append(field.getName()).append(" = ");
                    if (field.isAnnotationPresent(SQLAdapterFormat.class)){
                        sql.append(getAdapterFormater(field, model));
                    } else sql.append(getFieldType(field.get(model)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //remove a ultima virgula
        sql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")) + " WHERE " + getPrimaryKey(model) + ";");
        return sql.toString();
    }

    /**
     * Script SELECT para um unico Elemento contendo PrimaryKey
     *
     * @param model Classe Model que deve conter as anotações
     * @return SELECT * FROM teste WHERE id = '1';
     */
    public static String selectById(Object model) {
        return "SELECT * FROM " + getTableName(model) + " WHERE " + getPrimaryKey(model) + ";";
    }

    /**
     * Script SELECT para varios Elementos
     *
     * @param tableName Nome da Classe da consulta
     * @param search    Busca de todos os elemetos personalizado
     * @return SELECT * FROM teste WHERE Script Personalizado;
     */
    public static String selectSpecial(String tableName, String search) {
        return "SELECT * FROM " + tableName + " WHERE " + search + ";";
    }

    /**
     * Script Select para todos os itens da tabela sem restrições
     *
     * @param tableName Nome da tabela a ser consultada
     * @return SELECT * FROM teste;
     */
    public static String selectAll(String tableName) {
        return "SELECT * FROM " + tableName + ";";
    }

    /**
     * Script de busca do Ultimo PrimaryKey inserido na tabela
     *
     * @param model Classe que deve conter as anotações;
     * @return SELECT id FROM teste ORDER BY id DESC LIMIT 1;
     */
    public static String getLastID(Object model) {
        var sql1 = "SELECT ";
        var sql2 = " FROM " + getTableName(model) + " ORDER BY ";
        var sql3 = "";
        var sql4 = " DESC LIMIT 1;";
        Class<?> classe = model.getClass();
        boolean hasKey = false;
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey att = field.getAnnotation(PrimaryKey.class);
                if (att.value().isBlank()) sql3 = field.getName();
                else sql3 = att.value();
                hasKey = true;
                break;
            }
        }
        if (!hasKey)
            throw new NullPointerException("Anotação TableName não encontrada na classe " + classe.getName());
        return sql1 + sql3 + sql2 + sql3 + sql4;
    }

    private static String getTableName(Object model) {
        Class<?> classe = model.getClass();
        String tableName;
        if (classe.isAnnotationPresent(TableName.class)) {
            TableName table = classe.getAnnotation(TableName.class);
            tableName = table.value();
        } else throw new NullPointerException("Anotação TableName não encontrada na classe " + classe.getName());
        return tableName;
    }

    private static String getPrimaryKey(Object model) {
        Class<?> classe = model.getClass();
        String key = "";
        boolean haskey = false;
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                field.setAccessible(true);
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                try {
                    try {
                        if (primaryKey.value().isBlank())
                            key = field.getName() + " = " + field.get(model).toString() + "";
                        else key = primaryKey.value() + " = " + field.get(model).toString() + "";
                    } catch (NullPointerException e) {
                        if (primaryKey.value().isBlank()) key = field.getName() + " = " + 0;
                        else key = primaryKey.value() + " = " + 0;
                    }
                    haskey = true;
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!haskey)
            throw new NullPointerException("Anotação PrimaryKey não encontrada na classe " + classe.getName());
        return key;
    }

    private static String getFieldType(Object value) {
        if (value == null) return "default, ";
        if (Number.class.isInstance(value) || Boolean.class.isInstance(value)) return value + ", ";
        else return "'" + value + "', ";
    }

    private static String getAdapterFormater(Field target, Object model) {
        SQLAdapterFormat format = target.getAnnotation(SQLAdapterFormat.class);
        Class<?> adapter = format.value();
        try {
            Method method = adapter.getDeclaredMethod("setAdapter", Object.class);
            SQLAdapter t = (SQLAdapter) adapter.getDeclaredConstructor().newInstance();
            return getFieldType(method.invoke(t, target.get(model)));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
