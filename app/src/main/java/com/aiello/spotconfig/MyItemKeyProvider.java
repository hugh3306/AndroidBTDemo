package com.aiello.spotconfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.List;

public class MyItemKeyProvider extends ItemKeyProvider {
    private final List<DeviceItem> itemList;

    public MyItemKeyProvider(int scope, List<DeviceItem> itemList) {
        super(scope);
        this.itemList = itemList;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return itemList.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return itemList.indexOf(key);
    }
}
