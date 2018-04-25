package net.jmhossler.roastd.data.searchableItem;

import android.util.Log;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import net.jmhossler.roastd.data.bean.FirebaseRTBeanRepository;
import net.jmhossler.roastd.data.drink.FirebaseRTDrinkRepository;
import net.jmhossler.roastd.data.shop.FirebaseRTShopRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class FirebaseRTSearchableItemRepository extends FirebaseRTBaseRepository implements SearchableItemDataSource {

  private static final String TAG = "FirebaseRTSearchableItemRepository";

  private static FirebaseRTSearchableItemRepository sInstance = null;
  private static FirebaseRTShopRepository mShopRepository = null;
  private static FirebaseRTBeanRepository mBeanRepository = null;
  private static FirebaseRTDrinkRepository mDrinkRepository = null;
  private static Client algolia = null;
  private static Index drinks = null;
  private static Index shops = null;
  private static Index beans = null;

  public static FirebaseRTSearchableItemRepository getInstance() {
    if (sInstance == null) {
      sInstance = new FirebaseRTSearchableItemRepository();
      mShopRepository = FirebaseRTShopRepository.getInstance();
      mBeanRepository = FirebaseRTBeanRepository.getInstance();
      mDrinkRepository = FirebaseRTDrinkRepository.getInstance();
      algolia = new Client("YGJUJVF7P1", "27139f87e5e1924e3383cdc035e74804");
      drinks = algolia.getIndex("drinks");
      shops = algolia.getIndex("shops");
      beans = algolia.getIndex("beans");
    }
    return sInstance;
  }

  @Override
  public void get(String itemId, GetCallback callback) {

    final int SHOP = 0;
    final int DRINK = 1;
    final int BEAN = 2;

    List<TaskCompletionSource<SearchableItem>> queries = new ArrayList<>();
    queries.add(new TaskCompletionSource<>());
    queries.add(new TaskCompletionSource<>());
    queries.add(new TaskCompletionSource<>());

    getItemFromRepository(mShopRepository, itemId, new BaseDataSource.GetCallback() {
      @Override
      public void onLoaded(SearchableItem item) {
        queries.get(SHOP).setResult(item);
      }

      @Override
      public void onDataNotAvailable() {
        queries.get(SHOP).setException(new IOException("Could not get shop data from repository"));
      }
    });

    getItemFromRepository(mDrinkRepository, itemId, new BaseDataSource.GetCallback() {
      @Override
      public void onLoaded(SearchableItem item) {
        queries.get(DRINK).setResult(item);
      }

      @Override
      public void onDataNotAvailable() {
        queries.get(DRINK).setException(new IOException("Could not get drink data from repository"));
      }
    });

    getItemFromRepository(mBeanRepository, itemId, new BaseDataSource.GetCallback() {
      @Override
      public void onLoaded(SearchableItem item) {
        queries.get(BEAN).setResult(item);
      }

      @Override
      public void onDataNotAvailable() {
        queries.get(BEAN).setException(new IOException("Could not get bean data from repository"));
      }
    });

    List <Task<SearchableItem>> tasks =
      queries.stream()
        .map(tcs -> tcs.getTask())
        .collect(Collectors.toList());

    Task<Void> all = Tasks.whenAll(tasks);
    all.addOnSuccessListener(voidTask -> {
      List<SearchableItem> results =
        tasks.stream()
          .map(task -> task.getResult())
          .filter(item -> item != null)
          .collect(Collectors.toList());

      if (results.size() == 0) {
        callback.onLoaded(null);
      } else if (results.size() == 1) {
        callback.onLoaded(results.get(0));
      } else {
        // well shit!
        Log.d(TAG, itemId + ": Multiple instances found in multiple repositories");
      }
    });
    all.addOnFailureListener(e -> {
      callback.onDataNotAvailable();
      Log.e(TAG, e.getMessage());
    });
  }

  @Override
  public void getSearchableItems(List <String> ids, LoadSearchableItemsCallback callback) {
    List<TaskCompletionSource<SearchableItem>> queries = new ArrayList<>();

    for (String id : ids) {
      TaskCompletionSource<SearchableItem> ct = new TaskCompletionSource<>();
      get(id, new GetCallback() {
        @Override
        public void onLoaded(SearchableItem item) {
          ct.setResult(item);
        }

        @Override
        public void onDataNotAvailable() {
          ct.setException(new IOException("Could not retrieve searchable item..."));
        }
      });
      queries.add(ct);
    }

    List <Task<SearchableItem>> tasks =
      queries.stream()
        .map(tcs -> tcs.getTask())
        .collect(Collectors.toList());

    Task<Void> all = Tasks.whenAll(tasks);
    all.addOnSuccessListener(voidTask -> {
      List<SearchableItem> results =
        tasks.stream()
          .map(task -> task.getResult())
          .filter(item -> item != null)
          .collect(Collectors.toList());

      if (results.size() == 0) {
        callback.onSearchableItemsLoaded(null);
      }

      if (results.size() != ids.size()) {
        Log.w(TAG, "We didn't get all we were looking for...");
      }

      callback.onSearchableItemsLoaded(results);
    });
    all.addOnFailureListener(e -> {
      callback.onDataNotAvailable();
      Log.e(TAG, e.getMessage());
    });
  }

  private void handleResults(JSONObject jsonObject, AlgoliaException e,
                             TaskCompletionSource<List<SearchableItem>> tcs) {
    if (e != null) {
      Log.e(TAG, e.getMessage());
      tcs.setException(e);
      return;
    }

    try {
      List <String> ids = new ArrayList<>();
      JSONArray results = jsonObject.getJSONArray("hits");
      for (int i = 0; i < results.length(); ++i) {
        ids.add(results.getJSONObject(i).getString("objectID"));
      }
      getSearchableItems(ids, new LoadSearchableItemsCallback() {
        @Override
        public void onSearchableItemsLoaded(List<SearchableItem> items) {
          tcs.setResult(items);
        }

        @Override
        public void onDataNotAvailable() {
          tcs.setException(new IOException(TAG + "could not get qury results"));
        }
      });
    } catch (JSONException e1) {
      e1.printStackTrace();
      tcs.setException(e);
    }
  }

  public void getSearchableItemsByText(String query, LoadSearchableItemsCallback callback)  {

    List<IndexQuery> queries = new ArrayList<>();
    List<String> ids = new ArrayList<>();

    try {
      drinks.setSettingsAsync(new JSONObject()
          .put("attributesToRetrieve", new JSONArray())
          .put("searchableAttributes", new JSONArray()
            .put("description").put("name").put("type"))
        , null);
      shops.setSettingsAsync(new JSONObject()
          .put("attributesToRetrieve", new JSONArray())
          .put("searchableAttributes", new JSONArray()
            .put("description").put("name").put("address"))
        , null);
      beans.setSettingsAsync(new JSONObject()
          .put("attributesToRetrieve", new JSONArray())
          .put("searchableAttributes", new JSONArray()
            .put("description").put("name").put("roastType"))
        , null);

    } catch(Exception e) {
      e.printStackTrace();
      Log.e(TAG, e.getMessage());
    }

    queries.add(new IndexQuery(
        drinks,
        new Query(query)
          .setResponseFields("hits")
          .setAttributesToRetrieve("")
      )
    );
    queries.add(new IndexQuery(
        shops,
        new Query(query)
          .setResponseFields("hits")
          .setAttributesToRetrieve("")
      )
    );
    queries.add(new IndexQuery(
        beans,
        new Query(query)
          .setResponseFields("hits")
          .setAttributesToRetrieve("")
      )
    );

    final Client.MultipleQueriesStrategy strategy = Client.MultipleQueriesStrategy.NONE;


    CompletionHandler completionHandler = new CompletionHandler() {
      @Override
      public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
        if (e != null) {
          Log.e(TAG, e.getMessage());
          return;
        }

        try {
          JSONArray results = jsonObject.getJSONArray("results");
          for(int i = 0; i < results.length(); ++i) {
            JSONArray hits = results.getJSONObject(i).getJSONArray("hits");
            for(int j = 0; j < hits.length(); j++) {
              ids.add(hits.getJSONObject(j).getString("objectID"));
            }
          }
        } catch (JSONException e1) {
          e1.printStackTrace();
        }
        getSearchableItems(ids, callback);
      }};

      algolia.multipleQueriesAsync(queries, strategy, completionHandler);
  }

  private void getItemFromRepository(FirebaseRTBaseRepository repository, String itemId, BaseDataSource.GetCallback callback) {
    repository.get(itemId, callback);
  }
}
