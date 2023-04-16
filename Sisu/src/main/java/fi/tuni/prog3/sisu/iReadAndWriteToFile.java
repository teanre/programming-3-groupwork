package fi.tuni.prog3.sisu;

/**
 * Interface with methods to read from a file and write to a file.
 */
public interface iReadAndWriteToFile {
    /**
     * Reads JSON from the given file.
     * @param fileName name of the file to read from.
     * @return JsonObject
     * @throws Exception if the method e.g, cannot find the file. 
     */
    public boolean readFromFile(String fileName) throws Exception; 
    
    /**
     * Write the student progress as JSON into the given file.
     * @param fileName name of the file to write to.
     * @return true if the write was successful, otherwise false.
     * @throws Exception if the method e.g., cannot write to a file.
     */
    public boolean writeToFile(String fileName) throws Exception;
}
