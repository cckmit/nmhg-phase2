/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.deployment.tasks;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.infra.EnhancedAnnotationSessionFactoryBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * @author vikas.sasidharan
 *
 */
public class GenerateDbPatch extends DefaultTask {

    private String dbPatchesOutputDir = ".";

    private File dbPatchFile;

    private static final String PATCH_NAME_PREFIX = "PATCH_";
    private static final String PATCH_NAME_EXTENSION = ".sql";

    public static final MessageFormat DEFAULT_PATCH_NAME_TEMPLATE = new MessageFormat("PATCH_{0}.sql");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d-MMM-y");

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String CONSOLE_MESSAGE_PREFIX = "---- [Db Patch Generation] ";
    private static final String CONSOLE_MESSAGE_SUFFIX = " ----";

    private static final Logger logger = Logger.getLogger(GenerateDbPatch.class);

    public void setDbPatchesOutputDir(String dbPatchesOutputDir) {
        this.dbPatchesOutputDir = dbPatchesOutputDir;
    }

    public void perform() {

        try {
			EnhancedAnnotationSessionFactoryBean sessionFactory = (EnhancedAnnotationSessionFactoryBean)
                    this.applicationContext.getBean("&sessionFactory");
            String[] sqls = sessionFactory.generateUpdateSchemaScript();

            if(sqls.length == 0) {
                logAndPrintToConsole("No updates detected. Skipping patch generation.");
                return;
            }

            writeOutSqlsToPatchFile(sqls);

            logAndPrintToConsole("Wrote patch file [" + this.dbPatchFile + "].");
        } catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    protected void logAndPrintToConsole(String message) {
        if(logger.isInfoEnabled())
        {
            logger.info(message);
        }

        StringBuffer messageBuffer = new StringBuffer(message.length() + 50);
        messageBuffer.append(CONSOLE_MESSAGE_PREFIX);
        messageBuffer.append(message);
        messageBuffer.append(CONSOLE_MESSAGE_SUFFIX);

        System.out.println(messageBuffer);
    }

    void writeOutSqlsToPatchFile(String[] sqls) throws IOException {

        createDbPatchFile();

        BufferedWriter out = new BufferedWriter(new FileWriter(this.dbPatchFile, true)); // Append to header information.
		for(String s : sqls) {
			out.write(s);
			out.write("\n");
			out.write("/"); // Version Manager expects SQLs to be delimited by a "/" on a separate line.
			out.write("\n");
		}

        out.flush();
		out.close();
	}

    protected void createDbPatchFile() throws IOException {
        determineDbPatchFileLocation();
        addHeaderToDbPatchFile();
    }

    protected void determineDbPatchFileLocation() {

        String patchNameSuffix = getPatchNameSuffix();
        String patchName;

        if(StringUtils.hasText(patchNameSuffix)) {
            patchName = PATCH_NAME_PREFIX + patchNameSuffix + PATCH_NAME_EXTENSION;
        } else {
            patchName = DEFAULT_PATCH_NAME_TEMPLATE.format(new Object[] {
                    getFormattedDate()
            });
        }

        this.dbPatchFile = new File(this.dbPatchesOutputDir, patchName);
	}

    private String getFormattedDate() {
        return DATE_FORMAT.format(new Date());
    }

    protected void addHeaderToDbPatchFile() throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.dbPatchFile));

        out.write("--Purpose    : ");
        out.write(getPatchComment());

        out.write(LINE_SEPARATOR);

        out.write("--Author     : ");
        out.write(System.getProperty("user.name"));

        out.write(LINE_SEPARATOR);

        out.write("--Created On : ");
        out.write(getFormattedDate());

        out.write(LINE_SEPARATOR);
        out.write(LINE_SEPARATOR);

        out.flush();
		out.close();
    }

    public String getPatchNameSuffix() {
        return getInputFromUser("Enter the patch name suffix (eg. PMT_SR) : ");
    }

    public String getPatchComment() {
        return getInputFromUser("Enter a comment for the patch : ");
    }

    protected String getInputFromUser(String message) {
        System.out.print(message);
        Scanner sc = new Scanner(System.in);
        while(!sc.hasNextLine()) {
        }

        return sc.nextLine();
    }
}

