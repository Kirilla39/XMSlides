package com.kirilla.xmslides.util;

import com.kirilla.xmslides.model.SlideModel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class ProjectManager {
    private static final String RECENT_PROJECTS_KEY = "recentProjects";
    private static final String ORDER_FILE = "slide_order.txt";
    private static final int MAX_RECENT_PROJECTS = 5;
    private static volatile ProjectManager instance;
    private static String currentProject;

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
        currentProject = path + "/";
        addProject(path);
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
        if (currentProject == null) return new ArrayList<>();

        List<String> orderedPaths = readSlideOrder();
        if (!orderedPaths.isEmpty()) {
            return orderedPaths.stream()
                    .map(path -> currentProject + path)
                    .filter(path -> new File(path).exists())
                    .collect(Collectors.toList());
        }

        File[] files = new File(currentProject).listFiles((dir, name) ->
                name.toLowerCase().endsWith(".xml"));
        if (files == null) return new ArrayList<>();

        return Arrays.stream(files)
                .sorted(Comparator.comparing(File::getName))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    private static List<String> readSlideOrder() {
        File orderFile = new File(currentProject + ORDER_FILE);
        if (!orderFile.exists()) return new ArrayList<>();

        try {
            return Files.readAllLines(orderFile.toPath()).stream()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveSlideOrder(List<SlideModel> slides) {
        if (currentProject == null) return;

        List<String> slideNames = slides.stream()
                .map(slide -> new File(slide.getPath()).getName())
                .collect(Collectors.toList());

        try {
            Files.write(Path.of(currentProject + ORDER_FILE),
                    slideNames,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SlideModel> getSlides() {
        return getSlidePaths().stream()
                .map(SlideModel::new)
                .collect(Collectors.toList());
    }
}