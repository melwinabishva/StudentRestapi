package controller;


import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.repackaged.com.google.gson.Gson;
import model.StudentForm;
import model.StudentRepositary;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class StudentServices extends HttpServlet {
    public StudentRepositary studentRepository;

    @Override
    public void init() throws ServletException {

        studentRepository = new StudentRepositary();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            // get the student id from the url path
            String pathInfo = req.getPathInfo();

            System.out.println(" ==========="+pathInfo);

            if (pathInfo == null || pathInfo.isEmpty()) {
                sendErrorResponse(resp, "Missing Student id");

                return;
            }
            String[] pathParts = pathInfo.split("/");
            System.out.println(pathParts.length);


            if (pathParts.length >= 2) {
                String studentId = pathParts[1];

                boolean studentExist = studentRepository.checkIfStudentExists(studentId);
                if (!studentExist) {

                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                } else {

                    // Reterive the student information form the database
                    StudentForm student = studentRepository.getStudentById(studentId, resp);

                    // convert the student object to JSON
//					Gson gson = new Gson();
//					String json = gson.toJson(student);

                    sendJsonResponse(resp, student);

                }
            } else {
//				sendErrorResponse(resp, "Invalid URL path");

                // Convert the student list to JSON
                List<StudentForm> students = studentRepository.getAllStudents();
//				Gson gson = new Gson();
//				String json = gson.toJson(students);

                sendJsonResponse(resp, students);

            }

        } catch (IOException e) {

            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("internal sever error occurred");

        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "Enter valid number");
            return;

        }catch (EntityNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            // Rertrive the JSON data from req
            BufferedReader read = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = read.readLine()) != null) {
                requestBody.append(line);
            }
            read.close();
            String userData = requestBody.toString();

            // Change the JSON data into student obj
            Gson gson = new Gson();
            StudentForm newStudent = gson.fromJson(userData, StudentForm.class);

            if(!checkJsonBody(newStudent,gson,resp)) {
                return;
            }else {


                studentRepository.addStudent(gson,newStudent,resp);

                resp.setStatus(HttpServletResponse.SC_CREATED);


            }

        } catch (IOException e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.isEmpty()) {
                sendErrorResponse(resp, "Missing Student ID");
                return;
            }

            String[] pathParts = pathInfo.split("/");

            if (pathParts.length >= 2) {

                String studentId = pathParts[1];

                boolean studentExist = studentRepository.checkIfStudentExists(studentId);
                if (!studentExist) {

                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } else {
                    // Retrieve the updated student information from the request body
                    BufferedReader reader = req.getReader();
                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }
                    reader.close();
                    // Convert the JSON request body to a Student object
                    Gson gson = new Gson();
                    StudentForm updatedStudent = gson.fromJson(requestBody.toString(), StudentForm.class);
                    if(!checkJsonBody(updatedStudent,gson,resp)){
                        return;
                    }else {

                        // Update the student in DataStore
                        studentRepository.updateStudent(studentId, updatedStudent,gson,resp);

                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write("Student updated successfully");
                    }
                }
            } else {
                sendErrorResponse(resp, "Invalid URL path");
            }

        } catch (IOException e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal server error occurred");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "Enter a valid number");
            return;
        }

    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            // Get the student id from the URL path
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.isEmpty()) {
                sendErrorResponse(resp, "Missing Student ID");
                return;
            }

            String[] pathParts = pathInfo.split("/");

            if (pathParts.length >= 2) {
                String studentId = pathParts[1];

                boolean studentExist = studentRepository.checkIfStudentExists(studentId);
                if (!studentExist) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		           return;


                } else {
                    // Delete the student from datastore
                    studentRepository.deleteStudent(studentId,resp);

                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

                }
            } else {
                sendErrorResponse(resp, "Invalid URL path");
            }


        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "Invalid Student ID format");
        }
        catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void sendErrorResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("text/plain");
        resp.getWriter().write(message);
    }

    public void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    public boolean checkJsonBody(StudentForm student,Gson gson,HttpServletResponse resp) throws IOException {
        boolean flag=true;

        if (student.getId()== null && student.getName()== null && student.getAge()==0) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            flag=false;
        }
        return flag;
    }
}
