package com.lokaur.materialsearchview.interfaces;

public interface OnSearchListener {
    void onSearchViewShown();

    void onSearchViewClosed();

    void onSearchQuerySubmit(String searchQuery);

    void onSearchQueryChange(String searchQuery);

    void onSearchQueryCleared();
}
