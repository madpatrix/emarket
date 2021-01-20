package com.demo.web.emarket.infra.async.send;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class JdbcSendMsgContainerRepository {

    private static final RowMapper<SentMsgConainer> ROW_MAPPER = new RowMapper<SentMsgConainer>() {
        @Override
        public SentMsgConainer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SentMsgConainer(
                    rs.getString("id"),
                    rs.getString("transaction_id"),
                    rs.getString("msg_type"),
                    rs.getString("json_serialized_msg"),
                    rs.getTimestamp("creation_time").toLocalDateTime(),
                    SentMsgConainer.Status.values()[rs.getInt("status")],
                    rs.getString("topic")
            );
        }
    };

    private static final String SQL_INSERT = "INSERT INTO SENT_MSG_CONTAINER " +
            "(ID, TRANSACTION_ID, CREATION_TIME, JSON_SERIALIZED_MSG, MSG_TYPE, STATUS, TOPIC ) " +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_DELETE = "DELETE FROM SENT_MSG_CONTAINER WHERE ID=?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcSendMsgContainerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SentMsgConainer> findAll(){
        return this.jdbcTemplate.query("SELECT * FROM SENT_MSG_CONTAINER", ROW_MAPPER);
    }

    public void save(SentMsgConainer sentMsgConainer){
        this.jdbcTemplate.update(
                SQL_INSERT,
                sentMsgConainer.getId(),
                sentMsgConainer.getTransactionId(),
                Timestamp.valueOf(sentMsgConainer.getCreationTime()),
                sentMsgConainer.getJsonSerializedMsg(),
                sentMsgConainer.getMsgType(),
                sentMsgConainer.getStatus().ordinal(),
                sentMsgConainer.getTopic()
                );
    }

    public void deleteAll(){
        this.jdbcTemplate.update("DELETE FROM SENT_MSG_CONTAINER");
    }

    public void updateStatus(SentMsgConainer sentMsgConainer){
        this.jdbcTemplate.update(
                "UPDATE SENT_MSG_CONTAINER SET STATUS = ? WHERE ID = ?",
                sentMsgConainer.getStatus().ordinal(),
                sentMsgConainer.getId());
    }


    public void delete(String id){
        this.jdbcTemplate.update(SQL_DELETE, id);
    }

    public void save(List<SentMsgConainer> sentMsgConainers) {
        this.jdbcTemplate.batchUpdate(SQL_INSERT, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        SentMsgConainer sent = sentMsgConainers.get(i);
                        ps.setString(1, sent.getId());
                        ps.setString(2, sent.getTransactionId());
                        ps.setTimestamp(3, Timestamp.valueOf(sent.getCreationTime()));
                        ps.setString(4, sent.getJsonSerializedMsg());
                        ps.setString(5, sent.getMsgType());
                        ps.setInt(6, sent.getStatus().ordinal());
                        ps.setString(7, sent.getTopic());
                    }

                    @Override
                    public int getBatchSize() {
                        return sentMsgConainers.size();
                    }
                }

        );
    }

    public long countByStatus(SentMsgConainer.Status status) {
        return this.jdbcTemplate.queryForObject(
                "SELECT COUNT(ID) FROM SENT_MSG_CONTAINER WHERE STATUS = ?",
                new Object[]{status.ordinal()},
                Long.class
        );
    }

    public List<String> findTopicList() {
        return this.jdbcTemplate.queryForList(
                "SELECT DISTINCT TOPIC FROM SENT_MSG_CONTAINER",
                String.class
        );
    }

    public List<SentMsgConainer> findFirst1000ByStatusAndTopic(SentMsgConainer.Status status, String topic) {
        return this.jdbcTemplate.query(
                "SELECT * FROM SENT_MSG_CONTAINER WHERE STATUS = ? AND TOPIC = ? ORDER BY CREATION_TIME ASC FETCH FIRST 1000 ROWS ONLY",
                new Object[]{status.ordinal(), topic},
                ROW_MAPPER
        );
    }
}
