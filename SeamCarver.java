import java.awt.Color;
import java.util.Stack;

import edu.princeton.cs.algs4.Picture;


//resize the picture
public class SeamCarver {

    private Picture picture;
    private Color[][] matrix_Color;
    private double[][] weightMatrix;
    private int sizeDigraph;
    private Color[][] resMatrixColor;
    private EdgeWeightedDigraph digraph;
    private int verticalCarver[];
    private Picture pictureRes;

    public SeamCarver(Picture picture, int n) {
        this.picture = picture;
        makeCarver(n);
    }
    private void makeCarver(int n) {
        if (n >= width()) {
            System.err.println("n must be less than the width");
            return;
        }
        picture.show();
        for (int i = 0; i < n; i++) {
            RGBMatrix();
            makeWeightMatrix();
            makeGraph();
            findSeam();
            removeSeam();
        }
        pictureRes.show();
    }
    private void RGBMatrix() {
        matrix_Color = new Color[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                matrix_Color[i][j] = picture.get(j, i);
            }
        }
    }
    private void makeWeightMatrix() {
        weightMatrix = new double[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                weightMatrix[i][j] = energy(i, j);
            }
        }
    }
    private void makeGraph() {
        sizeDigraph = width() * height() + 2;
        digraph = new EdgeWeightedDigraph(sizeDigraph);

        for (int i = 1; i <= width(); i++) {
            DirectedEdge edge = new DirectedEdge(0, i, weightMatrix[0][i - 1]);
            digraph.addEdge(edge);
        }
        for (int i = 0; i < height() - 1; i++) {
            for (int j = 0; j < width(); j++) {
                int v = i * width() + j + 1;
                int w = v + width();
                DirectedEdge edge = new DirectedEdge(v, w, weightMatrix[i + 1][j]);
                digraph.addEdge(edge);
                if (j != 0) {
                    edge = new DirectedEdge(v, w - 1, weightMatrix[i + 1][j - 1]);
                    digraph.addEdge(edge);
                }
                if (j != width() - 1) {
                    edge = new DirectedEdge(v, w + 1, weightMatrix[i + 1][j + 1]);
                    digraph.addEdge(edge);
                }
            }
        }
        for (int i = 1; i <= width(); i++) {
            int n = width() * (height() - 1) + i;
            DirectedEdge edge = new DirectedEdge(n, sizeDigraph - 1, 0);
            digraph.addEdge(edge);
        }
    }

    public int[] findSeam() {
        AcyclicSP acyclic = new AcyclicSP(digraph, 0);
        verticalCarver = new int[height()];
        Stack<DirectedEdge> edges = (Stack<DirectedEdge>) acyclic.pathTo(getSizeDigraph() - 1);
        int j = 0;
        for (int i = edges.size() - 1; i > 0; i--) {
            int n = edges.get(i).to();
            n %= width();
            verticalCarver[j++] = n - 1;
        }

        return verticalCarver;
    }
    public void removeSeam() {
        resMatrixColor = new Color[height()][width() - 1];
        for (int i = 0; i < height(); i++) {
            int n = 0;
            for (int j = 0; j < width() - 1; j++) {
                if (j == verticalCarver
                        [i])
                    n++;
                resMatrixColor[i][j] = matrix_Color[i][j + n];
            }
        }

        picture = newPic();
    }
    public Picture newPic() {
        pictureRes = new Picture(width() - 1, height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width() - 1; j++) {
                pictureRes.set(j, i, resMatrixColor[i][j]);
            }
        }
        return pictureRes;
    }

    public int getSizeDigraph() {
        return sizeDigraph;
    }

    public int height() {
        return picture.height();
    }

    public int width() {
        return picture.width();
    }


    public double energy(int x, int y) {
        if (x == 0 || y == 0 || x == height() - 1 || y == width() - 1)
            return 1000;
        double dxR = matrix_Color[x - 1][y].getRed() - matrix_Color[x + 1][y].getRed();
        double dxG = matrix_Color[x - 1][y].getGreen() - matrix_Color[x + 1][y].getGreen();
        double dxB = matrix_Color[x - 1][y].getBlue() - matrix_Color[x + 1][y].getBlue();
        double resX = Math.pow(dxR, 2) + Math.pow(dxG, 2) + Math.pow(dxB, 2);

        double dyR = matrix_Color[x][y - 1].getRed() - matrix_Color[x][y + 1].getRed();
        double dyG = matrix_Color[x][y - 1].getGreen() - matrix_Color[x][y + 1].getGreen();
        double dyB = matrix_Color[x][y - 1].getBlue() - matrix_Color[x][y + 1].getBlue();
        double resY = Math.pow(dyR, 2) + Math.pow(dyG, 2) + Math.pow(dyB, 2);

        return Math.sqrt(resX + resY);
    }

    public static void main(String[] args) {
        Picture picture = new Picture("resize.jfif");
        SeamCarver seamCarver = new SeamCarver(picture, 100);
    }
}