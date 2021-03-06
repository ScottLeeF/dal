package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoBySqlBuilder {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getVersionById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT version FROM task_auto WHERE id =?", new Object[] {id},
                    new RowMapper<Integer>() {
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getInt(1);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<GenTaskBySqlBuilder> getAllTasks() {
        try {
            return jdbcTemplate.query("SELECT id, project_id, db_name,table_name,class_name,method_name,sql_style,"
                    + "crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,"
                    + "update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints" + " FROM task_auto",
                    new RowMapper<GenTaskBySqlBuilder>() {
                        public GenTaskBySqlBuilder mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GenTaskBySqlBuilder.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<GenTaskBySqlBuilder> getTasksByProjectId(int id) {
        try {
            return jdbcTemplate.query(
                    "SELECT id, project_id,db_name, table_name,class_name,method_name,sql_style,"
                            + "crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,"
                            + "update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints "
                            + " FROM task_auto WHERE project_id=?",
                    new Object[] {id}, new RowMapper<GenTaskBySqlBuilder>() {
                        public GenTaskBySqlBuilder mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GenTaskBySqlBuilder.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public GenTaskBySqlBuilder getTasksByTaskId(int taskId) {
        try {
            List<GenTaskBySqlBuilder> list = jdbcTemplate.query(
                    "SELECT id, project_id,db_name, table_name,class_name,method_name,sql_style,"
                            + "crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,"
                            + "update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints "
                            + " FROM task_auto WHERE id=?",
                    new Object[] {taskId}, new RowMapper<GenTaskBySqlBuilder>() {
                        public GenTaskBySqlBuilder mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GenTaskBySqlBuilder.visitRow(rs);
                        }
                    });
            return list != null && list.size() > 0 ? list.get(0) : null;
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<GenTaskBySqlBuilder> updateAndGetAllTasks(int projectId) {
        try {
            final List<GenTaskBySqlBuilder> tasks = new ArrayList<>();
            jdbcTemplate.query("SELECT  id, project_id, db_name,table_name,class_name,method_name,sql_style,"
                    + "crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,"
                    + "update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints "
                    + " FROM task_auto WHERE project_id=?", new Object[] {projectId}, new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            GenTaskBySqlBuilder task = GenTaskBySqlBuilder.visitRow(rs);
                            task.setGenerated(true);
                            if (updateTask(task) > 0) {
                                tasks.add(task);
                            }
                        }
                    });
            return tasks;
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<GenTaskBySqlBuilder> updateAndGetTasks(int projectId) {
        try {
            final List<GenTaskBySqlBuilder> tasks = new ArrayList<>();
            jdbcTemplate.query(
                    " SELECT  id, project_id, db_name,table_name,class_name,method_name,sql_style, "
                            + " crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no, "
                            + " update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints "
                            + " FROM task_auto  " + " WHERE project_id=? AND `generated`=FALSE",
                    new Object[] {projectId}, new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            GenTaskBySqlBuilder task = GenTaskBySqlBuilder.visitRow(rs);
                            task.setGenerated(true);
                            if (updateTask(task) > 0) {
                                tasks.add(task);
                            }
                        }
                    });
            return tasks;
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertTask(GenTaskBySqlBuilder task) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO task_auto "
                            + "( project_id, db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints)"
                            + " SELECT * FROM (SELECT ? AS p1,? AS p2,? AS p3,? AS p4,? AS p5,? AS p6,? AS p7,? AS p8,? AS p9,? AS p10,? AS p11,? AS p12,? AS p13,? AS p14,? AS p15,? AS p16,? AS p17,? AS p18,? AS p19,? AS p20,? AS p21) tmp WHERE NOT exists "
                            + "(SELECT 1 FROM task_auto WHERE project_id=? AND db_name=? AND table_name=? AND method_name=? LIMIT 1)",
                    task.getProject_id(), task.getDatabaseSetName(), task.getTable_name(), task.getClass_name(),
                    task.getMethod_name(), task.getSql_style(), task.getCrud_type(), task.getFields(),
                    task.getCondition(), task.getSql_content(), task.isGenerated(), task.getVersion(),
                    task.getUpdate_user_no(), task.getUpdate_time(), task.getComment(), task.getScalarType(),
                    task.isPagination(), task.getOrderby(), task.getApproved(), task.getApproveMsg(), task.getHints(),
                    task.getProject_id(), task.getDatabaseSetName(), task.getTable_name(), task.getMethod_name());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateTask(GenTaskBySqlBuilder task) {
        try {
            final List<Integer> counts = new ArrayList<>();
            jdbcTemplate.query(
                    "SELECT 1 FROM task_auto WHERE id != ? AND project_id=? AND db_name=? AND table_name=? AND method_name=? LIMIT 1",
                    new Object[] {task.getId(), task.getProject_id(), task.getDatabaseSetName(), task.getTable_name(),
                            task.getMethod_name()},
                    new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            counts.add(1);
                        }
                    });

            if (counts.size() > 0)
                return -1;

            return this.jdbcTemplate.update(
                    "UPDATE task_auto SET project_id=?,db_name=?, table_name=?, class_name=?,method_name=?,"
                            + "sql_style=?,crud_type=?,fields=?,where_condition=?,sql_content=?,`generated`=?,"
                            + "version=version+1,update_user_no=?,update_time=?,comment=?,scalarType=?,"
                            + "pagination=?,orderby=?,approved=?,approveMsg=?,hints=? WHERE id=? AND version = ?",
                    task.getProject_id(), task.getDatabaseSetName(), task.getTable_name(), task.getClass_name(),
                    task.getMethod_name(), task.getSql_style(), task.getCrud_type(), task.getFields(),
                    task.getCondition(), task.getSql_content(), task.isGenerated(), task.getUpdate_user_no(),
                    task.getUpdate_time(), task.getComment(), task.getScalarType(), task.isPagination(),
                    task.getOrderby(), task.getApproved(), task.getApproveMsg(), task.getHints(), task.getId(),
                    task.getVersion());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateTask(int taskId, int approved, String approveMsg) {
        try {
            return jdbcTemplate.update("UPDATE task_auto SET approved=?, approveMsg=? WHERE id=?", approved, approveMsg,
                    taskId);
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteTask(GenTaskBySqlBuilder task) {
        try {
            return jdbcTemplate.update("DELETE FROM task_auto WHERE id=?", task.getId());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteByProjectId(int id) {
        try {
            return jdbcTemplate.update("DELETE FROM task_auto WHERE project_id=?", id);
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteByServerId(int id) {
        try {
            return jdbcTemplate.update("DELETE FROM task_auto WHERE server_id=?", id);
        } catch (Throwable e) {
            throw e;
        }
    }

}
