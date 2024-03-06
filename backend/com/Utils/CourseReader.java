package com.lingvosphere.backend.Utils;

import com.lingvosphere.backend.Courses;
import com.lingvosphere.backend.Models.Course;
import com.lingvosphere.backend.Models.Course.Chapter;
import com.lingvosphere.backend.Models.Course.Exam;
import com.lingvosphere.backend.Models.Course.Exam.Question;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Scanner;

public class CourseReader {
    public static File[] getJSONFiles(String path) {
        File folder = new File(path); // 替换为您的目录路径
        FilenameFilter jsonFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };

        File[] files = folder.listFiles(jsonFilter);
        return files;
    }
    public static Course read(String filename) {
        String jsonData = readJsonFile(filename);
        JSONObject jsonObject = new JSONObject(jsonData);

        Course course = new Course(jsonObject.getInt("courseId"), jsonObject.getString("courseName"));
        if(Courses.languages.get(jsonObject.getString("language")) == null)
            Courses.languages.put(jsonObject.getString("language"), new ArrayList<>());
        Courses.languages.get(jsonObject.getString("language")).add(jsonObject.getInt("courseId"));
        JSONArray chaptersArray = jsonObject.getJSONArray("chapters");
        for (int i = 0; i < chaptersArray.length(); i++) {
            JSONObject chapterObject = chaptersArray.getJSONObject(i);
            Chapter chapter = new Chapter(chapterObject.getString("chapterId"), chapterObject.getString("title"));
            JSONObject wordsAndDefinitions = chapterObject.getJSONObject("wordsAndDefinitions");
            for (String key : wordsAndDefinitions.keySet()) {
                chapter.addWordDefinition(key, wordsAndDefinitions.getString(key));
            }
            course.addChapter(chapter);
        }

        JSONArray examsArray = jsonObject.getJSONArray("exams");
        for (int i = 0; i < examsArray.length(); i++) {
            JSONObject examObject = examsArray.getJSONObject(i);
            Exam exam = new Exam(examObject.getString("examId"));
            JSONArray questionsArray = examObject.getJSONArray("questions");
            for (int j = 0; j < questionsArray.length(); j++) {
                JSONObject questionObject = questionsArray.getJSONObject(j);
                Question question = new Question(questionObject.getString("questionText"), questionObject.getString("answer"));
                JSONArray choicesArray = questionObject.getJSONArray("choices");
                for (int k = 0; k < choicesArray.length(); k++) {
                    question.addChoice(choicesArray.getString(k));
                }
                exam.addQuestion(question);
            }
            course.addExam(exam);
        }

        return course;
    }

    private static String readJsonFile(String filePath) {
        StringBuilder jsonData = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                jsonData.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return jsonData.toString();
    }
}
