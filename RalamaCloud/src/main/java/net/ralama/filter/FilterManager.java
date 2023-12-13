package net.ralama.filter;

import com.google.common.collect.ImmutableList;
import net.ralama.Ralama;
import net.ralama.packets.out.FilterPacket;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilterManager {
    private final List<String> entries = new ArrayList<>();
    public ImmutableList<String> getEntries() {
        return ImmutableList.copyOf(entries);
    }

    public void loadEntries() throws SQLException {
        entries.clear();

        ResultSet rs = Ralama.getDatabase().getConnection().prepareStatement("SELECT * FROM filter;").executeQuery();

        while (rs.next()) {
            entries.add(rs.getString("word"));
        }

        Ralama.getServerManager().sendProxiesPacket(new FilterPacket(this.entries));
    }

    public void add(String word) {
        word = word.toLowerCase();
        if (entries.contains(word)) return;

        entries.add(word);
        try {
            PreparedStatement ps = Ralama.getDatabase().getConnection().prepareStatement("INSERT INTO filter (word) VALUES (?);");
            ps.setString(1, word);
            ps.executeUpdate();

            Ralama.getServerManager().sendProxiesPacket(new FilterPacket(this.entries));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String word) {
        word = word.toLowerCase();
        if (!entries.contains(word)) return;

        entries.remove(word);
        try {
            PreparedStatement ps = Ralama.getDatabase().getConnection().prepareStatement("DELETE FROM filter WHERE word = ?;");
            ps.setString(1, word);
            ps.executeUpdate();

            Ralama.getServerManager().sendProxiesPacket(new FilterPacket(this.entries));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
