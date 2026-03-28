package pl.polsl.blissapp.ui.views.alchemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CraftingTable {
    private final List<Object> mItems;

    CraftingTable() {
        mItems = Collections.emptyList();
    }

    private CraftingTable(List<Object> items) {
        // Wraps the list in an unmodifiable view without making a second copy
        mItems = Collections.unmodifiableList(items);
    }

    public CraftingTable addItem(Object item) {
        List<Object> copy = new ArrayList<>(mItems);
        copy.add(item);
        return new CraftingTable(copy);
    }

    public CraftingTable removeItem(Object item) {
        List<Object> copy = new ArrayList<>(mItems);
        copy.remove(item);
        return new CraftingTable(copy);
    }

    public CraftingTable removeLast() {
        if (mItems.isEmpty()) return this;
        List<Object> copy = new ArrayList<>(mItems);
        copy.remove(copy.size() - 1);
        return new CraftingTable(copy);
    }

    public List<Object> getAllItems() {
        return mItems; // It's already unmodifiable!
    }
}