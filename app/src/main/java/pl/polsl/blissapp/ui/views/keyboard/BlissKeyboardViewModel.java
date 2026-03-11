package pl.polsl.blissapp.ui.views.keyboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.data.model.Primitive;

@HiltViewModel
public class BlissKeyboardViewModel extends ViewModel
{
    private final MutableLiveData<Primitive> mPrimitiveInput;
    private final MutableLiveData<ControlKey> mControlInput;
    private final MutableLiveData<KeyboardMode> mKeyboardMode;

    public enum KeyboardMode {
        BLISS,
        ALPHANUMERIC
    }

    @Inject
    public BlissKeyboardViewModel()
    {
        mPrimitiveInput = new MutableLiveData<>();
        mControlInput = new MutableLiveData<>();
        mKeyboardMode = new MutableLiveData<>(KeyboardMode.BLISS);
    }

    public void onBlissKeyTapped(Primitive primitive)
    {
        mPrimitiveInput.setValue(primitive);
    }

    public void onControlKeyTapped(ControlKey key)
    {
        mControlInput.setValue(key);
    }

    public void setKeyboardMode(KeyboardMode mode) {
        mKeyboardMode.setValue(mode);
    }

    public LiveData<Primitive> getPrimitiveInput() { return mPrimitiveInput; }
    public LiveData<ControlKey> getControlInput() { return mControlInput; }
    public LiveData<KeyboardMode> getKeyboardMode() { return mKeyboardMode; }

    public void clearInputs()
    {
        mPrimitiveInput.setValue(null);
        mControlInput.setValue(null);
    }
}
