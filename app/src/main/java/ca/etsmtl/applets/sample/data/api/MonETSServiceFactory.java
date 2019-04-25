package ca.etsmtl.applets.sample.data.api;

import android.content.Context;

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
                    .build();

            instance = retrofit.create(MonETSService.class);
        }

        return instance;
    }
}
