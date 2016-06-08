/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.util;

import java.util.Arrays;

/**
 * 
 * @author owner
 */
public class Statistics {

    public static double mean(double[] d) {
        return Numerics.sum(d) / d.length;
    }

    public static double mean(double[] d, int startIdx, int endIdx) {
        double[] subArray = Arrays.copyOfRange(d, startIdx, endIdx);
        return mean(subArray);
    }

    public static double var(double[] d) {
        double result = 0;
        double mean = mean(d);
        for (int i = 0; i < d.length; i++) {
            result += (d[i] - mean) * (d[i] - mean);
        }
        return result;
    }

    public static double var(double[] d, int startIdx, int endIdx) {
        double[] subArray = Arrays.copyOfRange(d, startIdx, endIdx);
        return var(subArray);
    }

    public static double binomial(int N, int k, double p) {
        double[][] b = new double[N + 1][k + 1];

        for (int i = 0; i <= N; i++)
            b[i][0] = Math.pow(1.0 - p, i);

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= k; j++) {
                b[i][j] = p * b[i - 1][j - 1] + (1.0 - p) * b[i - 1][j];
            }
        }
        return b[N][k];
    }

    // Performs a linear regression on y vs. x and reports the result in the
    // form of an array [m, B, r^2] where m is the slope and B is the intercept
    public static double[] linearRegression(double[] x, double[] y) {
        if (x.length != y.length) {
            return null;
        }
        double[] result = new double[3];

        double[] xy = new double[x.length];
        double[] xsquared = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            xy[i] = x[i] * y[i];
            xsquared[i] = x[i] * x[i];
        }
        double mx = mean(x); // Stored ahead of time to eliminate the need
        double my = mean(y); // for multiple calls to mean(), which is O(n)
        result[0] = (mx * mean(y) - mean(xy)) / (mx * mx - mean(xsquared));
        result[1] = my - result[0] * mx;

        // Calculate model values
        double[] yhat = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            yhat[i] = result[0] * x[i] + result[1];
        }
        result[2] = rSquared(y, yhat);
        return result;
    }

    // Gives the r-squared value between some data (x) and a model for the data
    // (y)
    public static double rSquared(double[] x, double[] y) {
        double[] res = new double[x.length];
        double[] tot = new double[x.length];
        double meanX = Statistics.mean(x);
        for (int i = 0; i < x.length; i++) {
            tot[i] = (x[i] - meanX) * (x[i] - meanX);
            res[i] = (x[i] - y[i]) * (x[i] - y[i]);
        }
        double SStot = Numerics.sum(tot);
        double SSres = Numerics.sum(res);
        return 1 - (SSres / SStot);
    }
}
