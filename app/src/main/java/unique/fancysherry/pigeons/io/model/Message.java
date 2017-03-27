package unique.fancysherry.pigeons.io.model;

/**
 * Created by fancysherry on 16-1-14.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

//创建表
@Table(name = "user", id = "_id")
public class Message extends Model {
    @Column(name = "message_type")
    public Type type;
    @Column(name = "message_content")
    public String message;
    @Column(name = "message_from")
    public String from;
    @Column(name = "message_to")
    public String to;//username;
    @Column(name = "message_to_group_id")
    public long gid;

    public enum Type {TEXT, IMAGE, FILE, VOICE, MIX, TEXT_OTHER, IMAGE_OTHER, FILE_OTHER, VOICE_OTHER, MIX_OTHER}
}
