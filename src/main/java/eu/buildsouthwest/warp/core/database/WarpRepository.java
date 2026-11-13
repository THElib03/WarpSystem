package eu.buildsouthwest.warp.core.database;

import eu.buildsouthwest.warp.core.model.*;
import org.bukkit.Bukkit;

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
    private final String GET_WARP = "SELECT b.*, g.*, e.*, c.* FROM bte_warps b LEFT JOIN geo_meta g ON g.warpID = b.id LEFT JOIN educational e ON e.warpID = b.id LEFT JOIN city_hub c ON c .warpID = b.id WHERE b.id = ?;";
    private final String GET_WARPS = "SELECT b.*, g.*, e.*, c.* FROM bte_warps b LEFT JOIN geo_meta g ON g.warpID = b.id LEFT JOIN educational e ON e.warpID = b.id LEFT JOIN city_hub c ON c .warpID = b.id";
    private final String NEW_WARP = "INSERT IGNORE INTO bte_warps (id,`name`,world,`description`,`type`,`x`,`y`,`z`,pitch,yaw,icon,visits,creatorId,creationDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,NOW());";
    private final String NEW_AUTHOR = "INSERT IGNORE INTO author (creatorId,creatorName) VALUES (?,?);";
    private final String NEW_GEO = "INSERT IGNORE INTO geo_meta VALUES (?,?,?);";
    private final String DROP_WARP = "DELETE FROM bte_warps WHERE = ?";

    private final DatabaseService db;

    public WarpRepository(DatabaseService db){
        this.db = db;
    }

    public CompletableFuture<Optional<Warp>> getWarp(UUID warp) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = db.getConnection();
                PreparedStatement query = conn.prepareStatement(GET_WARP)) {
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
                PreparedStatement query = db.getConnection().prepareStatement(GET_WARPS);
                ResultSet rs = query.executeQuery();

                while(rs.next()) warps.add(WarpWrapper.map(rs));
                return warps;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                return warps;
            }
        }, db.executor());
    }

    public CompletableFuture<Boolean> newWarp(Warp warp, Author auth) {
        return CompletableFuture.supplyAsync( () -> {
            try{
                PreparedStatement queryAuthor = db.getConnection().prepareStatement(NEW_AUTHOR);
                PreparedStatement queryWarp = db.getConnection().prepareStatement(NEW_WARP);

                queryAuthor.setString(1, auth.id().toString());
                queryAuthor.setString(2, auth.name());

                queryWarp.setString(1, warp.uuid().toString());
                queryWarp.setString(2, warp.displayName());
                queryWarp.setString(3, warp.world());
                queryWarp.setString(4, warp.description());
                queryWarp.setString(5, warp.type());
                queryWarp.setDouble(6, warp.x());
                queryWarp.setDouble(7, warp.y());
                queryWarp.setDouble(8, warp.z());
                queryWarp.setDouble(9, warp.pitch());
                queryWarp.setDouble(10, warp.yaw());
                queryWarp.setString(11, warp.icon());
                queryWarp.setInt(12, 0);
                queryWarp.setString(13, auth.id().toString());

                if(queryAuthor.execute() && queryWarp.execute()){
                    queryAuthor.close();
                    queryWarp.close();

                    return true;
                }
                else{
                    throw new SQLException();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                Bukkit.getLogger().info("SQL error for new warp");
                return false;
            }
        }, db.executor());
    }

    public CompletableFuture<Boolean> deleteWarp(String warp){
        return CompletableFuture.supplyAsync( () -> {
            try{
                PreparedStatement dropWarp = db.getConnection().prepareStatement(DROP_WARP);
                dropWarp.setString(1, warp);

                if(dropWarp.execute()){
                    return true;
                }
                else{
                    throw new SQLException("Failed to delete warp");
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                Bukkit.getLogger().info("SQL error for delete warp");
                return false;
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
        String lat = rs.getString("latitude");
        if(lat == null) return Optional.empty();

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

final class AuthorMapper{
    private AuthorMapper(){}
    static Optional<Author> map(ResultSet rs) throws SQLException {
        String authName = rs.getString("e.creatorName");

        if(authName == null) return Optional.empty();
        return Optional.of(new Author(UUID.fromString(rs.getString("a.creatorId")), authName));
    }
}