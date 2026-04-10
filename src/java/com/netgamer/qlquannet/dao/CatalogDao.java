package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.Food;
import com.netgamer.qlquannet.model.Game;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class CatalogDao {
    private final ServletContext ctx;

    public CatalogDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public List<Game> getAllGames() throws Exception {
        String sql = "EXEC sp_GetAllGames";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Game> out = new ArrayList<>();
            while (rs.next()) {
                Game g = new Game();
                g.setMaGame(rs.getString("maGame"));
                g.setTenGame(rs.getString("tenGame"));
                g.setGiaGame(rs.getDouble("giaGame"));
                g.setTheLoai(rs.getString("tenTheLoai"));
                g.setMoTa(rs.getString("moTa"));
                g.setHinhAnh(rs.getString("hinhAnh"));
                out.add(g);
            }
            return out;
        }
    }

    public List<Food> getAllFoods() throws Exception {
        String sql = "EXEC sp_GetAllFoods";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Food> out = new ArrayList<>();
            while (rs.next()) {
                Food f = new Food();
                f.setMaDoAn(rs.getString("maDoAn"));
                f.setTenDoAn(rs.getString("tenDoAn"));
                f.setGiaDoAn(rs.getDouble("giaDoAn"));
                f.setLoaiDoAn(rs.getString("tenLoaiDoAn"));
                f.setMoTa(rs.getString("moTa"));
                f.setHinhAnh(rs.getString("hinhAnh"));
                out.add(f);
            }
            return out;
        }
    }
}

