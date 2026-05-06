package com.beaker.mintcraft.user.infrastructure.mapper;

import com.beaker.mintcraft.user.infrastructure.util.AesUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author beaker
 * @Date 2026/5/6 20:31
 * @Description AES 加密类型处理器
 */
public class AesEncryptTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String encrypted = rs.getString(columnName);
        return encrypted == null ? null : decrypt(encrypted);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String encrypted = rs.getString(columnIndex);
        return encrypted == null ? null : decrypt(encrypted);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encrypted = cs.getString(columnIndex);
        return encrypted == null ? null : decrypt(encrypted);
    }

    private String encrypt(String data) {
        // 实现加密
        return AesUtil.encrypt(data);
    }

    private String decrypt(String data) {
        // 实现解密
        return AesUtil.decrypt(data);
    }
}
