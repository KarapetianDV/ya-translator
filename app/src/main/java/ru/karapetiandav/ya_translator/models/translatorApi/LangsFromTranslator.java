package ru.karapetiandav.ya_translator.models.translatorApi;

import java.util.HashMap;

public class LangsFromTranslator {

    private String[] dirs;
    private HashMap<String, String> langs;

    public HashMap<String, String> getLangs() {
        return langs;
    }

    public String[] getDirs() {
        return dirs;
    }

    public void setDirs(String[] dirs) {
        this.dirs = dirs;
    }

    @Override
    public String toString() {
        return "ClassPojo [langs = " + langs + ", dirs = " + dirs + "]";
    }
}