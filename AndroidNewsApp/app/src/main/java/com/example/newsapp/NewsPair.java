package com.example.newsapp;

public class NewsPair{
    private String left;
    private String right;

    public NewsPair(){
        this.left = null;
        this.right = null;
    }

    public NewsPair(String left, String right){
        this.left = left;
        this.right = right;
    }

    public void setLeft(String left){
        this.left = left;
    }

    public void setRight(String right){
        this.right = right;
    }

    public String getLeft(){
        return this.left;
    }

    public String getRight(){
        return this.right;
    }
}