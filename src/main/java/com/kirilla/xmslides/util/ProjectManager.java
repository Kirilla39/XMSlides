package com.kirilla.xmslides.util;

import com.kirilla.xmslides.model.SlideModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class ProjectManager {
    private static final String RECENT_PROJECTS_KEY = "recentProjects";
    private static final int MAX_RECENT_PROJECTS = 5;
    private static volatile ProjectManager instance;
    private static String currentProject;
    private Boolean isNamed;
    private List<String> order;

    private ProjectManager() {}

    public static ProjectManager getInstance() {
        if (instance == null) {
            synchronized (ProjectManager.class) {
                if (instance == null) {
                    instance = new ProjectManager();
                }
            }
        }
        return instance;
    }

    public static List<String> getLastProjects() {
        Preferences prefs = Preferences.userNodeForPackage(ProjectManager.class);
        String projects = prefs.get(RECENT_PROJECTS_KEY, "");
        return projects.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(projects.split(";")));
    }

    private static void saveRecentProjects(List<String> projects) {
        Preferences prefs = Preferences.userNodeForPackage(ProjectManager.class);
        prefs.put(RECENT_PROJECTS_KEY, String.join(";", projects));
    }

    public static void addProject(String path) {
        List<String> projects = new ArrayList<>(getLastProjects());

        projects.remove(path);

        projects.addFirst(path);

        if (projects.size() > MAX_RECENT_PROJECTS) {
            projects = projects.subList(0, MAX_RECENT_PROJECTS);
        }

        saveRecentProjects(projects);
    }

    public static void setCurrentProject(String path) {
        System.out.println(path+"/");
        currentProject = path+"/"; addProject(path);
    }

    public static String getCurrentProject() {
        return currentProject;
    }

    public boolean isValidProject(String path) {
        if (path == null || path.isEmpty()) return false;
        File dir = new File(path);
        return dir.exists() && dir.isDirectory();
    }

    public static List<String> getSlidePaths() {
        List<String> slides = new ArrayList<>();
        if (currentProject != null) {
            File[] files = new File(currentProject).listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
            if (files != null) {
                for (File file : files) slides.add(file.getAbsolutePath());
            }
        }
        return slides;
    }

    public static List<SlideModel> getSlides() {
        List<SlideModel> slides = new ArrayList<>();
        for (String path : getSlidePaths()) {
            slides.add(new SlideModel(path));
        }
        return slides;
    }
}