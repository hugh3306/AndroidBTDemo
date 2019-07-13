package com.aiello.spotconfig;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class MyItemDetail extends ItemDetailsLookup.ItemDetails {
    private final int adapterPosition;
    private final DeviceItem selectionKey;

    public MyItemDetail(int adapterPosition, DeviceItem selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }
}
