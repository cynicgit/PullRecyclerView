package ip.cynic.pullrecyclerview.network;

import ip.cynic.pullrecyclerview.network.api.GankApi;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cynic on 2016/6/2.
 */
public class NetworkClient {

    private final static String GANK_URL = "http://gank.io/api/";
    private final static String WenXin_URL = "http://apicloud.mob.com/";

    private static GankApi mGankApi = null;

    private static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static GsonConverterFactory mGsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory mRxjavaCallAdapter = RxJavaCallAdapterFactory.create();


    public static GankApi getGankApi() {

        if(mGankApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(mOkHttpClient)
                    .baseUrl(GANK_URL)
                    .addConverterFactory(mGsonConverterFactory)
                    .addCallAdapterFactory(mRxjavaCallAdapter)
                    .build();
            mGankApi = retrofit.create(GankApi.class);
        }
        return mGankApi;
    }


}
