import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class CatalogReader {
	
	private ArrayList<Course> allCourses;
	
	public CatalogReader() {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        allCourses = new ArrayList<Course>(1584);

        try {
            br = new BufferedReader(new InputStreamReader(MainGUI.class.getResourceAsStream("/fallcatalog.csv")));
            while ((line = br.readLine()) != null) {
                String[] course = line.split(cvsSplitBy);
                allCourses.add(new Course(course[0],course[1],course[2]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public ArrayList<Course> getAllCourses(){
		return allCourses;
	}

}
