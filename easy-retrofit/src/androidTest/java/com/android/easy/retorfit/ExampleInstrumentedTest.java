package com.android.easy.retorfit;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.easy.retrofit.ApiService;
import com.android.easy.retrofit.initerceptor.TokenInterceptor;

import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.android.easy.retorfit.test", appContext.getPackageName());


        ApiService.init(appContext,"")
                .setTimeOut(30,30)
                .addInterceptor(new TokenInterceptor() {
                    @Override
                    public String getToken() {
                        return null;
                    }

                    @Override
                    public boolean testResponse(Response response) {
                        return false;
                    }

                    @Override
                    public void toLogin() {

                    }
                });
    }
}
