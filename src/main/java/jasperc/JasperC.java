/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jasperc;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;

/**
 *
 * @author carlos
 */
public class JasperC extends Task implements Serializable {

    private List<FileSet> filesets = new ArrayList();
    private String srcDir = "src";
    private String buildDir = "build/classes";

    private String srcToExt(String jrxml, String ext) {
        int pos = jrxml.lastIndexOf(".jrxml");
        String jasper = jrxml.substring(0, pos);
        return jasper + ext;
    }

    private String srcToBuild(String jasper) {
        jasper = jasper.replaceAll(getSrcDir(), getBuildDir());
        return jasper;
    }

    private void mvToBuild(File src) {
        File f = new File(srcToBuild(src.getAbsolutePath()));
        f.getParentFile().mkdirs();
        src.renameTo(f);
    }

    @Override
    public void execute() {
        String jrxml;
        String jasper;

        log("-------------------------compilador JasperC-----------------------------------");
        String estoy = new File("").getAbsolutePath();
        log("carpeta de trabajo:" + estoy);
        for (FileSet unF : getFilesets()) {
            Iterator iter = unF.iterator();
            while (iter.hasNext()) {

                Object obj = iter.next();
                FileResource uno = (FileResource) obj;
                jrxml = uno.getFile().getAbsolutePath();
                jasper = srcToBuild(srcToExt(jrxml, ".jasper"));
                FileResource dos = new FileResource(new File(jasper));
                try {
                    if (dos.getLastModified() < uno.getLastModified()) {
                        log(uno.getFile().getAbsolutePath());
                        JasperCompileManager.compileReportToFile(uno.getFile().getAbsolutePath());
                    }
                } catch (Exception ex) {
                    log(ex.toString(), Project.MSG_ERR);
                } finally {
                    mvToBuild(new File(srcToExt(jrxml, ".jasper")));
                }
            }
        }
        log("------------------fin de compilador JasperC-----------------------------------");
    }

    public void addFileset(FileSet fileset) {
        getFilesets().add(fileset);
    }

    public List<FileSet> getFilesets() {
        return filesets;
    }

    public String getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    public String getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }
}
