package com.example.companyReputationManagement.analysis;

import java.util.ArrayList;
import java.util.List;

public class OnlineClustering {

    public static class Cluster {
        public final List<Integer> indices = new ArrayList<>();
        public double[] centroid;
    }

    public static List<Cluster> cluster(List<double[]> vectors, double simThreshold) {
        List<Cluster> clusters = new ArrayList<>();

        for (int i = 0; i < vectors.size(); i++) {
            double[] v = vectors.get(i);
            int bestIdx = -1;
            double bestSim = -1.0;

            for (int c = 0; c < clusters.size(); c++) {
                double sim = cosine(v, clusters.get(c).centroid);
                if (sim > bestSim) {
                    bestSim = sim;
                    bestIdx = c;
                }
            }

            if (bestIdx >= 0 && bestSim >= simThreshold) {
                Cluster cl = clusters.get(bestIdx);
                cl.indices.add(i);
                cl.centroid = recomputeCentroid(cl.centroid, v, cl.indices.size());
            } else {
                Cluster cl = new Cluster();
                cl.indices.add(i);
                cl.centroid = v.clone();
                clusters.add(cl);
            }
        }

        return clusters;
    }

    private static double[] recomputeCentroid(double[] oldCentroid, double[] v, int newSize) {
        double[] c = oldCentroid.clone();
        for (int i = 0; i < c.length; i++) {
            c[i] = c[i] + (v[i] - c[i]) / newSize;
        }
        return c;
    }

    private static double cosine(double[] a, double[] b) {
        double dot = 0, na = 0, nb = 0;
        int n = Math.min(a.length, b.length);
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
