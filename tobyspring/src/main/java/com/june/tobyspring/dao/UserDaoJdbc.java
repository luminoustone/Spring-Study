package com.june.tobyspring.dao;

import com.june.tobyspring.domain.Level;

import com.june.tobyspring.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    private String sqlAdd;
    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public void setSqlAdd(String sqlAdd){
        this.sqlAdd = sqlAdd;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<User> userMapper =
        new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setLevel(Level.valueOf(rs.getInt("level")));
                user.setLogin(rs.getInt("login"));
                user.setRecommend(rs.getInt("recommend"));
                return user;
            }
        };

    public void add(final User user) {
        this.jdbcTemplate.update(
                this.sqlMap.get("add"),
                user.getId(), user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id}, this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }


    public int getCount() {
        //queryForInt is duplicated!
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                "update users set name = ?, password = ?, email = ?, level = ?, login = ?," +
                        "recommend = ? where id =?", user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId()
        );
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
    }
}