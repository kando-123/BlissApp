package pl.polsl.blissapp.ui.views.keyboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.polsl.blissapp.common.Radical;

public class BlissKeyboardViewModel extends ViewModel
{
    private final MutableLiveData<Radical> radicalKey;
    private final MutableLiveData<ControlKey> controlKey;

    public BlissKeyboardViewModel()
    {
        radicalKey = new MutableLiveData<>();
        controlKey = new MutableLiveData<>();
    }

    void setRadical(Radical radical)
    {
        radicalKey.setValue(radical);
    }

    void setControl(ControlKey control)
    {
        controlKey.setValue(control);
    }

    public LiveData<Radical> getRadical()
    {
        return radicalKey;
    }

    public LiveData<ControlKey> getControl()
    {
        return controlKey;
    }
}
