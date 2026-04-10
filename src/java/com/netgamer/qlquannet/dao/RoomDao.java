package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class RoomDao {
    private final ServletContext ctx;

    public RoomDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public List<Room> findAll() throws Exception {
        String sql = "SELECT maKhu, tenKhu, moTa FROM KhuVuc ORDER BY maKhu";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Room> out = new ArrayList<>();
            while (rs.next()) {
                Room r = new Room();
                r.setMaKhu(rs.getString("maKhu"));
                r.setTenKhu(rs.getString("tenKhu"));
                r.setMoTa(rs.getString("moTa"));
                out.add(r);
            }
            return out;
        }
    }
}

