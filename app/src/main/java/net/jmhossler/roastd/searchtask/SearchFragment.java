package net.jmhossler.roastd.searchtask;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jmhossler.roastd.R;

public class SearchFragment extends Fragment implements SearchContract.View {

  private SearchContract.Presenter mPresenter;
  private SearchView searchBar;

  public static SearchFragment newInstance() { return new SearchFragment(); }

  public SearchFragment() {
    // Required empty public constructor
  }

  @Override
  public void setPresenter(@NonNull SearchContract.Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.search_fragment, container, false);
    searchBar = root.findViewById(R.id.search_bar);
    searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        mPresenter.search(query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    return root;
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.start();
  }
}
