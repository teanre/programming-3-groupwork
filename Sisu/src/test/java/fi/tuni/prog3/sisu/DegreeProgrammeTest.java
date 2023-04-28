package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testfile for the class DegreeProgramme
 * @author jamik
 */
public class DegreeProgrammeTest {
    
    private DegreeProgramme degreeProgramme = new DegreeProgramme();

    /**
     * Test of addDegreeProgrammes method, of class DegreeProgramme.
     */
    @Test
    public void testAddDegreeProgrammes() {
        ArrayList<DegreeProgramme> degreeProgrammes = degreeProgramme.addDegreeProgrammes();

        assertNotNull(degreeProgrammes);
        assertEquals(273, degreeProgrammes.size());
    }

    /**
     * Test of the constructor of the class DegreeProgramme.
     */
    @Test
    public void testConstructor() {
        DegreeProgramme degreeProgramme = new DegreeProgramme("Computer Science", "ComSci", "Comp", 180);

        assertEquals("Computer Science", degreeProgramme.getName());
        assertEquals("ComSci", degreeProgramme.getId());
        assertEquals("Comp", degreeProgramme.getGroupId());
        assertEquals(180, degreeProgramme.getMinCredits());
    }
    
}
