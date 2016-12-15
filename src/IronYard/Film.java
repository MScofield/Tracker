package IronYard;

/**
 * Created by scofieldservices on 12/7/16.
 */
public class Film {

    int id;
    int filmId;
    String userName;
    String title;
    String writer;
    String director;
    String releaseDate;
    String notes;
    boolean seen;

    public Film() {
    }

    public Film(int id, String userName, String title, String writer, String director, String releaseDate, String notes, Boolean seen) {
        this.id = id;
        this.userName = userName;
        this.title = title;
        this.writer = writer;
        this.director = director;
        this.releaseDate = releaseDate;
        this.notes = notes;
        this.seen = seen;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}

