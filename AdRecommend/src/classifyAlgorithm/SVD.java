package classifyAlgorithm;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class SVD {
    Matrix matrix;
    public SVD(Matrix matrix) {
        this.matrix = matrix;
    }






    public static void main(String[] args) {
        double[][] arr = new double[][]{{1, 5, 9},{2, 6, 10},{3, 7, 11},{4, 8, 12}};
        Matrix B= new Matrix(arr);
        SingularValueDecomposition singularValueDecomposition = B.svd();
        singularValueDecomposition.getV().print(4, 3);
        singularValueDecomposition.getS().print(4, 3);
        /*// create M-by-N matrix that doesn't have full rank
        int M = 8, N = 5;
        Matrix B = Matrix.random(5, 3);
        System.out.println("B = ");
        B.print(5, 3);
        Matrix A = Matrix.random(M, N).times(B).times(B.transpose());
        System.out.print("A = ");
        A.print(9, 6);

        // compute the singular vallue decomposition
        System.out.println("A = U*S*T(V)");
        System.out.println();
        SingularValueDecomposition s = A.svd();
        System.out.print("U = ");
        Matrix U = s.getU();
        U.print(9, 6);
        System.out.print("Sigma = ");
        Matrix S = s.getS();
        S.print(9, 6);
        System.out.print("V = ");
        Matrix V = s.getV();
        V.print(9, 6);
        System.out.println("rank = " + s.rank());
        System.out.println("condition number = " + s.cond());
        System.out.println("2-norm = " + s.norm2());

        // print out singular values
        System.out.print("singular values = ");
        Matrix svalues = new Matrix(s.getSingularValues(), 1);
        svalues.print(9, 6);*/
    }

}