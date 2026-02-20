package pl.polsl.blissapp.ui.views.keyboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import pl.polsl.blissapp.common.Radical;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.data.model.Radical;

@HiltViewModel
public class BlissKeyboardViewModel extends ViewModel {
    private final MutableLiveData<Radical> radicalInput = null;
    private final MutableLiveData<ControlKey> controlInput = null;

    @Inject
    public BlissKeyboardViewModel()
    {
        radicalInput = new MutableLiveData<>();
        controlInput = new MutableLiveData<>();
    }

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