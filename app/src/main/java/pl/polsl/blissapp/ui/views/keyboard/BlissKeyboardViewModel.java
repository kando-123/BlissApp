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
    private final MutableLiveData<Primitive> mRadicalInput;
    private final MutableLiveData<ControlKey> mControlInput;

    @Inject
    public BlissKeyboardViewModel()
    {
        mRadicalInput = new MutableLiveData<>();
        mControlInput = new MutableLiveData<>();
    }

    public void onBlissKeyTapped(Primitive primitive)
    {
        mRadicalInput.setValue(primitive);
    }

    public void onControlKeyTapped(ControlKey key)
    {
        mControlInput.setValue(key);
    }

    public LiveData<Primitive> getPrimitiveInput() { return mRadicalInput; }
    public LiveData<ControlKey> getControlInput() { return mControlInput; }
}