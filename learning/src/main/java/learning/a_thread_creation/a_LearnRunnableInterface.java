package learning.a_thread_creation;

class Class001 implements Runnable{
    @Override
    public void run() {
        System.out.println("Class001 | Current thread is: " + Thread.currentThread().getName());
    }
}

public class a_LearnRunnableInterface {
    public static void main(String[] args) {
        System.out.println("LearnRunnableInterface | main | Current thread is: " + Thread.currentThread().getName());

        Class001 class001Obj = new Class001();
        Thread thread = new Thread(class001Obj);
        thread.start();

        System.out.println("LearnRunnableInterface | main | Current thread is: " + Thread.currentThread().getName());
    }
}

/*
OUTPUT:
LearnRunnableInterface | main | Current thread is: main
LearnRunnableInterface | main | Current thread is: main
Class001 | Current thread is: Thread-0
*/
