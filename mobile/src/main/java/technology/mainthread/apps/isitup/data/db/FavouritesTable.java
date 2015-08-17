package technology.mainthread.apps.isitup.data.db;

import java.util.List;

import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;
import technology.mainthread.apps.isitup.data.vo.enumeration.StatusCode;

public interface FavouritesTable {

    void add(IsItUpInfo response);

    IsItUpInfo getFavourite(int id);

    IsItUpInfo getFavourite(String domain);

    boolean hasSite(String domain);

    List<IsItUpInfo> getAllFavourites();

    List<IsItUpInfo> getAllForStatusCode(StatusCode code);

    void update(int id, IsItUpInfo info);

    void update(IsItUpInfo info);

    boolean delete(int id);

    boolean delete(String domain);

    boolean deleteAll();

    boolean hasFavourites();
}
