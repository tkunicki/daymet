/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.daymet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFileWriter;

/**
 *
 * @author tkunicki
 */
@Ignore
public class DaymetTest  {
    
    public DaymetTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.setProperty("jna.library.path", "/opt/local/lib");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetTileList() throws IOException {
        Daymet.TileList tileList = Daymet.getTileListFromDirectory(new File("/Users/tkunicki/Data/daymet/tck"), 2003, "srad");
        try {
            assertNotNull(tileList);
            assertTrue(tileList.size() > 0);
        } finally {
            if (tileList != null) {
                tileList.dispose();
            }
        }
    }
    
    @Test
    public void testGetTileList_ExceptionThrowing() {
        boolean threwExpected;
        try {
            Daymet.getTileListFromDirectory(null, 2003, "srad");
            threwExpected = false;
        } catch (NullPointerException e) {
            threwExpected = true;
        } catch (Exception e) {
            threwExpected = false; // unexpected exception type
        }
        assertTrue("Expected NullPointerException on \'null\' parameter", threwExpected);
        try {
            Daymet.getTileListFromDirectory(new File(""), 2003, "srad");
            threwExpected = false;
        } catch (IllegalStateException e) {
            threwExpected = true;
        } catch (Exception e) {
            threwExpected = false; // unexpected exception type
        }
        assertTrue("Expected IllegalStateException on \'null\' parameter", threwExpected);
    }
    
    @Test
    public void testGenerateNetCDFInputFileName() {
        // NOTE: .cdl for dev
        assertEquals("12663_2003_srad.nc", Daymet.generateNetCDFInputFileName(12663, 2003, "srad"));
    }
    
    @Test
    public void testGenerateNetCDFOutputFileName() {
        // NOTE: .cdl for dev
        assertEquals("2003-01_srad.nc", Daymet.generateNetCDFOutputFileName(2003, 1, "srad"));
    }
    
    @Test
    public void testTile() throws IOException {
        Daymet.Tile tile = new Daymet.Tile(
                new File("/Users/tkunicki/Data/daymet/tck/12663/12663_2003_srad.nc"), "srad");
        try {
            assertNotNull(tile);
            assertNotNull(tile.getProjectionRect());
        } finally {
            if (tile != null) {
                tile.dispose();
            }
        }
    }
    
    @Test
    public void testTileList() throws IOException {
        Daymet.TileList tileList = Daymet.getTileListFromDirectory(
                new File("/Users/tkunicki/Data/daymet"), 2002, "vp");
        try {
            assertNotNull(tileList);
            assertNotNull(tileList.getProjectionRect());
            System.out.println(tileList.getProjectionRect());
        } finally {
            if (tileList != null) {
                tileList.dispose();
            }
        }
    }
    
    @Test
    public void testCreateFile() throws IOException, InvalidRangeException {
        
        Daymet.TileList tileList = Daymet.getTileListFromDirectory(
                    new File("/Users/tkunicki/Data/daymet"), 2002, "vp");
        try {
            NetcdfFileWriter writer = Daymet.createFile("/Users/tkunicki/Data/daymet/combined/test-create.nc", tileList, Daymet.VARIABLE_MAP.get("vp"), new Range(0,3));
            assertNotNull(writer);
            // check layout
            // delete file
        } finally {
            if (tileList != null) {
                tileList.dispose();
            }
        }
    }
    
    @Test
    public void testWriteFile() throws IOException, InvalidRangeException {
        Daymet.TileList tileList = Daymet.getTileListFromDirectory(
                    new File("/Users/tkunicki/Data/daymet"), 2002, "vp");
        try {
            Daymet.writeFile("/Users/tkunicki/Data/daymet/combined/test-write.nc", tileList, Daymet.VARIABLE_MAP.get("vp"), new Range(0,3));
        } finally {
            tileList.dispose();
        }
    }
    
    @Test
    public void testTraverse() throws IOException, InvalidRangeException {
        try {
            Daymet.traverse(
                    new File("/Users/tkunicki/Data/daymet"),
                    new File("/Users/tkunicki/Data/daymet/combined"),
                    Arrays.asList(new Integer[] { 2002 }),
                    Arrays.asList(new String[] { "vp"}),
                    4);
        } finally {
        }
    }
}
