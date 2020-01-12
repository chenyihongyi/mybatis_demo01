package com.springboot_mybatis.mybatis_demo.domain;

import com.springboot_mybatis.mybatis_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2020/1/11 17:32
 */
@Service
@EnableTransactionManagement
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public boolean save(final User user) {

        Boolean result = jdbcTemplate.execute("INSERT INTO user (name, age) VALUE (?,?);",new PreparedStatementCallback<Boolean>() {
            @Override
            public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ps.setString(1,user.getName());
                ps.setInt(2,user.getAge());
                return ps.executeUpdate() > 0;
            }
        });
        return result;
    }
}
