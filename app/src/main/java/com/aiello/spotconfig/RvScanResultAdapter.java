package com.aiello.spotconfig;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RvScanResultAdapter extends RecyclerView.Adapter<RvScanResultAdapter.MyViewHolder> {
    private List<DeviceItem> mDataset;
    private SelectionTracker selectionTracker;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails {
        // each data item is just a string in this case
        TextView itemAddress, itemName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemAddress = itemView.findViewById(R.id.id_tv_bt_address);
            itemName = itemView.findViewById((R.id.id_tv_name));
        }

        public final void bind(DeviceItem item, boolean isActive) {
            itemView.setActivated(isActive);
            String name = "null";
            if (item.getDeviceName() != null)
                name = item.getDeviceName();

            itemName.setText(name);
            itemAddress.setText(item.getAddress());
        }

        @Override
        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new MyItemDetail(getAdapterPosition(), mDataset.get(getAdapterPosition()));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RvScanResultAdapter(List<DeviceItem> myDataset) {
        this.mDataset = myDataset;
    }

    public SelectionTracker getSelectionTracker() {
        return selectionTracker;
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RvScanResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DeviceItem item = mDataset.get(position);
        holder.bind(item, selectionTracker.isSelected(item));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setItems(List<DeviceItem> myDataset) {
        this.mDataset = myDataset;
    }
}
