package com.jingyou.main.core.interceptor;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisInterceptor implements Interceptor {
    private static final int TWO = 2;
    private static final String POINT = "`";
    private static final String COMMA = ",";
    private static final String EMPTY = "";
    private static final String ERROR_SQL = "ERROR_SQL";
    private static final String SELECT = "SELECT";
    private static final String INSERT = "INSERT";
    private static final String DELETE = "DELETE";
    private static final String UPDATE = "UPDATE";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            // 获取xml中的一个select/update/insert/delete节点，是一条SQL语句
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = null;
            // 获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
            if (invocation.getArgs().length > 1) parameter = invocation.getArgs()[1];
            // 获取到节点的id,即sql语句的id
            String sqlId = mappedStatement.getId();
            // BoundSql就是封装myBatis最终产生的sql类
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            // 获取节点的配置
            Configuration configuration = mappedStatement.getConfiguration();
            // 获取到最终的sql语句
            String sql = getSql(configuration, boundSql, sqlId);
            // 将所需SQL存入ELK
            saveSqlLog(sql);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("MybatisInterceptor 执行sql语句格式化报错 \n{}", e);
        }
        // 执行完上面的任务后，不改变原有的sql执行过程
        return invocation.proceed();
    }

    /**
     * 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
     * @param configuration
     * @param boundSql
     * @param sqlId
     * @return
     */
    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append(sql);
        return str.toString();
    }

    /**
     * 进行?的替换
     * @param configuration
     * @param boundSql
     * @return
     */
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?",
                        Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?",
                                Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?",
                                Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        // 打印出缺失，提醒该参数缺失并防止错位
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
     * @param obj
     * @return
     */
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
                    DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    /**
     * @Description 将sql解析, 并且存入至ELK中.
     */
    public static void saveSqlLog(String sql) {
        if (StringUtils.isNotBlank(sql) && sql.trim().toUpperCase().startsWith(SELECT)) {
            return;
        }
        sql = sql.replaceAll(POINT, EMPTY);
//    System.err.println("======================================\n" + sql + "\n======================================");
        // 新增操作
        if (StringUtils.isNotBlank(sql) && sql.trim().toUpperCase().startsWith(INSERT)) {
            saveInsertSql(sql);
            return;
        }
        // 删除操作
        if (StringUtils.isNotBlank(sql) && sql.trim().toUpperCase().startsWith(DELETE)) {
            saveDeleteSql(sql);
            return;
        }
        // 修改操作
        if (StringUtils.isNotBlank(sql) && sql.trim().toUpperCase().startsWith(UPDATE)) {
            saveUpdateSql(sql);
            return;
        }
    }

    /**
     * @Description 修改SQL语句参数提取
     */
    private static void saveUpdateSql(String sql) {
//    try {
////      System.err.println("Update操作");
//      sqlLog.setSqlType(UPDATE);
//      List<String> updateTablesName = GetTablesNameUtil.getUpdateTablesName(sql);
//      if (CollectionUtils.isEmpty(updateTablesName) || updateTablesName.size() > 1) {
//        sqlLog.setSqlParameter(ERROR_SQL);
//      } else {
//        // 找到insert语句中, 对应的字段名字与对应字段名字的value
//        sqlLog.setTableName(updateTablesName.get(0));
//        Update update = (Update) CCJSqlParserUtil.parse(sql);
//        List<Column> columns = update.getColumns();
//        List<Expression> expressions = update.getExpressions();
//        List<String> list = new LinkedList<>();
//        for (int i = 0; i < columns.size(); i++) {
//          list.add(columns.get(i).toString().trim() + "=" + expressions.get(i).toString().trim());
//        }
//        sqlLog.setSqlParameter(JSON.toJSONString(list));
//        Expression where = update.getWhere();
//        sqlLog.setWhere(where.toString());
//      }
//      System.err.println(JSON.toJSONString(sqlLog));
////      RabbitMqUtil.sendElk(sqlLog);
//    } catch (JSQLParserException e) {
//      e.printStackTrace();
////    } catch (IOException e1) {
////      e1.printStackTrace();
////    }
//      catch (Exception e2) {
//      e2.printStackTrace();
//    }
    }


    /**
     * @Description 删除SQL语句参数提取
     */
    private static void saveDeleteSql(String sql) {
//    try {
//      System.err.println("Delete操作");
//      sqlLog.setSqlType(DELETE);
//      List<String> deleteTablesName = GetTablesNameUtil.getDeleteTablesName(sql);
//      if (CollectionUtils.isEmpty(deleteTablesName) || deleteTablesName.size() > 1) {
//        sqlLog.setSqlParameter(ERROR_SQL);
//      } else {
//        // 找到insert语句中, 对应的字段名字与对应字段名字的value
//        sqlLog.setTableName(deleteTablesName.get(0));
//        Delete delete = (Delete) CCJSqlParserUtil.parse(sql);
//        Expression where = delete.getWhere();
//        sqlLog.setWhere(where.toString());
//      }
//      System.err.println(JSON.toJSONString(sqlLog));
////      RabbitMqUtil.sendElk(sqlLog);
//    } catch (JSQLParserException e) {
//      e.printStackTrace();
//    } catch (IOException e1) {
//      e1.printStackTrace();
//    } catch (Exception e2) {
//      e2.printStackTrace();
//    }
    }

    /**
     * @Description 新增SQL语句参数提取
     */
    private static void saveInsertSql(String sql) {
//    try {
//      System.err.println("Insert操作");
//      sqlLog.setSqlType(INSERT);
//      List<String> insertTablesName = GetTablesNameUtil.getInsertTablesName(sql);
//      if (CollectionUtils.isEmpty(insertTablesName) || insertTablesName.size() > 1) {
//        sqlLog.setSqlParameter(ERROR_SQL);
//      } else {
//        // 找到insert语句中, 对应的字段名字与对应字段名字的value
//        sqlLog.setTableName(insertTablesName.get(0));
//        List<String> params = GetTablesNameUtil.getInsertSQLParameter(sql);
//        if (CollectionUtils.isNotEmpty(params) && params.size() == TWO) {
//          // 进入此判断, 说明该SQL解析正常
//          String[] fields = params.get(0).split(COMMA);
//          String[] values = params.get(1).split(COMMA);
//          List<String> list = new LinkedList<>();
//          for (int i = 0; i < fields.length; i++) {
//            list.add(fields[i].trim() + "=" + values[i].trim());
//          }
//          sqlLog.setSqlParameter(JSON.toJSONString(list));
//        }
//      }
//      System.err.println(JSON.toJSONString(sqlLog));
////      RabbitMqUtil.sendElk(sqlLog);
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (Exception e1) {
//      e1.printStackTrace();
//    }
    }
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
