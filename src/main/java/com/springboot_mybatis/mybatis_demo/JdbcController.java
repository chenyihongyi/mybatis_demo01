package com.springboot_mybatis.mybatis_demo;

import com.springboot_mybatis.mybatis_demo.domain.User;
import com.springboot_mybatis.mybatis_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2020/1/10 23:13
 */
@RestController
public class JdbcController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/users")
public List<Map<String, Object>> getUsers(){
        return jdbcTemplate.execute(new StatementCallback<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> doInStatement(Statement stmt) throws SQLException, DataAccessException {
                ResultSet resultSet = stmt.executeQuery("SELECT * FROM user");
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
               List<String> columnNames = new ArrayList<>(columnCount);
               for(int i = 1; i<=columnCount; i++){
                   String columnName = resultSetMetaData.getColumnName(i);
                   columnNames.add(columnName);
               }
               List<Map<String, Object>> data = new LinkedList<>();
               while(resultSet.next()){
                   Map<String, Object> columnData = new LinkedHashMap<>();
                   for(String columnName : columnNames){
                       Object columnValue = resultSet.getObject(columnName);
                       columnData.put(columnName, columnValue);
                   }
                   data.add(columnData);
               }
                return data;
            }
        });
    }


    @RequestMapping("/user/get")
    public Map<String, Object> getUser(@RequestParam(value = "id", defaultValue = "1") int id) {
        Map<String, Object> data = new HashMap<String, Object>();
        Connection connection =null;
        Savepoint savepoint = null;
        try {
            connection = dataSource.getConnection();
//            savepoint = connection.setSavepoint();
            PreparedStatement statement = connection.prepareStatement("select id , name, age from user where id =?");
             statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int id_ = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                data.put("id", id);
                data.put("name", name);
                data.put("age",age);
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addUser(@RequestBody final User user){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("success", userService.save(user));
        return data;
    }
}
