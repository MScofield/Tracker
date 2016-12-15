package IronYard;

import jodd.json.JsonSerializer;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.FileWriter;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import jodd.json.JsonSerializer;
import java.net.URL;


public class Main {
    static HashMap<String, User> users = new HashMap<>();


    public static void main(String[] args) throws Exception {
        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("loginName");
                    String userPassword = session.attribute("loginPassword");
                    System.out.println("userName input to session:" +userName);
                    System.out.println("password input to session:" +userPassword);


                    User user = users.get(userName);
                    //User password = users.get(userPassword);
                    HashMap sessionHash = new HashMap();

                    if(user == null) {
                        return new ModelAndView(sessionHash, "login.html");
                    }else{
                        sessionHash.put("loginName", user.name);
                        sessionHash.put("userPassword", user.password);
                        sessionHash.put("filmObjects", user.films);
                        System.out.println(user.name);
                        System.out.println(user.password);
                        return new ModelAndView(sessionHash, "home.html");
                    }

//                    String filmId = request.queryParams("filmId");
//                    int filmIdNum = - 1;
//                    if (filmId != null){
//                        filmIdNum = Integer.parseInt("filmId");
//                    }
//
//                    ArrayList<Film> filmThreads = new ArrayList<Film>();
//                    for (Film film : User.films){
//                        if (film.filmId == filmIdNum){
//                            filmThreads.add(film);
//                        }
//                    }



                }),
                new MustacheTemplateEngine()
            );//end of SparkGet

//        Spark.get(
//                "/json",
//                ((request, response) -> {
//                    String replyId = request.queryParams("replyId");
//                    int replyIdNum = -1;
//                    if(replyId != null){
//                        replyIdNum = Integer.parseInt(replyId);
//                    }
//
//                    ArrayList<Film> filmThreads = new ArrayList<>();
//                    for (Film film : User.films){
//                        if (film.filmId == filmIdNum){
//                            filmThreads.add(film);
//                        }
//                    }
//
//                    JsonSerializer serializer = new JsonSerializer();
//                    String json = serializer.include("*").serialize(filmThreads);
//                    return json;
//                })
//        );

        Spark.post(
                "/login",
                ((request, response) -> {
                    Session session = request.session();
                    String nameInput = request.queryParams("loginName");
                    String passwordInput = request.queryParams("loginPassword");


                    if (nameInput == null || passwordInput == null ){
                        throw new Exception("You must login with name and password");
                    }
                    User user = users.get(nameInput);
                    if (user == null) {
                            user = new User(nameInput, passwordInput);
                            users.put(nameInput, user);
                            users.put(passwordInput, user);
                        }
                    else if(!user.password.equals(passwordInput)) {
                        throw new Exception("fool me once shame on you, fool me twice...");

                    }
                        session.attribute("loginName", nameInput);
                        session.attribute("loginPassword", passwordInput);

                        System.out.println(nameInput);
                        System.out.println(passwordInput);
                        System.out.println(user.name);
                        System.out.println(user.password);


                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/create-film-entry",
                ((request, response) -> {
                    Session session = request.session();
                    String nameInput = session.attribute("loginName");
                    User user = users.get(nameInput);
                    System.out.println(nameInput);
                    if(nameInput == null){
                        throw new Exception("Not logged in foo!");
                    }
                    String title = request.queryParams("titleInput");
                    String writer = request.queryParams("writerInput");
                    String director = request.queryParams("directorInput");
                    String release = request.queryParams("releaseInput");
                    String notes = request.queryParams("notesInput");
                    String seenString = request.queryParams("seenInput");
                    String filmIdString = request.queryParams("filmId");
                    if(title == null){
                        throw new Exception("No title text received foo!");
                    }
                    filmIdString = "0";//temporary until I remember how to generate these
//                    int filmIdNum = Integer.parseInt(filmId);
//                    int releaseInputNum = Integer.parseInt(releaseInputString);
                    boolean seenBoolean = Boolean.parseBoolean(seenString);
                    int filmId = Integer.parseInt(filmIdString);


                    Film filmObject = new Film(filmId, nameInput, title, writer, director, release, notes, seenBoolean);
                    user.films.add(filmObject);

//                    response.redirect(request.headers("Referer"));

                    response.redirect("/");
                    return "";
                })
        );

//        Spark.post(
//                "/edit-film-entry",
//                ((request, response) -> {
//                    Session session = request.session();
//                    String name = session.attribute("loginName");
//                    User user = users.get(name);
//                    String filmRemover = request.queryParams("filmRemover");
//                    int fut = Integer.parseInt(filmRemover);
//                    user.films.get(fut-1);
//                    user.films.remove(fut-1);
//
//                    String filmReplacer = request.queryParams("filmReplacer")
//                      or actually a bunch of these lines with most all of the variables that make up and Film object
//                    Film  = new Film(filmReplacer);
//
//
//
//                    user.films.add(fut-1, title);
//
//                    response.redirect("/");
//                    return "";
//
//                })
//        );
        Spark.post(
                "/delete-film-entry",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);
                    String filmKiller = request.queryParams("filmKiller");

                    int fu = Integer.parseInt(filmKiller);
                    user.films.remove(fu - 1);

                    response.redirect("/");
                    return "";
                })
        );

//        static void addTestUsers(){
//            users.put("Alice", new User("Thames", "123"));
//            users.put("Bob", new User("Ryan", "123"));
//            users.put("Charlie", new User("Tween", "123"));
//        }
//        static void addTestFilms(){
//            films.add(new Film(0,-1, "Alice","Hello World!"));
//            films.add(new Film(1,-1, "Bob","HI love it!"));
//            films.add(new Film(2,0, "Charlie","Mee too!"));
//            films.add(new Film(3,2, "Alice","Thanks!"));
//        }

    }//end of MainMethod

}//end of MainClass
