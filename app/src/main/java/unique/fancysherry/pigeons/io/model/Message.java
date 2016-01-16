package unique.fancysherry.pigeons.io.model;

/**
 * Created by fancysherry on 16-1-14.
 */
public class Message {
    public Type type;
    public String message;
    public String from;
    public String to;//username;
    public String gid;

    public enum Type {TEXT, IMAGE, FILE, VOICE, MIX,TEXT_OTHER, IMAGE_OTHER, FILE_OTHER, VOICE_OTHER, MIX_OTHER}
}
