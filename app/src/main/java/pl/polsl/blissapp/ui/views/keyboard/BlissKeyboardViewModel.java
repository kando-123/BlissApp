package pl.polsl.blissapp.ui.views.keyboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;

@HiltViewModel
public class BlissKeyboardViewModel extends ViewModel {
    private final MutableLiveData<Radical> radicalInput = new MutableLiveData<>();
    private final MutableLiveData<Indicator> indicatorInput = new MutableLiveData<>();
    private final MutableLiveData<ControlKey> controlInput = new MutableLiveData<>();

    @Inject
    public BlissKeyboardViewModel() {}

    public void onRadicalKeyTapped(Radical radical) {
        radicalInput.setValue(radical);
    }

    public void onIndicatorKeyTapped(Indicator indicator) {
        indicatorInput.setValue(indicator);
    }

    public void onControlKeyTapped(ControlKey key) {
        controlInput.setValue(key);
    }

    public LiveData<Radical> getRadicalInput() { return radicalInput; }
    public LiveData<Indicator> getIndicatorInput() { return indicatorInput; }
    public LiveData<ControlKey> getControlInput() { return controlInput; }
}