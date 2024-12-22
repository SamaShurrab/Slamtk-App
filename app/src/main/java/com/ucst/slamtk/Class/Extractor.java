package com.ucst.slamtk.Class;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;


public class Extractor {

    public Extractor(){}
    public  double colorDistribution(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] hist = new int[256];
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int red = (pixel >> 16) & 0xFF;
            hist[red]++;
        }

        double[] features = new double[256];
        for (int i = 0; i < 256; i++) {
            features[i] = (double) hist[i] / (width * height);
        }
        int counter=0;
        double sum=0;
        for(int i=0;i<features.length;i++){
           if (features[i]> 0){
               counter++;
               sum+=features[i];
           }
        }

        return (sum/counter);
    }
    public  double[] colorMoments(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] hist = new int[256];
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int red = (pixel >> 16) & 0xFF;
            hist[red]++;
        }
        double[] features = new double[7];
        features[0] = Utils.mean(hist);
        features[1] = Utils.variance(hist);
        features[2] = Utils.skewness(hist);
        features[3] = Utils.kurtosis(hist);
        features[4] = Utils.energy(hist);
        features[5] = Utils.entropy(hist);
        features[6] = Utils.uniformity(hist);
        return features;
    }

    public  double coOccurrenceMatrix(Bitmap image) {

        int width = image.getWidth();
        int height = image.getHeight();
         double value=0;
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        int counter=0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = pixels[i * j];
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                int xDiff = i - j;
                int yDiff = (j - height / 2) * 2;
           value=value+Math.abs(xDiff)+Math.abs(yDiff);
           counter++;
            //    cooccurrenceMatrix[red][Math.abs(xDiff) + Math.abs(yDiff)]++;

            }
        }

        return (value/counter);
    }

    public  int extractRegionShape(Bitmap bitmap, Region region) {
        int regionShape = 0;

        for (int x = region.getBounds().left; x < region.getBounds().right; x++) {
            for (int y = region.getBounds().top; y < region.getBounds().bottom; y++) {
                if (bitmap.getPixel(x, y) != 0) {
                    regionShape++;
                }
            }
        }

        return regionShape;
    }

    public  int extractRegionSize(Bitmap bitmap, Region region) {
        int regionSize = 0;

        for (int x = region.getBounds().left; x < region.getBounds().right; x++) {
            for (int y = region.getBounds().top; y < region.getBounds().bottom; y++) {
                if (bitmap.getPixel(x, y) != 0) {
                    regionSize++;
                }
            }
        }

        return regionSize;
    }

    public  Rect computeRectFromImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitmap.getPixel(x, y) != 0) {

                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        return new Rect(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    public  int[] getRegionPointsArrayFromRectInBitmap(Bitmap bitmap, Rect region) {
        int[] regionPoints = new int[region.width() * region.height()];
        int count = 0;
        for (int x = region.left; x < region.right; x++) {
            for (int y = region.top; y < region.bottom; y++) {
                if (bitmap.getPixel(x, y) != 0) {
                    if (count<regionPoints.length){
                    regionPoints[count] = x;
                    regionPoints[count + 1] = y;
                    count += 2;
                    }
                }
            }
        }
        int[] points = new int[count];
        for(int i=0; i<count; i++) {
            points[i] = regionPoints[i];
        }
        return points;
    }

      public int extractRegionPerimeter(Bitmap bitmap, Region region) {
        int regionPerimeter = 0;
        Rect rr=this.computeRectFromImage(bitmap);
        int[] regionPoints = this.getRegionPointsArrayFromRectInBitmap(bitmap,this.computeRectFromImage(bitmap));
        for (int i = 0; i < regionPoints.length; i++) {
            int x1 = regionPoints[i];
            int y1;
            if (i + 1 >= regionPoints.length)
             y1 = regionPoints[i];
            else
                y1 = regionPoints[i+1];

                 int y2;
            if (i + 1 < regionPoints.length) {
                int x2 = regionPoints[i + 1];

                if (i + 2 >= regionPoints.length)
                    y2 = regionPoints[i];
                else
                    y2 = regionPoints[i+2];

                regionPerimeter += Math.abs(x1 - x2) + Math.abs(y1 - y2);
            } else {
                int x2 = regionPoints[0];
                 y2 = regionPoints[1];

                regionPerimeter += Math.abs(x1 - x2) + Math.abs(y1 - y2);
            }
        }

        return regionPerimeter;
    }



      public double extractRegionCompactness(Bitmap bitmap, Region region) {
        int regionArea = extractRegionSize(bitmap, region);
        int regionPerimeter = extractRegionPerimeter(bitmap, region);

        double regionCompactness = 4 * Math.PI * regionArea / (Math.pow(regionPerimeter, 2));

        return regionCompactness;
    }
      public double extractRegionSolidity(Bitmap bitmap, Region region) {
        int regionArea = extractRegionSize(bitmap, region);
        int regionPerimeter = extractRegionPerimeter(bitmap, region);

        if (regionPerimeter == 0) {
            return 0;
        }

        double regionSolidity = (double) regionArea / (Math.pow(regionPerimeter, 2) / 4.0 * Math.PI);

        return regionSolidity;
    }
   double extractRegionEccentricity(Bitmap bitmap, Region region) {
        int regionWidth = region.getBounds().right - region.getBounds().left;
        int regionHeight = region.getBounds().bottom - region.getBounds().top;

        double regionMajorAxis = Math.sqrt(Math.pow(regionWidth, 2) + Math.pow(regionHeight, 2));
        double regionMinorAxis = Math.min(regionWidth, regionHeight);

        double regionEccentricity = 1 - (Math.pow(regionMinorAxis, 2) / Math.pow(regionMajorAxis, 2));

        return regionEccentricity;
    }

     public double extractRegionOrientation(Bitmap bitmap, Region region) {
        int[] regionPoints = this.getRegionPointsArrayFromRectInBitmap(bitmap,this.computeRectFromImage(bitmap));
        int regionCenterX = (regionPoints[0] + regionPoints[2]) / 2;
        int regionCenterY = (regionPoints[1] + regionPoints[3]) / 2;

        double regionSum = 0;

        for (int i = 0; i < regionPoints.length; i++) {
            int x = regionPoints[i];
            int y;
            if (i+1<regionPoints.length)
             y = regionPoints[i + 1];
            else  y = regionPoints[i];
            regionSum += (x - regionCenterX) * (y - regionCenterY);
        }
        double regionOrientation = Math.atan2(regionSum, this.extractRegionPerimeter(bitmap,Utils.computeRegion(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight())));
        return regionOrientation;
    }

}
