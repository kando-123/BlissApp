package pl.polsl.blissapp.ui.views.blisswriter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

/**
 * ViewModel that manages the Bliss writer state: message composition, primitive filter,
 * symbol hints, and user actions.
 */
@HiltViewModel
public class BlissWriterViewModel extends ViewModel {

    // ---------- Nested data classes ----------
    public static abstract sealed class MessageItem {
        public static final class SymbolItem extends MessageItem {
            public final Symbol symbol;
            public SymbolItem(Symbol symbol) { this.symbol = symbol; }
        }
        public static final class EmptySlot extends MessageItem {
            public EmptySlot() {}
        }
    }

    public static class WriterState {
        public final List<MessageItem> items;   // immutable list
        public final int cursorIndex;

        public WriterState(List<MessageItem> items, int cursorIndex) {
            this.items = Collections.unmodifiableList(new ArrayList<>(items));
            this.cursorIndex = cursorIndex;
        }
    }

    // ---------- Dependencies ----------
    private final SymbolRepository symbolRepository;

    // ---------- LiveData ----------
    private final MutableLiveData<WriterState> state = new MutableLiveData<>();
    private final MutableLiveData<List<Symbol>> hints = new MutableLiveData<>();
    private final MutableLiveData<Map<Primitive, Integer>> filter = new MutableLiveData<>();
    private final MutableLiveData<Exception> failure = new MutableLiveData<>();

    private static final int MAX_HINT_COUNT = 48;

    @Inject
    public BlissWriterViewModel(SymbolRepository symbolRepository) {
        this.symbolRepository = symbolRepository;

        // Initial state: one empty slot, cursor at 0.
        List<MessageItem> initialItems = new ArrayList<>();
        initialItems.add(new MessageItem.EmptySlot());
        state.setValue(new WriterState(initialItems, 0));

        // Start with empty filter
        filter.setValue(new LinkedHashMap<>());
    }

    // ---------- Public LiveData getters ----------
    public LiveData<WriterState> getState() { return state; }
    public LiveData<List<Symbol>> getHints() { return hints; }
    public LiveData<Map<Primitive, Integer>> getFilter() { return filter; }
    public LiveData<Exception> getFailure() { return failure; }

    // ---------- Public actions ----------
    /**
     * Adds a primitive to the current filter. Updates hints accordingly.
     */
    public void putPrimitive(@Nullable Primitive primitive) {
        if (primitive == null) return;
        Map<Primitive, Integer> current = new LinkedHashMap<>(filter.getValue());
        current.compute(primitive, (k, count) -> count == null ? 1 : count + 1);
        filter.setValue(current);
        updateHints();
    }

    /**
     * Removes a primitive from the current filter. Updates hints accordingly.
     */
    public void removePrimitive(@NonNull Primitive primitive) {
        Map<Primitive, Integer> current = new LinkedHashMap<>(filter.getValue());
        current.compute(primitive, (k, count) -> count == null || count <= 1 ? null : count - 1);
        filter.setValue(current);
        updateHints();
    }

    /**
     * Moves the cursor to the specified position in the current message.
     * Resets the filter and updates hints.
     */
    public void setCursorIndex(int index) {
        WriterState current = state.getValue();
        if (current == null) return;
        if (index < 0 || index >= current.items.size()) return;

        state.setValue(new WriterState(current.items, index));
        updateHints();
    }

    /**
     * Replaces the item at the current cursor position with the chosen symbol.
     * The message is then rebuilt to enforce the alternating pattern.
     * Filter is cleared and hints updated.
     */
    public void selectHint(Symbol symbol) {
        WriterState current = state.getValue();
        if (current == null) return;

        List<MessageItem> newItems = new ArrayList<>(current.items);
        int cursorIdx = current.cursorIndex;
        newItems.set(cursorIdx, new MessageItem.SymbolItem(symbol));

        WriterState rebuilt = rebuildState(newItems, cursorIdx);
        state.setValue(rebuilt);
        filter.setValue(new LinkedHashMap<>());
        updateHints();
    }

    /**
     * Moves cursor to the next position.
     */
    public void moveCursorRight() {
        WriterState current = state.getValue();
        if (current == null) return;
        int cursorIdx = current.cursorIndex;

        if (cursorIdx + 1 < current.items.size()) {
            setCursorIndex(cursorIdx + 1);
        }
    }

    /**
     * Moves cursor to the previous position.
     */
    public void moveCursorLeft() {
        WriterState current = state.getValue();
        if (current == null) return;
        int cursorIdx = current.cursorIndex;

        if (cursorIdx > 0) {
            setCursorIndex(cursorIdx - 1);
        }
    }

    /**
     * Removes the symbol at or before the cursor.
     * - If cursor is on a symbol: remove it, cursor moves to the preceding gap.
     * - If cursor is on a gap and not the first item: remove the symbol to the left,
     * cursor moves to the gap before that symbol.
     */
    public void popSymbol() {
        WriterState current = state.getValue();
        if (current == null || current.items.isEmpty()) return;

        List<MessageItem> items = new ArrayList<>(current.items);
        int cursorIdx = current.cursorIndex;

        MessageItem currentItem = items.get(cursorIdx);

        if (currentItem instanceof MessageItem.SymbolItem) {
            // Delete the symbol
            items.remove(cursorIdx);
            // Target the gap that was before the removed symbol
            WriterState rebuilt = rebuildState(items, Math.max(0, cursorIdx - 1));
            state.setValue(rebuilt);
        } else if (cursorIdx > 0) {
            // On a gap -> delete the symbol to the left
            items.remove(cursorIdx - 1);
            // Target the gap that was before the deleted symbol
            WriterState rebuilt = rebuildState(items, Math.max(0, cursorIdx - 2));
            state.setValue(rebuilt);
        } // else first gap, nothing to delete

        updateHints();
    }

    // ---------- Private helpers ----------
    /**
     * Rebuilds the message list to enforce the alternating pattern:
     * empty, symbol, empty, symbol, ... , empty.
     * @param rawItems          The items before rebuilding (may contain consecutive gaps).
     * @param targetItemIndex   Index in rawItems of the item that should be selected after rebuild.
     * @return A new WriterState with the rebuilt list and correct cursor index.
     */
    private WriterState rebuildState(List<MessageItem> rawItems, int targetItemIndex) {
        // Extract all symbols in order
        List<MessageItem.SymbolItem> symbols = new ArrayList<>();
        for (MessageItem item : rawItems) {
            if (item instanceof MessageItem.SymbolItem si) symbols.add(si);
        }

        // Build new alternating list: gap, (symbol, gap) repeated
        List<MessageItem> newList = new ArrayList<>();
        newList.add(new MessageItem.EmptySlot()); // initial gap
        for (MessageItem.SymbolItem symbol : symbols) {
            newList.add(symbol);
            newList.add(new MessageItem.EmptySlot());
        }

        // Determine new cursor index based on target item's type and position in rawItems
        MessageItem target = rawItems.get(targetItemIndex);
        int newCursorIndex;

        if (target instanceof MessageItem.SymbolItem) {
            // Find which symbol (by order) in the raw list
            int symbolPos = 0;
            for (MessageItem item : rawItems) {
                if (item instanceof MessageItem.SymbolItem) {
                    if (item == target) break;
                    symbolPos++;
                }
            }
            // In new list, the i-th symbol is at index 2*i + 1
            newCursorIndex = 2 * symbolPos + 1;
        } else {
            // Target is an empty slot – count how many symbols appear before it
            int symbolsBefore = 0;
            for (int i = 0; i < targetItemIndex; i++) {
                if (rawItems.get(i) instanceof MessageItem.SymbolItem) symbolsBefore++;
            }
            // The gap after `symbolsBefore` symbols is at index 2 * symbolsBefore
            newCursorIndex = 2 * symbolsBefore;
        }

        // Clamp to valid range (should be safe, but just in case)
        newCursorIndex = Math.min(newCursorIndex, newList.size() - 1);

        return new WriterState(newList, newCursorIndex);
    }

    /**
     * Fetches new symbol hints based on the current context symbol and filter primitives.
     * Updates the hints LiveData or posts an error.
     */
    private void updateHints() {
        WriterState currentState = state.getValue();
        if (currentState == null) return;

        Symbol contextSymbol = null;
        MessageItem current = currentState.items.get(currentState.cursorIndex);
        if (current instanceof MessageItem.SymbolItem symbolItem) {
            contextSymbol = symbolItem.symbol;
        }

        Map<Primitive, Integer> currentFilter = filter.getValue();
        if (currentFilter == null) currentFilter = new LinkedHashMap<>();

        symbolRepository.getMatchingSymbols(contextSymbol, currentFilter, MAX_HINT_COUNT,
                new Callback<List<Symbol>, Exception>() {
                    @Override
                    public void onSuccess(List<Symbol> data) {
                        hints.postValue(data);
                    }

                    @Override
                    public void onFailure(Exception data) {
                        hints.postValue(Collections.emptyList());
                        failure.postValue(data);
                    }
                });
    }
}
