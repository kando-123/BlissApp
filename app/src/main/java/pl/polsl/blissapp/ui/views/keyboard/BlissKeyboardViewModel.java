package pl.polsl.blissapp.ui.views.keyboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import pl.polsl.blissapp.common.Radical;

public class BlissKeyboardViewModel extends ViewModel {
    private final MutableLiveData<Radical> radicalInput = new MutableLiveData<>();
    private final MutableLiveData<ControlKey> controlInput = new MutableLiveData<>();

    public void onRadicalKeyTapped(Radical radical) {
        radicalInput.setValue(radical);
    }

    public void onControlKeyTapped(ControlKey key) {
        controlInput.setValue(key);
    }

    public LiveData<Radical> getRadicalInput() {
        return radicalInput;
    }

    public LiveData<ControlKey> getControlInput() {
        return controlInput;
    }
}