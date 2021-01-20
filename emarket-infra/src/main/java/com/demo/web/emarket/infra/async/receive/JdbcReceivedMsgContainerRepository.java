package com.demo.web.emarket.infra.async.receive;

import com.demo.web.emarket.infra.async.send.SentMsgConainer;
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
public class JdbcReceivedMsgContainerRepository {

    private static final String SQL_DELETE = "DELETE FROM RECEIVED_MSG_CONTAINER WHERE ID=?";
    private static final String SQL_INSERT = "INSERT INTO RECEIVED_MSG_CONTAINER " +
            "(id, msg_type, json_serialized_msg, msg_creation_time, received_time, status, topic ) " +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?)";

    private static final RowMapper<ReceivedMsgContainer> ROW_MAPPER = new RowMapper<ReceivedMsgContainer>() {
        @Override
        public ReceivedMsgContainer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReceivedMsgContainer(
                    rs.getString("id"),
                    rs.getString("msg_type"),
                    rs.getString("json_serialized_msg"),
                    rs.getTimestamp("msg_creation_time").toLocalDateTime(),
                    rs.getTimestamp("received_time").toLocalDateTime(),
                    ReceivedMsgContainer.Status.values()[rs.getInt("status")],
                    rs.getString("topic")
            );
        }
    };

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcReceivedMsgContainerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReceivedMsgContainer> findAll(){
        return this.jdbcTemplate.query("SELECT * FROM RECEIVED_MSG_CONTAINER", ROW_MAPPER);
    }

    public void save(ReceivedMsgContainer receivedMsgContainer){
        this.jdbcTemplate.update(
                SQL_INSERT,
                receivedMsgContainer.getId(),
                receivedMsgContainer.getMsgType(),
                receivedMsgContainer.getJsonSerializedMsg(),
                Timestamp.valueOf(receivedMsgContainer.getMsgCreationTime()),
                Timestamp.valueOf(receivedMsgContainer.getReceivedTime()),
                receivedMsgContainer.getStatus().ordinal(),
                receivedMsgContainer.getTopic()
        );
    }

    public List<ReceivedMsgContainer> findFirst1000ByStatus(ReceivedMsgContainer.Status status) {
        return this.jdbcTemplate.query(
                "SELECT * FROM RECEIVED_MSG_CONTAINER WHERE STATUS = ? FETCH FIRST 1000 ROWS ONLY",
                new Object[]{status.ordinal()},
                ROW_MAPPER
        );
    }

    public void save(List<ReceivedMsgContainer> receivedMsgContainers) {
        this.jdbcTemplate.batchUpdate(SQL_INSERT, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ReceivedMsgContainer received = receivedMsgContainers.get(i);
                        ps.setString(1, received.getId());
                        ps.setString(2, received.getMsgType());
                        ps.setString(3, received.getJsonSerializedMsg());
                        ps.setTimestamp(4, Timestamp.valueOf(received.getMsgCreationTime()));
                        ps.setTimestamp(5, Timestamp.valueOf(received.getReceivedTime()));
                        ps.setInt(6, received.getStatus().ordinal());
                        ps.setString(7, received.getTopic());
                    }

                    @Override
                    public int getBatchSize() {
                        return receivedMsgContainers.size();
                    }
                }

        );
    }

    public void deleteAll(){
        this.jdbcTemplate.update("DELETE FROM RECEIVED_MSG_CONTAINER");
    }

    public void updateStatus(ReceivedMsgContainer receivedMsgContainer){
        this.jdbcTemplate.update(
                "UPDATE RECEIVED_MSG_CONTAINER SET STATUS = ? WHERE ID = ?",
                receivedMsgContainer.getStatus().ordinal(),
                receivedMsgContainer.getId());
    }

    public void delete(ReceivedMsgContainer receivedMsgContainer){
        this.jdbcTemplate.update(SQL_DELETE, receivedMsgContainer.getId());
    }
}
