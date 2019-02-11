package cn.skyui.practice.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    static final String BASE_URL = "https://app.foundersc.com/";

    // https://app.foundersc.com/quote/search/m?text=600&count=40
    @GET("quote/search/m")
    Observable<FounderHttpResponse<String>> search(@Query("text") String text, @Query("count") int count);

}
