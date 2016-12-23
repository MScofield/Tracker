package IronYard;

import org.h2.tools.Server;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;


public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection jdbc = DriverManager.getConnection("jdbc:h2:./main");
        createTables(jdbc);

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    HashMap sparkLover = new HashMap();
                    Session session = request.session();
                    String userName = session.attribute("loginName");
                    String userPassword = session.attribute("loginPassword");
                    User user = selectUser(jdbc, userName);
                    if(user == null) {
                        return new ModelAndView(sparkLover, "login.html");
                    }else{
                        ArrayList<Film> films = selectFilms(jdbc, user.id);
                        sparkLover.put("loginName", userName);
                        sparkLover.put("userPassword", userPassword);
                        sparkLover.put("films", films);

                        return new ModelAndView(sparkLover, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
            );//end of "/" SparkGet


        Spark.post(
                "/login",
                ((request, response) -> {
                    Session session = request.session();
                    String nameInput = request.queryParams("loginName");
                    String passwordInput = request.queryParams("loginPassword");
                    String userId = request.queryParams("userId");
                    User user = selectUser(jdbc, nameInput);

                    if (nameInput == null || passwordInput == null ){
                        throw new Exception("You must login with name and password");
                    }
                    if (user == null) {
                            insertUser(jdbc, nameInput, passwordInput);
                        }
                    else if(!user.password.equals(passwordInput)) {
                        throw new Exception("fool me once shame on you, fool me twice...");
                    }
                        session.attribute("loginName", nameInput);
                        session.attribute("loginPassword", passwordInput);
                        session.attribute("userId", userId);
                        response.redirect("/");
                    return "";
                })
        );//login post end

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );// logout

        Spark.post(
                "/create-film-entry",
                ((request, response) -> {
                    Session session = request.session();
                    String nameInput = session.attribute("loginName");
                    User user = selectUser(jdbc, nameInput);
                    if(user == null){
                        throw new Exception("Not logged in foo!");
                    }
                    int userId = user.id;
                    String title = request.queryParams("titleInput");
                    String writer = request.queryParams("writerInput");
                    String director = request.queryParams("directorInput");
                    String release = request.queryParams("releaseInput");
                    String notes = request.queryParams("notesInput");
                    String seenString = request.queryParams("seenInput");
                    if(title == null){
                        throw new Exception("No title text received foo!");
                    }
                    boolean seen = Boolean.parseBoolean(seenString);
                    insertFilm(jdbc, userId, title, writer, director, release, notes, seen);

                    response.redirect ("/");
                    return "";
                })
        );//create post end

        Spark.get(
                "/edit-film-entry",
                ((request, response) -> {
                    HashMap sparkLover = new HashMap();
                    String filmIdString = request.queryParams("filmId");
                    int filmId = Integer.parseInt(filmIdString);
                    Film film = selectFilm(jdbc, filmId);
                    sparkLover.put("film", film);
                    return new ModelAndView(sparkLover, "edit.html");
                }),
        new MustacheTemplateEngine()
        );//end of spark edit get

        Spark.post(
                "/edit",
                ((request, response) -> {
                    Session session = request.session();
                    String nameInput = session.attribute("loginName");
                    int id = session.attribute("filmId");
                    User user = selectUser(jdbc, nameInput);
                    if(user == null){
                        throw new Exception("Not logged in foo!");
                    }
                    String title = request.queryParams("titleInput");
                    String writer = request.queryParams("writerInput");
                    String director = request.queryParams("directorInput");
                    String release = request.queryParams("releaseInput");
                    String notes = request.queryParams("notesInput");
                    String seenString = request.queryParams("seenInput");
                    if(title == null){
                        throw new Exception("No title text received foo!");
                    }
                    boolean seen = Boolean.parseBoolean(seenString);
                    editFilmEntry(jdbc, id, title, writer, director, release, notes, seen);
                    response.redirect("/");
                    return "";

                })
        );// edit post end

        Spark.post(
                "/delete-film-entry",
                ((request, response) -> {
                    String filmIdString = request.queryParams("deleteFilmId");
                    int filmId = Integer.parseInt(filmIdString);
                    deleteFilmEntry(jdbc, filmId);
                    response.redirect("/");
                    return "";
                })
        );//delete post end
    }//end of MainMethod

        public static void createTables(Connection jdbc) throws SQLException{
            Statement interact = jdbc.createStatement();
            interact.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
            interact.execute("CREATE TABLE IF NOT EXISTS film (id IDENTITY, userId INTEGER, title VARCHAR, writer VARCHAR, director VARCHAR, releaseDate VARCHAR, notes VARCHAR, seen BOOLEAN)");
        }//end of creattables method

        public static void insertUser(Connection jdbc, String name, String password) throws SQLException{
            PreparedStatement safeInteract = jdbc.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
            safeInteract.setString(1, name);
            safeInteract.setString(2, password);
            safeInteract.execute();
        }//end of insertuser method

        public static User selectUser(Connection jdbc, String name) throws SQLException {
            PreparedStatement safeInteract = jdbc.prepareStatement("SELECT * FROM users WHERE name = ?");
            safeInteract.setString(1, name);
            ResultSet queryOutput = safeInteract.executeQuery();
            if (queryOutput.next()){
                int id = queryOutput.getInt("id");
                String password = queryOutput.getString("password");
                return new User(id, name, password);
            }
            return null;
        }//end of selectuser method

    public static ArrayList<User> selectUsers (Connection jdbc) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement safeInteract = jdbc.prepareStatement("SELECT * FROM users");
        ResultSet queryOutput = safeInteract.executeQuery();
        while(queryOutput.next()){
            int id = queryOutput.getInt("users.id");
            String userName = queryOutput.getString("users.name");
            String userPassword = queryOutput.getString("users.password");
            User user = new User (id, userName, userPassword);
            users.add(user);
        }
        return users;
    }//end of select users, method not used for this project afterall

        public static void insertFilm(Connection jdbc, int userId, String title, String writer, String director, String releaseDate, String notes, boolean seen) throws SQLException {
            PreparedStatement safeInteract = jdbc.prepareStatement("INSERT INTO film VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)");
            safeInteract.setInt(1, userId);
            safeInteract.setString(2, title);
            safeInteract.setString(3, writer);
            safeInteract.setString(4, director);
            safeInteract.setString(5, releaseDate);
            safeInteract.setString(6, notes);
            safeInteract.setBoolean(7, seen);
            safeInteract.execute();
        }//end of insertfilm method

        public static Film selectFilm(Connection jdbc, int id) throws SQLException{
        PreparedStatement safeInteract = jdbc.prepareStatement("SELECT * FROM film INNER JOIN users ON film.userId = users.id WHERE film.id = ?");
        safeInteract.setInt(1, id);
        ResultSet queryOutput = safeInteract.executeQuery();

        if (queryOutput.next()) {
            int filmId = queryOutput.getInt("film.id");
            int userId = queryOutput.getInt("users.id");
            String title = queryOutput.getString ("film.title");
            String writer = queryOutput.getString("film.writer");
            String director = queryOutput.getString("film.director");
            String releaseDate = queryOutput.getString("film.releaseDate");
            String notes = queryOutput.getString("film.notes");
            Boolean seen = queryOutput.getBoolean("film.seen");
            return new Film(filmId, userId, title, writer, director, releaseDate, notes, seen);
            }
        return null;
        }//end of select film method for selecting individual films


        public static ArrayList<Film> selectFilms (Connection jdbc, int id) throws SQLException{

            PreparedStatement safeInteract = jdbc.prepareStatement("SELECT * FROM film INNER JOIN users ON film.userId = users.id WHERE film.id = ?");
            safeInteract.setInt(1, id);
            ResultSet queryOutput = safeInteract.executeQuery();
            ArrayList<Film> selectedFilms = new ArrayList<>();

            while (queryOutput.next()) {

                int filmId = queryOutput.getInt("film.id");
                int userId = queryOutput.getInt("users.id");
                String title = queryOutput.getString("film.title");
                String writer = queryOutput.getString("film.writer");
                String director = queryOutput.getString("film.director");
                String releaseDate = queryOutput.getString("film.releaseDate");
                String notes = queryOutput.getString("film.notes");
                Boolean seen = queryOutput.getBoolean("film.seen");
                Film film = new Film(filmId, userId, title, writer, director, releaseDate, notes, seen);
                selectedFilms.add(film);
            }
        return selectedFilms;
        }//end of selectfilms method

    public static void deleteFilmEntry (Connection jdbc, int id) throws SQLException {
        PreparedStatement safeInteract = jdbc.prepareStatement("DELETE FROM film WHERE id = ?");
        safeInteract.setInt(1, id);
        System.out.println(id);
        safeInteract.execute();
    }// end of delete method

    public static void editFilmEntry (Connection jdbc, int id, String title, String writer, String director, String releaseDate, String notes, boolean seen) throws SQLException {
        PreparedStatement safeInteract = jdbc.prepareStatement
                ("UPDATE films SET title=? SET writer=? SET director=? SET releaseDate=? SET notes=? SET seen=? WHERE id=?");
        safeInteract.setInt(7, id);
        safeInteract.setString(1, title);
        safeInteract.setString(2, writer);
        safeInteract.setString(3, director);
        safeInteract.setString(4, releaseDate);
        safeInteract.setString(5, notes);
        safeInteract.setBoolean(6, seen);
        safeInteract.execute();
    }//end of edit method

}//end of MainClass