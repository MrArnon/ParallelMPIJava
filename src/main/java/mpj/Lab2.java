package mpj;
import mpi.MPI;
/*
e^(-x^2)
 */
public class Lab2 {
    private final static double A = -1.0;
    private final static double B = 1.0;
    private final static int N = 8;
    private final static double EPSILON = 0.001;

    public static void main (String [] args){


    MPI.Init(args);

    int rank = MPI.COMM_WORLD.Rank();
    int size = MPI.COMM_WORLD.Size();
    if (size>=2 & (size % 2==0)) {
        int dotsForProcess = N / size;

        double[] sendArr = new double[dotsForProcess * 2];
        double[] recvArr = new double[dotsForProcess];


        double[] dots = new double[N];
        double[] result = new double[N * 2];


        if (rank == 0) {
            double dot = A;
            double additional = (B - A) / (N - 1);
            for (int i = 0; i < N; i++) {
                dots[i] = dot;
                dot += additional;
            }
        }

        MPI.COMM_WORLD.Scatter(dots, (rank) * dotsForProcess, dotsForProcess, MPI.DOUBLE, recvArr, 0, dotsForProcess, MPI.DOUBLE, 0);
        for (int i = 0; i < recvArr.length; i++) {
            sendArr[2 * i] = accuracyFunc(recvArr[i]);
            sendArr[2 * i + 1] = seriesFunc(recvArr[i], EPSILON);
        }
        MPI.COMM_WORLD.Gather(sendArr, 0, sendArr.length, MPI.DOUBLE, result, rank * dotsForProcess, sendArr.length, MPI.DOUBLE, 0);
        if (rank == 0) {
            System.out.println("x\t\taccuracyFunc\t\tseriesFunc");
            for (int i = 0; i < N; i++) {
                System.out.println(String.format("%f\t%f\t%f", dots[i], result[2 * i], result[2 * i + 1]));
            }
        }
    }
    MPI.Finalize();
}
    private static double seriesFunc(double dot, double eps) {
        int i = 1;
        double sum = 1;
        int factorial = 1;
        double x = 1;
        while (Math.abs(x) > eps) {
            x = Math.pow(-1, i) * Math.pow(dot, 2 * i) / factorial;
            sum += x;
            i += 1;
            factorial *= i;
        }
        return sum;
    }
    private static double accuracyFunc(double dot) {
        return  Math.exp(-1*(dot*dot)) ;
    }


}
