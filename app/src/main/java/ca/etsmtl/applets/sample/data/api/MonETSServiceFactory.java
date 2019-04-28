package ca.etsmtl.applets.sample.data.api;

import android.content.Context;

import ca.etsmtl.applets.sample.data.api.util.LiveDataCallAdapterFactory;
import ca.etsmtl.applets.sample.data.api.util.TLSUtilities;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sonphil on 25-04-19.
 */

public final class MonETSServiceFactory {
    private static volatile MonETSService instance;

    public static MonETSService getMonETSService(Context context) {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://portail.etsmtl.ca/api/")
                    .client(TLSUtilities.createETSOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .build();

            instance = retrofit.create(MonETSService.class);
        }

        return instance;
    }
}
