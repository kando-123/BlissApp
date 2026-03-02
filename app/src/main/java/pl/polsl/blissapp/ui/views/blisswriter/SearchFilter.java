package pl.polsl.blissapp.ui.views.blisswriter;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.data.model.Primitive;

class SearchFilter
{
    private final List<Primitive> mPrimitives;

    SearchFilter()
    {
        mPrimitives = new ArrayList<>();
    }

    void addPrimitive(Primitive primitive)
    {
        mPrimitives.add(primitive);
    }

    void removePrimitive(Primitive primitive)
    {
        mPrimitives.remove(primitive);
    }

    List<Primitive> getPrimitives()
    {
        return List.copyOf(mPrimitives);
    }
}
