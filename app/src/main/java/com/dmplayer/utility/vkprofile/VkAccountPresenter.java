package com.dmplayer.utility.vkprofile;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.externalaccount.ExternalAccountPresenter;
import com.dmplayer.externalaccount.ExternalAccountView;

public class VkAccountPresenter implements ExternalAccountPresenter {
    private final ExternalAccountView view;

    private VkProfileHelper profileHelper;

    private Context context;

    public VkAccountPresenter(ExternalAccountView view) {
        this.view = view;
    }

    @Override
    public void onCreate(Fragment fragment) {
        context = fragment.getActivity();

        profileHelper = new VkProfileHelper.Builder(context)
                .build();

        if (profileHelper.isLogged()) {
            VkProfileModel profile = (VkProfileModel) profileHelper.loadProfileOffline();
            view.showProfile(profile);
        } else {
            view.showLogIn();
        }
    }

    @Override
    public void logIn(String token, String userId) {
        profileHelper = new VkProfileHelper.Builder(context)
                .setLogged(true)
                .setToken(token)
                .setUserId(userId)
                .build();

        new LoadProfileTask().execute();
    }

    @Override
    public void refresh() {
        new LoadProfileTask().execute();
    }

    @Override
    public void logOut() {
        profileHelper.logOut();
        view.getBehaviorCallbacks().onLoggedOut();
    }

    @Override
    public void onDestroy() {
        profileHelper.logOut();
        view.getBehaviorCallbacks().onLoggedOut();
    }

    private class LoadProfileTask extends AsyncTask<Void, Void, VkProfileModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            view.getBehaviorCallbacks().onLoadingStarted();
        }

        @Override
        protected VkProfileModel doInBackground(Void... voids) {
            return (VkProfileModel) profileHelper.loadProfileOnline();
        }

        @Override
        protected void onPostExecute(VkProfileModel profile) {
            super.onPostExecute(profile);

            view.showProfile(profile);
            view.getBehaviorCallbacks().onLoadingFinished();
        }
    }
}