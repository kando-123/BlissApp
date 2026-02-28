package pl.polsl.blissapp.ui.views.keyboard;

public class ControlKeyUI extends KeyUI {
    public final ControlKey action;

    public ControlKeyUI(ControlKey action, float weight) {
        super(weight);
        this.action = action;
    }
}