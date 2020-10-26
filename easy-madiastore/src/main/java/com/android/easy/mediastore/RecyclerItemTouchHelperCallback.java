package com.android.easy.mediastore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author abook23@163.com
 * @date 2020/05/08
 */
public class RecyclerItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private OnItemTouchListener mItemTouchListener;

    public RecyclerItemTouchHelperCallback(OnItemTouchListener itemTouchListener) {
        mItemTouchListener = itemTouchListener;
    }

    /**
     * Item是否支持长按拖动
     *
     * @return true  支持长按操作; false 不支持长按操作
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }

    /**
     * Item是否支持滑动
     *
     * @return true  支持滑动操作; false 不支持滑动操作
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //START  右向左 END左向右 LEFT  向左 RIGHT向右  UP向上
        // 拖拽的标记，这里允许上下左右四个方向
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        // 滑动的标记，这里允许左右滑动
        int swipeFlags = ItemTouchHelper.UP;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * @param recyclerView recyclerView
     * @param viewHolder   移动钱
     * @param target       移动后
     * @return 如果Item切换了位置，返回true；反之，返回false
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mItemTouchListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 当onMove返回true时调用
     */
    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        mItemTouchListener.onItemMoved(fromPos, toPos);
    }

    /**
     * @param viewHolder viewHolder
     * @param direction  Item滑动的方向
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.UP) {
            mItemTouchListener.onItemDelete(viewHolder.getAdapterPosition());
        }
    }

    /**
     * Item被选中时候回调
     *
     * @param viewHolder  viewHolder
     * @param actionState 当前Item的状态 ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
     *                    ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
     *                    ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

    public interface OnItemTouchListener {

        /**
         * 移动
         *
         * @param form 从哪
         * @param to   到哪
         */
        void onItemMove(int form, int to);

        void onItemMoved(int form, int to);

        /**
         * 删除
         *
         * @param position
         */
        void onItemDelete(int position);
    }
}
