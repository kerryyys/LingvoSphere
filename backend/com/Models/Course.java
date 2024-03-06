package com.lingvosphere.backend.Models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Course {
    public int courseId;
    public String courseName;
    public ArrayList<Chapter> chapters; // List of chapters in the course
    public ArrayList<Exam> exams; // List of exams associated with the course

    // Constructor
    public Course(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.chapters = new ArrayList<>();
        this.exams = new ArrayList<>();
    }

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    public void addExam(Exam exam) {
        exams.add(exam);
    }

    // Nested class for Chapter
    public static class Chapter {
        public String chapterId;
        public String title;
        public HashMap<String, String> wordsAndDefinitions; // Key: Word, Value: Definition

        // Constructor
        public Chapter(String chapterId, String title) {
            this.chapterId = chapterId;
            this.title = title;
            this.wordsAndDefinitions = new HashMap<>();
        }

        public String toJSON() {
            JSONObject json = new JSONObject();
            json.put("chapterId", chapterId);
            json.put("title", title);

            // 将 wordsAndDefinitions 转换为一个 JSON 对象
            JSONObject wordsJson = new JSONObject();
            for (Map.Entry<String, String> entry : wordsAndDefinitions.entrySet()) {
                wordsJson.put(entry.getKey(), entry.getValue());
            }
            json.put("wordsAndDefinitions", wordsJson);

            return json.toString();
        }


        public void addWordDefinition(String key, String string) {
            wordsAndDefinitions.put(key, string);
        }
    }

    // Nested class for Exam
    public static class Exam {
        public String examTitle;
        public ArrayList<Question> questions; // List of questions in the exam

        // Constructor
        public Exam(String examTitle) {
            this.examTitle = examTitle;
            this.questions = new ArrayList<>();
        }

        public void addQuestion(Question question) {
            questions.add(question);
        }

        // Nested class for Question
        public static class Question {
            public String questionText;
            public ArrayList<String> choices; // List of choices for the question
            public String answer; // Correct answer

            // Constructor
            public Question(String questionText, String answer) {
                this.questionText = questionText;
                this.answer = answer;
                this.choices = new ArrayList<>();
            }

            public void addChoice(String string) {
                choices.add(string);
            }

            public JSONObject toJSON() {
                JSONObject questionJson = new JSONObject();
                questionJson.put("questionText", questionText);
                questionJson.put("answer", answer);

                JSONArray choicesArray = new JSONArray();
                for (String choice : choices) {
                    choicesArray.put(choice);
                }
                questionJson.put("choices", choicesArray);

                return questionJson;
            }
        }

        public String toJSON() {
            JSONObject examJson = new JSONObject();
            examJson.put("examTitle", examTitle);

            JSONArray questionsArray = new JSONArray();
            for (Question question : questions) {
                questionsArray.put(question.toJSON());
            }
            examJson.put("questions", questionsArray);

            return examJson.toString();
        }
    }
}
