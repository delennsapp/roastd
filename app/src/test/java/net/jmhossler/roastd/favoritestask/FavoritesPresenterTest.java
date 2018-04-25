package net.jmhossler.roastd.favoritestask;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;

import net.jmhossler.roastd.applicationtask.ApplicationPresenter;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.data.shopApplicationInfo.ShopApplicationInfo;
import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;
import net.jmhossler.roastd.viewtask.SearchableItemListContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class FavoritesPresenterTest {

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

    // For simulating TextUtils.isEmpty
    PowerMockito.mockStatic(TextUtils.class);
    PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer((Answer<Boolean>) invocation -> {
      CharSequence a = (CharSequence) invocation.getArguments()[0];
      return !(a != null && a.length() > 0);
    });
    presenter = new FavoritesPresenter(view, firebaseAuth, searchableItemDataSource, userDataSource);
  }

  @Test
  public void createPresenter_setsThePresenterToView() {
    presenter = new FavoritesPresenter(view, firebaseAuth, searchableItemDataSource, userDataSource);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void onStart_favoritesRetrieved() {
    presenter.start();

    /* it's difficult to test asynchronous, TODO
    verify(userDataSource).getUser(firebaseAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {}

      @Override
      public void onDataNotAvailable() {}
    });
    */
  }
}
