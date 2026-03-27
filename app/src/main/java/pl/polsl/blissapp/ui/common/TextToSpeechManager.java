package pl.polsl.blissapp.ui.common;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class TextToSpeechManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "TTSManager";
    private TextToSpeech tts;
    private boolean isInitialized = false;

    @Inject
    public TextToSpeechManager(@ApplicationContext Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true;
        } else {
            Log.e(TAG, "Initialization failed");
        }
    }

    // Require the language to be passed directly every time
    public void speak(List<String> texts, String language) {
        if (!isInitialized || texts == null || texts.isEmpty()) return;

        // 1. Set the correct voice engine for this specific request
        Locale locale = "Polish".equalsIgnoreCase(language) ? new Locale("pl", "PL") : Locale.US;
        int result = tts.setLanguage(locale);

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(TAG, "Language not supported: " + language);
            return;
        }

        // 2. Speak the text
        tts.stop();
        for (String text : texts) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
            tts.playSilentUtterance(200, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
        }
    }
}