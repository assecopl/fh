package pl.fhframework.docs.forms.model.example;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Przykladowa klasa, ktora reprezentuje node w drzewku rozwijalnym.
 */
@Getter
@Setter
public class FileSystemNodeExample {

    public static final int MAX_FILES = 4;

    private long id;
    private String name;
    private String icon;
    private int filesToCalculate;
    private boolean folder;

    private List<FileSystemNodeExample> files;

    public FileSystemNodeExample(long id, String name, int filesToCalculate, boolean folder) {
        this.id = id;
        this.name = name;
        this.filesToCalculate = filesToCalculate;
        this.folder = folder;
        this.icon = folder ? "fa fa-folder-open" : "fa fa-file";
    }

    public List<FileSystemNodeExample> getFiles() {
        if (files == null) {
            files = new ArrayList<>();
            for (int i = 0; i < filesToCalculate; i++) {
                int newFilesCount = MAX_FILES - i;
                if (i == MAX_FILES - 1) {
                    newFilesCount = 0; // surprise - potentially has files (folder == true), but eventually does not
                }
                files.add(new FileSystemNodeExample(new Random().nextInt(), name + "/" + "folder" + i, newFilesCount, true));
            }
            if (!files.isEmpty()) {
                files.add(new FileSystemNodeExample(new Random().nextInt(), name + "/" + "file", 0, false));
            }
        }
        return files;
    }
}