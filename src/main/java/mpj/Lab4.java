package mpj;

import mpi.Datatype;
import mpi.MPI;


public class Lab4 {
    //1 процесс
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int N = 3;
        int[] array = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                array[i * N + j] = (int) (Math.random() * 100); //[0;100]
            }
        }

        int[] result = new int[3 * N + (N - 3)];
        int[] arrayOfBlockLength = new int[N];
        int[] arrayOfDisplacement = new int[N];

        System.out.print(N);
        for (int i = 0; i < 2; i++) {
            arrayOfBlockLength[i] = N;
            arrayOfDisplacement[i] = N * i;
        }
        for (int i = 2; i < N - 1; i++) {
            arrayOfBlockLength[i] = 1;
            arrayOfDisplacement[i] = (N * (i + 1) - 1);
        }

        arrayOfBlockLength[N - 1] = N;
        arrayOfDisplacement[N - 1] = N * (N - 1);


        Datatype matrixtype = Datatype.Indexed(arrayOfBlockLength, arrayOfDisplacement, MPI.INT);
        matrixtype.Commit();

        MPI.COMM_WORLD.Sendrecv(array, 0, 1, matrixtype, rank, 0, result, 0, result.length, MPI.INT, rank, 0);

        System.out.println("before");
        printMatrix(array, N);
        System.out.println("after");
        printResMatrix(result, N);
       /* for(int i: result) {
            System.out.println(String.format("\t%d",i));
        }*/
        MPI.Finalize();
    }

    private static void printResMatrix(int[] matrix, int lenght) {
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < lenght; i++) {
                System.out.print(String.format("%d\t", matrix[j * lenght + i]));
            }
            System.out.println();
        }
        for (int j = 0; j < lenght - 2; j++) {
            System.out.print(repeater("\t", lenght - 1));
            System.out.print(String.format("%d\t", matrix[2 * lenght + j]));
            System.out.println();
        }
        for (int i = 0; i < lenght; i++) {
            System.out.print(String.format("%d\t", matrix[3 * lenght - 3 + i]));
        }
        System.out.println();
    }

    private static String repeater(String text, int count) {
        String res = "";
        for (int i = 0; i < count; i++) res += text;
        return res;
    }

    private static void printMatrix(int[] array, int lenght) {
        for (int i = 0; i < lenght; i++) {
            for (int j = 0; j < lenght; j++) {
                System.out.print(String.format("%d\t", array[i * lenght + j]));
            }
            System.out.println();
        }
    }
}