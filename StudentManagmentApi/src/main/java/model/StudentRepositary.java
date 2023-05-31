package model;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.gson.Gson;
import controller.StudentServices;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentRepositary {
    private DatastoreService datastoreService;
    private StudentForm student;
    private StudentServices studentService;

    public StudentRepositary() {

        datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

    public boolean checkIfStudentExists(String id) {

        Key key = KeyFactory.createKey("Student", id);
        try {
            Entity entity = datastoreService.get(key);
            return entity != null;
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void addStudent(Gson gson, StudentForm student, HttpServletResponse resp) throws IOException {


        //Generate the UUID
        UUID generatedUUID = UUID.randomUUID();

        String uuidString = generatedUUID.toString();

        student.setId(uuidString);
        PrintWriter printWriter = resp.getWriter();
        try {
            Entity entity = new Entity("Student", student.getId());
            entity.setProperty("id", student.getId());
            entity.setProperty("name", student.getName());
            entity.setProperty("age", student.getAge());
            datastoreService.put(entity);



            resp.setContentType("application/json");

            if (datastoreService.get(entity.getKey()) != null) {
                String json = gson.toJson(student);
                printWriter.println(json);

                System.out.println(json);

            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                printWriter.println("Failed to add student");
            }
        } catch (EntityNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            printWriter.println("Failed to add student");
        }


    }


    public StudentForm getStudentById(String studentId, HttpServletResponse resp) throws IOException, EntityNotFoundException {

        Key key = KeyFactory.createKey("Student", studentId);
        try {
            Entity entity = datastoreService.get(key);
            if (entity != null) {
                String id = (String) entity.getProperty("id");
                String name = (String) entity.getProperty("name");
                long age = (long) entity.getProperty("age");
                return new StudentForm(id, name, age);
            }
        } catch (EntityNotFoundException e) {
            resp.setContentType("text/plain");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");

        }
        return null;
    }


        public List<StudentForm> getAllStudents() {
            Query query = new Query("Student");
            PreparedQuery preparedQuery = datastoreService.prepare(query);

            List<StudentForm> students = new ArrayList();

            for (Entity stu : preparedQuery.asIterable()) {
                String id = (String) stu.getProperty("id");
                String name = (String) stu.getProperty("name");
                int age = (int) stu.getProperty("age");
                students.add(new StudentForm(id, name, age));
            }
            return students;
        }


        public void updateStudent (String id, StudentForm student, Gson gson, HttpServletResponse resp) throws
        IOException {
            Key key = KeyFactory.createKey("Student", id);
            Transaction transaction = datastoreService.beginTransaction();
            try {
                Entity entity = datastoreService.get(key);
                entity.setProperty("name", student.getName());
                entity.setProperty("age", student.getAge());
                datastoreService.put(entity);
                transaction.commit();


                //Reterive the json format
                String sId = (String) entity.getProperty("id");
                String name = (String) entity.getProperty("name");
                int age = (int) entity.getProperty("age");
                StudentForm studentForm = new StudentForm(sId, name, age);

                PrintWriter writer = resp.getWriter();
                resp.setContentType("application/json");
                writer.println(gson.toJson(studentForm));


            } catch (EntityNotFoundException e) {
                transaction.rollback();
                PrintWriter writer = resp.getWriter();
                resp.setContentType("text/plain");
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Failed to update Student.");
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }


        }

        public void deleteStudent (String id, HttpServletResponse resp) throws IOException {

            Key key = KeyFactory.createKey("Student", id);

            try {
                Entity entity = datastoreService.get(key);

                if (id == null) {
                    resp.setContentType("text/plain");
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
                    return;

                }
            } catch (EntityNotFoundException | IOException e) {
                resp.setContentType("text/plain");
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
                e.printStackTrace();
            }

            datastoreService.delete(key);


        }
    }





