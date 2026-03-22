package pl.polsl.blissapp.ui.views.blisswriter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.views.keyboard.BlissKeyboardViewModel;

/**
 * Fragment that allows the user to compose a message using Bliss symbols.
 * It shows the current message, a set of hints based on selected primitives,
 * and a filter row showing chosen primitives.
 */
@AndroidEntryPoint
public class BlissWriterFragment extends Fragment {

    private BlissKeyboardViewModel keyboardViewModel;
    private BlissWriterViewModel writerViewModel;
    private FilterAdapter filterAdapter;
    private SymbolAdapter hintsAdapter;
    private SymbolAdapter messageAdapter;

    private View cursorVertical;
    private View cursorHorizontal;
    private View cursorContainer;
    private RecyclerView messageRecyclerView;
    private int currentCursorIndex = 0;
    private List<BlissWriterViewModel.MessageItem> currentItems;

    @Inject
    SymbolRepository symbolRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bliss_writer, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        keyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);
        writerViewModel = new ViewModelProvider(this).get(BlissWriterViewModel.class);

        cursorVertical = view.findViewById(R.id.cursor_vertical);
        cursorHorizontal = view.findViewById(R.id.cursor_horizontal);
        cursorContainer = view.findViewById(R.id.cursor_container);
        messageRecyclerView = view.findViewById(R.id.rv_message);

        setupFilterView(view);
        setupHintsView(view);
        setupMessageView(view);

        keyboardViewModel.clearInputs();

        keyboardViewModel.getPrimitiveInput().observe(getViewLifecycleOwner(),
                writerViewModel::putPrimitive);

        keyboardViewModel.getControlInput().observe(getViewLifecycleOwner(), controlKey -> {
            if (controlKey == null) return;
            switch (controlKey) {
                case POP_SYMBOL:
                    writerViewModel.popSymbol();
                    break;
                case PUSH_SYMBOL:
                    writerViewModel.confirmSymbol();
                    break;
            }
        });

        writerViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            currentItems = state.items;
            currentCursorIndex = state.cursorIndex;
            messageAdapter.update(state.items);

            // 1. STAGE ONE: Trigger the initial scroll.
            // If the item is off-screen, this forces the RecyclerView to move towards it,
            // bringing it on-screen so the SVG can start loading.
            messageRecyclerView.post(() -> scrollToCursor(currentCursorIndex));

            scheduleCursorUpdate();
        });

        writerViewModel.getHints().observe(getViewLifecycleOwner(),
                symbols -> hintsAdapter.update(symbols));

        writerViewModel.getFilter().observe(getViewLifecycleOwner(),
                filterAdapter::update);

        writerViewModel.getFailure().observe(getViewLifecycleOwner(), exception -> {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        });

        messageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                updateCursorPosition();
            }
        });

        messageRecyclerView.getViewTreeObserver().addOnPreDrawListener(() -> {
            updateCursorPosition();
            return true;
        });

        cursorContainer.post(() -> {
            Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
            if (blink != null) {
                cursorContainer.startAnimation(blink);
            }
        });

        scheduleCursorUpdate();
    }

    private void scheduleCursorUpdate() {
        if (messageRecyclerView == null) return;
        messageRecyclerView.post(this::updateCursorPosition);
    }

    private void updateCursorPosition() {
        if (!isAdded() || messageRecyclerView == null ||
                messageRecyclerView.getLayoutManager() == null ||
                currentItems == null || currentCursorIndex < 0 ||
                currentCursorIndex >= currentItems.size()) {
            cursorVertical.setVisibility(View.INVISIBLE);
            cursorHorizontal.setVisibility(View.INVISIBLE);
            return;
        }

        View itemView = messageRecyclerView.getLayoutManager()
                .findViewByPosition(currentCursorIndex);

        if (itemView == null || itemView.getWidth() <= 0 || itemView.getHeight() <= 0) {
            cursorVertical.setVisibility(View.INVISIBLE);
            cursorHorizontal.setVisibility(View.INVISIBLE);
            return;
        }

        int[] containerLocation = new int[2];
        int[] itemLocation = new int[2];
        cursorContainer.getLocationOnScreen(containerLocation);
        itemView.getLocationOnScreen(itemLocation);

        float relativeX = itemLocation[0] - containerLocation[0];
        float relativeY = itemLocation[1] - containerLocation[1];

        BlissWriterViewModel.MessageItem currentItem = currentItems.get(currentCursorIndex);
        boolean isSymbol = currentItem instanceof BlissWriterViewModel.MessageItem.SymbolItem;

        if (isSymbol) {
            cursorVertical.setVisibility(View.INVISIBLE);
            cursorHorizontal.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = cursorHorizontal.getLayoutParams();
            if (params.width != itemView.getWidth()) {
                params.width = itemView.getWidth();
                cursorHorizontal.setLayoutParams(params);
            }

            float targetX = relativeX - cursorHorizontal.getLeft();
            float targetY = relativeY + itemView.getHeight() -
                    cursorHorizontal.getHeight() - cursorHorizontal.getTop() - 4;

            cursorHorizontal.setTranslationX(targetX);
            cursorHorizontal.setTranslationY(targetY);
        } else {
            cursorHorizontal.setVisibility(View.INVISIBLE);
            cursorVertical.setVisibility(View.VISIBLE);

            float targetX = relativeX + (itemView.getWidth() / 2f) -
                    (cursorVertical.getWidth() / 2f) - cursorVertical.getLeft();
            float targetY = relativeY + itemView.getPaddingTop() - cursorVertical.getTop();

            cursorVertical.setTranslationX(targetX);
            cursorVertical.setTranslationY(targetY);
        }
    }

    private void scrollToCursor(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) messageRecyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        if (position >= firstVisible && position <= lastVisible && firstVisible != -1) {
            return;
        }

        RecyclerView.SmoothScroller smoothScroller = getScroller(position);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    private RecyclerView.SmoothScroller getScroller(int position) {
        int offsetPx = (int) (12 * getResources().getDisplayMetrics().density);

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_END;
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                // 2. Corrected math to align the end of the item with the end of the RecyclerView
                return (boxEnd - offsetPx) - viewEnd;
            }
        };
        smoothScroller.setTargetPosition(position);
        return smoothScroller;
    }

    private void setupFilterView(View root) {
        RecyclerView filterView = root.findViewById(R.id.rv_filters);
        filterAdapter = new FilterAdapter(writerViewModel);
        filterView.setAdapter(filterAdapter);
        filterView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupHintsView(View root) {
        RecyclerView hintsView = root.findViewById(R.id.rv_hints);

        hintsAdapter = new SymbolAdapter(symbolRepository, R.layout.item_bliss_symbol,
                (position, item) -> writerViewModel.selectHint((Symbol) item));

        hintsView.setAdapter(hintsAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        hintsView.setLayoutManager(layoutManager);

        hintsView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                int width = Math.max(0, right - left - hintsView.getPaddingLeft() - hintsView.getPaddingRight());
                float defaultSize = getResources().getDimension(R.dimen.writer_hint_default_size);

                int spanCount = Math.max(1, (int) Math.ceil(width / defaultSize));
                if (layoutManager.getSpanCount() != spanCount)
                {
                    layoutManager.setSpanCount(spanCount);
                }
            }
        });
    }

    private void setupMessageView(View root) {
        messageAdapter = new SymbolAdapter(symbolRepository,
                R.layout.item_bliss_message_symbol,
                R.layout.item_bliss_cursor,
                (position, item) -> writerViewModel.setCursorIndex(position));

        // 3. Added listener to trigger scroll only AFTER the image has fully loaded and laid out
        messageAdapter.setOnImageRenderedListener(position -> {
            if (position == currentCursorIndex) {
                scrollToCursor(currentCursorIndex);
                scheduleCursorUpdate(); // Ensure cursor line updates to the new, exact dynamic width
            }
        });

        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }
}