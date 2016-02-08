
import java.io.IOException;

public class CalendarQuickstart
{           
    public static void main(String[] args) throws IOException
    {
        CalendarHandler c = new CalendarHandler();
        Database db = new Database();

        //db.insertUser("jessica");
        //db.deleteUser("albertoscalco");
        
        db.addFollowing("jessica","riccardo@gmail.com");
        db.addFollowing("jessica","albertoscalco11@gmail.com");
        db.addFollowing("jessica","matteo@gmail.com");
        db.removeFollowing("jessica","albertoscalco11@gmail.com");
        
    }
  
}
