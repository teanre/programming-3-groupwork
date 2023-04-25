
package fi.tuni.prog3.sisu;

/**
 * Class to store constants used throughout the programme
 * @author terhi
 */
public class Constants {
   // name of the file where user data is stored
   public static final String FILENAME = "studentInfo.json";
   
   public static final String REQUEST_METHOD_GET = "GET";
    
    public static final String READ_ERROR = "Error reading the file: ";
    public static final String WRITE_ERROR = "Error writing to file: ";
    public static final String FILE_CREATE_ERROR = "Creating a file failed: ";   
    public static final String EXCEPTION_MSG = "Exception occurred: ";
    
    public static final String COMPLETED_MARK = "**";
    
    public static final String COMPLETED_COURSES = "completedCourses";
    public static final String STUDENTS = "students";
    public static final String STUDENT_NR ="studentNumber";
    
    public static final String TYPE = "type";
    public static final String RULE = "rule";
    public static final String RULES = "rules";
    public static final String SEARCH_RESULTS ="searchResults";

    public static final String ANY_COURSE_UNIT_RULE = "AnyCourseUnitRule";
    public static final String ANY_MODULE_RULE = "AnyModuleRule";
    public static final String COMPOSITE_RULE = "CompositeRule";
    public static final String COURSE_UNIT_RULE = "CourseUnitRule";
    public static final String MODULE_RULE = "ModuleRule";
    public static final String MODULE_GROUP_ID = "moduleGroupId";
    public static final String COURSE_UNIT_GROUP_ID ="courseUnitGroupId";
    
    public static final String FI = "fi";
    public static final String EN = "en";  
   
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String GROUP_ID = "groupId";
    public static final String CREDITS = "credits";
    public static final String MIN_CREDITS ="minCredits";
    public static final String MIN = "min";
    public static final String MAX = "max";

    public static final String MODULE = "Module";
    public static final String COURSE = "Course";
}
