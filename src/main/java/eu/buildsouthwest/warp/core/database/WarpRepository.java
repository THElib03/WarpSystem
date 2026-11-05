package eu.buildsouthwest.warp.core.database;

import eu.buildsouthwest.warp.core.model.CityHubData;
import eu.buildsouthwest.warp.core.model.EducationalData;
import eu.buildsouthwest.warp.core.model.GeoMetadata;
import eu.buildsouthwest.warp.core.model.Warp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WarpRepository {
    private final String GETWARP = "SELECT b.*, g.*, e.*, c.* FROM bte_warps b LEFT JOIN geo_meta g ON g.warpID = b.id LEFT JOIN educational e ON e.warpID = b.id LEFT JOIN city_hub c ON c .warpID = b.id WHERE b.id = ?;";
    private final String GETWARPS = "SELECT b.*, g.*, e.*, c.* FROM bte_warps b LEFT JOIN geo_meta g ON g.warpID = b.id LEFT JOIN educational e ON e.warpID = b.id LEFT JOIN city_hub c ON c .warpID = b.id";
    private final DatabaseService db;

    public WarpRepository(DatabaseService db){
        this.db = db;
    }

    public CompletableFuture<Optional<Warp>> getWarp(UUID warp) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = db.getConnection();
                PreparedStatement query = conn.prepareStatement(GETWARP)) {
                query.setString(1, warp.toString());

                try (ResultSet rs = query.executeQuery()) {
                    if(!rs.next()) return Optional.empty();

                    return Optional.of((WarpWrapper.map(rs)));
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                return Optional.empty();
            }
        }, db.executor());
    }

    public CompletableFuture<List<Warp>> getAllWarps() {
        List<Warp> warps = new ArrayList<>();
        return CompletableFuture.supplyAsync( () -> {
            try{
                PreparedStatement query = db.getConnection().prepareStatement(GETWARPS);
                ResultSet rs = query.executeQuery();

                while(rs.next()) warps.add(WarpWrapper.map(rs));
                return warps;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                return warps;
            }
        }, db.executor());
    }
}

final class WarpWrapper {
    private WarpWrapper() {}

    static Warp map(ResultSet rs) throws SQLException {
        return new Warp(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("world"),
                rs.getString("description"),
                rs.getString("type"),
                rs.getDouble("x"),
                rs.getDouble("y"),
                rs.getDouble("z"),
                rs.getDouble("pitch"),
                rs.getDouble("yaw"),
                rs.getString("icon"),
                GeoMetaWrapper.map(rs),
                EducationalDataMapper.map(rs),
                CityHubMapper.map(rs)
        );
    }
}

final class GeoMetaWrapper {
    private GeoMetaWrapper() {}

    static Optional<GeoMetadata> map(ResultSet rs) throws SQLException {
        String sj = rs.getString("latitude");
        if(sj == null) return Optional.empty();

        return Optional.of(new GeoMetadata(rs.getDouble("latitude"),rs.getDouble("longitude"),rs.getString("state")));
    }
}

final class EducationalDataMapper{
    private EducationalDataMapper(){}

    static Optional<EducationalData> map(ResultSet rs) throws SQLException {
        String sj = rs.getString("e.warpId");
        if(sj == null) return Optional.empty();

        return Optional.of(new EducationalData());
    }
}

final class CityHubMapper{
    private CityHubMapper(){}

    static Optional<CityHubData> map(ResultSet rs) throws SQLException {
        String sj = rs.getString("c.warpId");
        if(sj == null) return Optional.empty();

        return Optional.of(new CityHubData());
    }
}
