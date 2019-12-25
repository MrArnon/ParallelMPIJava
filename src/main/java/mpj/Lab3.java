package mpj;

import mpi.*;
/*
задание 19
 найти сууму четных иначе 0
 */
public class Lab3 {
    public static void main(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
       //System.out.println(String.format("size = %d",size));
        int sizeOfNumbers = 100;
        int n = 5;
        int[] localArray = new int[n];
        int[] resArray = new int[n];
        int[] localResArray = new int[2];

        for(int i = 0; i < localArray.length; i++) {
            localArray[i] = (int) (Math.random()*sizeOfNumbers);
          System.out.println(String.format("pre res Process %d, number %d: %d", rank, localArray[i], i));
        }

        FindOperation op = new FindOperation();
        MPI.COMM_WORLD.Reduce(localArray,0,resArray,0, n,MPI.INT, new Op(op, true), 0);
        if(rank == 0) {
            for(int i = 0; i < n;i++) {
                System.out.println(String.format("Column - %d: %d", i, resArray[i]));
            }
        }

        MPI.Finalize();
    }
}

class FindOperation extends User_function{

    @Override
    public void Call(Object o, int i, Object o1, int i1, int i2, Datatype datatype) throws MPIException {
        try {
            int[] input = (int[]) o;
            int[] res = (int[]) o1;

            for(int j = 0; j < i2; j++) {
               if ((res[j] % 2 )== 1 ){
                   res[j]=0;
               }
                if ((input[j] % 2) == 0) {

                  //  System.out.println(String.format("odd  -   %d", input[j]));
                    res[j] = res[j] + input[j];
                }
                //else {
                   // System.out.println(String.format("not odd  -   %d", input[j]));
                //}
            }
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }

}