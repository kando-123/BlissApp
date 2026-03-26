package pl.polsl.blissapp.ui.views.alchemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CraftingTable {
    private final List<Object> mItems;

    CraftingTable() {
        mItems = new ArrayList<>();
    }

    private CraftingTable(List<Object> items) {
        mItems = new ArrayList<>(items);
    }

    public CraftingTable addItem(Object item) {
        List<Object> copy = new ArrayList<>(mItems);
        copy.add(item);
        return new CraftingTable(copy);
    }

    public CraftingTable removeItem(Object item) {
        List<Object> copy = new ArrayList<>(mItems);
        copy.remove(item); // Removes the first occurrence
        return new CraftingTable(copy);
    }

    public CraftingTable removeLast() {
        if (mItems.isEmpty()) return this;
        List<Object> copy = new ArrayList<>(mItems);
        copy.remove(copy.size() - 1);
        return new CraftingTable(copy);
    }

    public List<Object> getAllItems() {
        return Collections.unmodifiableList(mItems);
    }
}