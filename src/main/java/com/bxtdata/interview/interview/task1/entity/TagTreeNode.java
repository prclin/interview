package com.bxtdata.interview.interview.task1.entity;

import java.util.LinkedList;
import java.util.List;

public class TagTreeNode {
    private String key;
    private List<TagTreeNode> children;
    private String name;


    public TagTreeNode(String key) {
        this.key = key;
    }

    public TagTreeNode(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public TagTreeNode() {
    }

    public String getKey() {
        if (key == null) return "";
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addChild(TagTreeNode node) {
        if (children == null) children = new LinkedList<>();
        children.add(node);
    }

    public List<TagTreeNode> getChildren() {
        if (children == null) children = new LinkedList<>();
        return children;
    }

    public void setChildren(List<TagTreeNode> children) {
        this.children = children;
    }

    public String getName() {
        if (name == null) name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
