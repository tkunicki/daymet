package gov.usgs.cida.daymet;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

/**
 * Found out after the fact the valid_range attribute is tested *before* scale_factor is applied.
 * This is a quick fix for pre-converted files.  Should only be run once and is no longer
 * needed as this has been fixed in the Daymet class.
 * 
 * to run:  java -cp daymet.jar gov.usgs.cida.FixScaledValidRange
 * 
 */
public class FixScaledValidRange {
    
    private final static Pattern PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}_(\\w+)\\.nc");
    
    public static void main(String[] args) {
        File file = new File(".");
        System.out.println("checking " + file.getName());
        for (File child : file.listFiles()) {
            Matcher matcher = PATTERN.matcher(child.getName());
            if (matcher.matches()) {
                System.out.println("found " + child.getName());
                String variableName = matcher.group(1);
                NetcdfFileWriter writer = null;
                try {
                    writer = new NetCDFHACK(child.getPath());
                    Variable variable = writer.findVariable(variableName);
                    if (variable != null) {
                        System.out.println("  checking " + variableName);
                        Daymet.VariableOut variableOut = Daymet.VARIABLE_MAP.get(variableName);
                        if (variableOut.scale != 1) {
                            Attribute oldAttribute = variable.findAttribute("valid_range");
                            if (oldAttribute != null) {
                                Attribute newAttribute = Daymet.createValidRangeAttribute(oldAttribute, Daymet.VARIABLE_MAP.get(variableName));
                                if (newAttribute != null) {
                                    System.out.println("  rewriting valid range from " + oldAttribute.toString() + " to " + newAttribute.toString());
                                    writer.setRedefineMode(true);
                                    variable.remove(oldAttribute);
                                    variable.addAttribute(newAttribute);
                                    writer.setRedefineMode(false);
                                }
                            }
                        } else {
                            System.out.println("  ignoring, scale_factor is 1");
                        }
                    } else {
                        System.out.println("  couldn't find variable:" + variableName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (IOException ignore) { }
                }
            }
        }
        
    }
    
    private static class NetCDFHACK extends NetcdfFileWriter {

        public NetCDFHACK(String location) throws IOException {
            super(Version.netcdf3, null, null, location, true);
        }
        
    }
    
}
