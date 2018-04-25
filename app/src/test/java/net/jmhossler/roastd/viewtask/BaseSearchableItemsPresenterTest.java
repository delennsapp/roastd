package net.jmhossler.roastd.viewtask;

import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.data.bean.Bean;
import net.jmhossler.roastd.data.drink.Drink;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;
import net.jmhossler.roastd.favoritestask.FavoritesPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Map;


public class BaseSearchableItemsPresenterTest {

  @Mock
  private SearchableItemListContract.View view;
  @Mock
  private FirebaseAuth firebaseAuth;
  @Mock
  private SearchableItemDataSource searchableItemDataSource;
  @Mock
  private UserDataSource userDataSource;

  private FavoritesPresenter presenter;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    presenter = new FavoritesPresenter(view, firebaseAuth, searchableItemDataSource, userDataSource);
    presenter.mItems = new ArrayList<SearchableItem>();
    presenter.mItems.add(new Bean("123", "whoa", "epic bean", null, "what is this", "yo mamma", "11111"));
    presenter.mItems.add(new Drink("321", "nice", "epic drink", null, "a nice drink type", 12345.11, "11111"));
    presenter.mUser = new User("uuid", "email@yomamma.com", "Ree", "nope");
  }

  @Test
  public void testSetFavorite() {
    presenter.setFavorite(1, true);
    Map<String, Boolean> favorites = presenter.mUser.getFavoriteUUIDs();
    assert favorites != null;
    assert favorites.get("321");
    assert favorites.get("123") == null;
    presenter.setFavorite(0, true);
    assert favorites.size() == 2;
    assert favorites.get("123");
    presenter.setFavorite(0, false);
    assert favorites.size() == 1;
    assert favorites.get("123") == null;
  }
}
