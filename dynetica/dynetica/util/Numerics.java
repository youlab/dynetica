/**
 * Numerics.java
 *
 *
 * Created: Sun Sep 03 11:24:28 2000
 *
 * @author Lingchong You
 * @version 0.2
 */

package dynetica.util;

import java.util.List;

public class Numerics {

    public static double log10(double d) {
        return (Math.log(d) / Math.log(10.0));
    }

    public static double sum(double[] d) {
        double s = d[0];
        for (int i = 1; i < d.length; i++) {
            s += d[i];
        }

        return s;
    }

    public static double product(double[] d) {
        double p = d[0];
        for (int i = 1; i < d.length; i++) {
            p *= d[i];
        }

        return p;
    }

    public static void pow(double[] d, double n) {
        for (int i = 0; i < d.length; i++) {
            d[i] = Math.pow(d[i], n);
        }
    }

    public static double max(double[] d) {
        double tmp = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < d.length; i++)
            if (d[i] > tmp)
                tmp = d[i];
        return tmp;
    }

    public static double min(double[] d) {
        double tmp = Double.POSITIVE_INFINITY;
        for (int i = 0; i < d.length; i++)
            if (d[i] < tmp)
                tmp = d[i];
        return tmp;
    }

    public static int max(int[] d) {
        int tmp = Integer.MIN_VALUE;
        for (int i = 0; i < d.length; i++)
            if (d[i] > tmp)
                tmp = d[i];
        return tmp;
    }

    public static int min(int[] d) {
        int tmp = Integer.MAX_VALUE;
        for (int i = 0; i < d.length; i++)
            if (d[i] < tmp)
                tmp = d[i];
        return tmp;
    }

    public static int max(int[][] n) {
        int tmp = Integer.MIN_VALUE;
        for (int i = 0; i < n.length; i++) {
            int nmax = max(n[i]);
            if (nmax > tmp)
                tmp = nmax;
        }
        return tmp;
    }

    public static int min(int[][] n) {
        int tmp = Integer.MAX_VALUE;
        for (int i = 0; i < n.length; i++) {
            int nmin = min(n[i]);
            if (nmin < tmp)
                tmp = nmin;
        }
        return tmp;
    }

    public static double max(double[][] d) {
        double tmp = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < d.length; i++) {
            double dmax = max(d[i]);
            if (dmax > tmp)
                tmp = dmax;
        }
        return tmp;
    }

    public static double min(double[][] d) {
        double tmp = Double.POSITIVE_INFINITY;
        for (int i = 0; i < d.length; i++) {
            double dmin = min(d[i]);
            if (dmin < tmp)
                tmp = dmin;
        }
        return tmp;
    }

    public static double minPositive(double[] d) {
        double tmp = Double.POSITIVE_INFINITY;
        for (int i = 0; i < d.length; i++)
            if (d[i] > 0 && d[i] < tmp)
                tmp = d[i];
        return tmp;
    }

    public static double minPositive(double[][] d) {
        double tmp = Double.POSITIVE_INFINITY;
        for (int i = 0; i < d.length; i++) {
            double dmin = minPositive(d[i]);
            if (minPositive(d[i]) < tmp)
                tmp = dmin;
        }
        return tmp;
    }

    public static Double sum(List<Double> list) {
        Double sum = 0.0;
        for (Double d : list) {
            sum += d;
        }
        return sum;
    }

    // Works exactly like MATLAB's linspace
    public static double[] linspace(double min, double max, int points) {
        double[] d = new double[points];
        for (int i = 0; i < points; i++) {
            d[i] = min + i * (max - min) / (points - i);
        }
        return d;
    }

    // Slightly different than MATLAB's logspace. Instead of giving it powers of
    // 10,
    // you directly give it the minimum and maximum values you want to use.
    public static double[] logspace(double min, double max, int points) {
        double[] d = new double[points];
        d[0] = min;
        double scale = Math.exp((Math.log(max) - Math.log(min)) / (points - 1));
        for (int i = 1; i < points; i++) {
            d[i] = d[i - 1] * scale;
        }
        d[points - 1] = max; // in case there was some float error (which there
                             // probably was)
        return d;
    }

    /**
     * Transposes a matrix
     * 
     * @param m
     * @return mT[j][i] = m[i][j]
     */
    public static double[][] transpose(double[][] m) {
        double[][] mT = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                mT[j][i] = m[i][j];
        return mT;
    }

    public static String displayFormattedValue(double value) {
        // String display;
        java.text.NumberFormat formatter;
        if (value == 0.0)
            return String.valueOf(value);

        if (value < 1.0e-10)
            formatter = new java.text.DecimalFormat("0.#E000");
        else if (value < 1.0e-3)
            formatter = new java.text.DecimalFormat("0.#E00");
        else if (value < 1.0e-2)
            formatter = new java.text.DecimalFormat("0.0000");
        else if (value < 0.1)
            formatter = new java.text.DecimalFormat("0.000");
        else if (value < 1)
            formatter = new java.text.DecimalFormat("0.00");
        else if (value < 10)
            formatter = new java.text.DecimalFormat("0.0");
        else if (value < 100)
            formatter = new java.text.DecimalFormat("00");
        else if (value < 1000)
            formatter = new java.text.DecimalFormat("000");
        else if (value < 10000)
            formatter = new java.text.DecimalFormat("0000");
        else if (value < 100000)
            formatter = new java.text.DecimalFormat("00000");
        else if (value < 1e10)
            formatter = new java.text.DecimalFormat("0.0E0");
        else
            formatter = new java.text.DecimalFormat("0.0E00");

        return formatter.format(value);
    }

} // Numerics
