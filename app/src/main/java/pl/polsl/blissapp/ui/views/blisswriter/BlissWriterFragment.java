package pl.polsl.blissapp.ui.views.blisswriter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

@AndroidEntryPoint
public class BlissWriterFragment extends Fragment
{
    private BlissKeyboardViewModel mKeyboardViewModel;
    private BlissWriterViewModel mWriterViewModel;
    private FilterAdapter mFilterAdapter;
    private SymbolAdapter mHintsAdapter;
    private SymbolAdapter mMessageAdapter;

    private View mCursorVertical;
    private View mCursorHorizontal;
    private View mCursorContainer;
    private RecyclerView mMessageRecyclerView;

    private int mCurrentCursorIndex = 0;
    private List<BlissWriterViewModel.MessageItem> mCurrentItems;

    @Inject
    SymbolRepository mSymbolRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bliss_writer, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mKeyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);
        mWriterViewModel = new ViewModelProvider(this).get(BlissWriterViewModel.class);

        mCursorVertical = view.findViewById(R.id.cursor_vertical);
        mCursorHorizontal = view.findViewById(R.id.cursor_horizontal);
        mCursorContainer = view.findViewById(R.id.cursor_container);
        mMessageRecyclerView = view.findViewById(R.id.rv_message);

        setupFilterView(view);
        setupHintsView(view);
        setupMessageView();

        mKeyboardViewModel.clearInputs();

        mKeyboardViewModel.getPrimitiveInput().observe(getViewLifecycleOwner(),
                mWriterViewModel::putPrimitive);

        mKeyboardViewModel.getControlInput().observe(getViewLifecycleOwner(), controlKey ->
        {
            if (controlKey == null) return;
            switch (controlKey)
            {
                case POP_SYMBOL:
                    mWriterViewModel.popSymbol();
                    break;
                case LEFT_SYMBOL:
                    mWriterViewModel.moveCursorLeft();
                    break;
                case RIGHT_SYMBOL:
                    mWriterViewModel.moveCursorRight();
                    break;
            }
        });

        mWriterViewModel.getState().observe(getViewLifecycleOwner(), state ->
        {
            mCurrentItems = state.items;
            mCurrentCursorIndex = state.cursorIndex;
            mMessageAdapter.update(state.items);

            mMessageRecyclerView.post(() -> scrollToCursor(mCurrentCursorIndex));
            scheduleCursorUpdate();
        });

        mWriterViewModel.getHints().observe(getViewLifecycleOwner(),
                symbols -> mHintsAdapter.update(symbols));

        mWriterViewModel.getFilter().observe(getViewLifecycleOwner(),
                mFilterAdapter::update);

        mWriterViewModel.getFailure().observe(getViewLifecycleOwner(), exception ->
                Toast.makeText(requireContext(), exception.getMessage(), Toast.LENGTH_SHORT).show());

        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                updateCursorPosition();
            }
        });

        mMessageRecyclerView.getViewTreeObserver().addOnPreDrawListener(() ->
        {
            updateCursorPosition();
            return true;
        });

        mCursorContainer.post(() -> {
            Animation blink = AnimationUtils.loadAnimation(requireContext(), R.anim.blink);
            if (blink != null) {
                mCursorContainer.startAnimation(blink);
            }
        });

        scheduleCursorUpdate();
        setupMessageTouchListener();
    }

    private void setupMessageTouchListener() {
        final GestureDetector gestureDetector = new GestureDetector(requireContext(),
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mMessageRecyclerView.getLayoutManager();
                if (layoutManager == null) return false;

                int lastVisiblePos = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = mMessageAdapter.getItemCount();

                if (lastVisiblePos == totalItemCount - 1 && lastVisiblePos >= 0) {
                    View lastVisibleView = layoutManager.findViewByPosition(lastVisiblePos);
                    if (lastVisibleView != null && e.getX() > lastVisibleView.getRight()) {
                        mMessageAdapter.triggerLastItemClick();
                        return true;
                    }
                }
                return false;
            }
        });

        mMessageRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });
    }

    private void scheduleCursorUpdate()
    {
        if (mMessageRecyclerView == null) return;
        mMessageRecyclerView.post(this::updateCursorPosition);
    }

    private void updateCursorPosition() {
        if (!isAdded() || mMessageRecyclerView == null ||
                mMessageRecyclerView.getLayoutManager() == null ||
                mCurrentItems == null || mCurrentCursorIndex < 0 ||
                mCurrentCursorIndex >= mCurrentItems.size()) {
            mCursorVertical.setVisibility(View.INVISIBLE);
            mCursorHorizontal.setVisibility(View.INVISIBLE);
            return;
        }

        View itemView = mMessageRecyclerView.getLayoutManager()
                .findViewByPosition(mCurrentCursorIndex);

        if (itemView == null || itemView.getWidth() <= 0 || itemView.getHeight() <= 0) {
            mCursorVertical.setVisibility(View.INVISIBLE);
            mCursorHorizontal.setVisibility(View.INVISIBLE);
            return;
        }

        int[] containerLocation = new int[2];
        int[] itemLocation = new int[2];
        mCursorContainer.getLocationOnScreen(containerLocation);
        itemView.getLocationOnScreen(itemLocation);

        float relativeX = itemLocation[0] - containerLocation[0];
        float relativeY = itemLocation[1] - containerLocation[1];

        BlissWriterViewModel.MessageItem currentItem = mCurrentItems.get(mCurrentCursorIndex);
        boolean isSymbol = currentItem instanceof BlissWriterViewModel.MessageItem.SymbolItem;

        if (isSymbol)
        {
            mCursorVertical.setVisibility(View.INVISIBLE);
            mCursorHorizontal.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = mCursorHorizontal.getLayoutParams();
            if (params.width != itemView.getWidth()) {
                params.width = itemView.getWidth();
                mCursorHorizontal.setLayoutParams(params);
            }

            float targetX = relativeX - mCursorHorizontal.getLeft();
            float targetY = relativeY + itemView.getHeight() -
                    mCursorHorizontal.getHeight() - mCursorHorizontal.getTop() - 4;

            mCursorHorizontal.setTranslationX(targetX);
            mCursorHorizontal.setTranslationY(targetY);
        }
        else
        {
            mCursorHorizontal.setVisibility(View.INVISIBLE);
            mCursorVertical.setVisibility(View.VISIBLE);

            float targetX = relativeX + (itemView.getWidth() / 2f) -
                    (mCursorVertical.getWidth() / 2f) - mCursorVertical.getLeft();
            float targetY = relativeY + itemView.getPaddingTop() - mCursorVertical.getTop();

            mCursorVertical.setTranslationX(targetX);
            mCursorVertical.setTranslationY(targetY);
        }
    }

    private void scrollToCursor(int position)
    {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mMessageRecyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        if (position >= firstVisible && position <= lastVisible && firstVisible != -1) {
            return;
        }

        RecyclerView.SmoothScroller smoothScroller = getScroller(position);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    private RecyclerView.SmoothScroller getScroller(int position)
    {
        int offsetPx = (int) (12 * getResources().getDisplayMetrics().density);

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(requireContext()) {
            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_END;
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                return (boxEnd - offsetPx) - viewEnd;
            }
        };
        smoothScroller.setTargetPosition(position);
        return smoothScroller;
    }

    private void setupFilterView(View root)
    {
        RecyclerView filterView = root.findViewById(R.id.rv_filters);
        mFilterAdapter = new FilterAdapter(mWriterViewModel);
        filterView.setAdapter(mFilterAdapter);
        filterView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupHintsView(View root)
    {
        RecyclerView hintsView = root.findViewById(R.id.rv_hints);

        mHintsAdapter = new SymbolAdapter(mSymbolRepository, R.layout.item_bliss_symbol,
                (position, item) -> mWriterViewModel.selectHint((Symbol) item));

        hintsView.setAdapter(mHintsAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 4);
        hintsView.setLayoutManager(layoutManager);

        hintsView.addOnLayoutChangeListener((v, left, top, right, bottom,
                                             oldLeft, oldTop, oldRight, oldBottom) -> {
            int width = Math.max(0, right - left - hintsView.getPaddingLeft() - hintsView.getPaddingRight());
            float defaultSize = getResources().getDimension(R.dimen.writer_hint_default_size);

            int spanCount = Math.max(1, (int) Math.ceil(width / defaultSize));
            if (layoutManager.getSpanCount() != spanCount) {
                layoutManager.setSpanCount(spanCount);
            }
        });
    }

    private void setupMessageView() {
        mMessageAdapter = new SymbolAdapter(mSymbolRepository,
                R.layout.item_bliss_message_symbol,
                R.layout.item_bliss_cursor,
                (position, item) -> mWriterViewModel.setCursorIndex(position));

        mMessageAdapter.setOnImageRenderedListener(position -> {
            if (position == mCurrentCursorIndex) {
                scrollToCursor(mCurrentCursorIndex);
                scheduleCursorUpdate();
            }
        });

        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }
}
