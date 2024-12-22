package com.ucst.slamtk.Class;
import android.graphics.Bitmap;
import android.graphics.Region;

public class Utils {
    public static double mean(int[] hist) {
        int sum = 0;
        for (int i = 0; i < hist.length; i++) {
            sum += i * hist[i];
        }
        return sum / hist.length;
    }

    public static double variance(int[] hist) {
        double mean = mean(hist);
        double sum = 0;
        for (int i = 0; i < hist.length; i++) {
            sum += (i - mean) * (i - mean) * hist[i];
        }
        return sum / hist.length;
    }

    public static double skewness(int[] hist) {
        double mean = mean(hist);
        double variance = variance(hist);
        double sum = 0;
        for (int i = 0; i < hist.length; i++) {
            sum += (i - mean) * (i - mean) * (i - mean) * hist[i];
        }
        return sum / hist.length / Math.pow(variance, 1.5);
    }

    public static double kurtosis(int[] hist) {
        double mean = mean(hist);
        double variance = variance(hist);
        double sum = 0;
        for (int i = 0; i < hist.length; i++) {
            sum += (i - mean) * (i - mean) * (i - mean) * (i - mean) * hist[i];
        }
        return sum / hist.length / Math.pow(variance, 2.5);
    }



    public static double energy(int[] hist) {
        int sum = 0;
        for (int i = 0; i < hist.length; i++) {
            sum += hist[i] * hist[i];
        }
        return sum;
    }

    public static double entropy(int[] hist) {
        double sum = 0;
        for (int i = 0; i < hist.length; i++) {
            if (hist[i] == 0) {
                continue;
            }
            sum += -hist[i] * Math.log(hist[i]);
        }
        return sum;
    }

    public static double uniformity(int[] hist) {
        double sum = 0;
        for (int i = 0; i < hist.length; i++) {
            if (hist[i] == 0) {
                continue;
            }
            sum += Math.pow(hist[i] / hist.length, 2);
        }
        return 1 - sum;
    }

    public  static Region computeRegion(Bitmap bitmap, int x, int y, int width, int height) {
        Region region = new Region(x, y, x + width, y + height);

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (bitmap.getPixel(i, j) != 0) {
                    region.set(i,j,x+width,y+height);
                }
            }
        }

        return region;
    }

}
