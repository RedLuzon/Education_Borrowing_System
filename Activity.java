package EducationBorrowingSystem;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Activity {
    public enum Kind {
        LOAN(UiKit.PRIMARY), RETURN(UiKit.SUCCESS), AUTH(new Color(0x78716C)), SYSTEM(UiKit.WARNING);
        public final Color color;
        Kind(Color c) { color = c; }
    }

    private final Kind kind;
    private final String text;
    private final LocalTime time;

    public Activity(Kind kind, String text) {
        this.kind = kind;
        this.text = text;
        this.time = LocalTime.now();
    }

    public Kind getKind() { return kind; }
    public String getText() { return text; }
    public LocalTime getTime() { return time; }
    public String timeStamp() { return time.format(DateTimeFormatter.ofPattern("hh:mm a")); }
}