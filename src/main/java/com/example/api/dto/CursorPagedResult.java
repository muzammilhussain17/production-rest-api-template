package com.example.api.dto;

import java.util.List;

public class CursorPagedResult<T> {

    private List<T> data;
    private String nextCursor;
    private boolean hasNext;

    public CursorPagedResult() {
    }

    public CursorPagedResult(List<T> data, String nextCursor, boolean hasNext) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
