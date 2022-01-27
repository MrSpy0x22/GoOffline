package pl.gooffline;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import pl.gooffline.services.BroadcastLogger;

@RunWith(AndroidJUnit4.class)
public class BroadcastTest {
    private final Instant now = Instant.now();

    @Before
    public void prepareTest() {
    }

    @After
    public void finishTest() {
    }

    /**
     * Serializacja i deserializacja obiektu klasy w celu porównania danych.
     */
    @Test
    public void serializeAndDeserializeLog() throws JSONException {
        final String logMessage = "Auth message";
        BroadcastLogger loggerInfoOriginal = new BroadcastLogger(
                BroadcastLogger.LogType.LT_AUTH.getType(), logMessage , Instant.ofEpochSecond(now.getEpochSecond())
        );
        String json = loggerInfoOriginal.toJSON();

        BroadcastLogger loggerInfoDeserialized = new BroadcastLogger(json);

        Assert.assertEquals(loggerInfoOriginal.getType() , loggerInfoDeserialized.getType());
        Assert.assertEquals(loggerInfoOriginal.getMessage() , loggerInfoDeserialized.getMessage());
        Assert.assertEquals(loggerInfoOriginal.getTime() , loggerInfoDeserialized.getTime());
    }
}
