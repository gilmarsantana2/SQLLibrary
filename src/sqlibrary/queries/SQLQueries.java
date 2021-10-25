package sqlibrary.queries;

import sqlibrary.annotation.PrimaryKey;
import sqlibrary.annotation.TableName;
import sqlibrary.annotation.TableCollumn;
import sqlibrary.annotation.ForeignKey;

import java.lang.reflect.Field;

public class SQLQueries {
    /**
     * Script INSERT INTO
     * @param model Classe Model que deve conter as anotações
     * @return INSERT INTO teste (nome,estrangeiro_key) VALUES ('novo nome','100');
     */
    public static String insertInto(Object model) {
        Class<?> classe = model.getClass();
        String sql = "INSERT INTO " + getTableName(model) + " (";
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableCollumn.class)) {
                TableCollumn att = field.getAnnotation(TableCollumn.class);
                sql += att.name() + ",";
            }
            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey att = field.getAnnotation(ForeignKey.class);
                sql += att.key() + ",";
            }
        }
        //remove a ultima virgula
        sql = sql.substring(0, sql.lastIndexOf(",")) + ") VALUES (";

        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableCollumn.class)) {
                field.setAccessible(true);
                try {
                    try {
                        sql += "'" + field.get(model).toString() + "',";
                    } catch (NullPointerException e) {
                        sql += "default,";
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.isAnnotationPresent(ForeignKey.class)) {
                field.setAccessible(true);
                Class<?> foreignTable = field.getType();
                for(Field foreignKey: foreignTable.getDeclaredFields()){
                    foreignKey.setAccessible(true);
                    if (foreignKey.isAnnotationPresent(PrimaryKey.class)) {
                        try {
                            sql += "'" + foreignKey.get(field.get(model)) + "',";
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                }
            }

        }
        //remove as ulimas aspas
        sql = sql.substring(0, sql.length() - 1) + ");";
        return sql;
    }

    /**
     * Script DELETE
     * @param model Classe Model que deve conter as anotações
     * @return DELETE FROM teste WHERE id = '1';
     */
    public static String delete(Object model) {
        return "DELETE FROM " + getTableName(model) + " WHERE " + getPrimaryKey(model) + ";";
    }
    /**
     * Script UPDATE
     * @param model Classe Model que deve conter as anotações
     * @return UPDATE teste SET nome = 'novo nome', estrangeiro_key = '100' WHERE id = '1';
     */
    public static String update(Object model) {
        Class<?> classe = model.getClass();
        String sql = "UPDATE " + getTableName(model) + " SET ";
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableCollumn.class)) {
                field.setAccessible(true);
                TableCollumn collum = field.getAnnotation(TableCollumn.class);
                try {
                    try {
                        sql += collum.name() + " = '" + field.get(model) + "', ";
                    } catch (NullPointerException e) {
                        sql += collum.name() + " = default, ";
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.isAnnotationPresent(ForeignKey.class)) {
                field.setAccessible(true);
                Class<?> foreignTable = field.getType();
                ForeignKey key = field.getAnnotation(ForeignKey.class);
                for(Field foreignKey: foreignTable.getDeclaredFields()){
                    foreignKey.setAccessible(true);
                    if (foreignKey.isAnnotationPresent(PrimaryKey.class)) {
                        try {
                            sql += key.key() +  " = '" + foreignKey.get(field.get(model)) + "',";
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                }
            }

        }
        //remove a ultima virgula
        sql = sql.substring(0, sql.lastIndexOf(",")) + " WHERE " + getPrimaryKey(model) + ";";
        return sql;
    }

    /**
     * Script SELECT para um unico Elemento contendo PrimaryKey
     * @param model Classe Model que deve conter as anotações
     * @return SELECT * FROM teste WHERE id = '1';
     */
    public static String selectById(Object model) {
        return "SELECT * FROM " + getTableName(model) + " WHERE " + getPrimaryKey(model) + ";";
    }

    /**
     * Script SELECT para varios Elementos
     * @param tableName Nome da Classe da consulta
     * @param search Busca de todos os elemetos personalizado
     * @return SELECT * FROM teste WHERE Script Personalizado;
     */
    public static String selectSpecial(String tableName, String search) {
        return "SELECT * FROM " + tableName + " WHERE " + search + ";";
    }

    /**
     * Script Select para todos os itens da tabela sem restrições
     * @param tableName Nome da tabela a ser consultada
     * @return SELECT * FROM teste;
     */
    public static String selectAll(String tableName) {
        return "SELECT * FROM " + tableName + ";";
    }

    /**
     * Script de busca do Ultimo PrimaryKey inserido na tabela
     * @param model Classe que deve conter as anotações;
     * @return SELECT id FROM teste ORDER BY id DESC LIMIT 1;
     */
    public static String getLastID(Object model){
        var sql1 = "SELECT ";
        var sql2 = " FROM "+ getTableName(model) + " ORDER BY ";
        var sql3 = "";
        var sql4 = " DESC LIMIT 1;";
        Class<?> classe = model.getClass();
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey att = field.getAnnotation(PrimaryKey.class);
                sql3 = att.key();
                break;
            }
        }
        return sql1 + sql3 + sql2 + sql3 + sql4;
    }

    private static String getTableName(Object model) {
        Class<?> classe = model.getClass();
        String tableName = "";
        if (classe.isAnnotationPresent(TableName.class)) {
            TableName table = classe.getAnnotation(TableName.class);
            tableName = table.table();
        } else throw new NullPointerException();
        return tableName;
    }

    private static String getPrimaryKey(Object model) {
        Class<?> classe = model.getClass();
        String key = "";
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                field.setAccessible(true);
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                try {
                    key = primaryKey.key() + " = '" + field.getInt(model) + "'";
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else throw new NullPointerException();
        }
        return key;
    }

}
