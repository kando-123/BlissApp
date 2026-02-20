package pl.polsl.blissapp.ui.views.keyboard;

/**
 * The base blueprint for any key on the keyboard.
 */
public abstract class KeyUI {
    public final int viewId;

    public KeyUI(int viewId) {
        this.viewId = viewId;
    }
}