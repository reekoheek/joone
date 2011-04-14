package org.joone.edit;

import java.io.*;
import java.util.*;

/**
 * Extension of file to perform ini file functions.
 */
public class IniFile {

    String fileName;
    Collection cache = new ArrayList();

    public IniFile(String fileNameArg) throws IOException {
        fileName = fileNameArg;
        File file = new File(fileName);
        if (!file.exists()) {
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            pw.flush();
            pw.close();
        }
    }

    public void setParameter(String section, String item, String value)
            throws IOException, IllegalArgumentException {

        if (section == null || section.trim().equals("")) {
            throw new IllegalArgumentException("Section is null or blank.");
        }

        if (item == null || item.trim().equals("")) {
            throw new IllegalArgumentException("Item is null or blank.");
        }

        if (value == null) {
            throw new IllegalArgumentException("Value is null.");
        }

        cache = new ArrayList();

        // Read in till the [section] is found.
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        boolean foundSection = false;

        while ((line = br.readLine()) != null) {
            cache.add(line);
            if (line.toUpperCase().equals("[" + section.toUpperCase() + "]")) {
                foundSection = true;
                break;
            }
        }

        // See if section has been found. If not, add section.
        if (!foundSection) {
            cache.add("[" + section.toUpperCase() + "]");
            foundSection = true;
        }

        // Scan in remaining items.
        boolean foundItem  = false;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(item.toLowerCase() + "=")) {
                cache.add(item.toLowerCase() + "=" + value.toLowerCase());
                foundItem = true;
            } else {
                if (line.startsWith("[") && !foundItem) {
                    cache.add(item.toLowerCase() + "=" + value.toLowerCase());
                    foundSection = false;
                    foundItem = true;
                }
                cache.add(line);
            }
        }

        if (!foundItem && foundSection) {
            cache.add(item.toLowerCase() + "=" + value.toLowerCase());
        }

        br.close();

        // Now output everything to the file.
        PrintWriter pw = new PrintWriter(new FileWriter(fileName));

        for (Iterator iter = cache.iterator(); iter.hasNext();) {
            pw.println((String) iter.next());
        }

        pw.flush();
        pw.close();
    }

    public String getParameter(String section, String item)
            throws IOException, IllegalArgumentException {

        if (!cache.isEmpty()) {

            String result = null;
            boolean foundSection = false;

            for (Iterator iter = cache.iterator(); iter.hasNext();) {
                String line = (String) iter.next();
                if (line.toUpperCase().equals("[" + section.toUpperCase() + "]")) {
                    foundSection = true;
                    continue;
                }

                if (foundSection && line.startsWith(item + "=")) {
                    result = line.substring(1 + line.indexOf("="));
                }

                if (foundSection && line.startsWith("[")) {
                    break;
                }
            }

            if (result != null) {
                return result;
            }

        } else {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            boolean foundSection = false;

            String result = null;
            while ((line = br.readLine()) != null) {
                cache.add(line);

                if (line.toUpperCase().equals("[" + section.toUpperCase() + "]")) {
                    foundSection = true;
                    continue;
                }

                if (foundSection && line.startsWith(item + "=")) {
                    result = line.substring(1 + line.indexOf("="));
                }

                if (foundSection && line.startsWith("[")) {
                    foundSection = false;
                }
            }

            if (result != null) {
                return result;
            }
        }

        throw new IllegalArgumentException("Section {" + section + "} item {" + item + "} not found.");
    }

    public String getParameter(String section, String item, String deflt)
            throws IOException {

        try {
            return getParameter(section, item);
        } catch (IllegalArgumentException e) {
            return deflt;
        }
    }

    public static void main(String[] args) {
        try {
            IniFile i = new IniFile("q.ini");
            i.setParameter("section 1","item 1","value 1");
            i.setParameter("sec 2","item 2","val 2");
            i.setParameter("section 1","item 3","val 3");
            System.out.println(i.getParameter("section 1", "item 3", "55"));
            System.out.println(i.getParameter("sec 2", "item 2"));
            System.out.println(i.getParameter("section 1", "item 4", "55"));
            System.out.println(i.getParameter("section 2", "item 2"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}