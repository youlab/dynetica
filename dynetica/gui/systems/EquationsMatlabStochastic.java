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
import dynetica.system.*;
import dynetica.reaction.*;
import java.util.*;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EquationsMatlabStochastic extends javax.swing.JPanel {
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
    public EquationsMatlabStochastic(ReactiveSystem system) {
        this.system = system;
        resetBools();
        initComponents();
    }

    public String equations;

    private String getEquations() {
        StringBuffer equationText = new StringBuffer("");
        Object[] substanceList = system.getSubstances().toArray();
        Object[] parameterList = system.getParameters().toArray();
        Object[] reactionsList = system.getProgressiveReactions().toArray();
        String newline = System.getProperty("line.separator");

        // Define all used parameters
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
            equationText.append(newline);
        }
        equationText.append("numReactions = " + reactionsList.length + ";"
                + newline + newline);

        // Define the reaction matrix
        equationText.append("Reactions = [");
        for (int i = 0; i < reactionsList.length; i++) {
            Reaction r = (Reaction) reactionsList[i];
            for (int j = 0; j < substanceList.length; j++) {
                if (substanceList[j] instanceof ExpressionVariable) {
                    continue;
                }
                Substance s = (Substance) substanceList[j];
                if (r.contains(s)) {
                    equationText.append(r.getCoefficient(s) + " ");
                } else {
                    equationText.append("0 ");
                }
            }
            String ann = r.getAnnotation();
            if (ann == null) {
                ann = "";
            }
            if (i == (reactionsList.length - 1)) {
                equationText.append("]; % " + r.getName() + ": " + ann
                        + newline + newline);
            } else {
                equationText.append("; % " + r.getName() + ": " + ann + newline
                        + "             ");
            }
        }

        // Initial values
        equationText.append("t(1) = 0;" + newline + newline);
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            equationText.append(s.getName() + "(1) = " + s.getInitialValue()
                    + ";");
            String ann = s.getAnnotation().replaceAll("\\n", "\t");
            if (ann != null && ann.length() > 0) {
                equationText.append(" % " + ann);
            }
            equationText.append(newline);
        }
        for (int i = 0; i < substanceList.length; i++) {
            if (!(substanceList[i] instanceof ExpressionVariable)) {
                continue;
            }
            ExpressionVariable e = (ExpressionVariable) substanceList[i];
            equationText.append(e.getName() + "(1) = ");
            String exp = fixExpression(e.getExpression().toString(),
                    substanceList, "1");
            equationText.append(exp + ";" + newline);
        }

        // Iterations
        String nlt = newline + "    "; // newline + tab
        equationText.append("for i = 2:"
                + system.getAlgorithm().getIterations() + nlt);
        equationText.append("Rates = [");
        for (int i = 0; i < reactionsList.length; i++) {
            ProgressiveReaction r = (ProgressiveReaction) reactionsList[i];
            String rate = r.getRateExpression().toString();
            rate = fixExpression(rate, substanceList, "i-1");
            equationText.append(rate);
            if (i == (reactionsList.length - 1)) {
                equationText.append("];" + newline + nlt);
            } else {
                equationText.append(";" + nlt + "        ");
            }
        }
        equationText.append("Activity = sum(Rates);" + nlt);
        equationText.append("if (Activity==0)" + nlt + "    ");
        equationText.append("break;" + nlt + "end" + nlt);
        equationText.append("tstep = -1/Activity * log(rand());" + nlt);
        equationText.append("t(i) = t(i-1) + tstep;" + nlt);
        equationText.append("ra = rand()*Activity;" + nlt);
        equationText.append("tmp = 0;" + nlt);
        String tb = "    ";
        equationText.append("for j=1:numReactions" + nlt + tb);
        equationText.append("tmp = tmp + Rates(j);" + nlt + tb);
        equationText.append("if tmp>ra" + nlt + tb + tb);
        int counter = 1;
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            String name = s.getName();
            equationText.append(name + "(i) = ");
            equationText.append(name + "(i-1) + Reactions(j,");
            equationText.append(counter + ");" + nlt + tb + tb);
            counter++;
        }
        for (int i = 0; i < substanceList.length; i++) {
            if (!(substanceList[i] instanceof ExpressionVariable)) {
                continue;
            }
            ExpressionVariable e = (ExpressionVariable) substanceList[i];
            equationText.append(e.getName() + "(i) = ");
            String exp = fixExpression(e.getExpression().toString(),
                    substanceList, "i");
            equationText.append(exp + ";" + nlt + tb + tb);
        }
        equationText.append("break;" + nlt + tb + "end" + nlt + "end" + newline
                + "end" + newline + newline);
        if (jCheckBox1.isSelected()) {
            equationText.append(plotSingleFigure(substanceList));
        } else {
            equationText.append(plotMultipleFigures(substanceList));
        }

        StringBuilder annText = new StringBuilder();
        String sysAnnotation = system.systemInformation.getAnnotation();
        if (sysAnnotation != null && sysAnnotation.length() > 0) {
            String[] lines = sysAnnotation.split("\n");
            for (int i = 0; i < lines.length; i++) {
                annText.append("% " + lines[i] + newline);
            }
            annText.append(newline + newline);
        }

        equations = annText.toString() + generateFunctionAppendix()
                + equationText.toString();
        return equations;
    }

    private String plotMultipleFigures(Object[] substanceList) {
        StringBuilder text = new StringBuilder();
        String newline = System.getProperty("line.separator");
        int fignum = 1;
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            text.append("figure(" + fignum + "); clf" + newline);
            text.append("plot(t," + s.getName() + ")" + newline);
            text.append("title('" + s.getName() + " vs. Time')" + newline
                    + newline);
            fignum++;
        }
        return text.toString();
    }

    private String plotSingleFigure(Object[] substanceList) {
        String newLine = System.getProperty("line.separator");
        StringBuilder text = new StringBuilder();
        ArrayList<String> lineStyles = enumerateLineStyles();

        text.append("figure(1); clf" + newLine + "hold on" + newLine);
        int styleNum = 0;
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                continue;
            }
            while (styleNum > lineStyles.size()) {
                styleNum -= lineStyles.size();
            }
            String style = lineStyles.get(styleNum);
            text.append("plot(t," + s.getName() + "," + style + ")" + newLine);
            styleNum++;
        }
        text.append("xlabel 'Time'" + newLine);
        text.append("title 'Time Course Plots'" + newLine);

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

    // Ensures proper indexing during iterations
    private String fixExpression(String exp, Object[] substanceList, String idx) {
        for (int i = 0; i < substanceList.length; i++) {
            Substance s = (Substance) substanceList[i];
            if (s instanceof ExpressionVariable) {
                exp = exp
                        .replaceAll(s.getName(), s.getName() + "(" + idx + ")");
            } else {
                exp = exp.replaceAll("\\[" + s.getName() + "\\]", s.getName()
                        + "(" + idx + ")");
            }
            exp = exp.replaceAll("Time", "t(" + idx + ")");
        }
        return checkExpression(exp);
    }

    private String checkExpression(String exp) {
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

    // Finds the index of the first close paren that hasn't been opened
    // (Assumes the parenthesis was opened before the substring was extracted
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

    private String generateFunctionAppendix() {
        String newLine = System.getProperty("line.separator");
        StringBuffer appendix = new StringBuffer(newLine);
        if (HAS_RANDOM) {
            appendix.append("random = @(a,b) a + (b-a)*rand;" + newLine
                    + newLine);
        }
        if (HAS_RANDENG) {
            appendix.append("randENG = @(x) (-1/x)*rand;" + newLine + newLine);
        }
        if (HAS_NORMAL) {
            appendix.append("normal = @() normrnd(0,1);" + newLine + newLine);
        }
        if (HAS_PULSE) {
            appendix.append("pulse = @(t,a,b) (t >= a).*(t < b);" + newLine
                    + newLine);
        }
        if (HAS_PULSES) {
            appendix.append("pulses = @(T,T0,T1,T2) ((T >= floor((T-T0)/T1)*T1 + T0) && (T < T0 + floor((T-T0)/T1)*T1 + T2));");
            appendix.append(newLine + newLine);
        }
        if (HAS_TRIGGERAT) {
            appendix.append("%WARNING - The triggerAt function behaves differently in MATLAB than dynetica"
                    + newLine);
            appendix.append("triggerAt = @(d,c,t) (c>=0).*t + (c<0).*d;"
                    + newLine + newLine);
        }
        if (HAS_HILL) {
            appendix.append("hill = @(c, n(c.^nH)./(c.^nH + KH.^nH);" + newLine
                    + newLine);
        }

        if (HAS_STEP) {
            appendix.append("step = @(a,b) (a > b);" + newLine + newLine);
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

    private void refresh() {
        resetBools();
        this.equationArea.setText(this.getEquations());
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
        jLabel1.setText("Stochastic Differential Solver MATLAB Code");
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
