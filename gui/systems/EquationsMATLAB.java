/*
 * EquationsViewer.java
 *
 * Created on July 6, 2002, 11:33 AM
 */

package dynetica.gui.systems;

/**
 *
 * @author  Lingchong You
 * @version 0.01
 */
import dynetica.entity.*;
import dynetica.expression.GeneralExpression;
import dynetica.system.*;
import dynetica.reaction.*;
import java.util.*;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EquationsMATLAB extends javax.swing.JPanel {
    private ReactiveSystem system;
    private boolean SINGLE_PLOT;

    private boolean HAS_RANDOM;
    private boolean HAS_RANDENG;
    private boolean HAS_NORMAL;
    private boolean HAS_PULSE;
    private boolean HAS_PULSES;
    private boolean HAS_HILL;
    private boolean HAS_STEP;
    private boolean HAS_TRIGGERAT;

    /** Creates new form ODE_ModelPanel */
    public EquationsMATLAB(ReactiveSystem system) {
        this.system = system;
        resetBools();
        initComponents();
    }

    public String equations;

    private String getEquations() {
        StringBuffer equationText = new StringBuffer("");
        Object[] substanceList = system.getSubstances().toArray();
        Object[] parameterList = system.getParameters().toArray();

        String newLine = System.getProperty("line.separator");

        String sysAnnotation = system.systemInformation.getAnnotation();
        if (sysAnnotation != null && sysAnnotation.length() > 0) {
            String[] lines = sysAnnotation.split("\n");
            for (int i = 0; i < lines.length; i++) {
                equationText.append("% " + lines[i] + newLine);
            }
            equationText.append(newLine + newLine);
        }

        equationText.append("function " + system.getName() + "()" + newLine);

        /* Define all parameters and ExpressionVariables as global variables */
        equationText.append("global ");
        for (int i = 0; i < parameterList.length; i++) {
            Parameter p = (Parameter) parameterList[i];
            if (p.getName() == "Time") {
                continue;
            }
            equationText.append(p.getName() + " ");
        }
        equationText.append(newLine);

        /* Put all parameters and their values at top of file for easy access */
        for (int i = 0; i < parameterList.length; i++) {
            Parameter p = (Parameter) parameterList[i];
            if (p.getName() == "Time") {
                continue;
            }
            equationText.append(p.getName() + " = " + p.getValue() + ";");
            String ann = p.getAnnotation().replaceAll("\\n", "\t");
            if (ann != null && ann.length() > 0) {
                equationText.append(" % " + ann);
            }
            equationText.append(newLine);
        }
        double t_final = system.getAlgorithm().getIterations()
                * system.getAlgorithm().getSamplingStep();
        equationText.append("tspan = [0 " + t_final + "];" + newLine);

        /* Define the initial values for each substance */
        equationText.append("y0 = [");
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            equationText.append(s.getInitialValue() + "; ");
        }
        equationText.append("];" + newLine);
        equationText.append("[t,y] = ode45(@eom, tspan, y0);" + newLine
                + newLine);

        /*
         * Create a substanceMap of substance names to their integer indexes
         * Exclude ExpressionVariables from this map
         */
        HashMap<String, Integer> substanceMap = new HashMap<String, Integer>();
        int count = 1;
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            substanceMap.put(s.getName(), (Integer) count);
            count++; /* count is the number of non-ExpressionVariable substances */
        }

        /* Create plots for concentration of all substances vs. time */
        SINGLE_PLOT = jCheckBox1.isSelected();
        if (SINGLE_PLOT) {
            equationText.append(plotSingleFigure(substanceList, substanceMap));
        } else {
            equationText
                    .append(plotMultipleFigures(substanceList, substanceMap));
        }

        /* ODE function */
        equationText.append("function dydt = eom(Time,y)" + newLine);
        equationText.append("global ");
        for (int i = 0; i < parameterList.length; i++) {
            Parameter p = (Parameter) parameterList[i];
            if (p.getName() == "Time") {
                continue;
            }
            equationText.append(p.getName() + " ");
        }
        equationText.append(newLine);

        /* Define the equations for ExpressionVariables */
        for (int i = 0; i < substanceList.length; i++) {
            if (!(substanceList[i] instanceof ExpressionVariable)) {
                continue;
            }
            ExpressionVariable s = (ExpressionVariable) substanceList[i];
            String exp = s.getExpression().toString();
            for (int j = 0; j < substanceList.length; j++) {
                Substance temp = (Substance) substanceList[j];
                if (!(temp instanceof ExpressionVariable)) {
                    String concen = "\\[" + temp.getName() + "\\]";
                    exp = exp.replaceAll(concen,
                            "y(" + substanceMap.get(temp.getName()) + ")");
                }
            }
            exp = checkExp(exp);
            equationText.append(s.getName() + " = " + exp + ";" + newLine);
        }

        equationText.append(newLine + "dydt = [");
        /* Now put in rate expressions for all non-ExpressionVariable substances */
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            GeneralExpression ge = s.getRateExpression();
            if (ge == null) {
                equationText.append("0;" + newLine + "        ");
                continue;
            }
            String exp = ge.toString();
            for (int j = 0; j < substanceList.length; j++) {
                Substance temp = (Substance) substanceList[j];
                if (!(temp instanceof ExpressionVariable)) {
                    String concen = "\\[" + temp.getName() + "\\]";
                    exp = exp.replaceAll(concen,
                            "y(" + substanceMap.get(temp.getName()) + ")");
                }
            }
            exp = checkExp(exp);
            equationText.append(exp + ";" + newLine + "        ");
        }
        equationText.append("];" + newLine);
        equationText.append(generateFunctionAppendix());

        equations = equationText.toString();
        return equations;
    }

    private String plotMultipleFigures(Object[] substanceList,
            HashMap<String, Integer> subMap) {
        String newLine = System.getProperty("line.separator");
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            int idx = subMap.get(s.getName());
            text.append("figure(" + (idx) + "); clf" + newLine);
            text.append("plot(t, y(:," + idx + "))");
            String ann = s.getAnnotation().replaceAll("\\n", "\t");
            if (ann != null && ann.length() > 0) {
                text.append(" % " + s.getName() + ": " + ann);
            }
            text.append(newLine + "xlabel 'Time'; ylabel '" + s.getName()
                    + "';" + newLine);
            text.append("title 'Concentration of " + s.getName() + " vs. Time'");
            text.append(newLine + newLine);
        }
        return text.toString();
    }

    private String plotSingleFigure(Object[] substanceList,
            HashMap<String, Integer> subMap) {
        String newLine = System.getProperty("line.separator");
        StringBuilder text = new StringBuilder();
        ArrayList<String> lineStyles = enumerateLineStyles();

        text.append("figure(1); clf" + newLine + "hold on" + newLine);
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            int idx = subMap.get(s.getName());
            int styleNum = idx - 1;
            while (styleNum > lineStyles.size()) {
                styleNum -= lineStyles.size();
            }
            String style = lineStyles.get(styleNum);
            text.append("plot(t, y(:," + idx + "), " + style + ")");
            String ann = s.getAnnotation().replaceAll("\\n", "\t");
            if (ann != null && ann.length() > 0) {
                text.append(" % " + s.getName() + ": " + ann);
            }
            text.append(newLine);
        }
        text.append("xlabel 'Time'; ylabel 'Concentration';" + newLine);
        text.append("title 'Concentration vs. Time'" + newLine);

        text.append("legend(");
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            text.append("'" + s.getName() + "',");
        }
        text.append("0) " + newLine + newLine);
        return text.toString();
    }

    private ArrayList<String> enumerateLineStyles() {
        ArrayList<String> styles = new ArrayList<String>();
        String[] colors = { "k", "b", "r", "g", "c", "m", "y" };
        String[] lines = { "-", "--", "-.", ":" };
        for (int j = 0; j < lines.length; j++) {
            for (int i = 0; i < colors.length; i++) {
                styles.add("'" + colors[i] + lines[j] + "'");
            }
        }
        return styles;
    }

    private String checkExp(String exp) {
        if (!HAS_RANDOM && exp.contains("random(")) {
            HAS_RANDOM = true;
        }
        if (!HAS_RANDENG && exp.contains("randENG(")) {
            HAS_RANDENG = true;
        }
        if (!HAS_NORMAL && exp.contains("normal()")) {
            HAS_NORMAL = true;
        }
        if (!HAS_PULSE && exp.contains("pulse(")) {
            HAS_PULSE = true;
        }
        if (!HAS_PULSES && exp.contains("pulses(")) {
            HAS_PULSES = true;
        }
        if (!HAS_STEP && exp.contains("step(")) {
            HAS_STEP = true;
        }
        if (!HAS_HILL && exp.contains("hill(")) {
            HAS_HILL = true;
        }
        if (!HAS_TRIGGERAT && exp.contains("triggerAt(")) {
            HAS_TRIGGERAT = true;
        }
        if (exp.contains("min(")) {
            exp = fixMin(exp);
        }
        if (exp.contains("max(")) {
            exp = fixMax(exp);
        }
        return exp;
    }

    private String fixMax(String str) {
        StringBuilder result = new StringBuilder("");
        while (str.contains("max(")) {
            int index = str.indexOf("max(");
            result.append(str.substring(0, index + 4));
            str = str.substring(index + 4);
            if (str.charAt(0) == '[') {
                continue;
            }
            int closeParen = findCloseParen(str);
            result.append("[" + str.substring(0, closeParen) + "])");
            str = str.substring(closeParen + 1);
        }
        result.append(str);
        return result.toString();
    }

    private String fixMin(String str) {
        StringBuilder result = new StringBuilder("");
        while (str.contains("min(")) {
            int index = str.indexOf("min(");
            result.append(str.substring(0, index + 4));
            str = str.substring(index + 4);
            if (str.charAt(0) == '[') {
                continue;
            }
            int closeParen = findCloseParen(str);
            result.append("[" + str.substring(0, closeParen) + "])");
            str = str.substring(closeParen + 1);
        }
        return result.toString();
    }

    /*
     * Finds the index of the first close paren that hasn't been opened (Assumes
     * the parenthesis was opened before the substring was extracted
     */
    protected int findCloseParen(String substr) {
        int countOpen = 1;
        for (int i = 0; i < substr.length(); i++) {
            if (substr.charAt(i) == '(') {
                countOpen++;
            }
            if (substr.charAt(i) == ')') {
                countOpen--;
            }
            if (countOpen == 0) {
                return i;
            }
        }
        return -1; /*
                    * This should never happen for properly constructed
                    * expressions
                    */
    }

    private String generateFunctionAppendix() {
        String newLine = System.getProperty("line.separator");
        StringBuffer appendix = new StringBuffer(newLine);
        if (HAS_RANDOM) {
            appendix.append("function r = random(a,b)" + newLine);
            appendix.append("    r = a + (b-a)*rand;" + newLine + newLine);
        }
        if (HAS_RANDENG) {
            appendix.append("function r = randENG(x)" + newLine);
            appendix.append("    r = (-1/x)*rand;" + newLine + newLine);
        }
        if (HAS_NORMAL) {
            appendix.append("function n = normal()" + newLine);
            appendix.append("    n = normrnd(0,1);" + newLine + newLine);
        }
        if (HAS_PULSE) {
            appendix.append("function p = pulse(t,a,b)" + newLine);
            appendix.append("    p = (t >= a).*(t < b);" + newLine + newLine);
        }
        if (HAS_PULSES) {
            appendix.append("function p = pulses(T,T0,T1,T2)" + newLine);
            appendix.append("    n = floor((T - T0)/T1);" + newLine);
            appendix.append("    p = ((T >= n*T1 + T0 ) && (T < T0 + n* T1 + T2));");
            appendix.append(newLine + newLine);
        }
        if (HAS_TRIGGERAT) {
            appendix.append("global trig = false;" + newLine);
            appendix.append("function r = triggerAt(d,c,t)" + newLine);
            appendix.append("    if (c>=0) trig = true;");
            appendix.append("    r = (trig).*t + (~trig).*d;" + newLine
                    + newLine);
        }
        if (HAS_HILL) {
            appendix.append("function r = hill(c, nH, KH)" + newLine);
            appendix.append("    r = (c.^nH)./(c.^nH + KH.^nH);" + newLine
                    + newLine);
        }

        if (HAS_STEP) {
            appendix.append("function r = step(a, b)" + newLine);
            appendix.append("    r = (a > b);" + newLine + newLine);
        }

        return appendix.toString();
    }

    private void resetBools() {
        HAS_RANDOM = false;
        HAS_RANDENG = false;
        HAS_NORMAL = false;
        HAS_PULSE = false;
        HAS_PULSES = false;
        HAS_HILL = false;
        HAS_STEP = false;
        HAS_TRIGGERAT = false;
    }

    private void refresh() {
        resetBools();
        this.equationArea.setText(this.getEquations());
    }

    protected void saveAsMfile() {
        String workingDir = dynetica.util.DyneticaProperties
                .getProperty("workingDirectory");
        JFileChooser chooser = new JFileChooser(workingDir);
        chooser.setAcceptAllFileFilterUsed(true);

        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".m")
                        || pathname.isDirectory();
            }

            public String getDescription() {
                return "M Files (*.m)";
            }
        });

        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // System.out.println(input.getParentFile().getPath());
            workingDir = file.getParentFile().getAbsolutePath();
            if (chooser.getFileFilter()
                    .equals(chooser.getAcceptAllFileFilter()))
                this.saveM(file);
            else {
                String name = file.getPath();
                if (name.indexOf(".m") < 0)
                    saveM(new File(name + ".m"));
                else
                    saveM(new File(name));
            }
        }
    }

    protected void saveM(File file) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(file));
            out.println(equations);
            out.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        equationArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        jButton1.setText("jButton1");

        setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MATLAB Simulation Code using ODE45");
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(500, 400));

        equationArea.setEditable(false);
        equationArea.setText(getEquations());
        jScrollPane1.setViewportView(equationArea);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        refreshButton.setText("refresh");
        refreshButton
                .setToolTipText("Rrefresh the equations and parameters after the system has been changed.");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        jPanel1.add(refreshButton);

        jButton2.setText("save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        jCheckBox1.setText("Single Plot");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox1);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshButtonActionPerformed
        refresh();
    }// GEN-LAST:event_refreshButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        saveAsMfile();
    }// GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
        refresh();
    }// GEN-LAST:event_jCheckBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea equationArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton refreshButton;
    // End of variables declaration//GEN-END:variables

}
