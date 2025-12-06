package com.zarpar.dto;

import java.util.List;

public class PageDTO<T> {
    private List<T> content;
    private int page;
    private int size;

    public PageDTO() {}

    public PageDTO(List<T> content, int page, int size) {
        this.content = content;
        this.page = page;
        this.size = size;
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }

    public void setContent(List<T> content) { this.content = content; }
    public void setPage(int page) { this.page = page; }
    public void setSize(int size) { this.size = size; }
}


