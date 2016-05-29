package project.stutisrivastava.waochers.util;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by stutisrivastava on 3/2/16.
 */
public class LearningToUseVolley extends Application {

        private static final String TAG = LearningToUseVolley.class.getName();
        private RequestQueue mRequestQueue;
        private static LearningToUseVolley mInstance;

        @Override
        public void onCreate() {
            super.onCreate();
            mInstance = this;
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        public static synchronized LearningToUseVolley getInstance() {
            return mInstance;
        }

        public RequestQueue getRequestQueue() {
            return mRequestQueue;
        }

        public <T> void add(Request<T> req) {
            req.setTag(TAG);
            getRequestQueue().add(req);
        }

        public void cancel() {
            mRequestQueue.cancelAll(TAG);
        }
}
