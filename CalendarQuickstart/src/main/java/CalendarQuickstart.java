
import java.io.IOException;
import java.util.Arrays;

public class CalendarQuickstart
{           
    public static void main(String[] args) throws IOException
    {
        CalendarHandler c = new CalendarHandler();
        Database db = new Database();

        //db.insertUser("jessica@gmail.com", "jessica", "azzolin");
        //db.deleteUser("albertoscalco");
        
        db.addFollowing("jessica@gmail.com","alberto@gmail.com");
        //System.out.println(Arrays.toString(db.getFollowing("jessica@gmail.com")));
        //db.removeFollowing("jessica@gmail.com","alberto@gmail.com");
        
        
    }
  
}
