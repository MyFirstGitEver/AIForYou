package com.example.aiforyou.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aiforyou.custom.ReceiptDTO;

import java.util.List;

public class AccountFragmentViewModel extends ViewModel {
    private ReceiptDTO[] receiptsHolder = null;
    private Integer currentTabIndex = 0;

    public ReceiptDTO[] getReceiptsHolder() {
        return receiptsHolder;
    }

    public void loadReceipts(ReceiptDTO[] receipts) {
        receiptsHolder = receipts;
    }

    public Integer getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(Integer currentTabIndex) {
        this.currentTabIndex = currentTabIndex;
    }
}