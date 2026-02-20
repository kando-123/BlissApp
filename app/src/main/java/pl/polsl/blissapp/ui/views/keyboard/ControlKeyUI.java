package pl.polsl.blissapp.ui.views.keyboard;

/**
 * Represents a functional key (Space, Enter, Shift, Backspace).
 */
public class ControlKeyUI extends KeyUI {
    public final ControlKey action;

    public ControlKeyUI(int viewId, ControlKey action) {
        super(viewId);
        this.action = action;
    }
}