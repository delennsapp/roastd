package net.jmhossler.roastd.searchtask;


import android.content.Intent;
import android.text.Editable;

import net.jmhossler.roastd.BasePresenter;
import net.jmhossler.roastd.BaseView;

public interface SearchContract {
  interface View extends BaseView<SearchContract.Presenter> {

  }

  interface Presenter extends BasePresenter {

    void search(String query);
  }
}
